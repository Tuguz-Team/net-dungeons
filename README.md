# Net Dungeons

## Информация об итоговом проекте

### 1. Суть проекта
Игра **“Net Dungeons”** – это многопользовательская пошаговая игра для Android-устройств!

#### Особенности игры:
 - **одновременность** выполнения персонажами своих ходов (игроку на выполнения хода даётся максимум 15 секунд)

 - **процедурная случайная генерация** игровых уровней

   - выбор из 2-х режимов игры: *Командный бой* и *Мясорубка*
   - выбор из 3-х размеров карты: *Огромный*, *Большой* и *Средний*
   - выбор из 3-х типов карты: *Дворец*, *Крепость* и *Трущобы*
 
 - наличие у персонажа небольшого инвентаря и **использование разнообразных предметов**, повышающих его характеристики

 - **Дерево Навыков** для развития персонажа за очки, получаемых за новые уровни

### 2. Направления проекта
- 2 - *Мобильная разработка*
- 7 - *Game development*

### 3. Технологии разработки
- язык программирования **Kotlin**
- фреймворк разработки видеоигр **LibGDX** и его расширения (**VisUI**, **KTX**, ...)
- облачный сервис **Firebase** по работе с базами данных от *Google*

### 4. Инструменты разработки
- интегрированная среда разработки **Android Studio**
- редакторы трёхмерной графики **MagicaVoxel** (*создание* стилизованных моделей) и **Blender** (*оптимизация* моделей)
- утилиты **GDX Texture Packer** (редактор файлов для *отрисовки дизайна интерфейса*) и **Hiero** (редактор файлов, представляющих собой *шрифты* в **LibGDX**)

## Как собрать приложение с помощью Docker?
Все действия выполняются в корневой директории проекта!
- получить файл `google-services.json` из консоли [Firebase](https://console.firebase.google.com/project/net-dungeons/overview) данного проекта
- создать секрет из файла `google-services.json`, находящегося в корневой директории:
    - прописать команду `docker swarm init` для инициализации swarm
    - создать секрет командой `docker secret create google-services google-services.json`
- создать новый образ командой `docker build -t android-builder docker`
- создать новый сервис командой:
```bash
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

## Информация по сдаче итогового проекта

### 1. Информация про проект, UserStory, Макет
- Дата сдачи: 18 марта 2021 года
- Баллы: 1 балл

### 2. Работа над проектом. Оформление ReadMe
- Дата сдачи: 20 мая 2021 года
- Баллы: 0 баллов

### 3. Docker
- Дата сдачи: 20 мая 2021 года
- Баллы: 1 балл
