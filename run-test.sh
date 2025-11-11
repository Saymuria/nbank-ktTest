#!/bin/bash
IMAGE_NAME=nbank-kttest
TEST_PROFILE=${1:-ui} #аргумент запуска
TIMESTAMP=$(date +"%Y%m%d_%H%M")
TEST_OUTPUT_DIR=./test-output/$TIMESTAMP
NETWORK_NAME="nbank-network"

#Собираем докер образ
docker build -t $IMAGE_NAME .

mkdir -p "$TEST_OUTPUT_DIR/logs"
mkdir -p "$TEST_OUTPUT_DIR/report"
mkdir -p "$TEST_OUTPUT_DIR/allure-results"
mkdir -p "$TEST_OUTPUT_DIR/allure-report"

# Запуск докер контейнера
echo "Tесты запущены"
docker run --rm \
  --network "$NETWORK_NAME" \
  -v "$TEST_OUTPUT_DIR/logs":/app/logs \
  -v "$TEST_OUTPUT_DIR/report":/app/build/reports/tests \
  -v "$TEST_OUTPUT_DIR/allure-results":/app/target/allure-results \
  -e TEST_PROFILE="$TEST_PROFILE" \
  -e APIBASEURL=http://backend:4111 \
  -e UIBASEURL=http://frontend:80 \
  -e UIREMOTE=http://selenoid:4444/wd/hub \
$IMAGE_NAME

# Генерируем Allure отчет
echo "Генерация Allure отчета..."
docker run --rm \
  -v "$TEST_OUTPUT_DIR/allure-results":/allure-results \
  -v "$TEST_OUTPUT_DIR/allure-report":/allure-report \
  frankescobar/allure-docker-service:2.27.0 \
  /allure-2.27.0/bin/allure generate /allure-results -o /allure-report --clean

#Вывод итогов
echo "Тесты завершены"
echo "Лог файл: $TEST_OUTPUT_DIR/logs"
echo "Репорт: $TEST_OUTPUT_DIR/report"
echo "Allure отчет сгенерирован: $TEST_OUTPUT_DIR/allure-report"