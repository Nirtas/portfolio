FROM python:3.11.5-slim as builder

RUN apt-get update && apt-get install -y --no-install-recommends build-essential libpq-dev && rm -rf /var/lib/apt/lists/*

WORKDIR /install

COPY requirements.txt .
RUN pip install --no-cache-dir --prefix="/install" -r requirements.txt


FROM python:3.11.5-slim

ARG APP_VERSION=unknown
LABEL version=$APP_VERSION

RUN apt-get update && apt-get install -y --no-install-recommends libpq5 dos2unix  && rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY --from=builder /install /usr/local

COPY . .

COPY docker-entrypoint.sh /docker-entrypoint.sh
RUN dos2unix /docker-entrypoint.sh && chmod +x /docker-entrypoint.sh

ENTRYPOINT ["/docker-entrypoint.sh"]
CMD ["gunicorn", "-c", "/app/gunicorn_conf.py", "server:server"]