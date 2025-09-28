FROM ollama/ollama:latest

RUN apt-get update && apt-get install -y --no-install-recommends \
    python3 \
    python3-pip \
    libpq5 \
    dos2unix \
    curl \
    && rm -rf /var/lib/apt/lists/*

RUN pip3 install --no-cache-dir huggingface-hub

WORKDIR /app

COPY . .

COPY model_initializer.py /model_initializer.py
COPY startup_wrapper.sh /startup_wrapper.sh
COPY ollama_entrypoint.sh /ollama_entrypoint.sh
COPY Modelfile /app/Modelfile

RUN dos2unix /startup_wrapper.sh && chmod +x /startup_wrapper.sh
RUN dos2unix /ollama_entrypoint.sh && chmod +x /ollama_entrypoint.sh

ENTRYPOINT ["/startup_wrapper.sh", "/ollama_entrypoint.sh"]
