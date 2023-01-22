### Дипломный проект - java-explore-with-me (Афиша мероприятий - Исследуй со мной)

**Техническое задание**

В рамках дипломного проекта вам предстоит разработать приложение ExploreWithMe (англ. «исследуй со мной»).
Оно должно давать возможность делиться информацией об интересных событиях и помогать найти компанию для участия в них.

**Идея**

Свободное время — ценный ресурс. Ежедневно мы планируем, как его потратить — куда и с кем сходить.
Сложнее всего в таком планировании поиск информации и переговоры.
Какие намечаются мероприятия, свободны ли в этот момент друзья, как всех пригласить и где собраться.
Приложение, которое вы будете создавать, — афиша,
где можно предложить какое-либо событие от выставки до похода в кино и набрать компанию для участия в нём.

**Картинка**

**Два сервиса**

Вам требуется создать два сервиса.

1. Основной сервис — содержит всё необходимое для работы продукта.
2. Сервис статистики — хранит количество просмотров и позволяет делать различные выборки для анализа работы приложения.

Разберём подробнее, что требуется от каждого сервиса.

**Основной сервис**

API основного сервиса разделите на три части.
Первая — публичная, доступна без регистрации любому пользователю сети.
Вторая — закрытая, доступна только авторизованным пользователям.
Третья — административная, для администраторов сервиса.
К каждой из частей свои требования.

**Требования к публичному API**

Публичный API должен предоставлять возможности поиска и фильтрации событий.

1. Сортировка списка событий должна быть организована либо по количеству просмотров,
   которое должно запрашиваться в сервисе статистики, либо по датам событий.
2. При просмотре списка событий возвращается только краткая информация о мероприятиях.
3. Просмотр подробной информации о конкретном событии нужно настроить отдельно (через отдельный эндпоинт).
4. Каждое событие должно относиться к какой-то из закреплённых в приложении категорий.
5. Должна быть настроена возможность получения всех имеющихся категорий и подборок событий
   (такие подборки будут составлять администраторы ресурса).
6. Каждый публичный запрос для получения списка событий или полной информации о мероприятии
   должен фиксироваться сервисом статистики.

**Подсказка: как узнать IP-адрес клиента, который отправил запрос**

Чтобы передать в сервис статистики информацию об обработке запроса к эндпоинту вам потребуется IP-адрес пользователя,
сделавшего запрос, а также путь эндпоинта. Получить эту информацию можно через класс HttpServletRequest.
Чтобы получить объект этого класса, добавьте его в качестве дополнительного аргумента в метод контроллера.

    @GetMapping("/some/path/{id}")
    public void logIPAndPath(@PathVariable long id, HttpServletRequest request) {
        log.info("client ip: {}", request.getRemoteAddr());
        log.info("endpoint path: {}", request.getRequestURI());
    }

**Требования к API для авторизованных пользователей**

Закрытая часть API призвана реализовать возможности зарегистрированных пользователей продукта.

1. Авторизованные пользователи должны иметь возможность добавлять в приложение новые мероприятия,
   редактировать их и просматривать после добавления.
2. Должна быть настроена подача заявок на участие в интересующих мероприятиях.
3. Создатель мероприятия должен иметь возможность подтверждать заявки, которые отправили другие пользователи сервиса.

**Требования к API для администратора**

Административная часть API должна предоставлять возможности настройки и поддержки работы сервиса.

1. Нужно настроить добавление, изменение и удаление категорий для событий.
2. Должна появиться возможность добавлять, удалять и закреплять на главной странице подборки мероприятий.
3. Требуется наладить модерацию событий, размещённых пользователями, — публикация или отклонение.
4. Также должно быть настроено управление пользователями — добавление, просмотр и удаление.

**Сервис статистики**

Второй сервис, статистики, призван собирать информацию.
Во-первых, о количестве обращений пользователей к спискам событий и,
во-вторых, о количестве запросов к подробной информации о событии.
На основе этой информации должна формироваться статистика о работе приложения.

