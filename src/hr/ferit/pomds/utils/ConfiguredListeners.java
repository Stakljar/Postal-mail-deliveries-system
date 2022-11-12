package hr.ferit.pomds.utils;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.FocusEvent.Cause;

import javax.swing.JComponent;

public class ConfiguredListeners {
	
	public static void addKeyListenerForFocusRequest(JComponent component, JComponent up, 
			JComponent right, JComponent down, JComponent left) {
		
		component.addKeyListener(new KeyAdapter() {
			
			public void keyPressed(KeyEvent e) {
				
				try {
					if(e.getKeyCode() == KeyEvent.VK_UP) {
						up.requestFocusInWindow(Cause.TRAVERSAL_UP);
					}
					else if(e.getKeyCode() == KeyEvent.VK_RIGHT) {
						right.requestFocusInWindow(Cause.TRAVERSAL_FORWARD);
					}
					else if(e.getKeyCode() == KeyEvent.VK_DOWN) {
						down.requestFocusInWindow(Cause.TRAVERSAL_DOWN);
					}
					else if(e.getKeyCode() == KeyEvent.VK_LEFT) {
						left.requestFocusInWindow(Cause.TRAVERSAL_BACKWARD);
					}
				}
				catch(NullPointerException e1) {}
			}
		});
	}
	
	public static void addMouseListenerForHandCursor(JComponent component) {
		
		component.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseEntered(MouseEvent e) {
				component.setCursor(new Cursor(Cursor.HAND_CURSOR));
				
			}

		});
	}
	
	public static void addFocusListenerForColorChange(JComponent component, Color focused, Color notFocused) {
		
		component.addFocusListener(new FocusListener() {
			
			@Override
			public void focusLost(FocusEvent e) {
				component.setBackground(notFocused);
			}
			
			@Override
			public void focusGained(FocusEvent e) {
				component.setBackground(focused);
			}
		});
	}
}