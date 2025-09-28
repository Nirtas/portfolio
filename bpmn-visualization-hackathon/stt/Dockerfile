FROM python:3.11.5-slim

ARG APP_VERSION=unknown
LABEL version=$APP_VERSION

RUN apt-get update && apt-get install -y --no-install-recommends ffmpeg build-essential dos2unix && rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

COPY . .

COPY docker-entrypoint.sh /docker-entrypoint.sh
RUN dos2unix /docker-entrypoint.sh && chmod +x /docker-entrypoint.sh

ENTRYPOINT ["/docker-entrypoint.sh"]
CMD ["gunicorn", "-c", "/app/gunicorn_conf.py", "stt:server"]