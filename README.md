*currently moved as history (chrono stages)*

### Дипломная разработка проекта - Афиша мероприятий (с англ. "Explore with me")

<details><summary><b>Краткое описание функционала программы</b></summary>

Сервис для организации совместного времяпровождения (Spring Boot, JPA+API, Hibernate, Postgres, Docker). Пользователи
могут создавать и искать мероприятия, участвовать в них, оставлять комментарии и т.д.

</details>

-----

🧩 Стек-технологий и опыт разработки 🧩

<details><summary><i>Спринт 17</i></summary>


Проект в своей основе включает Java Core, Spring Boot Framework и возможности развертывания в сборке от Maven в
контейнерах с использованием Docker. Программа представляет собой микросервис с архитектурой REST API, отвечающей
требованиям [спецификаций](./specifications) из [Swagger](https://editor-next.swagger.io/). Микросервис состоит из двух
модулей:

1) модуль [ewc-core](./ewm-core) отвечает за базовую и дополнительную бизнес-логику, ядро приложения;
2) модуль [ewm-statistics](./ewm-statistics) призван решать вспомогательные задачи по сбору и выдачи статистики ресурса.

Для удобства дальнейшей разработки и расширения приложения в каждом из модулей объекты DTO выделены в отдельный
субмодуль api: [ewm-core-api](./ewm-core/ewm-core-api) и [ewm-statistics-api](./ewm-statistics/ewm-statistics-api).
Кроме того, в сервисе статистики для обращения к нему, в этом же пакете
реализован [клиент](./ewm-statistics/ewm-statistics-api/src/main/java/ru/practicum/ewm/stats/client),
который можно легко интегрировать в другой сервис, в данном случае в модуль бизнес-логики. Клиент построен на Спринговом
`RestTemplate` с
использованием [TypeUtils](https://commons.apache.org/proper/commons-lang/apidocs/org/apache/commons/lang3/reflect/TypeUtils.html)
в качестве 'super class token' из библиотеки Apache Commons.

</details>

### Про репозиторий, ТЗ и пулл-реквесты с ревью по проекту

Непосредственно в текущем репозитории представлена моя разработка функционала согласно ТЗ по мере прохождения спринта
№ 17.

С текстом задания можно ознакомиться по ссылке ниже в Таблице 1.

<details><summary><b>Таблица 1. Разработка дипломного проекта</b></summary>

| Текст оригинала ТЗ                                                    | Текст разработанного кода<br/>(ссылка на Пулл-реквест из ветки)             |
|-----------------------------------------------------------------------|-----------------------------------------------------------------------------|
| [Основной функционал](./specifications/develop.md)                    | [develop](https://github.com/gityanizamovramil/java-explore-with-me/pull/2) |
| [Дополнительный функционал: Комментарии](./specifications/feature.md) | [feature](https://github.com/gityanizamovramil/java-explore-with-me/pull/3) |

</details>

<details><summary><b>Системные требования</b></summary>

-----

В данном репозитории представлен бэкенд приложения. Работоспособность приложения протестирована по WEB API с помощью
Postman-тестов.

Тесты лежат здесь:

- для модуля бизнес-логики - [ewm-main-service.json](./postman/ewm-main-service.json),
- для модуля статистики - [ewm-stat-service.json](./postman/ewm-stat-service.json).

Приложение работает корректно в текущем виде при наличии:

- установленный [JDK версии 11](https://docs.aws.amazon.com/corretto/),
- сборка с использованием [Maven](https://maven.apache.org/),
- установленный [Docker](https://www.docker.com/products/docker-desktop/).

</details>