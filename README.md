# LAB_DB_CRUD
Приложение использует Mysql 8.0<br>
Перед запуском приложения нужно установить БД. Для этого:
  1. Откройте консоло mysql
  1. Выполните команду create database rus_rullers;
  1. Скопируйте файл проекта LAB_DB_CRUD/src/resources/rus_rullers.sql в папку c:/program files/MySQL/MySQL Server 8.0/bin
  1. Откройте консоль
  1. выполните команду cd c:\Program Files\MySQL\MySQL Server 8.0\bin
  1. Выполните команду mysql –u root –p rus_rullers < rus_rullers.sql

После этого :
  1. Откройте файл проекта LAB_DB_CRUD/src/resources/config.properties
  1. Поменяйте your_mySql_username и your_mySql_password на ваше имя пользователя и пароль mySql
  1. Сохраните изменения
 
 
  Архитектуры проекта: <br>
    Класс Database содержит функции взаимодействия с базой данных mysql.<br>
    Класс Handler отвечает за обработку всех запросов<br>
    Класс ConfigLoader обеспечивает конфигурацию сервера через env-переменные.<br>
    Класс LogSystem реализует запись accesLog в файл<br>
    Класс xmlParser генерирует html <br>
  
  Модель базы данных:<br>
  БД содержит 4 таблицы: ruller, ruller_town_relation, ruller_years_of_life, town.<br>
  Таблицы ruller содержит столбцы ID(обязательно), имя(обязательно), отчество, титул.<br>
  Таблица ruller_town_relation содержит столбцы ID правителя,ID города, год начала правления, год конца правления. ID правителя,ID города 
  связаны с стобцами ID таблиц ruller и town соответственно.<br>
  Таблица ruller_years_of_life содержит таблицы ID, год рождения, год смерти.Столбец ID связан со столбцом ID таблицы ruller.<br>
  Таблица town содержит столбцы ID и название города 
  
