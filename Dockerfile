FROM openjdk:8-jdk-alpine
VOLUME /tmp
ADD target/producer-0.1.0.jar app.jar
ENV JAVA_OPTS="-Xms1024m -Xmx2048m"
ENTRYPOINT exec java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app.jar
