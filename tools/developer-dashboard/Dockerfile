FROM debian:stable

RUN apt-get update
RUN apt-get -y upgrade
RUN apt-get install -y nodejs npm

RUN groupadd -r app -g 1000 && \
    useradd -r -g app -u 1000 app -d /app && \
    mkdir -p /app && \
    chown -R app:app /app

WORKDIR /app

RUN echo 'export PATH=/sbt/bin:$PATH' >> /etc/bash.bashrc
RUN echo 'export PS1="\[\e[1;32m\]sdk-developer-dashboard\[\e[0m\]> "' >> /etc/bash.bashrc

USER 1000

ENTRYPOINT /bin/bash
