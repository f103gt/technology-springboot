FROM openjdk:17
VOLUME /technology-mysql
COPY /build/libs/*.jar technology.jar
ENTRYPOINT ["java", "-jar","/technology.jar"]
