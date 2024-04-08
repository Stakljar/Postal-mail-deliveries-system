package hr.ferit.pomds.gui.panels.credentials;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import hr.ferit.pomds.db.UserDatabaseOperations;
import hr.ferit.pomds.gui.frames.EmployeesManagementFrame;
import hr.ferit.pomds.gui.frames.HomeFrame;
import hr.ferit.pomds.utils.ComponentDecorator;
import hr.ferit.pomds.utils.ConfiguredListeners;
import hr.ferit.pomds.utils.WindowSizeChecker;

public class EditEmployeeUsernamePanel extends JPanel {

	private static final long serialVersionUID = 4005671858195014422L;

	private JLabel usernameChangeLabel;
	private JTextField usernameChange;
	private JButton confirmButton;
	private JButton cancelButton;
	private String employeeId;
	private String managerId;
	
	private GridBagConstraints gridBagConstraints = new GridBagConstraints();

	public EditEmployeeUsernamePanel(String employeeId, String username, String managerId) {
		
		super();
		this.managerId = managerId;
		this.employeeId = employeeId;
		usernameChangeLabel = new JLabel("Novo korisničko ime:");
		usernameChange = new JTextField(username);
		confirmButton = new JButton("Potvrdi");
		cancelButton = new JButton("Odustani");
		
		setLayout(new GridBagLayout());
		setBackground(ComponentDecorator.getSecondaryBackgroundColor());
		setBorder(BorderFactory.createEmptyBorder(22, 30, 20, 30));
		setOpaque(true);
		configureComponents();
		addComponents();
	}
	
	private void configureComponents() {
		
		addListeners();
		
		ComponentDecorator.addDefaultColor(confirmButton, cancelButton);
		usernameChangeLabel.setForeground(ComponentDecorator.getSecondaryTextColor());
		ComponentDecorator.setFont(new Font(null, Font.PLAIN, 14), confirmButton, cancelButton);
		usernameChangeLabel.setFont(new Font(null, Font.PLAIN, 20));
		usernameChange.setFont(new Font(null, Font.PLAIN, 16));
		usernameChange.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.BLUE),
				BorderFactory.createEmptyBorder(3, 2, 3, 0)));
	}

	private void addListeners() {
		
		ConfiguredListeners.addKeyListenerForFocusRequest(usernameChange, null, null, confirmButton, null);
		ConfiguredListeners.addKeyListenerForFocusRequest(confirmButton, usernameChange, cancelButton, null, null);
		ConfiguredListeners.addKeyListenerForFocusRequest(cancelButton, usernameChange, null, null, confirmButton);
		
		ConfiguredListeners.addMouseListenerForHandCursor(confirmButton);
		ConfiguredListeners.addMouseListenerForHandCursor(cancelButton);
		
		confirmButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				usernameChange.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.BLUE),
						BorderFactory.createEmptyBorder(3, 2, 3, 0)));

				if(checkInputs() != 0) {
					return;
				}
				
				SwingUtilities.getWindowAncestor(EditEmployeeUsernamePanel.this).setEnabled(false);
				
				new SwingWorker<Object, Object>() {

					private boolean successChecker = false;
					private boolean employeeDeletedChecker = false;
					private boolean deletedChecker = false;

					@Override
					protected Object doInBackground() throws Exception {

						try {
							if(UserDatabaseOperations.isUserAlreadyDeleted(managerId, "manager")) {
								deletedChecker = true;
								successChecker = true;
								return null;
							}
							if(UserDatabaseOperations.updateUsername(employeeId, usernameChange.getText(), "employee") == 1) {
								employeeDeletedChecker = true;
							}
							successChecker = true;
						} catch (SQLException e) {}
						return null;
					}
					
					@Override
					protected void done() {
						
						SwingUtilities.getWindowAncestor(EditEmployeeUsernamePanel.this).setEnabled(true);
						SwingUtilities.getWindowAncestor(EditEmployeeUsernamePanel.this).toFront();
						if(successChecker == false) {
							JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(EditEmployeeUsernamePanel.this),
									"Pogreška prilikom mijenjanja podataka.", "Pogreška", JOptionPane.ERROR_MESSAGE);
							return;
						}
						if(deletedChecker == true) {
							SwingUtilities.getWindowAncestor(EditEmployeeUsernamePanel.this).dispose();
							SwingUtilities.getWindowAncestor(EditEmployeeUsernamePanel.this).getOwner().dispose();
							if(WindowSizeChecker.checkWindowSize(1400, 800) == 1) {
								new HomeFrame(1000, 600);
							}
							else {
								new HomeFrame(1000, 700);
							}
							return;
						}
						SwingUtilities.getWindowAncestor(EditEmployeeUsernamePanel.this).dispose();	
						if(employeeDeletedChecker == true) {
							JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(EditEmployeeUsernamePanel.this),
									"Korisnik je izbrisan.", "Informacija", JOptionPane.INFORMATION_MESSAGE);
						}
						((EmployeesManagementFrame) SwingUtilities.getWindowAncestor(EditEmployeeUsernamePanel.this).getOwner())
							.readEmployeesFromDB();
					}
				}.execute();
			}
		});
		
		cancelButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				SwingUtilities.getWindowAncestor(EditEmployeeUsernamePanel.this).dispose();	
			}
		});
	}
	
	private int checkInputs() {
		
		if(usernameChange.getText().isBlank()) {
			usernameChange.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.RED),
					BorderFactory.createEmptyBorder(3, 2, 3, 0)));
			JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(EditEmployeeUsernamePanel.this),
					"Unesite valjano korisničko ime.", "Pogreška", JOptionPane.ERROR_MESSAGE);
			return 1;
		}
		else if(usernameChange.getText().length() > 30) {
			usernameChange.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.RED),
					BorderFactory.createEmptyBorder(3, 2, 3, 0)));
			JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(EditEmployeeUsernamePanel.this),
					"Korisničko ime predugačko.", "Pogreška", JOptionPane.ERROR_MESSAGE);
			return 2;
		}
		return 0;
	}
	
	private void addComponents() {
		
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.weightx = 1;
		gridBagConstraints.weighty = 1;
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new Insets(0, 0, 16, 0);
		
		add(usernameChangeLabel, gridBagConstraints);
		
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.ipady = 6;
		
		add(usernameChange, gridBagConstraints);		
		
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.gridwidth = 1;
		gridBagConstraints.insets = new Insets(0, 0, 0, 40);
		
		add(confirmButton, gridBagConstraints);
		
		gridBagConstraints.gridx = 1;
		gridBagConstraints.insets = new Insets(0, 0, 0, 0);
		
		add(cancelButton, gridBagConstraints);		
	}
}
