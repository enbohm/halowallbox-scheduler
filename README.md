# Charge Amps - Halo Wallbox scheduler
A project that connects with the Charge Amps Halo Wallbox API. The APIs supports turning on/off lights, add schedules, etc. This project turns the LED light on and off at the specified time stated in the [property file](src/main/resources/application.properties). The LED light will always turn off at 23:00 hours.

API reference at https://eapi.charge.space/swagger/index.html

## Building and running locally
```mvn clean verify``` builds the code and runs tests.

## Packaging
You can create a native executable using: 

`mvn package -Pnative` (requires GraalVM installed on you local OS) OR you can use Docker to build the native executable using:

`mvn package -Pnative -Dquarkus.native.container-build=true`.

The produced executable will be a 64 bit Linux executable, so depending on your operating system it may no longer be runnable. If you are running on Linux you can then execute your binary: `./halo-wallbox-scheduler-runner` otherwise you will need to wrap the exucutable in a container. This can be done manually or by executing

```mvn package -Pnative -Dquarkus.native.container-build=true -Dquarkus.container-image.build=true```

## Running as a Docker container
```docker run --env-file=.env enbohm/halo-wallbox-scheduler``` where the .env-file must contain (at least) the entries:

HALO_APIKEY=your API key
  
HALO_PWD=your password for your account
