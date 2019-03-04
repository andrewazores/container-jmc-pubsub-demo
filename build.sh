#!/bin/bash

set -x
set -e

./gradlew clean build
docker build -f Dockerfile.publisher -t andrewazores/container-jmx-publisher:latest .
docker build -f Dockerfile.subscriber -t andrewazores/container-jmx-subscriber:latest .
# docker push andrewazores/container-jmx-publisher
# docker push andrewazores/container-jmx-subscriber
