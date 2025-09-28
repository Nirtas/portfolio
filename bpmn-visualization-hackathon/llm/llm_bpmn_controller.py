from flask import Flask, request, jsonify
from llm_bpmn import get_bpmn_json
server = Flask(__name__)

@server.route('/generate_bpmn', methods=['POST'])
def chat():
    try:
        if not request.is_json:
            return jsonify({"error": "Запрос должен быть в формате JSON"}), 415
        data = request.get_json()
        user_query = data.get('user_query')
        res = get_bpmn_json(user_query)

        return jsonify({
            "answer": res
        }), 201
    except Exception as e:
        print(e)

    