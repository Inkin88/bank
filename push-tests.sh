#!/bin/bash

# Локальное имя образа
LOCAL_IMAGE_NAME="nbank_tests"

# Docker Hub username
DOCKERHUB_USERNAME="gckjwdz9"

# Тег: берём первый аргумент, иначе latest
TAG=${1:-latest}

# Docker Hub токен передаётся через переменную окружения DOCKERHUB_TOKEN
if [ -z "$DOCKERHUB_TOKEN" ]; then
  echo "ERROR: DOCKERHUB_TOKEN is not set!"
  exit 1
fi

echo "Логинимся в Docker Hub..."
echo "$DOCKERHUB_TOKEN" | docker login --username "$DOCKERHUB_USERNAME" --password-stdin

REMOTE_IMAGE_NAME="$DOCKERHUB_USERNAME/$LOCAL_IMAGE_NAME:$TAG"
echo "Тегируем локальный образ: $LOCAL_IMAGE_NAME -> $REMOTE_IMAGE_NAME"
docker tag "$LOCAL_IMAGE_NAME" "$REMOTE_IMAGE_NAME"

echo "Отправляем образ в Docker Hub..."
docker push "$REMOTE_IMAGE_NAME"

echo "Готово! Образ доступен как: $REMOTE_IMAGE_NAME"
