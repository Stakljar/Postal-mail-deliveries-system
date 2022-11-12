DELIMITER **
CREATE TRIGGER TR_mail_insert BEFORE INSERT 
ON mail
FOR EACH ROW
	IF NEW.type != 'package' AND NEW.type != 'letter' THEN
		SIGNAL SQLSTATE '03102'
        SET MESSAGE_TEXT = 'Type must be strictly letter or package.';
	ELSEIF NEW.type = 'letter' AND (NEW.name IS NOT NULL OR NEW.is_fragile = TRUE) THEN
		SIGNAL SQLSTATE '03103'
        SET MESSAGE_TEXT = 'Letter must not have a name and cannot be fragile.';
	END IF;
**