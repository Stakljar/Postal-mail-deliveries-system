package hr.ferit.pomds.gui.dialogues;

import java.awt.Window;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class DefaultDialog extends JDialog {

	private static final long serialVersionUID = 7587577645071247041L;

	private JPanel panel;
	
	public DefaultDialog(Window owner, String title, ModalityType modalityType, int width, int height,
			JPanel panel) {
		
		super(owner, title, modalityType);
		this.panel = panel;
		
		addComponents();
		configureWindow(width, height);
	}
	
	private void configureWindow(int width, int height) {
		
		setSize(width, height);
		setResizable(false);
		setLocationRelativeTo(SwingUtilities.getWindowAncestor(this));
		setVisible(true);
	}
	
	private void addComponents() {
		
		add(panel);
	}
}
