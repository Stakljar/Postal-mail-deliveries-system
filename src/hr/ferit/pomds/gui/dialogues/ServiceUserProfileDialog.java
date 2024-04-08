package hr.ferit.pomds.gui.dialogues;

import java.awt.Window;

import javax.swing.JDialog;
import javax.swing.JMenuBar;
import javax.swing.SwingUtilities;

import hr.ferit.pomds.gui.panels.service_user_information.ServiceUserProfilePanel;

public class ServiceUserProfileDialog extends JDialog {
	
	private static final long serialVersionUID = 5518283756872357821L;

	private ServiceUserProfilePanel serviceUserProfilePanel;
	private JMenuBar menuBar;
	
	public ServiceUserProfileDialog(Window owner, String title, ModalityType modalityType, int width, int height,
			ServiceUserProfilePanel serviceUserProfilePanel) {
		
		super(owner, title, modalityType);
		this.serviceUserProfilePanel = serviceUserProfilePanel;
		menuBar = new JMenuBar();
		
		addComponents();
		setJMenuBar(menuBar);
		configureWindow(width, height);
	}
	
	private void configureWindow(int width, int height) {
		
		setSize(width, height);
		setResizable(false);
		setLocationRelativeTo(SwingUtilities.getWindowAncestor(this));
		setVisible(true);
	}
	
	private void addComponents() {
		
		menuBar.add(serviceUserProfilePanel.getMenu());
		
		add(serviceUserProfilePanel);
	}
}
