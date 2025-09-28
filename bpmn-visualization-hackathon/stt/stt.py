import whisper
import tempfile
import os
from flask import Flask, request, jsonify

server = Flask(__name__)

MODEL_SIZE = os.environ.get("WHISPER_MODEL_SIZE", "base")

try:
    print(f"Loading Whisper model: {MODEL_SIZE}...")
    model = whisper.load_model(MODEL_SIZE, device='cpu')
    print("Model loaded successfully.")
except Exception as e:
    print(f"Error loading Whisper model: {e}")
    model = None

@server.route('/transcribe', methods=['POST'])
def transcribe_audio():
    if model is None:
        return jsonify({"error": "Whisper model not loaded"}), 500

    if 'file' not in request.files:
        return jsonify({"error": "No file part in the request"}), 400

    audio_file = request.files['file']

    if audio_file.filename == '':
        return jsonify({"error": "No selected file"}), 400

    temp_dir = tempfile.mkdtemp()
    temp_filepath = os.path.join(temp_dir, audio_file.filename)

    try:
        audio_file.save(temp_filepath)
        print(f"Transcribing file: {audio_file.filename}")
        result = model.transcribe(temp_filepath)
        transcribed_text = result.get("text", "").strip()
        print("Transcription complete.")
        return jsonify({"text": transcribed_text}), 200

    except Exception as e:
        print(f"Error during transcription: {e}")
        return jsonify({"error": f"Transcription failed: {e}"}), 500
    finally:
        if os.path.exists(temp_filepath):
            os.remove(temp_filepath)
        if os.path.exists(temp_dir):
            os.rmdir(temp_dir)
