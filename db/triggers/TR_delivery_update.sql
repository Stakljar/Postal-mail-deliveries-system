DELIMITER **
CREATE TRIGGER TR_delivery_update BEFORE UPDATE
ON delivery
FOR EACH ROW
	IF NEW.takeover_date IS NULL AND (NEW.completion_date IS NOT NULL OR NEW.is_unsuccessful = TRUE) THEN
		SIGNAL SQLSTATE '03000'
        SET MESSAGE_TEXT = 'Completion date cannot be set or delivery cannot be marked unsuccessful if delivery does not have takeover date set.';
	ELSEIF NEW.completion_date IS NOT NULL AND NEW.is_unsuccessful = TRUE THEN
		SIGNAL SQLSTATE '03001'
        SET MESSAGE_TEXT = 'Completion date cannot be set if delivery has been marked unsuccessful.';
	END IF;
**