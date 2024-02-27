[![Maven Central](https://img.shields.io/maven-central/v/com.vipulasri.kachetor/kachetor)](https://search.maven.org/search?q=g:com.vipulasri.kachetor) ![Build status](https://github.com/vipulasri/kachetor/actions/workflows/build.yml/badge.svg)

## Kachetor

Kachetor is an easy-to-use Kotlin Multiplatform caching library to support persistent cache with [Ktor client](https://ktor.io/docs/getting-started-ktor-client.html).

***Supported platforms:***
* **Android**
* **iOS** (iosArm64, iosX64, iosSimulatorArm64)

## Setup (Gradle)

```kotlin
dependencies {
    implementation("com.vipulasri.kachetor:kachetor:<version>")
}
```

Don't forget to replace <version> with the latest found on the badges above or the desired version.

## Usage

Simply provide the `Kachetor` instance to the `HttpCache` plugin and you are good to go.

```kotlin
install(HttpCache) {
    publicStorage(KachetorStorage(10 * 1024 * 1024)) // 10MB
}
```

Kachetor will use the default cache directory on each platform. For Android, it will use the cache directory and for iOS, it will use the `caches` directory.
It can also fallback to using in-memory cache if the persistent cache is not available.

## License

```
Copyright 2024 Vipul Asri
 
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    https://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```