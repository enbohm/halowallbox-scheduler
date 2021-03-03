# `Halo wallbox scheduler
A project with connects with a Halo Wallbox API to turn on/off lights, add schedules, etc.

## Building and running locally
```mvn clean verify``` builds the code and runs tests.

## Packaging
You can create a native executable using: 

`mvn package -Pnative` (requires GraalVM installed on you local OS) OR you can use Docker to build the native executable using:

`mvn package -Pnative -Dquarkus.native.container-build=true`.

The produced executable will be a 64 bit Linux executable, so depending on your operating system it may no longer be runnable. If you are running on Linux you can then execute your binary: `./halo-wallbox-scheduler-runner` otherwise you will need to wrap the exucutable in a container. This can be done manually or by executing

```mvn package -Pnative -Dquarkus.native.container-build=true -Dquarkus.container-image.build=true```
