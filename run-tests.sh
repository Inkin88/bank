#!/bin/bash

IMAGE_NAME=nbank_tests
TEST_PROFILE=${1:-api}
TIMESTAMP=$(date +"%Y%m%d_%H%M")
TEST_OUTPUT_DIR="$(pwd)/test-output/$TIMESTAMP"

mkdir -p "$TEST_OUTPUT_DIR/logs"
mkdir -p "$TEST_OUTPUT_DIR/results"
mkdir -p "$TEST_OUTPUT_DIR/report"

docker build -t $IMAGE_NAME .

echo "Тесты запущены"

docker run --rm \
  -v "$TEST_OUTPUT_DIR/logs":/app/logs \
  -v "$TEST_OUTPUT_DIR/results":/app/target/surefire-reports \
  -v "$TEST_OUTPUT_DIR/report":/app/target/site \
  -e TEST_PROFILE="$TEST_PROFILE" \
  -e APIBASEURL=http://192.168.1.174:4111/ \
  -e UIBASEURL=http://192.168.1.174 \
$IMAGE_NAME

echo "Тесты завершены"
echo "Лог файла: $TEST_OUTPUT_DIR/logs/run.log"
echo "Результаты: $TEST_OUTPUT_DIR/results"
echo "Репорт: $TEST_OUTPUT_DIR/report"
read -p "Press enter to exit..."
