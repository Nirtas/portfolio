import os
import sys
from pathlib import Path
from huggingface_hub import hf_hub_download


hf_model_repo = os.getenv("HF_MODEL_REPO")
hf_model_file = os.getenv("HF_MODEL_FILE")
model_path_in_container = os.getenv("MODEL_PATH_IN_CONTAINER", "/models")

if not hf_model_repo or not hf_model_file:
    print("Ошибка: Переменные окружения HF_MODEL_REPO и HF_MODEL_FILE должны быть установлены.")
    sys.exit(1)


model_save_path = Path(model_path_in_container)
model_full_path = model_save_path / hf_model_file

print(f"Проверка наличия модели: {model_full_path}")
print(os.listdir())

if model_full_path.exists():
    print(f"Модель '{hf_model_file}' уже существует в {model_save_path}. Пропускаем скачивание.")
    sys.exit(0)


model_save_path.mkdir(parents=True, exist_ok=True)
print(f"Директория {model_save_path} готова.")

print(f"Скачивание модели '{hf_model_file}' из репозитория '{hf_model_repo}' в {model_save_path}...")

try:
    hf_hub_download(
        repo_id=hf_model_repo,
        filename=hf_model_file,
        local_dir=str(model_save_path),
        local_dir_use_symlinks=False,
        resume_download=True
    )
    print(f"Модель '{hf_model_file}' успешно скачана.")
    sys.exit(0)

except Exception as e:
    print(f"Непредвиденная ошибка при скачивании модели: {e}")
    if model_full_path.exists():
        try:
            model_full_path.unlink()
            print(f"Удален неполный файл: {model_full_path}")
        except OSError as rm_err:
            print(f"Не удалось удалить неполный файл {model_full_path}: {rm_err}")
    sys.exit(1)