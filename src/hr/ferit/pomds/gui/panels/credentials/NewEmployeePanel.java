package hr.ferit.pomds.gui.panels.credentials;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import hr.ferit.pomds.db.UserDatabaseOperations;
import hr.ferit.pomds.gui.frames.EmployeesManagementFrame;
import hr.ferit.pomds.gui.frames.HomeFrame;
import hr.ferit.pomds.utils.ComponentDecorator;
import hr.ferit.pomds.utils.ConfiguredListeners;
import hr.ferit.pomds.utils.WindowSizeChecker;

public class NewEmployeePanel extends CredentialsInsertPanel {

	private static final long serialVersionUID = 7519104762752705135L;

	private EmployeesManagementFrame employeeManagementFrame;
	private String id;

	public NewEmployeePanel(EmployeesManagementFrame employeeManagementFrame, String id) {
		
		super();
		this.id = id;
		this.employeeManagementFrame = employeeManagementFrame;
		
		setOpaque(true);
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
				
				username.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.BLUE),
						BorderFactory.createEmptyBorder(3, 2, 3, 0)));
				password.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.BLUE),
						BorderFactory.createEmptyBorder(3, 2, 3, 0)));
				
				if(checkInputs() != 0) {
					return;
				}

				SwingUtilities.getWindowAncestor(NewEmployeePanel.this).setEnabled(false);
				
				new SwingWorker<Object, Object>() {
					
					private boolean successChecker = false;
					private String errorMessage = "Pogreška baze podataka.";
					private boolean deletedChecker = false;
					
					@Override
					protected Object doInBackground() throws Exception {

						try {
							if(UserDatabaseOperations.isUserAlreadyDeleted(id, "manager")) {
								deletedChecker = true;
								successChecker = true;
								return null;
							}
							UserDatabaseOperations.insertEmployee(username.getText(), String.valueOf(password.getPassword()));
							successChecker = true;
						} catch (SQLIntegrityConstraintViolationException e1) {
							errorMessage = "Korisničko ime već postoji.";
							SwingUtilities.invokeLater(new Runnable() {
								
								@Override
								public void run() {
									username.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.RED),
											BorderFactory.createEmptyBorder(3, 2, 3, 0)));
								}
							});
						} catch (SQLException e1) {}
						return null;
					}
					
					@Override
					protected void done() {
						
						SwingUtilities.getWindowAncestor(NewEmployeePanel.this).setEnabled(true);
						if(!successChecker) {
							JOptionPane.showMessageDialog(NewEmployeePanel.this, errorMessage, "Pogreška", JOptionPane.ERROR_MESSAGE);
							return;
						}
						if(deletedChecker) {
							SwingUtilities.getWindowAncestor(NewEmployeePanel.this).dispose();
							SwingUtilities.getWindowAncestor(NewEmployeePanel.this).getOwner().dispose();
							if(WindowSizeChecker.checkWindowSize(1400, 800) == 1) {
								new HomeFrame(1000, 600);
							}
							else {
								new HomeFrame(1000, 700);
							}
							return;
						}
						SwingUtilities.getWindowAncestor(NewEmployeePanel.this).dispose();
						employeeManagementFrame.readEmployeesFromDB();
					}
				}.execute(); 
			}
		});

		cancelButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				SwingUtilities.getWindowAncestor(NewEmployeePanel.this).dispose();	
			}
		});
	}
	
	private int checkInputs() {
		
		if(username.getText().isBlank() && password.getPassword().length == 0) {
			username.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.RED),
					BorderFactory.createEmptyBorder(3, 2, 3, 0)));
			password.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.RED),
					BorderFactory.createEmptyBorder(3, 2, 3, 0)));
			JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(NewEmployeePanel.this),
					"Unesite valjano korisničko ime i lozinku.", "Pogreška", JOptionPane.ERROR_MESSAGE);
			return 1;
		}
		else if(username.getText().isBlank()) {
			username.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.RED),
					BorderFactory.createEmptyBorder(3, 2, 3, 0)));
			JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(NewEmployeePanel.this),
					"Unesite valjano korisničko ime.", "Pogreška", JOptionPane.ERROR_MESSAGE);
			return 2;
		}
		else if(password.getPassword().length == 0) {
			password.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.RED),
					BorderFactory.createEmptyBorder(3, 2, 3, 0)));
			JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(NewEmployeePanel.this),
					"Unesite valjanu lozinku.", "Pogreška", JOptionPane.ERROR_MESSAGE);
			return 3;
		}
		else if(username.getText().length() > 30) {
			password.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.RED),
					BorderFactory.createEmptyBorder(3, 2, 3, 0)));
			JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(NewEmployeePanel.this),
					"Korisničko ime predugačko.", "Pogreška", JOptionPane.ERROR_MESSAGE);
			return 4;
		}
		return 0;
	}
}
