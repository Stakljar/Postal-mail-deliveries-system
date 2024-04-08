package hr.ferit.pomds.data;

public record Country(String alphaTwoCode, String name) {

	@Override
	public String toString() {
		
		return name;
	}
}
