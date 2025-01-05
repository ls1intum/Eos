FROM eclipse-temurin:17-jdk-jammy

MAINTAINER Stephan Krusche <krusche@tum.de>

RUN apt-get update && apt-get upgrade -y && apt-get install -y \
    gnupg libpango1.0-0 git \
 && rm -rf /var/lib/apt/lists/*

RUN echo "$LANG -- $LANGUAGE -- $LC_ALL" \
    && curl --version \
    && gpg --version \
    && git --version \
    && java --version \
    && javac --version


ADD artemis-java-template /opt/artemis-java-template

RUN cd /opt/artemis-java-template && pwd && ls -la && ./gradlew clean test check -x test publishToMavenLocal && ./gradlew --version && ./gradlew --stop

RUN rm -rf /opt/artemis-java-template
