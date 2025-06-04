FROM maven:3.9.6-eclipse-temurin-17-alpine AS builder

WORKDIR /app

COPY pom.xml .

COPY src ./src

RUN mvn clean package -DskipTests spring-boot:repackage

FROM eclipse-temurin:17-jre-slim-jammy

ARG APP_USER_NAME=appuser
ARG APP_GROUP_NAME=appgroup
ARG APP_UID=1001
ARG APP_GID=1001

RUN groupadd -r -g ${APP_GID} ${APP_GROUP_NAME} && \
    useradd --no-log-init -r -u ${APP_UID} -g ${APP_GROUP_NAME} ${APP_USER_NAME}

WORKDIR /app

COPY --from=builder /app/target/redalert-0.0.1-SNAPSHOT.jar app.jar

RUN chown -R ${APP_USER_NAME}:${APP_GROUP_NAME} /app

USER ${APP_USER_NAME}

EXPOSE 8074

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/app/app.jar"]