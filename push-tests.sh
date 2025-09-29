#!/bin/bash

LOCAL_IMAGE_NAME="nbank_tests"

DOCKERHUB_USERNAME="gckjwdz9"

TAG=${1:-latest}

# Docker Hub токен (создан в аккаунте Docker Hub)
DOCKERHUB_TOKEN="${DOCKERHUB_TOKEN:-dckr_pat_kIajJvX6SIwnmDFwscaqSt9dVbg}"

echo "Логинимся в Docker Hub..."
echo "$DOCKERHUB_TOKEN" | docker login --username "$DOCKERHUB_USERNAME" --password-stdin

REMOTE_IMAGE_NAME="$DOCKERHUB_USERNAME/$LOCAL_IMAGE_NAME:$TAG"
echo "Тегируем локальный образ: $LOCAL_IMAGE_NAME -> $REMOTE_IMAGE_NAME"
docker tag "$LOCAL_IMAGE_NAME" "$REMOTE_IMAGE_NAME"

echo "Отправляем образ в Docker Hub..."
docker push "$REMOTE_IMAGE_NAME"

echo "Готово! Образ доступен как: $REMOTE_IMAGE_NAME"
