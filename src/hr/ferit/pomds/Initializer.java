package hr.ferit.pomds;

import java.awt.Color;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import hr.ferit.pomds.db.DatabaseConnectionSetup;
import hr.ferit.pomds.gui.frames.HomeFrame;
import hr.ferit.pomds.utils.ComponentDecorator;
import hr.ferit.pomds.utils.WindowSizeChecker;

public class Initializer {

	public static void main(String[] args) {
		
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				try {
					UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
				} catch (ClassNotFoundException e) {
					JOptionPane.showMessageDialog(null, "Pogreška prilikom učitavanja izgleda i doživljaja.", "Pogreška", JOptionPane.ERROR_MESSAGE);
					ComponentDecorator.setDefaultBackgroundColor(new Color(48, 71, 227));
					ComponentDecorator.setDefaultForegroundColor(Color.WHITE);
				} catch (InstantiationException e) {
					JOptionPane.showMessageDialog(null, "Pogreška prilikom učitavanja izgleda i doživljaja.", "Pogreška", JOptionPane.ERROR_MESSAGE);
					ComponentDecorator.setDefaultBackgroundColor(new Color(48, 71, 227));
					ComponentDecorator.setDefaultForegroundColor(Color.WHITE);
				} catch (IllegalAccessException e) {
					JOptionPane.showMessageDialog(null, "Pogreška prilikom učitavanja izgleda i doživljaja.", "Pogreška", JOptionPane.ERROR_MESSAGE);
					ComponentDecorator.setDefaultBackgroundColor(new Color(48, 71, 227));
					ComponentDecorator.setDefaultForegroundColor(Color.WHITE);
				} catch (UnsupportedLookAndFeelException e) {
					JOptionPane.showMessageDialog(null, "Pogreška prilikom učitavanja izgleda i doživljaja.", "Pogreška", JOptionPane.ERROR_MESSAGE);
					ComponentDecorator.setDefaultBackgroundColor(new Color(48, 71, 227));
					ComponentDecorator.setDefaultForegroundColor(Color.WHITE);
				}
				try {
					DatabaseConnectionSetup.setUpConnectionParameters("root", "daloq412", "localhost", "3306", "mail_delivery");
				} catch (ClassNotFoundException e) {
					JOptionPane.showMessageDialog(null, "Pogreška pronalaženja JDBC pogonskog programa.", "Pogreška", JOptionPane.ERROR_MESSAGE);
				}
				if(WindowSizeChecker.checkWindowSize(1400, 800) == 1) {
					new HomeFrame(1000, 600);
				}
				else {
					new HomeFrame(1000, 700);
				}
			}
		});
	}
}
