
FROM        alpine:3.4
MAINTAINER  Artem Labazin <xxlabaza@gmail.com>

ARG USERNAME=popa
ARG PASSWORD=123

ENV LANG=en_US.UTF-8 \
    LC_ALL=en_US.UTF-8 \
    LANGUAGE=en_US.UTF-8 \
    LC_CTYPE=en_US.UTF-8 \
    LC_COLLATE=en_US.UTF-8 \
    LC_MESSAGES=en_US.UTF-8 \
    LC_NUMERIC=en_US.UTF-8 \
    LC_TIME=en_US.UTF-8 \
    LC_MONETARY=en_US.UTF-8 \
    LC_PAPER=en_US.UTF-8 \
    LC_IDENTIFICATION=en_US.UTF-8 \
    LC_NAME=en_US.UTF-8 \
    LC_ADDRESS=en_US.UTF-8 \
    LC_TELEPHONE=en_US.UTF-8 \
    LC_MEASUREMENT=en_US.UTF-8 \
    TZ=Europe/Moscow

RUN apk add --no-cache --no-progress \
        tzdata \
        vsftpd \
    && echo "$TZ" > /etc/timezone \
    && cp "/usr/share/zoneinfo/$TZ" /etc/localtime \
    && adduser -s /bin/sh -D ${USERNAME} \
    && echo "${USERNAME}:${PASSWORD}" | chpasswd

COPY    vsftpd.conf /etc/vsftpd.conf
VOLUME  /home/${USERNAME}

EXPOSE 21
EXPOSE 10100-10200

ENTRYPOINT ["vsftpd"]
