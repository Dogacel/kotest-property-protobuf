package dogacel.kotest

import com.google.protobuf.Descriptors
import com.google.protobuf.DynamicMessage
import com.google.protobuf.GeneratedMessage
import com.google.protobuf.Message
import com.google.protobuf.MessageOrBuilder
import kotlin.reflect.KClass

internal object ReflectionUtils {
    /**
     * Use reflection to get the default instance for the given protobuf message class.
     */
    fun <T : MessageOrBuilder> defaultInstance(klass: KClass<T>): Message {
        val defaultInstance =
            klass.java
                .getMethod("getDefaultInstance")
                .invoke(null) as GeneratedMessage
        return defaultInstance
    }

    /**
     * Use reflection to create a new builder for the given protobuf message class.
     */
    fun newBuilder(descriptor: Descriptors.Descriptor): Message.Builder {
        return DynamicMessage.newBuilder(descriptor)
    }

    /**
     * Use reflection to get the descriptor for the given protobuf message class.
     */
    fun <T : MessageOrBuilder> messageClassToDescriptor(klass: KClass<T>): Descriptors.Descriptor {
        return defaultInstance(klass).descriptorForType
    }
}
