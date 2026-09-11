# syntax=docker/dockerfile:1
FROM golang:1.27.0-alpine AS build
WORKDIR /src
COPY go.mod go.sum ./
RUN go mod download
COPY main.go hello.glj ./
RUN CGO_ENABLED=0 go build -tags glj_aot_runtime -trimpath -ldflags="-s -w" -o /hello .

FROM scratch
COPY --from=build /hello /hello
USER 65532:65532
EXPOSE 8080
ENTRYPOINT ["/hello"]
