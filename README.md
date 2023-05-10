# ER-диаграмма filmorate

<img src = "src/main/resources/static/QuickDBD-Filmorate.svg" width="900" height = "650" alt="ER-diagram">

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
* mpa_id — идентификатор рейтинга MPAA


#### mpa
Содержит информацию о возрастном рейтинге MPAA

**Поля:**

* mpa_id — идентификатор рейтинга, Primary Key
* name — название рейтинга
* description — описание, например:
  - G — без возрастных ограничений
  - NC-17 — лицам до 18 лет просмотр запрещён


#### genre
Содержит информацию о жанрах кинематографа

**Поля:**

* genre_id — идентификатор жанра, Primary Key
* name — название жанра, например:
  - Комедия
  - Драма


#### film_genre
Содержит информацию о жанрах фильмов из таблицы film

**Поля:**

* film_id (отсылает к таблице films) — идентификатор фильма, Foreign Key
* genre_id (отсылает к таблице genre) — идентификатор жанра, Foreign Key


#### likes
Содержит информацию о лайках фильмов из таблицы film

**Поля:**

* film_id (отсылает к таблице films) — идентификатор фильма, Foreign Key
* user_id (отсылает к таблице users) — id пользователя, поставившего лайк, Foreign Key



#### friendship
Содержит информацию о дружбе: id1 — id2 — confirmed

**Поля:**

* user_id (отсылает к таблице users) — идентификатор пользователя-1, Foreign Key
* friend_id (отсылает к таблице users) — идентификатор пользователя-2, Foreign Key
* confirmed — статус дружбы (подтверждена или нет)



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
     WHERE (user_id = 1 AND confirmed = '1')
     UNION SELECT user_id
     FROM friendship
     WHERE (friend_id = 1 AND confirmed = '1'));
```

#### Запрос общих друзей пользователей id = 1 и id = 2

```
SELECT DISTINCT *
FROM users
WHERE user_id IN
    (SELECT friend_id
     FROM friendship
     WHERE user_id = 1
       AND confirmed = '1'
       AND friend_id NOT IN (1, 2)
     UNION SELECT user_id
     FROM friendship
     WHERE friend_id = 1
       AND confirmed = '1'
       AND user_id NOT IN (1, 2))
INTERSECT SELECT DISTINCT *
FROM users
WHERE user_id IN
    (SELECT friend_id
     FROM friendship
     WHERE user_id = 2
       AND confirmed = '1'
       AND friend_id NOT IN (1, 2)
     UNION SELECT user_id
     FROM friendship
     WHERE friend_id = 2
       AND confirmed = '1'
       AND user_id NOT IN (1, 2));
```