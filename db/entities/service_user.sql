CREATE TABLE service_user(
	id CHAR(36),
    username VARCHAR(30) CHARACTER SET UTF8MB4 COLLATE UTF8MB4_BIN NOT NULL UNIQUE,
    password VARCHAR(56) NOT NULL,
    first_name VARCHAR(20) NOT NULL,
    last_name VARCHAR(30) NOT NULL,
	address VARCHAR(60) NOT NULL,
    town_postal_code VARCHAR(13),
    alpha_2_town_country_code CHAR(2),
    town_name VARCHAR(50),
    
    CONSTRAINT prkey_service_user_id PRIMARY KEY(id),
    CONSTRAINT frkey_service_user_city_postal_and_country_code FOREIGN KEY(town_postal_code, alpha_2_town_country_code, town_name) REFERENCES town(postal_code, alpha_2_country_code, name)
		ON DELETE SET NULL ON UPDATE CASCADE
)