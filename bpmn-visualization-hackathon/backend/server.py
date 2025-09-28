import os
import sys
from functools import wraps
from datetime import datetime
from flask import Flask, request, jsonify, session, send_from_directory
from flask_sqlalchemy import SQLAlchemy
from sqlalchemy.dialects.postgresql import UUID
from dotenv import load_dotenv
import uuid
import requests
import json
from json_Parse_v2 import get_bpmn
from utils import  transform_bpmn_v2
import mimetypes
import shutil
load_dotenv()

DB_USER = os.getenv('DB_USER')
DB_PASSWORD = os.getenv('DB_PASSWORD')
DB_HOST = os.getenv('DB_HOST')
DB_PORT = os.getenv('DB_PORT')
DB_NAME = os.getenv('DB_NAME')
PERSISTENT_STORAGE_PATH = os.getenv('PERSISTENT_STORAGE_PATH')

SQLALCHEMY_DATABASE_URI = None
if all([DB_USER, DB_PASSWORD, DB_HOST, DB_PORT, DB_NAME]):
    SQLALCHEMY_DATABASE_URI = f"postgresql://{DB_USER}:{DB_PASSWORD}@{DB_HOST}:{DB_PORT}/{DB_NAME}"
    print(f"DB URI собран из переменных окружения: postgresql://{DB_USER}:***@{DB_HOST}:{DB_PORT}/{DB_NAME}")
else:
    missing = [k for k,v in {'DB_USER':DB_USER, 'DB_PASSWORD':DB_PASSWORD, 'DB_HOST':DB_HOST, 'DB_PORT':DB_PORT, 'DB_NAME':DB_NAME}.items() if not v]
    print(f"Отсутствуют переменные окружения для подключения к БД: {missing}. DATABASE_URL не установлен.", file=sys.stderr)
    sys.exit("Критическая ошибка: Отсутствует конфигурация БД.")

if not PERSISTENT_STORAGE_PATH:
    print("Переменная окружения PERSISTENT_STORAGE_PATH не установлена.", file=sys.stderr)
    sys.exit("Критическая ошибка: Необходимо указать путь для постоянного хранилища.")

os.makedirs(PERSISTENT_STORAGE_PATH, exist_ok=True)

server = Flask(__name__)

if SQLALCHEMY_DATABASE_URI:
    server.config['SQLALCHEMY_DATABASE_URI'] = SQLALCHEMY_DATABASE_URI
else:
    print("SQLALCHEMY_DATABASE_URI не сконфигурирован.", file=sys.stderr)
    sys.exit("Критическая ошибка: Невозможно запустить без DB URI.")

server.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False
server.config['SECRET_KEY'] = os.getenv('FLASK_SECRET_KEY', 'a_default_local_secret_key_change_me')
if server.config['SECRET_KEY'] == 'a_default_local_secret_key_change_me' and os.getenv('KUBERNETES_SERVICE_HOST'):
    print("Используется секретный ключ Flask по умолчанию в среде Kubernetes. Установите переменную окружения FLASK_SECRET_KEY.", file=sys.stderr)
server.config['MAX_CONTENT_LENGTH'] = 30 * 1024 * 1024

db = SQLAlchemy(server)

STT_HOSTNAME = os.getenv('STT_HOSTNAME')
LLM_HOSTNAME = os.getenv('LLM_HOSTNAME')
STT_PORT = os.getenv('STT_PORT')
LLM_PORT =os.getenv('LLM_PORT') 
STT_URL = f"http://{STT_HOSTNAME}:{STT_PORT}/transcribe" if STT_HOSTNAME and STT_PORT else None
LLM_URL = f"http://{LLM_HOSTNAME}:{LLM_PORT}"
if not STT_URL:
    print("STT_HOSTNAME или STT_PORT не установлены. Транскрипция аудио не будет работать.", file=sys.stderr)

ALLOWED_AUDIO_EXTENSIONS = {'mp3', 'wav'}


def write_file(file_path,chat_id, llm_response_str):
    json_filename = f"{file_path}_llm_response.json"
    try:
        json_absolute_path, json_relative_path = get_persistent_path(chat_id,json_filename )
        print(json_relative_path)
        with open(json_absolute_path, 'w', encoding='utf-8') as f:
            f.write(llm_response_str)
        return json_relative_path
    except Exception as e:
        print(e)
        return None

class Chat(db.Model):
    __tablename__ = 'chats'
    id = db.Column(UUID(as_uuid=True), primary_key=True, server_default=db.text("uuid_generate_v4()"))
    chat_name = db.Column(db.String(100), nullable=False)

