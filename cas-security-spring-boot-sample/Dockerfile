FROM maven:3-eclipse-temurin-11 as build
WORKDIR /src
COPY . /src
RUN mvn clean install && mvn -f cas-security-spring-boot-sample/pom.xml clean install

FROM eclipse-temurin:11-jre
WORKDIR /app
COPY --from=build /src/cas-security-spring-boot-sample/target/cas-security-spring-boot-sample-2.0.0-ugent.jar /app
ENV JAVA_OPTS=""
CMD [ "sh", "-c", "java $JAVA_OPTS -jar /app/cas-security-spring-boot-sample-2.0.0-ugent.jar" ]
