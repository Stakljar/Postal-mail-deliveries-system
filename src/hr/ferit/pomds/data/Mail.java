package hr.ferit.pomds.data;

import hr.ferit.pomds.utils.TextOperations;

public record Mail(String id, String type, String name, boolean isFragile) {

	@Override
	public String toString() {
		
		return "<html><div style='text-align: center';><b>Pošiljka:</b><br>" + (type.toLowerCase().equals("package") ? "Paket" : "Pismo") + "<br>"
				+ (name == null ? "" : name) + "</div></html>";
	}
	
	public String toString(int mailLength) {
		
		return "<html><div style='text-align: center';><b>Pošiljka:</b><br>" + (type.toLowerCase().equals("package") ? "Paket" : "Pismo") + "<br>"
				+ (name == null ? "" : TextOperations.splitTextIntoRows(name, mailLength)) + (isFragile == true ? "<br>krhka" : "") + "</div></html>";
	}
}
