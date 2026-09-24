FROM eclipse-temurin:25-jdk-jammy

ARG DB_URL
ARG DB_USER
ARG DB_PASS
ARG MAILGUN_APIKEY
ARG MAILGUN_URL
ARG AWS_SECRET_KEY
ARG AWS_ACCESS_KEY
ARG AWS_REGION

WORKDIR /app

COPY .mvn/ .mvn/
COPY --chmod=0755 mvnw mvnw
COPY ./src src/
COPY pom.xml pom.xml

ENV DB_URL=$DB_URL
ENV DB_USER=$DB_USER
ENV DB_PASS=$DB_PASS
ENV MAILGUN_APIKEY=$MAILGUN_APIKEY
ENV MAILGUN_URL=$MAILGUN_URL
ENV AWS_SECRET_KEY=$AWS_SECRET_KEY
ENV AWS_ACCESS_KEY=$AWS_ACCESS_KEY
ENV AWS_REGION=$AWS_REGION

RUN ./mvnw package -DskipTests && \
     mv target/$(./mvnw help:evaluate -Dexpression=project.artifactId -q -DforceStdout)-$(./mvnw help:evaluate -Dexpression=project.version -q -DforceStdout).jar target/app.jar

ENTRYPOINT ["java", "-jar", "-Xms2g", "-Xmx8g", "-XX:MaxDirectMemorySize=8G",  "target/app.jar"]
