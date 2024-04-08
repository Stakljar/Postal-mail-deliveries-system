package hr.ferit.pomds.data;

public record Town(String postalCode, Country country, String name) {

	@Override
	public String toString() {
		
		return name + " - " + postalCode;
	}
}
