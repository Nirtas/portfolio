import os

BACKEND_PORT = os.environ.get("BACKEND_PORT")

bind = f"0.0.0.0:{BACKEND_PORT}"
workers = 4
timeout = 1000
limit_request_body = 0
wsgi_app = "server:server"