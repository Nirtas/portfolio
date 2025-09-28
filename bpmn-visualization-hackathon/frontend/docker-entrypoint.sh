#!/bin/sh
set -e

TEMPLATE_FILE="/etc/nginx/templates/default.conf.template"
CONFIG_FILE="/etc/nginx/conf.d/default.conf"

if [ -z "${BACKEND_HOSTNAME}" ]; then
    echo "ERROR: BACKEND_HOSTNAME environment variable is not set."
    exit 1
fi
if [ -z "${BACKEND_PORT}" ]; then
    echo "ERROR: BACKEND_PORT environment variable is not set."
    exit 1
fi

envsubst '$${BACKEND_HOSTNAME},$${BACKEND_PORT}' < "${TEMPLATE_FILE}" > "${CONFIG_FILE}"

exec "$@"