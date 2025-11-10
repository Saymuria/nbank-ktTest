#!/bin/bash
IMAGE_NAME=nbank-kttest
TEST_PROFILE=${1:-all} #аргумент запуска
TIMESTAMP=$(date +"%Y%m%d_%H%M")
TEST_OUTPUT_DIR=./test-output/$TIMESTAMP
NETWORK_NAME="nbank-network"

#Собираем докер образ
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

#Вывод итогов
echo "Тесты завершены"
echo "Лог файл: $TEST_OUTPUT_DIR/logs"
echo "Репорт: $TEST_OUTPUT_DIR/report"