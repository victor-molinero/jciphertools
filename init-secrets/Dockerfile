FROM alpine:latest

RUN apk add --no-cache openssl

WORKDIR /secrets

COPY init-secrets.sh /usr/local/bin/init-secrets.sh
RUN sed -i 's/\r$//' /usr/local/bin/init-secrets.sh \
	&& chmod +x /usr/local/bin/init-secrets.sh

ENTRYPOINT ["/usr/local/bin/init-secrets.sh"]
