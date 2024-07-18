CREATE DATABASE IF NOT EXISTS UserInfo;
CREATE USER IF NOT EXISTS 'user'@'%' IDENTIFIED BY 'user';
GRANT ALL PRIVILEGES ON UserInfo.* TO 'user'@'%';
FLUSH PRIVILEGES;
