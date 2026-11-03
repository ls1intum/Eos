FROM eclipse-temurin:25-jdk-noble

LABEL maintainer="Stephan Krusche <krusche@tum.de>"

# libpangoft2 pulls in pango, fontconfig and freetype, which JavaFX needs to render text with the headless platform
RUN apt-get update && apt-get upgrade -y && apt-get install -y --no-install-recommends \
    curl gnupg git libpangoft2-1.0-0 fonts-dejavu-core \
 && rm -rf /var/lib/apt/lists/*

RUN echo "$LANG -- $LANGUAGE -- $LC_ALL" \
    && curl --version \
    && gpg --version \
    && git --version \
    && java --version \
    && javac --version

ADD artemis-java-template /opt/artemis-java-template

RUN cd /opt/artemis-java-template && pwd && ls -la && ./gradlew clean test check -x test publishToMavenLocal resolveAresDependencies && ./gradlew --version && ./gradlew --stop

RUN rm -rf /opt/artemis-java-template
