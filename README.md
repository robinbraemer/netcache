# NetCache
Cache and Persistent Storage Framework on top of [KryoNetty](https://github.com/Koboo/kryonetty). 

With NetCache objects can easily be cached and stored in the persistent sotrage. These are coded into strings using [Kryo](https://github.com/EsotericSoftware/kryo) & Base64 and written to the database. So far only the MySQL storage is stable. In the future, off-heap caching and client inbound caching will be added.


## Documentation

Nothing done here..


## Add as dependecy

First of all add `jitpack.io` as repository. 

```java
    repositories {
        maven { url 'https://jitpack.io' }
    }
```

After that you can add it as dependency. Tag for example `1.1`
```java
    dependencies {
        implementation 'com.github.Koboo:netcache:1.1'
    }
```

## Build from source

If you want to build `netcache` from source, clone this repository and run `./gradlew buildNetCache`. The output-file will be in the directory: `/build/libs/netcache-{version}-all.jar`
Gradle downloads the required dependencies and inserts all components into the output-file.
If you are interested in the build task:

```java
task buildNetCache(type: Jar) {
    baseName = project.name + '-all'
    from {
        configurations.compile.collect {
            it.isDirectory() ? it : zipTree(it)
        }
    }
    with jar
}
```

Use the [LevenProxy Discord Server](https://discord.levenproxy.eu/) for `NetCache` support.