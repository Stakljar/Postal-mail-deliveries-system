CREATE TABLE country(
	alpha_2_code CHAR(2),
    country_name VARCHAR(50),
    
	CONSTRAINT prkey_country_code PRIMARY KEY(alpha_2_code)
)