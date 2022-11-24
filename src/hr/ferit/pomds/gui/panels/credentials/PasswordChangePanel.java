package hr.ferit.pomds.gui.panels.credentials;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.regex.Pattern;

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
import hr.ferit.pomds.utils.ConfiguredListeners;
import hr.ferit.pomds.utils.UserType;
import hr.ferit.pomds.utils.WindowSizeChecker;

public class PasswordChangePanel extends JPanel {

private static final long serialVersionUID = 3747179501045132351L;
	
	private JLabel oldPasswordLabel;
	private JLabel newPasswordLabel;
	private JLabel newPasswordRepeatLabel;
	private JPasswordField oldPassword;
	private JPasswordField newPassword;
	private JPasswordField newPasswordAgain;
	private JButton confirmButton;
	private JButton cancelButton;
	
	private GridBagConstraints gridBagConstraints = new GridBagConstraints();
	private UserType userType;
	private String id;
	
	public PasswordChangePanel(UserType userType, String id) {
		
		super();
		oldPasswordLabel = new JLabel("Stara lozinka:");
		newPasswordLabel = new JLabel("Nova lozinka:");
		newPasswordRepeatLabel = new JLabel("Ponovite novu lozinku:");
		oldPassword = new JPasswordField();
		newPasswordAgain = new JPasswordField();
		newPassword = new JPasswordField();
		confirmButton = new JButton("Potvrdi");
		cancelButton = new JButton("Odustani");
		this.userType = userType;
		this.id = id;
		
		setLayout(new GridBagLayout());
		setBackground(ComponentDecorator.getSecondaryBackgroundColor());
		setBorder(BorderFactory.createEmptyBorder(22, 23, 30, 23));
		addComponents();
		configureComponents();
	}
	
