#!/bin/bash

# Загружаем переменные из .env файла, файл env не пушится в пш
set -a  # Автоматически export все переменные
source .env
set +a

echo "${DOCKER_PASSWORD}" | docker login -u "${DOCKER_USERNAME}" --password-stdin
docker tag "${LOCAL_IMAGE_NAME}:${LOCAL_IMAGE_TAG}" "${DOCKER_USERNAME}/nbank-kttest:${REMOTE_IMAGE_TAG}"
docker push "${DOCKER_USERNAME}/nbank-kttest:${REMOTE_IMAGE_TAG}"