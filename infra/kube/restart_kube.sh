#!/bin/bash

#Запустили локальный кубер кластер с помощью миникуба используя докер как драйвер
# Кластер запущен внутри докер контайнера
minikube start --driver=docker

#Создали конфигмап с именем selenoid-config
kubectl create configmap selenoid-config --from-file=browsers.json=./nbank-chart/files/browsers.json

#Устанавливаем Helm чарт с именем релиза nbank, беря шаблоны из ./nbank-chart
helm install nbank ./nbank-chart

# все сервисы в namespace=default
kubectl get svc
kubectl get pods
kubectl logs deployment/backend
kubectl port-forward svc/frontend 3000:80 # /dev/null 2>&1 &
kubectl port-forward svc/backend 4111:4111
kubectl port-forward svc/selenoid 4444:4444
kubectl port-forward svc/selenoid-ui 8080:8080

