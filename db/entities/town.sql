CREATE TABLE town(
	postal_code VARCHAR(10),
	alpha_2_country_code CHAR(2),
	name VARCHAR(50) NOT NULL,
    
    CONSTRAINT frkey_country_code FOREIGN KEY(alpha_2_country_code) REFERENCES country(alpha_2_code) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT prkey_postal_and_country PRIMARY KEY(postal_code, alpha_2_country_code, name)
)
