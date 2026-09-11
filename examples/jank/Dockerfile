FROM --platform=linux/amd64 ubuntu:24.04
RUN apt-get update && apt-get install -y --no-install-recommends curl ca-certificates && \
    curl -fsSL https://ppa.jank-lang.org/pool/main/j/jank/jank_0.1-noble_amd64.deb -o /tmp/jank.deb && \
    echo '50ee76887963a39a74825453866af4efafe80aa2f1356495a10854a82bcc7854  /tmp/jank.deb' | sha256sum -c - && \
    apt-get install -y --no-install-recommends /tmp/jank.deb libsdl2-dev xvfb xauth && \
    rm -f /tmp/jank.deb && rm -rf /var/lib/apt/lists/*
WORKDIR /app
