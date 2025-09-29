FROM maven:3.9.9-eclipse-temurin-17

ARG TEST_PROFILE=api
ARG APIBASEURL=http://localhost:4111/
ARG UIBASEURL=http://localhost:3000

ENV TEST_PROFILE=${TEST_PROFILE}
ENV APIBASEURL=${APIBASEURL}
ENV UIBASEURL=${UIBASEURL}

WORKDIR /app

COPY pom.xml .

RUN mvn dependency:go-offline

COPY . .

USER root

CMD /bin/bash -c " \
    mkdir -p /app/logs ; \
    { \
    echo '>>>>> Running test profile: ${TEST_PROFILE}' ; \
    mvn test -q -P${TEST_PROFILE} ; \
    \
    echo '>>>>> Running surefire-report:report' ; \
    mvn -DskipTests=true surefire-report:report ; \
    } > /app/logs/run.log 2>&1"
