FROM openjdk:21-slim
add target/*.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]
