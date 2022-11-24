package hr.ferit.pomds.gui.panels.credentials;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import hr.ferit.pomds.db.UserDatabaseOperations;
import hr.ferit.pomds.gui.frames.HomeFrame;
import hr.ferit.pomds.utils.ComponentDecorator;
import hr.ferit.pomds.utils.WindowSizeChecker;

public class ConfirmPasswordPanel extends JPanel {

	private static final long serialVersionUID = 4174727523879510059L;
	
	private JLabel passwordLabel;
	private JPasswordField password;
	private JButton confirmButton;
	private String id;
	
	private GridBagConstraints gridBagConstraints = new GridBagConstraints();
	
	public ConfirmPasswordPanel(String id) {
		
		super();
		this.id = id;
		passwordLabel = new JLabel("Unesite lozinku:");
		password = new JPasswordField();
		confirmButton = new JButton("Potvrdi");
		
		setLayout(new GridBagLayout());
		setBackground(ComponentDecorator.getSecondaryBackgroundColor());
		configureComponents();
		addComponents();
	}

	private void configureComponents() {
		
		addListeners();
		
		ComponentDecorator.addDefaultColor(confirmButton);
		passwordLabel.setForeground(ComponentDecorator.getSecondaryTextColor());
		ComponentDecorator.setFont(new Font(null, Font.PLAIN, 16), passwordLabel, password);
		confirmButton.setFont(new Font(null, Font.PLAIN, 14));
		password.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.BLUE),
				BorderFactory.createEmptyBorder(3, 2, 3, 0)));
	}

	private void addListeners() {
		
		confirmButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {

				password.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.BLUE),
						BorderFactory.createEmptyBorder(3, 2, 3, 0)));
				if(password.getPassword().length == 0) {
					password.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.RED),
							BorderFactory.createEmptyBorder(3, 2, 3, 0)));
					JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(ConfirmPasswordPanel.this),
							"Unesite valjanu lozinku.", "Pogreška", JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				SwingUtilities.getWindowAncestor(ConfirmPasswordPanel.this).setEnabled(false);
				
				new SwingWorker<Object, Object>() {

					private boolean successChecker = false;
					private boolean verifyChecker = false;
					private boolean alreadyDeletedChecker = false;
					
					@Override
					protected Object doInBackground() throws Exception {


						if(UserDatabaseOperations.verifyUser("id", id, String.valueOf(password.getPassword()), "service_user") == null) {
							successChecker = true;
							return null;
						}
						verifyChecker = true;
						if(UserDatabaseOperations.deleteUserAccount(id, "service_user") == 1) {
							alreadyDeletedChecker = true;
						}
						successChecker = true;
						return null;
					}
					
					@Override
					protected void done() {

						SwingUtilities.getWindowAncestor(ConfirmPasswordPanel.this).setEnabled(true);
						if(!successChecker) {
							JOptionPane.showMessageDialog(ConfirmPasswordPanel.this, "Pogreška baze podataka.", "Pogreška", JOptionPane.ERROR_MESSAGE);
							return;
						}
						if(!verifyChecker) {
							password.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.RED),
									BorderFactory.createEmptyBorder(3, 2, 3, 0)));
							JOptionPane.showMessageDialog(ConfirmPasswordPanel.this, "Netočna lozinka.", "Pogreška", JOptionPane.ERROR_MESSAGE);
							return;
						}
						if(alreadyDeletedChecker) {
							JOptionPane.showMessageDialog(ConfirmPasswordPanel.this, "Korisnik je već izbrisan.",
									"Pogreška", JOptionPane.ERROR_MESSAGE);
						}
						SwingUtilities.getWindowAncestor(ConfirmPasswordPanel.this).dispose();
						SwingUtilities.getWindowAncestor(ConfirmPasswordPanel.this).getOwner().dispose();
						if(WindowSizeChecker.checkWindowSize(1400, 800) == 1) {
							new HomeFrame(1000, 700);
						}
						else {
							new HomeFrame(1000, 700);
						}
					}
				}.execute();
			}
		});
	}

	private void addComponents() {

		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.weightx = 0.7;
		gridBagConstraints.weighty = 1;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new Insets(10, 20, 0, 0);
		
		add(passwordLabel, gridBagConstraints);
		
		gridBagConstraints.gridy = 1;
		gridBagConstraints.ipady = 6;
		gridBagConstraints.insets = new Insets(0, 20, 10, 20);
		
		add(password, gridBagConstraints);
		
		gridBagConstraints.gridx = 1;
		gridBagConstraints.weightx = 0.3;
		gridBagConstraints.ipady = 8;
		gridBagConstraints.insets = new Insets(0, 0, 10, 20);
		
		add(confirmButton, gridBagConstraints);
	}
}
