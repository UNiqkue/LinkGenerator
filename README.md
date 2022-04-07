# LinkGenerator (LinkShortener)

## Task

Задача выполняется в соответствии с описаниями:

* https://docs.google.com/document/d/1CSL7HBTayxCztLq86Ne2arj5Iyf8KQvVufzoz66w__k
* https://docs.google.com/document/d/1RLGRpvO4QhE-Ocm9ZjHH7Vs3h0ss6iZ7

Необходимо разработать HTTP-сервис, который генерирует короткие ссылки.

## Сборка и вызов jar файла

Используется JDK 11.  
Postgre DB.  
все настройки в application.yaml - spring.datasource.   
Файл application-test.yaml используется для тестов - реализована h2.
Находятся в пакете resources.

Необходимо вызвать команду

```
mnv clean install
```

Запуск джарки выглядит так

```
java -jar target/link-generator-1.0.0.jar
```
Запускается на порту localhost:8080

## Объяснение решения и реализация

В сервисе реализована следующая функциональность:
Приложено [yaml описание](doc/openapi.yaml) и [коллекция postman](doc/Link%20generator.postman_collection.json) 
1. Генератор ссылок  
Пример запроса: **POST /generate**  
Пример тела запроса:

```json
{
  "original": "https://some-server.com/some/url?some_param=1"
}
```
Пример ответа:
```json
{
  "link": "/l/some-short-name"
}
```
2. Редирект  
Ресурс осуществляет редирект на оригинальный url.  
Пример запроса:
GET /l/some-short-name
3. Статистика  

3.1 Статистика по конкретным ссылкам  
Ресурс возвращает статистику переходов по конкретной ссылке.  
Пример запроса:  
GET /stats/some-short-name  
Пример ответа:
```json
{
  "link": "/l/some-short-name",
   "original": "http://some-server.com/some/url",
   "rank": 1,
   "count": 100500
}
```
3.2 Рейтинг ссылок  
Ресурс возвращает статистику запросов с сортировкой по частоте запросов по убыванию и возможностью постраничного отображения.  
Параметры строки запроса:  
page - номер страницы  
count - число записей, отображаемых на странице, максимальное возможное значение 100 (включительно)  
Пример запроса:  
GET /stats?page=1&count=2  
Пример ответа:
```json
[
   {
      "link": "/l/some-short-name",
      "original": "http://some-server.com/some/url",
      "rank": 1,
      "count": 100500
   },
   {
      "link": "/l/vdXC5atV8",
      "original": "https://www.baeldung.com/spring-yaml",
      "rank": 2,
      "count": 4000
   }
]
```

## Детали реализации
Для оптимизации используется:
1) Библиотека https://hashids.org/java/ - гарантирует, что у одной и той же строки будет один хеш, позволяет генерировать строки определённого размера.

| Token Length  | Unique Token Combination |     Range      |
|:-------------:|:------------------------:|:--------------:|
|       6       |      2,176,782,336       |  > 2 Billion   |
|       7       |      78,364,164,096      |  > 78 Billion  |
|       8       |    2,821,109,907,456     |  > 2 Trillion  |
|       9       |   101,559,956,668,416    | > 100 Trillion |

Ключ задаётся по параметру в application.yaml
```yaml
link:
  generator:
    hash:
      keys: 123, 234, 345
```
С данным ключом генерируется хеш размером 9, что гарантирует уникальное кол-во комбинаций более 100 Trillion.
Interesting resources:
* https://dzone.com/articles/how-a-url-shortening-application-works
* https://proglib.io/p/a-mozhno-pokoroche-kak-rabotayut-sokrashchateli-ssylok-2020-02-24
2) Кеширование осуществляется на запросах **POST /generate** и **GET /l/some-short-name**

