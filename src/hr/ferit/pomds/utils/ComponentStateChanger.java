package hr.ferit.pomds.utils;

import javax.swing.JComponent;

public class ComponentStateChanger {

	public static void setEnabled(Boolean value, JComponent... components) {
		
		for (JComponent component : components) {
			component.setEnabled(value);
		}
	}
}
