CREATE DATABASE my_database;
USE my_database;


CREATE TABLE social_media_accounts
(
    account_id    INT AUTO_INCREMENT PRIMARY KEY,
    access_token  VARCHAR(1024),
    platform_name VARCHAR(255) NOT NULL,
    user_id       TEXT         NOT NULL
);

# ALTER TABLE social_media_accounts
#     MODIFY access_token VARCHAR(1024);

# DESCRIBE social_media_accounts;



