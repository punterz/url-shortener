FROM openjdk:17-jdk-alpine
VOLUME /tmp
EXPOSE 80
ARG JAVA_OPTS
ENV JAVA_OPTS=$JAVA_OPTS
COPY target/url-shortener-0.0.1-SNAPSHOT.jar urlshortener.jar
ENTRYPOINT exec java $JAVA_OPTS -jar urlshortener.jar
# For Spring-Boot project, use the entrypoint below to reduce Tomcat startup time.
#ENTRYPOINT exec java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar urlshortener.jar
