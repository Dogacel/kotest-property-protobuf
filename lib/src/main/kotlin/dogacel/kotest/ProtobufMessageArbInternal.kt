package dogacel.kotest

import com.google.protobuf.ByteString
import com.google.protobuf.Descriptors
import com.google.protobuf.Descriptors.FieldDescriptor.JavaType
import com.google.protobuf.Message
import io.kotest.property.Arb
import io.kotest.property.arbitrary.ArbitraryBuilderContext
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.boolean
import io.kotest.property.arbitrary.byte
import io.kotest.property.arbitrary.byteArray
import io.kotest.property.arbitrary.double
import io.kotest.property.arbitrary.element
import io.kotest.property.arbitrary.float
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.orNull
import io.kotest.property.arbitrary.string

/**
 * Contains internal utilities for generating random protobuf messages used by [ProtobufMessageArb].
 */
internal class ProtobufMessageArbInternal(val settings: ProtobufMessageArbSettings) {
    private val visited: MutableMap<Descriptors.Descriptor, Int> = mutableMapOf()

    /**
     * Create a generator for protobuf messages with the given [descriptor].
     */
    internal fun arbProtobufMessageInternal(descriptor: Descriptors.Descriptor): Arb<Message> {
        return arbitrary { rs ->
            val builder = ReflectionUtils.newBuilder(descriptor)

            // Prevent recursive types from going into infinite loop.
            // Even though each type has a random probability of being null, a list of messages
            // might grow exponentially if we don't terminate the recursion.
            val currentDepth = visited.getOrDefault(descriptor, 0)
            if (currentDepth >= settings.maxDepth) {
                return@arbitrary builder.build()
            }

            visited[descriptor] = currentDepth + 1

            descriptor.fields.forEach {
                when {
                    it.isRepeated -> fillRepeatedField(builder, it)
                    it.containingOneof != null -> fillOneofField(builder, it)
                    else -> fillStandaloneField(builder, it)
                }
            }

            visited[descriptor] = visited.getOrDefault(descriptor, 1) - 1
            builder.build()
        }
    }

    /**
     * Fill a standalone field (not oneof, repeated and map) with a random value.
     */
    private suspend fun ArbitraryBuilderContext.fillStandaloneField(
        builder: Message.Builder,
        descriptor: Descriptors.FieldDescriptor,
    ) {
        val value = descriptor.randomValueForType().orNull(1.0 - settings.fieldPresenceProbability).bind()
        value?.let { builder.setField(descriptor, it) }
    }

    /**
     * Fill a field that is part of a oneof with a random value.
     */
    private suspend fun ArbitraryBuilderContext.fillOneofField(
        builder: Message.Builder,
        descriptor: Descriptors.FieldDescriptor,
    ) {
        // TODO: Probabilities are not uniformly distributed.
        val nullProbability = 1.0 - (1.0 / descriptor.containingOneof.fieldCount)
        val value = descriptor.randomValueForType().orNull(nullProbability).bind()
        value?.let { builder.setField(descriptor, it) }
    }

    /**
     * Fill a repeated field with random values. Maps are also considered repeated fields of Entry type, which is a
     * key-value pair. `randomValueForType` automatically finds the wrapper type for the map and generates a random
     * value for it.
     */
    private suspend fun ArbitraryBuilderContext.fillRepeatedField(
        builder: Message.Builder,
        descriptor: Descriptors.FieldDescriptor,
    ) {
        val value =
            Arb.list(
                descriptor.randomValueForType(),
                range = settings.defaultListItemCount,
            ).orNull(1.0 - settings.fieldPresenceProbability).bind()

        value?.forEach { builder.addRepeatedField(descriptor, it) }
    }

    /**
     * Get a random generator for a given field descriptor.
     */
    private fun Descriptors.FieldDescriptor.randomValueForType(): Arb<Any> {
        return when (javaType) {
            JavaType.INT -> Arb.int(Int.MIN_VALUE, Int.MAX_VALUE)
            JavaType.LONG -> Arb.long()
            JavaType.BOOLEAN -> Arb.boolean()
            JavaType.DOUBLE -> Arb.double()
            JavaType.STRING -> Arb.string()
            JavaType.FLOAT -> Arb.float()
            JavaType.BYTE_STRING -> generateByteArray()
            JavaType.ENUM -> Arb.element(enumType.values)
            JavaType.MESSAGE -> arbProtobufMessageInternal(this.messageType)
        }
    }

    private fun generateByteArray(): Arb<ByteString> {
        return Arb.byteArray(Arb.int(0, 256), Arb.byte()).map { ByteString.copyFrom(it) }
    }
}
