package hr.ferit.pomds.gui.panels.credentials;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import hr.ferit.pomds.db.UserDatabaseOperations;
import hr.ferit.pomds.gui.frames.DeliveriesManagementFrame;
import hr.ferit.pomds.gui.frames.EmployeesManagementFrame;
import hr.ferit.pomds.gui.frames.ServiceUserFrame;
import hr.ferit.pomds.utils.ComponentDecorator;
import hr.ferit.pomds.utils.ConfiguredListeners;
import hr.ferit.pomds.utils.UserType;
import hr.ferit.pomds.utils.WindowSizeChecker;

public class LoginPanel extends CredentialsInsertPanel {

	private static final long serialVersionUID = 7438543125451241221L;
	
	private UserType userType;
	
	public LoginPanel(UserType userType) {
		
		super();
		this.userType = userType;
		
		configureComponents();
		addComponents();
	}
	
	protected void addListeners() {
		
		ConfiguredListeners.addMouseListenerForHandCursor(confirmButton);
		ConfiguredListeners.addMouseListenerForHandCursor(cancelButton);
		
		ConfiguredListeners.addFocusListenerForColorChange(username, Color.WHITE, ComponentDecorator.getTernaryBackgroundColor());
		ConfiguredListeners.addFocusListenerForColorChange(password, Color.WHITE, ComponentDecorator.getTernaryBackgroundColor());
		
		ConfiguredListeners.addKeyListenerForFocusRequest(username, null, null, password, null);
		ConfiguredListeners.addKeyListenerForFocusRequest(password, username, null, confirmButton, null);
		ConfiguredListeners.addKeyListenerForFocusRequest(confirmButton, password, cancelButton, null, null);
		ConfiguredListeners.addKeyListenerForFocusRequest(cancelButton, password, null, null, confirmButton);

		confirmButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				ComponentDecorator.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.BLUE),
						BorderFactory.createEmptyBorder(3, 2, 3, 0)), username, password);
				
				if(checkInputs() != 0) {
					return;
				}
				
				SwingUtilities.getWindowAncestor(LoginPanel.this).setEnabled(false);
				
				new SwingWorker<Object, Object>() {
					
					private boolean verifyChecker = false;
					private boolean successChecker = false;
					private String id = null;
					
					@Override
					protected Object doInBackground() throws Exception {

						try {
							if((id = UserDatabaseOperations.verifyUser("username", username.getText(), String.valueOf(password.getPassword()),
									userType == UserType.EMPLOYEE ? "employee" : userType == UserType.MANAGER ? "manager" : "service_user"))
									!= null) {
								verifyChecker = true;
							}
							successChecker = true;
						} catch (SQLException e) {}
						return null;
					}
					
					@Override
					protected void done() {
						
						SwingUtilities.getWindowAncestor(LoginPanel.this).setEnabled(true);
						if(!successChecker) {
							JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(LoginPanel.this),
									"Pogreška baze podataka.", "Prijava", JOptionPane.ERROR_MESSAGE);
							return;
						}
						if(!verifyChecker) {
							username.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.RED),
									BorderFactory.createEmptyBorder(3, 2, 3, 0)));
							password.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.RED),
									BorderFactory.createEmptyBorder(3, 2, 3, 0)));
							JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(LoginPanel.this),
									"Netočno korisničko ime ili lozinka.", "Prijava", JOptionPane.ERROR_MESSAGE);
							return;
						}
						SwingUtilities.getWindowAncestor(LoginPanel.this).dispose();
						SwingUtilities.getWindowAncestor(LoginPanel.this).getOwner().dispose();
						if(WindowSizeChecker.checkWindowSize(1400, 800) == 1) {
							if(userType == UserType.EMPLOYEE) {
								new DeliveriesManagementFrame(1320, 700, id, username.getText());
							}
							else if(userType == UserType.MANAGER){
								new EmployeesManagementFrame(600, 700, id, username.getText());
							}
							else {
								new ServiceUserFrame(1300, 700, id, username.getText());
							}
						}
						else {
							if(userType == UserType.EMPLOYEE) {
								new DeliveriesManagementFrame(1320, 800, id, username.getText());
							}
							else if(userType == UserType.MANAGER){
								new EmployeesManagementFrame(600, 800, id, username.getText());
							}
							else {
								new ServiceUserFrame(1300, 800, id, username.getText());
							}
						}
					}
				}.execute(); 
			}
		});
		
		cancelButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				SwingUtilities.getWindowAncestor(LoginPanel.this).dispose();
				
			}
		});
		
	}
	
	private int checkInputs() {
		
		if(username.getText().isBlank() && password.getPassword().length == 0) {
			ComponentDecorator.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.RED),
					BorderFactory.createEmptyBorder(3, 2, 3, 0)), username, password);
			JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(LoginPanel.this),
					"Unesite valjano korisničko ime i lozinku.", "Pogreška", JOptionPane.ERROR_MESSAGE);
			return 1;
		}
		else if(username.getText().isBlank()) {
			username.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.RED),
					BorderFactory.createEmptyBorder(3, 2, 3, 0)));
			JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(LoginPanel.this),
					"Unesite valjano korisničko ime.", "Pogreška", JOptionPane.ERROR_MESSAGE);
			return 2;
		}
		else if(password.getPassword().length == 0) {
			password.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.RED),
					BorderFactory.createEmptyBorder(3, 2, 3, 0)));
			JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(LoginPanel.this),
					"Unesite valjanu lozinku.", "Pogreška", JOptionPane.ERROR_MESSAGE);
			return 3;
		}
		return 0;
	}
}