# Kotest Protobuf Property Testing
![Maven Central Version](https://img.shields.io/maven-central/v/io.github.dogacel/kotest-property-protobuf?style=for-the-badge)
![GitHub Release Date](https://img.shields.io/github/release-date/Dogacel/kotest-property-protobuf?display_date=published_at&style=for-the-badge)

[![Build](https://github.com/Dogacel/kotest-property-protobuf/actions/workflows/build.yaml/badge.svg)](https://github.com/Dogacel/kotest-property-protobuf/actions/workflows/build.yaml)
[![codecov](https://codecov.io/gh/Dogacel/kotest-property-protobuf/graph/badge.svg?token=6FE41BE2ZK)](https://codecov.io/gh/Dogacel/kotest-property-protobuf)
[![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Contributors](https://img.shields.io/github/contributors/dogacel/kotest-property-protobuf.svg)](https://github.com/dogacel/kotest-property-protobuf/contributors)
[![Commits](https://img.shields.io/github/commit-activity/m/dogacel/kotest-property-protobuf.svg?label=commits)](https://github.com/dogacel/kotest-property-protobuf/pulse)


Property testing extension for Protobuf using `kotest-property`. You can generate random protobuf messages using 
`Arb.protobufMessage<...>()` call and customize the generation properties.

## Usage

Include the dependency inside `build.gradle.kts`.

```kotlin
dependencies {
    testImplementation("io.github.dogacel:kotest-property-protobuf:LATEST")
}
```

Use the `Arb` generators provided by the module to generate random protobuf classes.

```kotlin
import dogacel.kotest.ProtobufMessageArb.protobufMessage

class SampleProtobufTest : FunSpec({
    test("should convert without errors") {
        val settings = ProtobufMessageArbSettings(fieldPresenceProbability = 0.80)
        Arb.protobufMessage<FooMessage>(settings).forAll { protoMessage ->
            val sut = convert(protoMessage)
            // Do some validation
            sut.id == protoMessage.id && sut.name == (protoMessage.firstName + protoMessage.lastName)
        }
    }
})
```

For configuring the generation settings, check `ProtobufMessageArbSettings` class.

## Roadmap

- [x] Allow generation of random protobuf messages. 
- [ ] Generate realistic well-known types. (I.e. generate timestamps in ±50 years)
- [ ] Allow customizing generation settings per field.
- [ ] Support [protovalidate](https://github.com/bufbuild/protovalidate) rules.

## Releasing

> [!NOTE]
> This section is applicable to official maintainers only.

1. Update `version` under root `build.gradle.kts`.
2. Make sure you set `SONATYPE_USERNAME`, `SONATYPE_PASSWORD`, `GPG_SIGNING_KEY` and `GPG_SIGNING_PASSPHRASE`.
2. `./gradlew publishToSonatype`
3. `./gradlew findSonatypeStagingRepository closeSonatypeStagingRepository`
4. `./gradlew findSonatypeStagingRepository releaseSonatypeStagingRepository`

For any errors, visit https://s01.oss.sonatype.org/#stagingRepositories.

### Snapshots

After you release a `-SNAPSHOT` version, you need the following block to import it.

```kotlin
repositories {
    maven {
        this.url = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
    }
}
```

## Contribution

For starters, start by checking issues. 

Linting can be done via

```bash
``./gradlew ktlintFormat
```

Building the whole project,

```bash
./gradlew build
```

Check coverage of the code,

```bash
./gradlew koverHtmlReport
```

Please feel free to open issues and PRs.
