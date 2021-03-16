#!/bin/bash

java -jar /app/target/spring-crowd-sso.jar &
APP_PID="$!"

echo "Java running with PID ${APP_PID}"
wait ${APP_PID}