package hr.ferit.pomds.data;

import java.sql.Date;

public record Delivery(String id, Mail mail, ServiceUser sender, ServiceUser recipient, Date takeoverDate, Date completionDate,
		boolean isUnsuccessful) {

}
