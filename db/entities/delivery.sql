CREATE TABLE delivery(
	id CHAR(36),
    mail_id CHAR(36),
    sender_id CHAR(36),
    recipient_id CHAR(36),
    takeover_date DATE,
    completion_date DATE,
    is_unsuccessful BOOLEAN DEFAULT FALSE,
    
    CONSTRAINT prkey_delivery_id PRIMARY KEY(id),
    CONSTRAINT frkey_delivery_mail_id FOREIGN KEY(mail_id) REFERENCES mail(id)ON DELETE SET NULL ON UPDATE CASCADE,
    CONSTRAINT frkey_delivery_sender_id FOREIGN KEY(sender_id) REFERENCES service_user(id) ON DELETE SET NULL ON UPDATE CASCADE,
    CONSTRAINT frkey_delivery_recipient_id FOREIGN KEY(recipient_id) REFERENCES service_user(id) ON DELETE SET NULL ON UPDATE CASCADE
)


