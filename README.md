# GAR Coursier
[Coursier](https://github.com/coursier/coursier) comes with the capability to resolve [URLs with custom protocols](https://get-coursier.io/docs/extra.html#extra-protocols). 
This requires an implementation of `URLStreamHandlerFactory` in the package `coursier.cache.protocol`. 
This library offers an implementation for Google Artifact Registry (GAR) urls protocol `artifactregistry://`.

## Usage
### SBT
Support is still [in development](https://github.com/sbt/sbt/pull/6375).
### Scala Steward
Support is still [in development](https://github.com/scala-steward-org/scala-steward/pull/2628)
### Coursier
> Coursier will search for your plugin with the following order on various classloaders:
> 
> 1. Custom classloaders provided via the API (see FileCache.withClassLoaders)
> 2. Thread.currentThread().getContextClassLoader
> 3. The classloader that loaded coursier itself (more precisely, coursier.cache.CacheUrl.getClass.getClassLoader)

https://get-coursier.io/docs/extra.html#extra-protocols