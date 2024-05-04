# Http4k clear color
### A first try
Starting of with [this](https://developer.mozilla.org/en-US/docs/Web/API/WebGL_API/Tutorial/Getting_started_with_WebGL) tutorial!

## Steps for running
### Check out Sea of Shadows repository
```shell
cd ../.. && git clone git@github.com:jdw/seaofshadows.git
```

### Check out 0.0.1-SNAPSHOT commit
```shell
cd ../../seaofshadows && git checkout ebceb32db7ac6c45fbc8e806f763f6804f4f59f1
```

### Build terminal javascript file
```shell
cd ../../seaofshadows && ./gradlew :seaofshadows-terminal:jsBrowserDevelopmentWebpack
```

#### or

```shell
cd ../../seaofshadows && ./gradlew :seaofshadows-terminal:jsBrowserProductionWebpack
```

### Copy terminal JS file to resources/static folder for serving
```shell
cp ../../seaofshadows/terminal/output/seaofshadows-terminal.js src/main/resources/static
```

### Publish to Maven local
```shell
cd ../../seaofshadows && ./gradlew publishToMavenLocal
```

### Copy terminal JS file to resources/static folder for serving
```shell
./gradlew shadowJar
```

### Run the compiled überJAR
```shell
java -jar build/libs/0.0.1-SNAPSHOT-http4k-proof-of-concept.jar
```

Point your browser to [localhost:9000](http://localhost:9000)!