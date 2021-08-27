FROM azul/zulu-openjdk-alpine:11.0.6-jre

MAINTAINER rajesh@brainupgrade.in

ADD target/app.jar app.jar

ENTRYPOINT ["/usr/bin/java", "-Djava.security.egd=file:/dev/./urandom", "-jar","app.jar"]