	private void configureComponents() {
		
		addListeners();
		
		ComponentDecorator.addDefaultColor(new JButton(), confirmButton, cancelButton);
		ComponentDecorator.setForeground(ComponentDecorator.getSecondaryTextColor(), oldPasswordLabel, newPasswordRepeatLabel, newPasswordLabel);
		ComponentDecorator.setBackground(ComponentDecorator.getTernaryBackgroundColor(), oldPassword, newPasswordAgain, newPassword);
		ComponentDecorator.setFont(new Font(null, Font.PLAIN, 16), oldPasswordLabel, newPasswordRepeatLabel, newPasswordLabel,
				oldPassword, newPasswordAgain, newPassword);
		ComponentDecorator.setFont(new Font(null, Font.PLAIN, 14), confirmButton, cancelButton);
		ComponentDecorator.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.BLUE),
				BorderFactory.createEmptyBorder(3, 2, 3, 0)), oldPassword, newPasswordAgain, newPassword);
	}
	
	private void addListeners() {
		
		ConfiguredListeners.addMouseListenerForHandCursor(confirmButton);
		ConfiguredListeners.addMouseListenerForHandCursor(cancelButton);
		
		ConfiguredListeners.addFocusListenerForColorChange(oldPassword, Color.WHITE, ComponentDecorator.getTernaryBackgroundColor());
		ConfiguredListeners.addFocusListenerForColorChange(newPassword, Color.WHITE, ComponentDecorator.getTernaryBackgroundColor());
		ConfiguredListeners.addFocusListenerForColorChange(newPasswordAgain, Color.WHITE, ComponentDecorator.getTernaryBackgroundColor());
		
		ConfiguredListeners.addKeyListenerForFocusRequest(oldPassword, null, null, newPassword, null);
		ConfiguredListeners.addKeyListenerForFocusRequest(newPassword, oldPassword, null, newPasswordAgain, null);
		ConfiguredListeners.addKeyListenerForFocusRequest(newPasswordAgain, newPassword, null, confirmButton, null);
		ConfiguredListeners.addKeyListenerForFocusRequest(confirmButton, newPasswordAgain, cancelButton, null, null);
		ConfiguredListeners.addKeyListenerForFocusRequest(cancelButton, newPasswordAgain, null, null, confirmButton);

		confirmButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				ComponentDecorator.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.BLUE),
						BorderFactory.createEmptyBorder(3, 2, 3, 0)), oldPassword, newPasswordAgain, newPassword);
				
				if(checkInputs() != 0) {
					return;
				}
				
				SwingUtilities.getWindowAncestor(PasswordChangePanel.this).setEnabled(false);
				
				new SwingWorker<Object, Object>() {
					
					private boolean successChecker = false;
					private boolean verifyChecker = false;
					private boolean deletedChecker = false;
					private String table = "";
					
					@Override
					protected Object doInBackground() throws Exception {

						table = userType == UserType.MANAGER ? "manager" : userType == UserType.EMPLOYEE ? "employee" : "service_user";
						try {
							if(UserDatabaseOperations.isUserAlreadyDeleted(id, table)) {
								deletedChecker = true;
								successChecker = true;
								return null;
							}
							if(UserDatabaseOperations.verifyUser("id", id, String.valueOf(oldPassword.getPassword()), table) == null) {
								successChecker = true;
								return null;
							}
							verifyChecker = true;
							UserDatabaseOperations.changePassword(table, id, String.valueOf(newPassword.getPassword()));
							successChecker = true;
						} catch (SQLException e1) {}
						return null;
					}
					
					@Override
					protected void done() {
						
						SwingUtilities.getWindowAncestor(PasswordChangePanel.this).setEnabled(true);
						if(!successChecker) {
							JOptionPane.showMessageDialog(PasswordChangePanel.this, "Pogreška baze podataka.", "Pogreška", JOptionPane.ERROR_MESSAGE);
							return;
						}
						if(deletedChecker) {
							SwingUtilities.getWindowAncestor(PasswordChangePanel.this).dispose();
							SwingUtilities.getWindowAncestor(PasswordChangePanel.this).getOwner().dispose();
							if(WindowSizeChecker.checkWindowSize(1400, 800) == 1) {
								new HomeFrame(1000, 600);
							}
							else {
								new HomeFrame(1000, 700);
							}
							return;
						}
						if(!verifyChecker) {
							oldPassword.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.RED),
									BorderFactory.createEmptyBorder(3, 2, 3, 0)));
							JOptionPane.showMessageDialog(PasswordChangePanel.this, "Netočna lozinka.", "Pogreška", JOptionPane.ERROR_MESSAGE);
							return;
						}
						SwingUtilities.getWindowAncestor(PasswordChangePanel.this).dispose();
						JOptionPane.showMessageDialog(PasswordChangePanel.this, "Lozinka uspješno promijenjena.",
								"Informacija", JOptionPane.INFORMATION_MESSAGE);
						return;
					}
				}.execute(); 
			}
		});
		
		cancelButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				SwingUtilities.getWindowAncestor(PasswordChangePanel.this).dispose();
				
			}
		});
	}
	
	private int checkInputs() {
		
		if(oldPassword.getPassword().length == 0) {
			oldPassword.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.RED),
					BorderFactory.createEmptyBorder(3, 2, 3, 0)));
			JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(PasswordChangePanel.this),
					"Unesite valjanu lozinku.", "Pogreška", JOptionPane.ERROR_MESSAGE);
			return 1;
		}
		else if(newPassword.getPassword().length == 0) {
			newPassword.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.RED),
					BorderFactory.createEmptyBorder(3, 2, 3, 0)));
			JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(PasswordChangePanel.this),
					"Unesite valjanu lozinku.", "Pogreška", JOptionPane.ERROR_MESSAGE);
			return 2;
		}
		else if(newPassword.getPassword().length < 8 || !Pattern.compile("\\d+").matcher(String.valueOf(newPassword.getPassword())).find() ||
				!Pattern.compile("[a-zA-Z]+").matcher(String.valueOf(newPassword.getPassword())).find()) {
			newPassword.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.RED),
					BorderFactory.createEmptyBorder(3, 2, 3, 0)));
			JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(PasswordChangePanel.this),
					"Nova lozinka mora imati najmanje 8 znakova uključujući 1 slovo i 1 broj.",
					"Pogreška", JOptionPane.ERROR_MESSAGE);
			return 4;
		}
		else if(!String.valueOf(newPassword.getPassword()).equals(String.valueOf(newPasswordAgain.getPassword()))) {
			ComponentDecorator.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.RED),
					BorderFactory.createEmptyBorder(3, 2, 3, 0)), newPassword, newPasswordAgain);
			JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(PasswordChangePanel.this),
					"Lozinke se moraju podudarati.", "Lozinka", JOptionPane.ERROR_MESSAGE);
			return 5;
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
		
		add(oldPasswordLabel, gridBagConstraints);
		
		gridBagConstraints.gridy = 1;
		gridBagConstraints.ipady = 6;
		
		add(oldPassword, gridBagConstraints);		
		
		gridBagConstraints.gridy = 2;
		gridBagConstraints.ipady = 0;
		
		add(newPasswordLabel, gridBagConstraints);
		
		gridBagConstraints.gridy = 3;
		gridBagConstraints.ipady = 6;
		
		add(newPassword, gridBagConstraints);
	
		gridBagConstraints.gridy = 4;
		gridBagConstraints.ipady = 0;
		
		add(newPasswordRepeatLabel, gridBagConstraints);
		
		gridBagConstraints.gridy = 5;
		gridBagConstraints.ipady = 6;
		
		add(newPasswordAgain, gridBagConstraints);
		
		gridBagConstraints.gridy = 6;
		gridBagConstraints.gridwidth = 1;
		gridBagConstraints.ipady = 10;
		gridBagConstraints.insets = new Insets(0, 0, 0, 40);
		
		add(confirmButton, gridBagConstraints);
		
		gridBagConstraints.gridx = 1;
		gridBagConstraints.insets = new Insets(0, 0, 0, 0);
		
		add(cancelButton, gridBagConstraints);		
	}
}
