#!/bin/bash

# Конфигурация
IMAGE_NAME=nbank-kttest
TEST_PROFILE=${1:-all}  # api или ui
TIMESTAMP=$(date +"%Y%m%d_%H%M")
TEST_OUTPUT_DIR=./test-output/$TIMESTAMP
NETWORK_NAME="nbank-network"

echo ">>> Подготовка и запуск тестового окружения"
cd infra/docker_compose && bash restart_docker.sh && cd ../..
#Если необходимо запустить тесты с мастера
#echo "Спуливаем образ Docker образ тестов: $IMAGE_NAME"
#docker pull saymuria/$IMAGE_NAME

#Собираем докер образ - если нужно запустить тесты с dev
echo "Собираем Docker образ тестов: $IMAGE_NAME"
docker build -t $IMAGE_NAME .

mkdir -p "$TEST_OUTPUT_DIR/logs"
mkdir -p "$TEST_OUTPUT_DIR/report"

# Запуск докер контейнера
echo "Tесты запущены"
docker run --rm \
  --network "$NETWORK_NAME" \
  -v "$TEST_OUTPUT_DIR/logs":/app/logs \
  -v "$TEST_OUTPUT_DIR/report":/app/build/reports/tests \
  -e TEST_PROFILE="$TEST_PROFILE" \
  -e APIBASEURL=http://backend:4111 \
  -e UIBASEURL=http://frontend:80 \
  -e UIREMOTE=http://selenoid:4444/wd/hub \
$IMAGE_NAME

echo "Тесты завершены"
echo "Лог файл: $TEST_OUTPUT_DIR/logs"
echo "Репорт: $TEST_OUTPUT_DIR/report"