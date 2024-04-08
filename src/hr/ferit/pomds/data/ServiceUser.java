package hr.ferit.pomds.data;

import hr.ferit.pomds.utils.TextOperations;

public record ServiceUser(String id, String username, String firstName, String lastName, String address, Town town) {

	@Override
	public String toString() {
		
		return "<html><b>" + username + "</b><br>" + firstName + " " + lastName + "<br>" + address + ", " + 
				town.name() + " " + town.postalCode() + ", " + town.country() + "</html>";
	}
	
	public String toString(int addressLength) {
		
		return "<html><b>" + username + "</b><br>" + firstName + " " + lastName +"<br>" + 
				TextOperations.splitTextIntoRows(address, addressLength) + "<br>" + 
				town.name() + " " + town.postalCode() + ",<br>" + town.country() + "</html>";
	}
	
	public String toString(int lastNameLength, int addressLength, String alignment) {
		
		return "<html><div style='text-align: " + alignment + "';><b>" + username + "</b><br>" + firstName + " " + TextOperations.splitTextIntoRows(lastName, lastNameLength) + "<br>" + 
				TextOperations.splitTextIntoRows(address, addressLength) +  "<br>" + 
				town.name() + " " + town.postalCode() + ",<br>" + town.country() + "</div></html>";
	}
}
