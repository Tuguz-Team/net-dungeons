# Net Dungeons

## Информация по сдаче итогового проекта
#### 1. Информация про проект, UserStory, Макет
- Дата сдачи: 18 марта 2021 года
- Баллы: 1 балл

## Как собрать приложение с помощью Docker?
Все действия выполняются в корневой директории проекта!
- получить файл `google-services.json` из консоли [Firebase](https://console.firebase.google.com/project/net-dungeons/overview) данного проекта
- создать секрет из файла `google-services.json`, находящегося в корневой директории:
    - прописать команду `docker swarm init` для инициализации swarm
    - создать секрет командой `docker secret create google-services google-services.json`
- создать новый образ командой `docker build -t android-builder docker`
- создать новый сервис командой:
```
docker service create \
    --name android-builder-service \
    --secret source=google-services,target=/home/gradle/android/google-services.json \
    --mount type=bind,source="$(pwd)",target=/home/gradle/ \
    -w /home/gradle/ android-builder gradle assembleDebug
```
- состояние выполнения сервиса можно посмотреть:
	- командой `docker service ls`, после чего найти сервис под названием **android-builder-service** в списке
	- командой `docker service logs android-builder-service`
- после окончания процесса сборки проекта файл с расширением `apk` будет расположен в директории `android/build/outputs/apk/debug`
