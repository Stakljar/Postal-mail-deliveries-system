package hr.ferit.pomds.gui.panels.credentials;

import java.awt.Color;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import hr.ferit.pomds.gui.dialogues.DefaultDialog;
import hr.ferit.pomds.gui.panels.service_user_information.ServiceUserRegistrationInfoPanel;
import hr.ferit.pomds.utils.ComponentDecorator;
import hr.ferit.pomds.utils.ConfiguredListeners;
import hr.ferit.pomds.utils.WindowSizeChecker;

public class RegistrationPanel extends CredentialsInsertPanel {

	private static final long serialVersionUID = 7951057129410024012L;
	
	public RegistrationPanel() {
		
		super();
		
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
					
				SwingUtilities.getWindowAncestor(RegistrationPanel.this).dispose();
				if(WindowSizeChecker.checkWindowSize(1400, 800) == 1) {
					new DefaultDialog(SwingUtilities.getWindowAncestor(RegistrationPanel.this), "Podaci korisnika usluge",
							ModalityType.APPLICATION_MODAL, 800, 340, new ServiceUserRegistrationInfoPanel(username.getText(), String.valueOf(password.getPassword())));
				}
				else {
					new DefaultDialog(SwingUtilities.getWindowAncestor(RegistrationPanel.this), "Podaci korisnika usluge",
							ModalityType.APPLICATION_MODAL, 900, 380, new ServiceUserRegistrationInfoPanel(username.getText(), String.valueOf(password.getPassword())));
				}
			}
		});
		
		cancelButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				SwingUtilities.getWindowAncestor(RegistrationPanel.this).dispose();
			}
		});
		
	}
	
	private int checkInputs() {
		
		if(username.getText().isBlank() && password.getPassword().length == 0) {
			ComponentDecorator.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.RED),
					BorderFactory.createEmptyBorder(3, 2, 3, 0)), username, password);
			JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(RegistrationPanel.this),
					"Unesite valjano korisničko ime ili lozinku.", "Pogreška", JOptionPane.ERROR_MESSAGE);
			return 1;
		}
		else if(username.getText().isBlank()) {
			username.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.RED),
					BorderFactory.createEmptyBorder(3, 2, 3, 0)));
			JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(RegistrationPanel.this),
					"Unesite valjano korisničko ime.", "Pogreška", JOptionPane.ERROR_MESSAGE);
			return 2;
		}
		else if(username.getText().length() > 30) {
			username.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.RED),
					BorderFactory.createEmptyBorder(3, 2, 3, 0)));
			JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(RegistrationPanel.this),
					"Korisničko ime predugačko.", "Pogreška", JOptionPane.ERROR_MESSAGE);
			return 3;
		}
		else if(password.getPassword().length == 0) {
			password.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.RED),
					BorderFactory.createEmptyBorder(3, 2, 3, 0)));
			JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(RegistrationPanel.this),
					"Unesite valjanu lozinku.", "Pogreška", JOptionPane.ERROR_MESSAGE);
			return 4;
		}
		else if(password.getPassword().length < 8 || !Pattern.compile("\\d+").matcher(String.valueOf(password.getPassword())).find() ||
				!Pattern.compile("[a-zA-Z]+").matcher(String.valueOf(password.getPassword())).find()) {
			password.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.RED),
					BorderFactory.createEmptyBorder(3, 2, 3, 0)));
			JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(RegistrationPanel.this),
					"Lozinka mora imati najmanje 8 znakova uključujući 1 slovo i 1 broj.",
					"Pogreška", JOptionPane.ERROR_MESSAGE);
			return 5;
		}
		return 0;
	}
}
