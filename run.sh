#!/bin/bash

set -x

function cleanup() {
    set +e
    # TODO: better container management
    docker kill $(docker ps -a -q --filter ancestor=andrewazores/container-jmx-publisher)
    docker kill $(docker ps -a -q --filter ancestor=andrewazores/container-jmx-subscriber)
    docker rm $(docker ps -a -q --filter ancestor=andrewazores/container-jmx-publisher)
    docker rm $(docker ps -a -q --filter ancestor=andrewazores/container-jmx-subscriber)
}

cleanup
trap cleanup EXIT

set +e
docker network create --attachable jmx-test
set -e

docker run --rm --net=jmx-test --name jmx-subscriber -d andrewazores/container-jmx-subscriber
docker run --rm --net=jmx-test --name jmx-publisher -d andrewazores/container-jmx-publisher

docker run \
    --net=jmx-test \
    --name jmx-client \
    -e CONTAINER_DOWNLOAD_HOST=$CONTAINER_DOWNLOAD_HOST \
    -e CONTAINER_DOWNLOAD_PORT=$CONTAINER_DOWNLOAD_PORT \
    --rm -it andrewazores/container-jmx-client "$@"