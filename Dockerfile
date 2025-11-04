#базовый докер образ
# можно создать образ поверх другого образа, где все уже установленно
# любое создание образа нужно начинать с установочного образа, содержащего базовые необходимые утилиты
FROM gradle:8.14.3-jdk21-corretto
#дeфолтные значения аргументов
ARG TEST_PROFILE=api
ARG APIBASEURL=http://localhost:4111
ARG UIBASEURL=http://localhost:3000
ARG UIREMOTE=http://localhost:4444/wd/hub

#Переменные окружения для контейнера
ENV TEST_PROFILE ${TEST_PROFILE}
ENV APIBASEURL ${APIBASEURL}
ENV UIBASEURL ${UIBASEURL}
ENV UIREMOTE ${UIREMOTE}

#рабочая директория
WORKDIR /app

# копируем файл с зависимостями
COPY build.gradle.kts .

# загружаем зависимости
RUN gradle dependencies --no-daemon

#копируем весь проект
COPY . .

# теперь внутрие есть зависимости и есть проект, можно запускать тесты

USER root

#./gradlew test -Pprofile=api
CMD /bin/bash -c " \
    mkdir -p /app/logs; \
    { \
    echo '>>> Running tests with profile ${TEST_PROFILE}' ;\
    ./gradlew test -Pprofile=${TEST_PROFILE} ;\
    echo '>>> Finished tests with profile ${TEST_PROFILE}' ;\
    } 2>&1 | tee /app/logs/run.log"








