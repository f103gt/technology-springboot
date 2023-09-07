FROM openjdk:17
COPY /build/libs/*.jar technology.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar","/technology.jar"]
