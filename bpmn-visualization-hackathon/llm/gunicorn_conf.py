import os

LLM_PORT = os.environ.get("LLM_PORT")

bind = f"0.0.0.0:{LLM_PORT}"
workers = 2
timeout = 1000
limit_request_body = 0
wsgi_app = "llm_bpmn_controller:server"