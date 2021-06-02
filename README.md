# Charge Amps - Halo Wallbox scheduler
![Halowallbox status](https://github.com/enbohm/halowallbox-scheduler/actions/workflows/maven.yml/badge.svg)

A service that connects with the EV-charger from Charge Amps Halo Wallbox API. The APIs support turning on/off lights, add schedules, etc. This project turns the LED light on at sunset. The LED light will turn off at the time specified in application.properties.

API reference at https://eapi.charge.space/swagger/index.html

## Building and running locally
You need Java 11 and maven 3.8 (or above) installed on your local machine. 

```mvn clean verify``` builds the code and runs tests. 

```mvn quarkus:dev``` compiles and starts the app locally. You can also run [HaloMain](src/main/java/se/enbohms/halo/HaloMain.java) directly as standard 'Java main'-method from your IDE. 

## Packaging
You can create a native executable using: 

`mvn package -Pnative` (requires GraalVM installed on you local OS) OR you can use Docker to build the native executable using:

`mvn package -Pnative -Dquarkus.native.container-build=true`. (requires that Docker is installed)

The produced executable will be a 64 bit Linux executable, so depending on your operating system it may no longer be runnable. If you are running on Linux you can then execute your binary: `./halo-wallbox-scheduler-runner` otherwise you will need to wrap the exucutable in a container. This can be done manually or by executing

```mvn package -Pnative -Dquarkus.native.container-build=true -Dquarkus.container-image.build=true```

## Running as a Docker container
```docker run --env-file=.env enbohm/halo-wallbox-scheduler``` where the .env-file must contain (at least) the entries:

HALO_APIKEY=your API key
  
HALO_PWD=your password for your account