class ChatHistory(db.Model):
    __tablename__ = 'chat_history'
    id = db.Column(UUID(as_uuid=True), primary_key=True, server_default=db.text("uuid_generate_v4()"))
    chat_id = db.Column(UUID(as_uuid=True), db.ForeignKey('chats.id'), nullable=False)
    is_user_sender = db.Column(db.Boolean, nullable=False)
    message_text = db.Column(db.Text)
    created_at = db.Column(db.DateTime, server_default=db.func.now(), nullable=False)
    audio_path = db.Column(db.String(255))
    json_path = db.Column(db.String(255))


@server.route('/')
def hello():
    return "Hello, server!"

@server.route('/new_chat', methods=['POST'])
def create_new_chat():
    if not request.is_json:
        return jsonify({"error": "Запрос должен быть в формате JSON"}), 415

    data = request.get_json()
    chat_name = data.get('chat_name')

    if not chat_name:
        return jsonify({"error": "Отсутствует поле 'chat_name' в теле запроса"}), 400
    if not isinstance(chat_name, str) or len(chat_name) > 100 or len(chat_name.strip()) == 0:
        return jsonify({"error": "'chat_name' должно быть непустой строкой до 100 символов"}), 400

    try:
        new_chat = Chat(chat_name = chat_name.strip())
        db.session.add(new_chat)
        db.session.commit()
        return jsonify({
            "message": "Чат успешно создан",
            "chat_id": str(new_chat.id)
        }), 200

    except Exception as e:
        db.session.rollback()
        return jsonify({"error": f"Не удалось создать новый чат: {str(e)}"}), 500


@server.route('/send_text_message', methods=['POST'])
def send_text_message():
    if not request.is_json:
        return jsonify({"error": "Запрос должен быть в формате JSON"}), 415

    data = request.get_json()
    chat_id_str = data.get('chat_id')
    user_text = get_llm_prompt(chat_id_str) + data.get('text')

    if not chat_id_str or user_text is None:
        return jsonify({"error": "Необходимы 'chat_id' и 'text'"}), 400
    if not isinstance(user_text, str):
        return jsonify({"error": "'text' должен быть строкой"}), 400

    try:
        chat_id = uuid.UUID(chat_id_str)
    except ValueError:
        return jsonify({"error": "Неверный формат 'chat_id'"}), 400

    chat = db.session.get(Chat, chat_id)
    if not chat:
        return jsonify({"error": f"Чат с ID {chat_id_str} не найден"}), 404
    

    try:
        user_message = ChatHistory(
            chat_id = chat_id,
            is_user_sender = True,
            message_text = user_text
        )
        db.session.add(user_message)
        db.session.flush()

        llm_resp = requests.post(f"{LLM_URL}/generate_bpmn", json={"user_query": user_text})
        llm_resp.raise_for_status()
        resp = llm_resp.json()
        bpmn_diagramma = resp.get("answer", "")
        json_path = write_file(f"{user_message.id}", chat_id=chat_id_str, llm_response_str=json.dumps(bpmn_diagramma))
        answer = ChatHistory(
            chat_id = chat_id,
            is_user_sender = False,
            json_path = json_path
        )
        db.session.add(answer)        
               
        db.session.commit()
        return jsonify({
            "answer": get_bpmn(transform_bpmn_v2(bpmn_diagramma["pools"])),
            "user_message_id": str(user_message.id),
            "answer_message_id": str(answer.id),
            "created_at_user": user_message.created_at.isoformat(),
            "created_at_answer": answer.created_at.isoformat()
        }), 200
    except Exception as e:
        db.session.rollback()
        print(f"Ошибка в send_text_message для чата {chat_id_str}: {e}", file=sys.stderr)
        return jsonify({"error": f"Внутренняя ошибка сервера при сохранении сообщения: {str(e)}"}), 500

