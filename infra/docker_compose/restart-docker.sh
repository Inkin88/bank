#!/bin/bash

echo ">>>> Остановить Docker Compose"

docker compose down -v || true

echo ">>>> Docker pull все образы"

# Путь до файла
json_file="./config/browsers.json"

# Проверяем, что jq установлен
if ! command -v jq &> /dev/null; then
    echo "❌ jq is not installed. Please install jq and try again."
    exit 1
fi

jq -r '.. | objects | select(.image) | .image' "$json_file" | while read -r image; do
    image=$(echo "$image" | xargs)  # убираем пробелы на концах
    echo "Pulling $image..."
    docker pull "$image"
done

echo ">>>> Запуск Docker Compose"
docker compose up -d