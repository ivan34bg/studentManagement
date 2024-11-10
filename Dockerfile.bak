FROM amazoncorretto:21
ARG JAR_FILE=build/libs/*.jar
COPY /build/libs/studentManagement-0.0.1-SNAPSHOT.jar application.jar
CMD ["apt-get", "update -y"]
ENTRYPOINT ["java", "-Xmx2048M", "-jar", "/application.jar"]