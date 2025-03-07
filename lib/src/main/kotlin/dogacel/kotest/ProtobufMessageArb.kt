package dogacel.kotest

import com.google.protobuf.GeneratedMessage
import dogacel.kotest.ReflectionUtils.messageClassToDescriptor
import io.kotest.property.Arb
import io.kotest.property.arbitrary.map
import kotlin.reflect.KClass

/**
 * Contains utilities for generating random protobuf messages using Kotest [Arb] API.
 */
object ProtobufMessageArb {
    /**
     * Generate a random protobuf message of the given type.
     */
    inline fun <reified T : GeneratedMessage> Arb.Companion.protobufMessage(
        settings: ProtobufMessageArbSettings = GlobalProtobufMessageArbSettings,
    ): Arb<T> {
        return protobufMessage(T::class, settings)
    }

    /**
     * Generate a random protobuf message of the given type.
     */
    fun <T : GeneratedMessage> Arb.Companion.protobufMessage(
        kClass: KClass<T>,
        settings: ProtobufMessageArbSettings = GlobalProtobufMessageArbSettings,
    ): Arb<T> {
        val descriptor = messageClassToDescriptor(kClass)
        return ProtobufMessageArbInternal(settings).arbProtobufMessageInternal(descriptor).map {
            val bytes = it.toByteArray()
            val result = kClass.java.getMethod("parseFrom", ByteArray::class.java).invoke(null, bytes)
            result as T
        }
    }
}
