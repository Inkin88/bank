#!/bin/bash

set -e

# Конфигурация
PROJECT_ROOT="$(cd "$(dirname "$0")/../.." && pwd)"  # корень проекта
COMPOSE_FILE="$PROJECT_ROOT/infra/docker_compose/docker-compose.yml"
BROWSERS_JSON="$PROJECT_ROOT/infra/docker_compose/config/browsers.json"
DOCKERFILE="$PROJECT_ROOT/Dockerfile"
IMAGE_NAME="nbank_tests"
TIMESTAMP=$(date +"%Y%m%d_%H%M")
TEST_OUTPUT_DIR="$PROJECT_ROOT/test-output/$TIMESTAMP"

# Создаем директории для логов, результатов и отчета
mkdir -p "$TEST_OUTPUT_DIR/logs"
mkdir -p "$TEST_OUTPUT_DIR/results"
mkdir -p "$TEST_OUTPUT_DIR/report"

echo ">>>> Останавливаем старое тестовое окружение"
docker compose -f "$COMPOSE_FILE" down -v || true

echo ">>>> Docker pull браузеров для Selenoid"
if ! command -v jq &> /dev/null; then
    echo "❌ jq is not installed. Please install jq and try again."
    exit 1
fi

jq -r '.. | objects | select(.image) | .image' "$BROWSERS_JSON" | while read -r image; do
    image=$(echo "$image" | xargs)
    echo "Pulling $image..."
    docker pull "$image"
done

echo ">>>> Поднимаем тестовое окружение"
docker compose -f "$COMPOSE_FILE" up -d

# Ждем небольшой промежуток, чтобы сервисы успели стартовать
echo ">>>> Ждем 30 секунд, чтобы сервисы полностью поднялись..."
sleep 30

echo ">>>> Строим контейнер с тестами"
docker build -t $IMAGE_NAME -f "$DOCKERFILE" "$PROJECT_ROOT"

echo ">>>> Запуск API и UI тестов параллельно"

# API тесты
docker run --rm \
  --network nbank-network \
  -v "$TEST_OUTPUT_DIR/logs":/app/logs \
  -v "$TEST_OUTPUT_DIR/results":/app/target/surefire-reports \
  -v "$TEST_OUTPUT_DIR/report":/app/target/site \
  -e TEST_PROFILE="api" \
  -e APIBASEURL=http://backend:4111/ \
  -e UIBASEURL=http://frontend \
  $IMAGE_NAME &

# UI тесты
docker run --rm \
  --network nbank-network \
  -v "$TEST_OUTPUT_DIR/logs":/app/logs \
  -v "$TEST_OUTPUT_DIR/results":/app/target/surefire-reports \
  -v "$TEST_OUTPUT_DIR/report":/app/target/site \
  -e TEST_PROFILE="ui" \
  -e APIBASEURL=http://backend:4111/ \
  -e UIBASEURL=http://frontend \
  $IMAGE_NAME &

wait  # ждем завершения обоих контейнеров

echo ">>>> Тесты завершены"
echo "Лог файл: $TEST_OUTPUT_DIR/logs/run.log"
echo "Результаты: $TEST_OUTPUT_DIR/results"
echo "Репорт: $TEST_OUTPUT_DIR/report"

echo ">>>> Останавливаем тестовое окружение"
docker compose -f "$COMPOSE_FILE" down -v

echo "✅ Всё завершено"
