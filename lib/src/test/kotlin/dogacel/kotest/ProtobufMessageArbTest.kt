package dogacel.kotest

import com.google.protobuf.Value
import dogacel.kotest.ProtobufMessageArb.protobufMessage
import enums.Enums.AliasedEnum
import enums.Enums.MessageWithEnum
import enums.Enums.TestEnum
import io.kotest.core.spec.style.FunSpec
import io.kotest.inspectors.forAll
import io.kotest.inspectors.forNone
import io.kotest.inspectors.forSome
import io.kotest.matchers.collections.shouldHaveAtLeastSize
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.comparables.shouldBeLessThan
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldHaveLength
import io.kotest.matchers.string.shouldHaveMinLength
import io.kotest.property.Arb
import io.kotest.property.PropertyTesting
import io.kotest.property.arbitrary.take
import maps.Maps.MapsMessage
import messages.MessageNoFieldsOuterClass.MessageNoFields
import oneofs.Oneofs.OneofMessage
import primitives.Primities.PrimitivesMessage
import repeateds.Repeateds.RepeatedsMessage
import wkt.WellKnownTypes.MessageWithWellKnownTypes

class ProtobufMessageArbTest : FunSpec({
    PropertyTesting.defaultEdgecasesGenerationProbability = 0.9

    test("should generate arbitrary protobuf messages for primitive types") {
        val protobufMessageArb = Arb.protobufMessage<PrimitivesMessage>()

        val samples = protobufMessageArb.take(1000).toList()

        listOf(
            PrimitivesMessage::getOptionalInt32,
            PrimitivesMessage::getOptionalUint32,
            PrimitivesMessage::getOptionalSint32,
            PrimitivesMessage::getOptionalFixed32,
            PrimitivesMessage::getOptionalSfixed32,
        )
            .forEach { getter ->
                samples.map(getter)
                    .forSome { it shouldBeGreaterThan 0 }
                    .forSome { it shouldBeLessThan 0 }
                    .forSome { it shouldBe 0 }
            }

        listOf(
            PrimitivesMessage::getOptionalInt64,
            PrimitivesMessage::getOptionalUint64,
            PrimitivesMessage::getOptionalSint64,
            PrimitivesMessage::getOptionalFixed64,
            PrimitivesMessage::getOptionalSfixed64,
        )
            .forEach { getter ->
                samples.map(getter)
                    .forSome { it shouldBeGreaterThan 0 }
                    .forSome { it shouldBeLessThan 0 }
                    .forSome { it shouldBe 0 }
                    .forSome { it shouldBeGreaterThan Int.MAX_VALUE.toLong() }
                    .forSome { it shouldBeLessThan Int.MIN_VALUE.toLong() }
            }

        samples.map { it.optionalFloat }
            .forSome { it shouldBeGreaterThan 0.0f }
            .forSome { it shouldBeLessThan 0.0f }
            .forSome { it shouldBe 0f }

        samples.map { it.optionalDouble }
            .forSome { it shouldBeGreaterThan 0.0 }
            .forSome { it shouldBeLessThan 0.0 }
            .forSome { it shouldBe 0.0 }

        samples.map { it.optionalBool }
            .forSome { it shouldBe true }
            .forSome { it shouldBe false }

        samples.map { it.optionalString }
            .forSome { it shouldBe "" }
            .forSome { it shouldHaveLength 1 }
            .forSome { it shouldHaveMinLength 1 }

        samples.map { it.optionalBytes }
            .forSome { it shouldHaveSize 0 }
            .forSome { it shouldHaveAtLeastSize 1 }
    }

    test("should generate arbitrary protobuf messages for enums") {
        val protobufMessageArb = Arb.protobufMessage<MessageWithEnum>()

        val samples = protobufMessageArb.take(1000).toList()

        samples.map { it.testEnum }
            .forSome { it shouldBe TestEnum.FOO }
            .forSome { it shouldBe TestEnum.BAR }
            .forSome { it shouldBe TestEnum.BAZ }
            .forSome { it shouldBe TestEnum.TOP }
            .forSome { it shouldBe TestEnum.NEG }
            .forNone { it shouldBe TestEnum.UNRECOGNIZED }

        samples.map { it.aliasedEnum }
            .forSome { it shouldBe AliasedEnum.ALIAS_FOO }
            .forSome { it shouldBe AliasedEnum.ALIAS_BAR }
            .forSome { it shouldBe AliasedEnum.ALIAS_BAZ }
            .forSome { it shouldBe AliasedEnum.QUX }
            .forSome { it shouldBe AliasedEnum.qux }
            .forSome { it shouldBe AliasedEnum.bAz }
            .forNone { it shouldBe AliasedEnum.UNRECOGNIZED }
    }

    test("should generate arbitrary protobuf messages for repeated fields") {
        val protobufMessageArb = Arb.protobufMessage<RepeatedsMessage>()
        val samples = protobufMessageArb.take(1000).toList()

        listOf(
            RepeatedsMessage::getRepeatedInt32List,
            RepeatedsMessage::getRepeatedInt64List,
            RepeatedsMessage::getRepeatedUint32List,
            RepeatedsMessage::getRepeatedUint64List,
            RepeatedsMessage::getRepeatedSint32List,
            RepeatedsMessage::getRepeatedSint64List,
            RepeatedsMessage::getRepeatedFixed32List,
            RepeatedsMessage::getRepeatedFixed64List,
            RepeatedsMessage::getRepeatedSfixed32List,
            RepeatedsMessage::getRepeatedSfixed64List,
            RepeatedsMessage::getRepeatedFloatList,
            RepeatedsMessage::getRepeatedDoubleList,
            RepeatedsMessage::getRepeatedBoolList,
            RepeatedsMessage::getRepeatedStringList,
            RepeatedsMessage::getRepeatedBytesList,
            RepeatedsMessage::getRepeatedNestedMessageList,
            RepeatedsMessage::getRepeatedForeignMessageList,
            RepeatedsMessage::getRepeatedNestedEnumList,
            RepeatedsMessage::getRepeatedForeignEnumList,
        ).forEach { getter ->
            samples.map(getter)
                .forSome { it shouldHaveSize 0 }
                .forSome { it shouldHaveAtLeastSize 1 }
        }
    }

    test("should generate arbitrary protobuf messages for maps") {
        val protobufMessageArb = Arb.protobufMessage<MapsMessage>()
        val samples = protobufMessageArb.take(1000).toList()

        listOf(
            MapsMessage::getMapInt32Int32Map,
            MapsMessage::getMapInt64Int64Map,
            MapsMessage::getMapUint32Uint32Map,
            MapsMessage::getMapUint64Uint64Map,
            MapsMessage::getMapSint32Sint32Map,
            MapsMessage::getMapSint64Sint64Map,
            MapsMessage::getMapFixed32Fixed32Map,
            MapsMessage::getMapFixed64Fixed64Map,
            MapsMessage::getMapSfixed32Sfixed32Map,
            MapsMessage::getMapSfixed64Sfixed64Map,
            MapsMessage::getMapInt32FloatMap,
            MapsMessage::getMapInt32DoubleMap,
            MapsMessage::getMapBoolBoolMap,
            MapsMessage::getMapStringStringMap,
            MapsMessage::getMapStringBytesMap,
            MapsMessage::getMapStringNestedMessageMap,
            MapsMessage::getMapStringForeignMessageMap,
            MapsMessage::getMapStringNestedEnumMap,
            MapsMessage::getMapStringForeignEnumMap,
        ).forEach { getter ->
            samples.map(getter)
                .forSome { it.values shouldHaveSize 0 }
                .forSome { it.values shouldHaveAtLeastSize 1 }
        }
    }

    test("should generate arbitrary protobuf messages for empty message") {
        val protobufMessageArb = Arb.protobufMessage<MessageNoFields>()
        val samples = protobufMessageArb.take(1000).toList()

        samples.forAll { it shouldBe MessageNoFields.getDefaultInstance() }
    }

    test("should generate arbitrary protobuf messages with oneofs") {
        val protobufMessageArb = Arb.protobufMessage<OneofMessage>()
        val samples = protobufMessageArb.take(1000).toList()

        samples
            .forSome { it.oneofFieldCase shouldBe OneofMessage.OneofFieldCase.ONEOFFIELD_NOT_SET }
            .forSome { it.oneofFieldCase shouldBe OneofMessage.OneofFieldCase.ONEOF_UINT32 }
            .forSome { it.oneofFieldCase shouldBe OneofMessage.OneofFieldCase.ONEOF_NESTED_MESSAGE }
            .forSome { it.oneofFieldCase shouldBe OneofMessage.OneofFieldCase.ONEOF_STRING }
            .forSome { it.oneofFieldCase shouldBe OneofMessage.OneofFieldCase.ONEOF_BYTES }
            .forSome { it.oneofFieldCase shouldBe OneofMessage.OneofFieldCase.ONEOF_BOOL }
            .forSome { it.oneofFieldCase shouldBe OneofMessage.OneofFieldCase.ONEOF_UINT64 }
            .forSome { it.oneofFieldCase shouldBe OneofMessage.OneofFieldCase.ONEOF_FLOAT }
            .forSome { it.oneofFieldCase shouldBe OneofMessage.OneofFieldCase.ONEOF_DOUBLE }
            .forSome { it.oneofFieldCase shouldBe OneofMessage.OneofFieldCase.ONEOF_ENUM }

        samples
            .forSome { it.secondOneofFieldCase shouldBe OneofMessage.SecondOneofFieldCase.SECONDONEOFFIELD_NOT_SET }
            .forSome { it.secondOneofFieldCase shouldBe OneofMessage.SecondOneofFieldCase.LEFT }
            .forSome { it.secondOneofFieldCase shouldBe OneofMessage.SecondOneofFieldCase.RIGHT }
    }

    test("should generate arbitrary protobuf messages with well known types") {
        val protobufMessageArb = Arb.protobufMessage<MessageWithWellKnownTypes>()
        val samples = protobufMessageArb.take(1000).toList()

        samples
            .forSome { it.hasOptionalBoolWrapper() shouldBe true }
            .forSome { it.hasOptionalInt32Wrapper() shouldBe true }
            .forSome { it.hasOptionalInt64Wrapper() shouldBe true }
            .forSome { it.hasOptionalUint32Wrapper() shouldBe true }
            .forSome { it.hasOptionalUint64Wrapper() shouldBe true }
            .forSome { it.hasOptionalFloatWrapper() shouldBe true }
            .forSome { it.hasOptionalDoubleWrapper() shouldBe true }
            .forSome { it.hasOptionalStringWrapper() shouldBe true }
            .forSome { it.hasOptionalBytesWrapper() shouldBe true }
            .forSome { it.hasOptionalDuration() shouldBe true }
            .forSome { it.hasOptionalTimestamp() shouldBe true }
            .forSome { it.hasOptionalFieldMask() shouldBe true }
            .forSome { it.hasOptionalStruct() shouldBe true }
            .forSome { it.hasOptionalAny() shouldBe true }
            .forSome { it.hasOptionalValue() shouldBe true }
            .forSome { it.hasOptionalListValue() shouldBe true }

        samples
            .forSome { it.optionalBoolWrapper.value shouldBe true }
            .forSome { it.optionalBoolWrapper.value shouldBe false }
            .forSome { it.optionalInt32Wrapper.value shouldBeGreaterThan 0 }
            .forSome { it.optionalInt64Wrapper.value shouldBeGreaterThan 0 }
            .forSome { it.optionalUint32Wrapper.value shouldBeGreaterThan 0 }
            .forSome { it.optionalUint64Wrapper.value shouldBeGreaterThan 0 }
            .forSome { it.optionalFloatWrapper.value shouldBeGreaterThan 0.0f }
            .forSome { it.optionalDoubleWrapper.value shouldBeGreaterThan 0.0 }
            .forSome { it.optionalStringWrapper.value shouldHaveLength 1 }
            .forSome { it.optionalBytesWrapper.value shouldHaveSize 1 }

        samples
            .forSome { it.optionalDuration.seconds shouldBeGreaterThan 0 }
            .forSome { it.optionalDuration.nanos shouldBeGreaterThan 0 }
            .forSome { it.optionalTimestamp.seconds shouldBeGreaterThan 0 }
            .forSome { it.optionalTimestamp.nanos shouldBeGreaterThan 0 }

        samples
            .forSome { it.optionalFieldMask.pathsList shouldHaveSize 1 }
            .forSome { it.optionalStruct.fieldsMap.values shouldHaveSize 1 }
            .forSome { it.optionalAny.typeUrl shouldHaveLength 1 }
            .forSome { it.optionalValue.kindCase shouldBe Value.KindCase.NUMBER_VALUE }
            .forSome { it.optionalValue.kindCase shouldBe Value.KindCase.STRING_VALUE }
            .forSome { it.optionalValue.kindCase shouldBe Value.KindCase.BOOL_VALUE }
            .forSome { it.optionalValue.kindCase shouldBe Value.KindCase.STRUCT_VALUE }
            .forSome { it.optionalValue.kindCase shouldBe Value.KindCase.LIST_VALUE }
            .forSome { it.optionalValue.kindCase shouldBe Value.KindCase.NULL_VALUE }
            .forSome { it.optionalListValue.valuesList shouldHaveSize 1 }
    }
})
