FROM amazoncorretto:17

ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar

ENTRYPOINT ["java","-Dspring.profiles.active=prod","-Xmx512m", "-Dcom.amazonaws.sdk.disableAwsSdkMetrics=true","-jar","/app.jar"]