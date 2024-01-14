FROM openjdk:11.0.11-jre-slim

ARG KAKAO_CLIENT_ID
ARG KAKAO_CLIENT_SECRET
ARG KAKAO_REDIRECT_URI
ARG NAVER_CLIENT_ID
ARG NAVER_CLIENT_SECRET
ARG NAVER_REDIRECT_URI
ARG JWT_SECRET_KEY
ARG DB_URL
ARG DB_USERNAME
ARG DB_PASSWORD
ARG SENTRY_AUTH_TOKEN
ARG GPT_TOKEN
ARG REDIS_HOST
ARG REDIS_PORT
ARG AWS_ACCESS_KEY
ARG AWS_SECRET_KEY
ARG AWS_REGION
ARG AWS_S3_BUCKET

ARG JAR_FILE=./build/libs/*-SNAPSHOT.jar

COPY ${JAR_FILE} teum.jar

ENV kakao_client_id=${KAKAO_CLIENT_ID} \
    kakao_client_secret=${KAKAO_CLIENT_SECRET} \
    kakao_redirect_uri=${KAKAO_REDIRECT_URI} \
    naver_client_id=${NAVER_CLIENT_ID} \
    naver_client_secret=${NAVER_CLIENT_SECRET} \
    naver_redirect_uri=${NAVER_REDIRECT_URI} \
    jwt_secret_key=${JWT_SECRET_KEY} \
    db_url=${DB_URL} \
    db_user=${DB_USERNAME} \
    db_password=${DB_PASSWORD} \
    sentry_auth_token=${SENTRY_AUTH_TOKEN} \
    gpt_token=${GPT_TOKEN} \
    redis_host=${REDIS_HOST} \
    redis_port=${REDIS_PORT} \
    aws_access_key=${AWS_ACCESS_KEY} \
    aws_secret_key=${AWS_SECRET_KEY} \
    aws_region=${AWS_REGION} \
    aws_s3_bucket=${AWS_S3_BUCKET}


ENTRYPOINT java -jar teum.jar \
            --spring.datasource.url=${db_url} \
            --spring.security.oauth2.client.registration.kakao.client-id=${kakao_client_id} \
            --spring.security.oauth2.client.registration.kakao.client-secret=${kakao_client_secret} \
            --spring.security.oauth2.client.registration.kakao.redirect-uri=${kakao_redirect_uri} \
            --spring.security.oauth2.client.registration.naver.client-id=${naver_client_id} \
            --spring.security.oauth2.client.registration.naver.client-secret=${naver_client_secret} \
            --spring.security.oauth2.client.registration.naver.redirect-uri=${naver_redirect_uri} \
            --jwt.secret=${jwt_secret_key} \
            --spring.datasource.url=${db_url} \
            --spring.datasource.username=${db_user} \
            --spring.datasource.password=${db_password} \
            --spring.flyway.url=${db_url} \
            --spring.flyway.user=${db_user} \
            --spring.flyway.password=${db_password} \
            --gpt.token=${gpt_token} \
            --spring.data.redis.host=${redis_host} \
            --spring.data.redis.port=${redis_port} \
            --spring.cloud.aws.credentials.access-key=${aws_access_key} \
            --spring.cloud.aws.credentials.secret-key=${aws_secret_key} \
            --spring.cloud.aws.region.static=${aws_region} \
            --spring.cloud.aws.s3.bucket=${aws_s3_bucket}
