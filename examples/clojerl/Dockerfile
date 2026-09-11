FROM erlang:26-slim@sha256:0dcb80b08ba80849731b9936f262d1c8e55936d540f14082a77c0388a84877b0
RUN apt-get update && apt-get install -y --no-install-recommends git make gcc g++ ca-certificates && rm -rf /var/lib/apt/lists/*
WORKDIR /opt/clojerl
RUN git init && git remote add origin https://github.com/clojerl/clojerl && \
    git fetch --depth 1 origin 4ad14e57df85d30cc3332308f861f4e174383483 && \
    git checkout --detach FETCH_HEAD && rebar3 clojerl compile
ENV PATH="/opt/clojerl/bin:${PATH}"
WORKDIR /app
COPY greeter.clje client.clje ./
ENTRYPOINT ["clojerl"]
CMD ["greeter.clje"]
