#!/bin/sh
set -e

GUNICORN_CMD="gunicorn -c /app/gunicorn_conf.py stt:server"

if [ "$GUNICORN_RELOAD" = "true" ]; then
    echo "Starting Gunicorn with --reload enabled..."
    GUNICORN_CMD="$GUNICORN_CMD --reload --reload-engine poll"
else
    echo "Starting Gunicorn..."
fi

if [ "$#" -gt 0 ]; then
    exec "$@"
else
    exec $GUNICORN_CMD
fi