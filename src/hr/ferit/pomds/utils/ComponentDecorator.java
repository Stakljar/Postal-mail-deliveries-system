package hr.ferit.pomds.utils;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import javax.swing.JComponent;
import javax.swing.border.Border;

public class ComponentDecorator {

	private static Color defaultBackgroundColor = Color.BLUE;
	private static Color defaultForegroundColor = Color.BLUE;
	
	private static Color primaryTextColor = new Color(0, 5, 173);
	private static Color secondaryTextColor =  new Color(2, 40, 210);
	private static Color ternaryTextColor = new Color(0, 8, 82);
	
	private static Color primaryBackgroundColor  = new Color(240, 240, 255);
	private static Color secondaryBackgroundColor = new Color(189, 213, 252);
	private static Color ternaryBackgroundColor = new Color(232, 238, 252);
	
	private static Color primaryBorderColor = new Color(0, 18, 117);
	
	public static void setDefaultBackgroundColor(Color color) {
		
		defaultBackgroundColor = color;
	}
	
	public static void setDefaultForegroundColor(Color color) {
		
		defaultForegroundColor = color;
	}
	
	public static Color getPrimaryTextColor() {
		
		return primaryTextColor;
	}
	
	public static Color getSecondaryTextColor() {
		
		return secondaryTextColor;
	}
	
	public static Color getTernaryTextColor() {
		
		return ternaryTextColor;
	}
	
	public static Color getPrimaryBackgroundColor() {
		
		return primaryBackgroundColor;
	}
	
	public static Color getSecondaryBackgroundColor() {
		
		return secondaryBackgroundColor;
	}
	
	public static Color getTernaryBackgroundColor() {
		
		return ternaryBackgroundColor;
	}
	
	public static Color getPrimaryBorderColor() {
		
		return primaryBorderColor;
	}
	
	public static void addDefaultColor(Component... components) {
		
		for(Component component: components) {
			
			component.setBackground(defaultBackgroundColor);
			component.setForeground(defaultForegroundColor);
			
		}
	}
	
	public static void setForeground(Color color, Component... components) {
		
		for(Component component: components) {
			component.setForeground(color);
		}
	}
	
	public static void setBackground(Color color, Component... components) {
		
		for(Component component: components) {
			component.setBackground(color);
		}
	}
	
	public static void setFont(Font font, Component... components) {
		
		for(Component component: components) {
			component.setFont(font);
		}
	}
	
	public static void setBorder(Border border, JComponent... components) {
		
		for(JComponent component: components) {
			component.setBorder(border);
		}
	}
}
