FROM java:8
MAINTAINER "pengyongjun3"<pengyongjun3@163.com>
ARG APP_NAME
ENV APP_NAME=${finalName}
ADD ${APP_NAME}.jar msa-app.jar
EXPOSE 1688
CMD java -jar mas-app.jar