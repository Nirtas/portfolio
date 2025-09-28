import os

STT_PORT = os.environ.get("STT_PORT")

bind = f"0.0.0.0:{STT_PORT}"
workers = 2
timeout = 300
limit_request_body = 0
wsgi_app = "stt:server"