**Подсказка: Эндпоинты для статистики**

Сохранять статистику нужно будет по двум эндпоинтам:
`GET /events`, который отвечает за получение событий с возможностью фильтрации,
и `GET /events/{id}`, который позволяет получить подробную информацию об опубликованном событии по его идентификатору.

Функционал сервиса статистики должен содержать:

- запись информации о том, что был обработан запрос к эндпоинту API;
- предоставление статистики за выбранные даты по выбранному эндпоинту.

Чтобы можно было использовать сервис статистики, нужно разработать HTTP-клиент.
Он будет отправлять запросы и обрабатывать ответы. Можно
использовать [HTTP-клиент, который входит в состав JDK,](https://docs.oracle.com/en/java/javase/11/docs/api/java.net.http/java/net/http/HttpClient.html)
или [RestTemplate, входящий в состав Spring Framework.](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/client/RestTemplate.html)

**Модель данных**

Жизненный цикл события должен включать несколько этапов.

1. Создание.
2. Ожидание публикации. В статус ожидания публикации событие переходит сразу после создания.
3. Публикация. В это состояние событие переводит администратор.
4. Отмена публикации. В это состояние событие переходит в двух случаях.
   Первый — если администратор решил, что его нельзя публиковать.
   Второй — когда инициатор события решил отменить его на этапе ожидания публикации.

> ➡ **Аутентификация и авторизация**
> Оба сервиса ExploreWithMe работают за VPN. С внешним миром их связывает гейтвей.
> Он контактирует с системой аутентификации и авторизации, а затем перенаправляет запрос в сервисы.
> То есть, если гейтвей пропустил запрос к закрытой или административной части API,
> значит этот запрос успешно прошел аутентификацию и авторизацию.

**Спецификация API**

Для обоих сервисов мы разработали подробную спецификацию API.

1. спецификация основного сервиса: ewm-main-service-spec.json
2. спецификация сервиса статистики: ewm-stats-service.json

Для работы со спецификациями вам понадобится редактор Swagger. https://editor-next.swagger.io/
Чтобы просмотреть спецификацию в редакторе, необходимо выполнить ряд шагов:

- Скопировать ссылку на файл спецификации.
- Открыть онлайн-версию редактора Swagger. На верхней панели выбрать меню File, затем пункт Import URL.
- Вставить скопированную ссылку в текстовое поле появившегося диалогового окна и нажать OK.

**Как пользоваться спецификацией**

Спецификация представляет собой набор эндпоинтов, разделенных на группы.
У каждой группы есть название и краткое описание.
В одну группу объединены эндпоинты, связанные общим началом в пути и набором смысловых характеристик.

Внутри группы можно просмотреть информацию об эндпоинтах, которые она объединяет.
В частности, HTTP-метод, путь и краткое описание каждого эндпоинта.

Если развернуть описание эндпоинта, можно посмотреть о нём более подробную информацию, в том числе,
информацию об ограничениях, которые должны быть предусмотрены в реализации API.

Кроме того, подробное описание эндпоинта включает информацию о параметрах строки запроса.
К примеру, вот параметры строки запроса публичного эндпоинта для получения списка событий.

Если запрос к эндпоинту может содержать тело запроса, то внутри подробного описания указана схема,
которая используется для формирования тела запроса, а так же пример данных.

После информации о запросе также перечислены варианты ответов.

**Детали проверки работ**

Разработка должна вестись в публичном репозитории в отдельной ветке с названием develop.
В описании сборки для каждого из сервисов обязательна зависимость Spring Boot Actuator.
Вот идентификаторы для её добавления.

<code>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-actuator</artifactId>
    </dependency>

</code>

**Отчет об ошибках**

При отправке запроса на слияние изменений в GitHub запустится автоматическая проверка выполненной работы.
Если проверка неуспешна, сформируется отчет о непройденных тестах.
Этот отчет содержит ту же информацию, что и лог рабочего процесса проверки, но в более удобном виде.
И отчет об ошибках, и лог работы можно скачать в виде артефактов.

Файл отчета ewm_main_report.html находится внутри артефакта postman_tests_report.
Его можно просмотреть при помощи браузера.

**Как работать с отчетом**

При открытии отчета отобразится сводная информация обо всех тестах.

Раздел «Всего запросов» в верхней панели меню даёт возможность просмотра всех тестов.
Если кликнуть по нему, то вы перейдёте к списку тестов по группам.
Слева от названия каждой группы отображается красная или зеленая иконка с буквой i — она показывает,
есть ли в группе проваленные тесты или все тесты прошли проверку.

Если развернуть группу, можно посмотреть подробную информацию о каждом тесте.
В частности, информацию о тестируемом запросе и об ответе на него сервером вашего приложения.

**Как работать с проваленным тестом**

При анализе информации о непройденном тесте стоит обратить внимание на следующие разделы:

- ИНФОРМАЦИЯ О ТЕСТЕ;
- ЛОГИ КОНСОЛИ;
- ТЕЛО ОТВЕТА.

К примеру, в разделе с информацией о тесте указаны конкретные утверждения, которые при проверке не соответствовали
ожиданиям.

Здесь тест проверял ответ от сервера, который должен был иметь код статуса равный 200, а также не пустое тело ответа,
содержащее данные в формате JSON.
Он был провален на проверке первого же утверждения — код статуса ответа был равен 400 (BAD REQUEST), а не 200 (OK):
expected response to have status reason 'OK' but got 'BAD REQUEST’.
В таком случае стоит посмотреть что вернул сервер приложения в разделе о теле ответа.

Тело ответа содержит информацию об ошибке — для корректной обработки запроса необходимо,
чтобы в его теле были переданы соответствующие данные. Так как в теле запроса их не было, сервер вернул ошибку.
Чтобы понять, почему тестовый запрос не содержал нужных данных, можно обратиться к разделу с логами консоли.

В логах отражается информация о двух залогированных сообщениях, где сообщается, что при подготовке тестовых данных
возникла ошибка. Конкретно, ошибка возникла при отправке запроса к серверу приложения. Также указана вся информация об
этом запросе.

При работе с тестами стоит понимать, что приложение тестируется по принципу «черного ящика».
Тестовая система может влиять на приложение, только используя API, который был указан в спецификации.
Поэтому, чтобы протестировать одни эндпоинты, важна корректная работа других эндпоинтов.

В примере выше, чтобы протестировать добавление подборки событий, нужно сначала добавить событие в систему.
То есть если эндпоинт добавления события работает некорректно, то система тестирования не может корректно протестировать
и эндпоинт добавления подборки. Поэтому тестовый запрос был отправлен без необходимых данных — в процессе их подготовки
произошла ошибка.

Чтобы отладить сервер приложения, посмотрите на информацию о запросе в логах и
попробуйте выполнить его на своем локальном компьютере.

**Фичи для самостоятельного проектирования**

Помимо основной части, вам также необходимо реализовать дополнительную функциональность.
Приступайте к её реализации сразу после успешного прохождения ревью основной части дипломного задания
(спланировать то, какую функциональность вы возьмёте и как будете с ней работать стоит ещё в начале работы над дипломом)
.

Обратите внимание, реализацию дополнительной функциональности нужно поместить в отдельную ветку.

Как именно реализовывать дополнительную фичу в коде, каким требованиям соответствовать,
как интегрировать в проект и API — вы решаете сами.
Вы можете выбрать любую функциональность из нашего списка или придумать свою.

Мы предлагаем следующие варианты.

- Комментарии

Возможность оставлять комментарии к событиям и модерировать их.

- Подписки

Первый вариант. Возможность подписываться на других пользователей и получать список актуальных событий, опубликованных
этими пользователями.

Второй вариант. Подписка на друзей и возможность получать список актуальных событий, в которых они принимают участие.

- Рейтинги

Возможность лайкать/дизлайкать событие. Формирование рейтинга мероприятий и рейтинга их авторов.
Возможность сортировки событий в зависимости от рейтингов.

- Администрирование

Первый вариант. Возможность для администратора добавлять конкретные локации — города,
театры, концертные залы и другие в виде координат (широта, долгота, радиус). Получение списка этих локаций.
Возможность поиска событий в конкретной локации.

**Подсказка: функция для PostgreSQL для реализации работы с координатами**

Эта функция принимает на вход координаты (градусы широты и долготы) двух точек и вычисляет дистанцию между ними.
Её можно использовать для упрощённой проверки, попадает ли локация проведения события в заданную область.
Если дистанция от локации события до центра окружности (выбранной области) не превышает радиус этой окружности,
значит, оно проходит в выборку.

    CREATE OR REPLACE FUNCTION distance(lat1 float, lon1 float, lat2 float, lon2 float)
        RETURNS float
    AS
    '
    declare
        dist float = 0;
        rad_lat1 float;
        rad_lat2 float;
        theta float;
        rad_theta float;
    BEGIN
        IF lat1 = lat2 AND lon1 = lon2
        THEN
            RETURN dist;
        ELSE
            -- переводим градусы широты в радианы
            rad_lat1 = pi() * lat1 / 180;
            -- переводим градусы долготы в радианы
            rad_lat2 = pi() * lat2 / 180;
            -- находим разность долгот
            theta = lon1 - lon2;
            -- переводим градусы в радианы
            rad_theta = pi() * theta / 180;
            -- находим длину ортодромии
            dist = sin(rad_lat1) * sin(rad_lat2) + cos(rad_lat1) * cos(rad_lat2) * cos(rad_theta);

            IF dist > 1
                THEN dist = 1;
            END IF;

            dist = acos(dist);
            -- переводим радианы в градусы
            dist = dist * 180 / pi();
            -- переводим градусы в километры
            dist = dist * 60 * 1.8524;

            RETURN dist;
        END IF;
    END;
    '
    LANGUAGE PLPGSQL;

Второй вариант. Улучшение модерации событий администратором — возможность выгружать все события, ожидающие модерации,
делать их проверку и оставлять комментарий для инициатора события, если оно не прошло модерацию.
При этом инициатору дать возможность исправить замечания и отправить событие на повторную модерацию.

Обратите внимание, вам нужно реализовать только одну дополнительную функциональность.
Выбирайте её в зависимости от вашего уровня владения Java, времени на диплом и внутренних ресурсов.

**Как работать с функциональностью**

После выбора функциональности не бросайтесь сразу в её реализацию. Продумайте сценарии её использования и ограничения.
Вот примеры вопросов по каждой из фич, на которые лучше найти ответы до того, как приступать к написанию кода.

1. Комментирование: Может ли пользователь, оставивший комментарий, отредактировать его позже?
2. Подписка на пользователей: Могут ли пользователи запрещать подписку на себя?
3. Подписка на друзей: Показывать ли все события друзьям или дать возможность делиться/скрывать выбранные?
4. Рейтинг: Показывать ли рейтинг события/пользователя публично?
   Разрешить лайкать/дизлайкать любые события или только посещённые?
5. Локации: Ограничивать ли пользователей при добавлении события только заданными в системе локациями?
6. Модерация: Как повлияет на модерацию жизненный цикл события от создания до его проведения?

**\* Задача со звёздочкой — тесты**

Это необязательное задание. Однако вы уже знаете на собственном опыте, что разработчики не могут не ошибаться,
и поэтому код нужно обязательно покрывать тестами, чтобы находить ошибки и несоответствия как можно раньше.
Реализуйте модульные тесты для проверки функциональности вашего проекта.
Это потребует времени, но также принесёт ощутимый результат.

**Напутственная аффирмация**

У вас точно всё получится! Вы уже молодец! Вы много знаете и умеете!
Дипломный проект Java вам по зубам! Да прибудет с вами сила!


