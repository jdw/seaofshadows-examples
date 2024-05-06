# Sea of Shadows example #2
## Ktor different serialization techniques
### A first try
Starting of with [this](https://developer.mozilla.org/en-US/docs/Web/API/WebGL_API/Tutorial/Getting_started_with_WebGL) tutorial!

## Steps for running
### Check out Sea of Shadows repository
```shell
cd ../.. && git clone git@github.com:jdw/seaofshadows.git
```

### Check out 0.0.2-SNAPSHOT commit
```shell
cd ../../seaofshadows && git checkout aebc84eda1dbb732e365d728fd814f52c7c097d7
```

### Build terminal javascript file
```shell
./gradlew jsBrowserDevelopmentWebpack
```

#### or

```shell
./gradlew jsBrowserProductionWebpack
```

### Publish library to Maven local
```shell
cd ../../seaofshadows && ./gradlew publishToMavenLocal
```

### Compile and run überJAR
```shell
./gradlew runShadow
```

Point your browser to [localhost:8080](http://localhost:8080)!