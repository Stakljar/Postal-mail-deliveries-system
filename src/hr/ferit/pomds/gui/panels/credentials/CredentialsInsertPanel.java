package hr.ferit.pomds.gui.panels.credentials;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import hr.ferit.pomds.utils.ComponentDecorator;

public abstract class CredentialsInsertPanel extends JPanel {

	private static final long serialVersionUID = 941415543582312341L;
	
	protected JLabel usernameLabel;
	protected JLabel passwordLabel;
	protected JTextField username;
	protected JPasswordField password;
	protected JButton confirmButton;
	protected JButton cancelButton;
	
	private GridBagConstraints gridBagConstraints = new GridBagConstraints();
	
	public CredentialsInsertPanel() {
		
		super();
		usernameLabel = new JLabel("Korisničko ime:");
		passwordLabel = new JLabel("Lozinka:");
		username = new JTextField();
		password = new JPasswordField();
		confirmButton = new JButton("Potvrdi");
		cancelButton = new JButton("Odustani");
		
		setLayout(new GridBagLayout());
		setBackground(ComponentDecorator.getSecondaryBackgroundColor());
		setBorder(BorderFactory.createEmptyBorder(22, 23, 30, 23));
	}
	
	protected void configureComponents() {
		
		addListeners();
		
		ComponentDecorator.addDefaultColor(confirmButton, cancelButton);
		ComponentDecorator.setBackground(ComponentDecorator.getTernaryBackgroundColor(), username, password);
		ComponentDecorator.setForeground(ComponentDecorator.getSecondaryTextColor(), usernameLabel, passwordLabel);
		ComponentDecorator.setFont(new Font(null, Font.PLAIN, 16), usernameLabel, passwordLabel, username, password);
		ComponentDecorator.setFont(new Font(null, Font.PLAIN, 14), confirmButton, cancelButton);
		ComponentDecorator.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.BLUE),
				BorderFactory.createEmptyBorder(3, 2, 3, 0)), username, password);
	}
	
	protected abstract void addListeners();

	protected void addComponents() {
		
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.weightx = 1;
		gridBagConstraints.weighty = 1;
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new Insets(0, 0, 16, 0);
		
		add(usernameLabel, gridBagConstraints);
		
		gridBagConstraints.gridy = 1;
		gridBagConstraints.ipady = 6;
		
		add(username, gridBagConstraints);		
		
		gridBagConstraints.gridy = 2;
		gridBagConstraints.ipady = 0;
		
		add(passwordLabel, gridBagConstraints);
		
		gridBagConstraints.gridy = 3;
		gridBagConstraints.ipady = 6;
		
		add(password, gridBagConstraints);
		
		gridBagConstraints.gridy = 4;
		gridBagConstraints.gridwidth = 1;
		gridBagConstraints.ipady = 8;
		gridBagConstraints.insets = new Insets(0, 0, 0, 40);
		
		add(confirmButton, gridBagConstraints);
		
		gridBagConstraints.gridx = 1;
		gridBagConstraints.insets = new Insets(0, 0, 0, 0);
		
		add(cancelButton, gridBagConstraints);		
	}
}
