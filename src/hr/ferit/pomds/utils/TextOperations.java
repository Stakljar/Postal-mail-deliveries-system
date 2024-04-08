package hr.ferit.pomds.utils;

public class TextOperations {

	public static boolean isTextSizeUnderLimit(String text, int limit) {
		
		if(text.length() <= limit) {
			return true;
		}
		else {
			return false;
		}
	}
	
	/**
	 * @param text the string to be split
	 * @param sizePerRow maximum number of characters per row
	 * @return the split text that needs to be inserted inside <html></html> tags
	 * @author Dražen Antunović
	 */
	public static String splitTextIntoRows(String text, int sizePerRow) {
		
		int i;
		if(text == null) {
			return null;
		}
		if(sizePerRow == 0) {
		    return text;
		}
		StringBuilder splitTextBuilder = new StringBuilder();
		for (i = 0; i < (text.length() / sizePerRow); i++) {
			splitTextBuilder.append(text.substring(i * sizePerRow, (i + 1) * sizePerRow));
			if(i == (text.length() / sizePerRow - 1)) {
				if(text.substring((i + 1) * sizePerRow, text.length()) != "")
					splitTextBuilder.append("<br>");
			}
			else {
				splitTextBuilder.append("<br>");
			}
		}
		splitTextBuilder.append(text.substring(i * sizePerRow, text.length()));
		return splitTextBuilder.toString();
	}
}
