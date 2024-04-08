package hr.ferit.pomds.gui.frames;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;

import hr.ferit.pomds.gui.panels.HomePanel;

public class HomeFrame extends DefaultFrame {

	private static final long serialVersionUID = 4719581924814295721L;
	
	private JPanel loginPanel;

	public HomeFrame(int width, int height) {
		
		super("Sustav dostava poštanskih pošiljki");
		loginPanel = new HomePanel("resources\\images\\background.jpg");
	
		configureWindow(width, height);
		addComponents();
	}
	
	private void configureWindow(int width, int height) {
		
		setSize(width, height);
		setIconImage(new ImageIcon("resources\\images\\icon.png").getImage());
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		setVisible(true);
	}
	
	private void addComponents() {
		  
		add(loginPanel);
	}
}