@server.route('/generate_llm_answer', methods=['POST'])
def generate_llm_answer():
    if not request.is_json:
        return jsonify({"error": "Запрос должен быть в формате JSON"}), 415

    data = request.get_json()
    chat_id_str = data.get('chat_id')
    msg_id_str = data.get('msg_id')
    user_text = get_llm_prompt(chat_id_str) + data.get('text')

    print(user_text)

    if not chat_id_str or user_text is None or not msg_id_str:
        return jsonify({"error": "Необходимы 'chat_id' и 'text'"}), 400
    if not isinstance(user_text, str):
        return jsonify({"error": "'text' должен быть строкой"}), 400

    try:
        chat_id = uuid.UUID(chat_id_str)
    except ValueError:
        return jsonify({"error": "Неверный формат 'chat_id'"}), 400

    chat = db.session.get(Chat, chat_id)
    if not chat:
        return jsonify({"error": f"Чат с ID {chat_id_str} не найден"}), 404   

    try:
        llm_resp = requests.post(f"{LLM_URL}/generate_bpmn", json={"user_query": user_text})
        llm_resp.raise_for_status()
        resp = llm_resp.json()
        bpmn_diagramma = resp.get("answer", "")
        json_path = write_file(f"{msg_id_str}", chat_id=chat_id_str, llm_response_str=json.dumps(bpmn_diagramma))
        answer = ChatHistory(
            chat_id = chat_id,
            is_user_sender = False,
            json_path = json_path
        )
        db.session.add(answer)        
               
        db.session.commit()
        return jsonify({
            "answer": get_bpmn(transform_bpmn_v2(bpmn_diagramma["pools"])),
            "answer_message_id": str(answer.id),
            "created_at_answer": answer.created_at.isoformat()
        }), 200
    except Exception as e:
        db.session.rollback()
        print(f"Ошибка в generate_llm_answer для чата {chat_id_str}: {e}", file=sys.stderr)
        return jsonify({"error": f"Внутренняя ошибка сервера при сохранении сообщения: {str(e)}"}), 500

def get_persistent_path(chat_id: uuid.UUID, filename: str) -> tuple[str, str]:
    relative_dir = os.path.join('chats', str(chat_id))
    absolute_dir = os.path.join(PERSISTENT_STORAGE_PATH, relative_dir)
    os.makedirs(absolute_dir, exist_ok=True)
    relative_path = os.path.join(relative_dir, filename)
    absolute_path = os.path.join(absolute_dir, filename)
    return absolute_path, relative_path


def allowed_file(filename):
    if filename.find('.') == -1:
        return False
    return filename.rsplit('.', 1)[1].lower() in ALLOWED_AUDIO_EXTENSIONS


@server.route('/send_voice_message', methods=['POST'])
def send_voice_message():
    if 'audio_file' not in request.files:
        return jsonify({"error": "Отсутствует файл 'audio_file'"}), 400

    audio_file = request.files['audio_file']
    chat_id_str = request.form.get('chat_id')

    if not chat_id_str:
        return jsonify({"error": "Отсутствует поле 'chat_id' в данных формы"}), 400
    if audio_file.filename == '':
        return jsonify({"error": "Имя файла не может быть пустым"}), 400

    try:
        chat_id = uuid.UUID(chat_id_str)
    except ValueError:
        return jsonify({"error": "Неверный формат 'chat_id'"}), 400

    if not allowed_file(audio_file.filename):
        return jsonify({"error": f"Недопустимый тип файла. Разрешены: {', '.join(ALLOWED_AUDIO_EXTENSIONS)}"}), 400

    chat = db.session.get(Chat, chat_id)
    if not chat:
        return jsonify({"error": f"Чат с ID {chat_id_str} не найден"}), 404

    user_message = None
    saved_audio_absolute_path = None
    audio_relative_path = None
    temp_audio_path = None
    file_ext = audio_file.filename.rsplit('.', 1)[1].lower()
    audio_filename = f"audio.{file_ext}"

    try:
        temp_dir = os.path.join(PERSISTENT_STORAGE_PATH)
        temp_audio_filename = f"{uuid.uuid4()}.{file_ext}"
        temp_audio_path = os.path.join(temp_dir, temp_audio_filename)
        audio_file.save(temp_audio_path)
        print(f"Аудио временно сохранено: {temp_audio_path}")

        stt_data = call_stt_service(temp_audio_path)

        if not stt_data or not stt_data.get("success"):
            error_detail = stt_data.get("error", "Неизвестная ошибка STT") if stt_data else "Пустой ответ от STT"
            if os.path.exists(temp_audio_path): os.remove(temp_audio_path)
            return jsonify({"error": f"Ошибка при распознавании речи: {error_detail}"}), 502

        transcribed_text = stt_data.get("text", "")

        user_message = ChatHistory(
            chat_id = chat_id,
            is_user_sender = True,
            message_text = transcribed_text
        )
        db.session.add(user_message)
        db.session.flush()

        if not user_message.id:
            raise Exception("Не удалось получить ID для сообщения пользователя")

        saved_audio_absolute_path, audio_relative_path = get_persistent_path(chat_id, f"{user_message.id}_{audio_filename}")
        shutil.move(temp_audio_path, saved_audio_absolute_path)
        user_message.audio_path = audio_relative_path
        temp_audio_path = None

        db.session.commit()

        return jsonify({
            "user_message_id": str(user_message.id),
            "created_at_user": user_message.created_at.isoformat(),
            "transcribed_text": transcribed_text,
            "audio_path": audio_relative_path
        }), 200

    except Exception as e:
        db.session.rollback()
        if temp_audio_path and os.path.exists(temp_audio_path):
            try: os.remove(temp_audio_path)
            except OSError as re: print(f"Не удалось удалить временный файл {temp_audio_path} при откате: {re}", file=sys.stderr)
        if saved_audio_absolute_path and os.path.exists(saved_audio_absolute_path):
            try: os.remove(saved_audio_absolute_path)
            except OSError as re: print(f"Не удалось удалить аудио {saved_audio_absolute_path} при откате: {re}", file=sys.stderr)
        return jsonify({"error": "Внутренняя ошибка сервера при обработке голосового сообщения", "details": str(e)}), 500


