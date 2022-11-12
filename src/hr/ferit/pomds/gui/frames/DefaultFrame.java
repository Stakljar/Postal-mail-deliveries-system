package hr.ferit.pomds.gui.frames;

import java.awt.Dimension;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

public class DefaultFrame extends JFrame {

	private static final long serialVersionUID = 7414145069342141216L;
	
	public DefaultFrame() {
		
		super();
	}
	
	public DefaultFrame(String title) {
		
		super(title);
	}
	
	protected void configureWindow(int width, int height, int minWidth, int minHeight, int operation) {
		
		setSize(width, height);
		setIconImage(new ImageIcon("resources\\images\\icon.png").getImage());
		setMinimumSize(new Dimension(minWidth, minHeight));
		setLocationRelativeTo(null);
		setDefaultCloseOperation(operation);
		setResizable(true);
		setVisible(true);
	}
}
