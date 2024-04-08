package hr.ferit.pomds.utils;

import java.text.SimpleDateFormat;

public class AdaptedDateFormat extends SimpleDateFormat {

	private static final long serialVersionUID = 5151905989412412457L;
	private static AdaptedDateFormat adaptedDateFormat;
	
	private AdaptedDateFormat(String pattern){
		
		super(pattern);
	}
	
	public static AdaptedDateFormat getDateFormat() {
		
		if(adaptedDateFormat == null) {
			adaptedDateFormat = new AdaptedDateFormat("dd.MM.YYYY.");
		}
		return adaptedDateFormat;
	}
}