def call_stt_service(audio_file_path: str) -> dict:
    if not os.path.exists(audio_file_path):
        error_msg = f"Аудио файл не найден по пути: {audio_file_path}"
        return {"success": False, "error": error_msg}

    content_type, _ = mimetypes.guess_type(audio_file_path)
    if not content_type:
        ext = os.path.splitext(audio_file_path)[1].lower()
        if ext == '.mp3':
            content_type = 'audio/mpeg'
        elif ext == '.wav':
            content_type = 'audio/wav'
        else:
            content_type = 'application/octet-stream'

    try:
        with open(audio_file_path, 'rb') as audio_file_stream:
            filename = os.path.basename(audio_file_path)
            files = {'file': (filename, audio_file_stream, content_type)}
            stt_response = requests.post(STT_URL, files=files, timeout=60)
            stt_response.raise_for_status()
            stt_result = stt_response.json()
            transcribed_text = stt_result.get("text", None)
            if transcribed_text is None:
                error_msg = "Ответ от сервиса STT не содержит поле 'text'"
                return {"success": False, "error": error_msg}
            return {"success": True, "text": transcribed_text}
    except requests.exceptions.Timeout:
        error_msg = "Время ожидания запроса STT истекло"
        return {"success": False, "error": error_msg}
    except requests.exceptions.ConnectionError as e:
        error_msg = f"Не удалось подключиться к STT по адресу {STT_URL}: {e}"
        return {"success": False, "error": error_msg}
    except requests.exceptions.HTTPError as e:
        error_msg = f"STT вернул ошибку HTTP: {e.response.status_code} {e.response.reason}"
        try:
            details = e.response.json()
        except json.JSONDecodeError:
            details = e.response.text
        return {"success": False, "error": f"{error_msg}. Details: {details}"}
    except requests.exceptions.RequestException as e:
        error_msg = f"Во время запроса к STT произошла ошибка: {e}"
        return {"success": False, "error": error_msg}
    except json.JSONDecodeError as e:
        error_msg = f"Не удалось расшифровать ответ JSON от STT: {e}"
        return {"success": False, "error": error_msg}
    except Exception as e:
        error_msg = f"Во время обработки STT произошла непредвиденная ошибка: {e}"
        return {"success": False, "error": error_msg}


@server.route('/upload-audio', methods=['POST'])
def handle_audio_upload():
    if 'audio_file' not in request.files:
        return jsonify({"error": "В запросе нет audio_file"}), 400

    audio_file = request.files['audio_file']

    if audio_file.filename == '':
        return jsonify({"error": "Не выбран файл"}), 400

    try:
        files = {'file': (audio_file.filename, audio_file.stream, audio_file.content_type)}
        stt_response = requests.post(STT_URL, files=files)
        stt_response.raise_for_status()
        stt_result = stt_response.json()
        transcribed_text = stt_result.get("text", "")
        print(f"Transcribed text: {transcribed_text}")
        return jsonify({"status": "success", "transcribed_text": transcribed_text}), 200

    except requests.exceptions.RequestException as e:
        print(f"Ошибка при вызове STT: {e}")
        return jsonify({"error": f"Не удалось получить расшифровку: {e}"}), 500
    except Exception as e:
        print(f"Произошла непредвиденная ошибка: {e}")
        return jsonify({"error": f"Произошла непредвиденная ошибка: {e}"}), 500




