FROM haproxy:2.9-alpine

USER root
RUN apk add --no-cache curl

ENV CONSUL_TEMPLATE_VERSION=0.40.0
RUN curl -L https://releases.hashicorp.com/consul-template/${CONSUL_TEMPLATE_VERSION}/consul-template_${CONSUL_TEMPLATE_VERSION}_linux_amd64.zip \
    -o /tmp/ct.zip && \
    unzip /tmp/ct.zip -d /usr/local/bin && \
    chmod 755 /usr/local/bin/consul-template && \
    rm /tmp/ct.zip

RUN mkdir -p /haproxy-config && chmod 777 /haproxy-config

USER haproxy