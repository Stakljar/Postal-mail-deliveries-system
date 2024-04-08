CREATE TABLE mail(
	id CHAR(36),
    type VARCHAR(7) DEFAULT "letter" CHECK (type in ("letter", "package")),
    name VARCHAR(50),
    is_fragile BOOLEAN DEFAULT FALSE,
    
    CONSTRAINT prkey_mail_id PRIMARY KEY(id)
)

