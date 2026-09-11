FROM rust:1.98.0-slim-trixie
RUN rustup target add aarch64-unknown-none-softfloat && \
    apt-get update && apt-get install -y --no-install-recommends \
      binutils-aarch64-linux-gnu qemu-system-arm python3 python3-serial ca-certificates curl && \
    rm -rf /var/lib/apt/lists/*
WORKDIR /app
