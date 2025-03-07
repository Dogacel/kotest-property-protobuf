package dogacel.kotest

open class ProtobufMessageArbSettings(
    /**
     * Maximum depth to generate nested messages. Only applies to self-referencing messages.
     */
    var maxDepth: Int = 3,
    /**
     * Probability of a field being present in a generated message.
     */
    var fieldPresenceProbability: Double = 0.9,
    /**
     * Default range that determines the number of items in a repeated field.
     */
    var defaultListItemCount: IntRange = 0..10,
)

object GlobalProtobufMessageArbSettings : ProtobufMessageArbSettings()
