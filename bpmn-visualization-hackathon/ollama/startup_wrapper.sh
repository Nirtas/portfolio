#!/bin/sh
# /app/startup_wrapper.sh

set -e

echo "--- Запуск скрипта-обертки ---"

if [ -z "$HF_MODEL_REPO" ] || [ -z "$HF_MODEL_FILE" ]; then
  echo "Предупреждение: Переменные HF_MODEL_REPO и HF_MODEL_FILE не установлены. Пропуск скачивания модели."
else
  echo "--- Запуск скрипта скачивания Python ---"
  python3 /app/model_initializer.py

  exit_code=$?
  if [ $exit_code -ne 0 ]; then
    echo "Ошибка: Скрипт скачивания завершился с кодом $exit_code. Прерывание запуска." >&2
    exit $exit_code
  fi
  echo "--- Скрипт скачивания успешно завершен ---"
fi

echo "--- Передача управления основной команде: $@ ---"
exec "$@"