# ER-диаграмма filmorate

<img src = "src/main/resources/static/QuickDBD-Filmorate.svg" width="900" height = "650">

[Посмотреть в редакторе](https://app.quickdatabasediagrams.com/#/d/SLGmVl)

## Описание структуры проекта базы данных

#### users
Содержит данные о пользователях

**Поля:**

* user_id — идентификатор пользователя, Primary Key
* email — электронная почта
* login — логин
* name — имя пользователя
* birthday — дата рождения


#### films
Содержит информацию о фильмах

**Поля:**

* film_id — идентификатор фильма, Primary Key
* name — название фильма
* description — описание фильма
* release_date — дата выхода
* duration — длительность фильма в минутах
* rating_id — идентификатор рейтинга MPAA


#### rating
Содержит информацию о возрастном рейтинге MPAA

**Поля:**

* rating_id — идентификатор рейтинга, Primary Key
* name — название рейтинга
* description — описание, например:
  - G — без возрастных ограничений
  - NC-17 — лицам до 18 лет просмотр запрещён


#### genre
Содержит информацию о жанрах кинематографа

**Поля:**

* genre_id — идентификатор жанра, с Key
* name — название жанра, например:
  - Комедия
  - Драма


#### film_genre
Содержит информацию о жанрах фильмов из таблицы film

**Поля:**

* film_id (отсылает к таблице films) — идентификатор фильма, Foreign Key
* category_id (отсылает к таблице genre) — идентификатор жанра, Foreign Key


#### likes
Содержит информацию о лайках фильмов из таблицы film

**Поля:**

* film_id (отсылает к таблице films) — идентификатор фильма, Foreign Key
* user_id (отсылает к таблице users) — id пользователя, поставившего лайк, Foreign Key


#### friendship_status
Содержит информацию о возможных статусах дружбы из таблицы friendship

**Поля:**

* status_id (отсылает к таблице friendship) — идентификатор статуса, Primary Key
* name — наименование статуса, например:
  - OUTGOING
  - INCOMING
  - CONFIRMED
* description — описание статуса, например:
  - Отправлен запрос на дружбу
  - Получен запрос на дружбу
  - Дружба подтверждена


#### friendship
Содержит информацию о дружбе: id1 — id2 — status_id

**Поля:**

* user_id (отсылает к таблице users) — идентификатор пользователя-1, Foreign Key
* friend_id (отсылает к таблице users) — идентификатор пользователя-2, Foreign Key
* status_id — идентификатор статуса дружбы



## Примеры запросов из БД

### Film

#### Запрос списка всех фильмов

```
SELECT *
FROM films;
```

#### Запрос фильма по id

```
SELECT *
FROM films
WHERE film_id = 1;
```

#### Запрос топ-10 фильмов

```
SELECT *
FROM films
WHERE film_id IN
    (SELECT film_id
     FROM likes
     GROUP BY film_id
     ORDER BY COUNT(user_id) DESC
     LIMIT 10);
```

### User

#### Запрос списка всех пользователей

```
SELECT *
FROM users;
```

#### Запрос пользователя по id

```
SELECT *
FROM users
WHERE user_id = 1;
```

#### Запрос списка друзей пользователя id = 1

```
SELECT DISTINCT *
FROM users
WHERE user_id IN
    (SELECT friend_id
     FROM friendship
     WHERE (user_id = 1 AND status_id IN (SELECT status_id
          FROM friendship_status
          WHERE name = 'CONFIRMED'))
     UNION SELECT user_id
     FROM friendship
     WHERE (friend_id = 1 AND status_id IN (SELECT status_id
          FROM friendship_status
          WHERE name = 'CONFIRMED')));
```

#### Запрос общих друзей пользователей id = 1 и id = 2

```
SELECT DISTINCT *
FROM users
WHERE user_id IN
    (SELECT friend_id
     FROM friendship
     WHERE user_id = 1
       AND status_id IN (SELECT status_id
          FROM friendship_status
          WHERE name = 'CONFIRMED')
       AND friend_id NOT IN (1, 2)
     UNION SELECT user_id
     FROM friendship
     WHERE friend_id = 1
       AND status_id IN (SELECT status_id
          FROM friendship_status
          WHERE name = 'CONFIRMED')
       AND user_id NOT IN (1, 2))
INTERSECT SELECT DISTINCT *
FROM users
WHERE user_id IN
    (SELECT friend_id
     FROM friendship
     WHERE user_id = 2
       AND status_id IN (SELECT status_id
          FROM friendship_status
          WHERE name = 'CONFIRMED')
       AND friend_id NOT IN (1, 2)
     UNION SELECT user_id
     FROM friendship
     WHERE friend_id = 2
       AND status_id IN (SELECT status_id
          FROM friendship_status
          WHERE name = 'CONFIRMED')
       AND user_id NOT IN (1, 2));
```