#!/bin/bash
set -e

GGUF_FILENAME="${GGUF_FILE}"
OLLAMA_MODEL_NAME="${OLLAMA_MODEL_NAME:-custom_model}"
MODEL_DIR="${MODEL_FOLDER}"
GGUF_FILE_PATH="${HF_MODEL_FILE_PATH}"
TEMP_MODELFILE="Modelfile"
WAIT_TIMEOUT=600
SERVER_WAIT_TIMEOUT=120

echo "--- Ollama Entrypoint Script ---"
echo "Ожидаемый GGUF файл: ${GGUF_FILE_PATH}"
echo "Имя модели Ollama: ${OLLAMA_MODEL_NAME}"

if [ -z "$GGUF_FILENAME" ]; then
  echo "Ошибка: Переменная окружения GGUF_FILE не установлена!" >&2
  exit 1
fi
if [ -z "$OLLAMA_MODEL_NAME" ]; then
    echo "Ошибка: Переменная окружения OLLAMA_MODEL_NAME не установлена!" >&2
    exit 1
fi

echo "Ожидание скачивания файла ${GGUF_FILENAME}..."
counter=0
while [ ! -f "${GGUF_FILE_PATH}" ]; do
  if [ $counter -ge $WAIT_TIMEOUT ]; then
    echo "Ошибка: Файл ${GGUF_FILE_PATH} не появился в течение ${WAIT_TIMEOUT} секунд." >&2
    exit 1
  fi
  echo "Файл пока не найден, ожидание 5 сек... ($counter/$WAIT_TIMEOUT)"
  sleep 5
  counter=$((counter + 5))
done
echo "Файл ${GGUF_FILE_PATH} найден."

create_model_if_needed() {
  echo "Проверка существования модели '${OLLAMA_MODEL_NAME}' в Ollama..."

  echo "Запуск временного сервера Ollama для операций..."
  ollama serve &
  SERVER_PID=$!
  trap 'echo "Остановка временного сервера Ollama (PID: $SERVER_PID)..."; kill $SERVER_PID; wait $SERVER_PID 2>/dev/null; echo "Временный сервер остановлен."; exit 1' INT TERM EXIT

  echo "Ожидание готовности сервера Ollama (до ${SERVER_WAIT_TIMEOUT} сек)..."
  max_wait=$SERVER_WAIT_TIMEOUT
  current_wait=0
  while ! curl -sf http://localhost:11434/ > /dev/null; do
      if [ $current_wait -ge $max_wait ]; then
          echo "Ошибка: Временный сервер Ollama не ответил в течение $max_wait сек." >&2
          exit 1
      fi
      echo "Сервер Ollama недоступен, ожидание 2 сек... ($current_wait/$max_wait)"
      sleep 2
      current_wait=$((current_wait + 2))
  done
  echo "Временный сервер Ollama готов."

  if ollama list | awk '{print $1}' | grep -q "^${OLLAMA_MODEL_NAME}:latest$"; then
    echo "Модель '${OLLAMA_MODEL_NAME}' уже существует в Ollama. Пропускаем создание."
  else
    echo "Модель '${OLLAMA_MODEL_NAME}' не найдена. Создание из ${GGUF_FILE_PATH}..."

    echo "Выполнение: ollama create ${OLLAMA_MODEL_NAME} -f ${TEMP_MODELFILE}"
    if ollama create "${OLLAMA_MODEL_NAME}" -f "${TEMP_MODELFILE}"; then
      echo "Модель '${OLLAMA_MODEL_NAME}' успешно создана."
    else
      echo "Ошибка при создании модели '${OLLAMA_MODEL_NAME}'." >&2
      exit 1
    fi
  fi

  trap - INT TERM EXIT
  echo "Остановка временного сервера Ollama (PID: $SERVER_PID)..."
  kill $SERVER_PID
  wait $SERVER_PID 2>/dev/null
  echo "Временный сервер остановлен."
}

create_model_if_needed

echo "--- Запуск основного сервера Ollama ---"
exec ollama serve