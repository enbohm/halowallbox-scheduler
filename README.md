# `Halo wallbox scheduler
A project with connects with a Halo Wallbox API to turn on/off lights, add schedules, etc.

## Building and running locally
```mvn clean verify``` builds the code and runs tests.

## Packaging
The following command packages the application into a native executable.
```mvn package -Pnative -Dquarkus.native.container-build=true```
