# ---------- Build stage ----------
FROM gradle:8.7-jdk17-alpine AS build
WORKDIR /home/gradle/src
COPY --chown=gradle:gradle . .
RUN gradle clean bootJar -x test

# ---------- Runtime stage ----------
FROM eclipse-temurin:17-jre-alpine
ENV JAVA_OPTS="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005" \
    SPRING_PROFILES_ACTIVE=local \
    SERVER_PORT=20001
WORKDIR /app
COPY --from=build /home/gradle/src/build/libs/*.jar /app/app.jar
EXPOSE 20001 5005
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -Dserver.port=$SERVER_PORT -jar /app/app.jar"]