@server.route('/chat_history/<chat_id>', methods=['GET'])
def get_chat_history(chat_id):
    try:
        chat_uuid_to_find = uuid.UUID(chat_id)
    except ValueError:
        return jsonify({"message": "ID чата должен быть в формате UUID"}), 400
    
    try:
        all_messages_for_chat = ChatHistory.query.filter_by(chat_id=chat_uuid_to_find).order_by(ChatHistory.created_at).all()
        history_list_for_response = []
        for message_from_db in all_messages_for_chat:
            bpmn = None
            if message_from_db.json_path:
                with open(f"{PERSISTENT_STORAGE_PATH}/{message_from_db.json_path}") as f:               
                    bpmn = json.load(f)
            message_data = {
                "message_id": str(message_from_db.id),
                "is_user_sender": message_from_db.is_user_sender,
                "message_text": message_from_db.message_text,
                "created_at": message_from_db.created_at.isoformat(),
                "audio_path": message_from_db.audio_path,
                "bpmn": get_bpmn(transform_bpmn_v2(bpmn["pools"])) if bpmn  else ""
            }
            history_list_for_response.append(message_data)
        final_json_response = {
            "history": history_list_for_response
        }
        return jsonify(final_json_response), 200
    except Exception as e:
        print(e)
        return jsonify({"message": "Произошла ошибка на сервере при получении истории чата."}), 500


def get_llm_prompt(chat_id_str):
    try:
        all_messages_for_chat = ChatHistory.query.filter_by(chat_id=chat_id_str).order_by(ChatHistory.created_at).all()
        if len(all_messages_for_chat) == 0:
            return ""
        prompt = "Вот история сообщений, изучи её и измени JSON схему, если это требуется по заданию от пользователя. Question, это запрос от пользователя, а Answer твоя BPMN диаграмма в JSON формате:"
        for message_from_db in all_messages_for_chat[-6:]:
            typeAnswer = "Question" if message_from_db.is_user_sender else "Answer"
            answer = json.loads(message_from_db.json_path) if message_from_db.json_path  else  message_from_db.message_text
            prompt += f"""\n
    {typeAnswer}: {answer}
    \n"""
        return prompt
    except  Exception as e:
        return ""
    
@server.route('/download_audio/<path:relative_audio_path>', methods=['GET'])
def download_audio_file(relative_audio_path):
    print(f"Запрос на скачивание аудио файла по пути: {relative_audio_path}")
    print(f"Базовая директория для поиска: {PERSISTENT_STORAGE_PATH}")
    if not os.path.isdir(PERSISTENT_STORAGE_PATH):
        print(f"Базовая директория хранилища не найдена: {PERSISTENT_STORAGE_PATH}", file=sys.stderr)
        return jsonify({"status": "server_config_error", "message": "Ошибка конфигурации сервера (хранилище)"}), 500

    try:
        print(f"Попытка отправить файл: {relative_audio_path} из директории {PERSISTENT_STORAGE_PATH}")
        return send_from_directory(
            directory=PERSISTENT_STORAGE_PATH,
            path=relative_audio_path,
            as_attachment=False
        )
    except FileNotFoundError:
        print(f"Файл не найден по пути: {os.path.join(PERSISTENT_STORAGE_PATH, relative_audio_path)}")
        return jsonify({"status": "not_found", "message": "Аудиофайл не найден по указанному пути."}), 404
    except Exception as e:
        print(f"Ошибка при отправке файла {relative_audio_path}: {e}", file=sys.stderr)
        import traceback
        traceback.print_exc()
        return jsonify({"status": "server_error", "message": "Не удалось отправить файл из-за ошибки сервера."}), 500
    
@server.route('/get_all_chats', methods=['GET'])
def get_all_chats():
    try:
        all_chats_from_db = Chat.query.all()
        number_of_chats = len(all_chats_from_db)
        chats_list_to_send = []
        for chat_item in all_chats_from_db:
            chat_info = {
                "chat_id": str(chat_item.id),
                "chat_name": chat_item.chat_name
            }
            chats_list_to_send.append(chat_info)
        response = {
            "status": "success",
            "total_chats": number_of_chats,
            "chats": chats_list_to_send
        }
        return jsonify(response), 200
    except Exception as e:
        return jsonify({"message": f"Не удалось получить список чатов из базы данных: {str(e)}"}), 500