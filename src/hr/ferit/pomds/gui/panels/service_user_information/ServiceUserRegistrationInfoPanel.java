package hr.ferit.pomds.gui.panels.service_user_information;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import hr.ferit.pomds.data.Town;
import hr.ferit.pomds.db.UserDatabaseOperations;
import hr.ferit.pomds.gui.dialogues.DefaultDialog;
import hr.ferit.pomds.gui.frames.ServiceUserFrame;
import hr.ferit.pomds.gui.panels.credentials.RegistrationPanel;
import hr.ferit.pomds.utils.ComponentDecorator;
import hr.ferit.pomds.utils.ConfiguredListeners;
import hr.ferit.pomds.utils.WindowSizeChecker;
import hr.ferit.pomds.utils.format_check.EmptySpaceChecker;
import hr.ferit.pomds.utils.format_check.FormatChecker;
import hr.ferit.pomds.utils.format_check.LimitChecker;

public class ServiceUserRegistrationInfoPanel extends ServiceUserInfoPanel {

	private static final long serialVersionUID = 1952378951589853218L;
	
	private JButton backButton;
	private String username;
	private String password;

	public ServiceUserRegistrationInfoPanel(String username, String password) {
		
		super();
		this.username = username;
		this.password = password;
		backButton = new JButton("Nazad");
		
		firstName = new JTextField();
		lastName = new JTextField();
		address = new JTextField();
		
		setLayout(new GridLayout(6, 3, 20, 2));
		loadCountries();
		loadCities();
		configureComponents();
		addComponents();
	}
	
	protected void configureComponents() {
		
		addListeners();
		
		super.configureComponents();
		ComponentDecorator.addDefaultColor(confirmButton, backButton, citiesBox, countriesBox);
		ComponentDecorator.setForeground(ComponentDecorator.getPrimaryTextColor(), firstNameLabel, lastNameLabel, addressLabel,
				citiesLabel, countriesLabel);
		ComponentDecorator.setFont(new Font(null, Font.PLAIN, 14), confirmButton, backButton);
		ComponentDecorator.setFont(new Font(null, Font.PLAIN, 16), firstNameLabel, lastNameLabel, addressLabel,
				citiesLabel, countriesLabel, firstName, lastName, address, citiesBox, countriesBox);
		ComponentDecorator.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.BLUE),
				BorderFactory.createEmptyBorder(0, 4, 0, 0)), firstName, lastName, address, citiesBox, countriesBox);
	}
	
	private void addListeners() {
		
		ConfiguredListeners.addKeyListenerForFocusRequest(firstName, null, lastName, address, null);
		ConfiguredListeners.addKeyListenerForFocusRequest(lastName, null, null, null, firstName);
		ConfiguredListeners.addKeyListenerForFocusRequest(address, firstName, citiesBox, null, null);

		ConfiguredListeners.addMouseListenerForHandCursor(confirmButton);
		
		countriesBox.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				
				if(e.getStateChange() == ItemEvent.DESELECTED) {
					return;
				}
				fillCityBox();
			}
		});
		
		backButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {

				SwingUtilities.getWindowAncestor(ServiceUserRegistrationInfoPanel.this).dispose();
				if(WindowSizeChecker.checkWindowSize(1400, 800) == 1) {
					new DefaultDialog(SwingUtilities.getWindowAncestor(ServiceUserRegistrationInfoPanel.this).getOwner().getOwner(),
							"Registration", ModalityType.APPLICATION_MODAL, 400, 280, new RegistrationPanel());
				}
				else {
					new DefaultDialog(SwingUtilities.getWindowAncestor(ServiceUserRegistrationInfoPanel.this).getOwner().getOwner(),
							"Registration", ModalityType.APPLICATION_MODAL, 400, 300, new RegistrationPanel());
				}	
			}
		});
		
		confirmButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				ComponentDecorator.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.BLUE),
						BorderFactory.createEmptyBorder(0, 4, 0, 0)), firstName, lastName,
						address, citiesBox);
				
				FormatChecker formatChecker = new EmptySpaceChecker(new LimitChecker(new Integer[] {20, 30, 60}));
				if(formatChecker.isFormatCorrect(ServiceUserRegistrationInfoPanel.this, firstName, lastName, address) == false) {
					return;
				}
				
				SwingUtilities.getWindowAncestor(ServiceUserRegistrationInfoPanel.this).setEnabled(false);
				
				new SwingWorker<Object, Object>() {

					private boolean successChecker = false;
					private boolean integrityConstraintChecker = false;
					private String errorMessage = "Pogreška prilikom unošenja podataka.";
					private String id = null;
					
					@Override
					protected Object doInBackground() throws Exception {

						try {
							id = UserDatabaseOperations.registerServiceUser(username, password, firstName.getText(), lastName.getText(),
									address.getText(), citiesBox.getSelectedItem() == null ? null :
										((Town) citiesBox.getSelectedItem()).postalCode(), citiesBox.getSelectedItem() == null ? null :
											((Town) citiesBox.getSelectedItem()).country().alphaTwoCode(),  citiesBox.getSelectedItem() == null ? null :
										((Town) citiesBox.getSelectedItem()).name());
							successChecker = true;
						}
						catch (SQLIntegrityConstraintViolationException e1) {
							if(e1.getMessage().equals("Cannot add or update a child row: a foreign key constraint fails")) {
								errorMessage = "Mjesto ili država nedostaje iz baze podataka.";
							} else {
								errorMessage = "Korisničko ime već postoji.";
							}
							integrityConstraintChecker = true;
						}
						catch (SQLException e1) {}
						return null;
					}
					
					@Override
					protected void done() {
						
						SwingUtilities.getWindowAncestor(ServiceUserRegistrationInfoPanel.this).setEnabled(true);
						SwingUtilities.getWindowAncestor(ServiceUserRegistrationInfoPanel.this).toFront();
						if(integrityConstraintChecker) {
							JOptionPane.showMessageDialog(ServiceUserRegistrationInfoPanel.this, errorMessage, "Pogreška", JOptionPane.ERROR_MESSAGE);
							SwingUtilities.getWindowAncestor(ServiceUserRegistrationInfoPanel.this).dispose();
							if(WindowSizeChecker.checkWindowSize(1400, 800) == 1) {
								new DefaultDialog(SwingUtilities.getWindowAncestor(ServiceUserRegistrationInfoPanel.this).getOwner().getOwner(),
										"Registracija", ModalityType.APPLICATION_MODAL, 400, 280, new RegistrationPanel());
							}
							else {
								new DefaultDialog(SwingUtilities.getWindowAncestor(ServiceUserRegistrationInfoPanel.this).getOwner().getOwner(),
										"Registracija", ModalityType.APPLICATION_MODAL, 400, 300, new RegistrationPanel());
							}	
							return;
						}
						if(!successChecker) {
							JOptionPane.showMessageDialog(ServiceUserRegistrationInfoPanel.this, errorMessage, "Pogreška", JOptionPane.ERROR_MESSAGE);
							return;
						}
						SwingUtilities.getWindowAncestor(ServiceUserRegistrationInfoPanel.this).dispose();
						SwingUtilities.getWindowAncestor(ServiceUserRegistrationInfoPanel.this).getOwner().getOwner().dispose();
						if(WindowSizeChecker.checkWindowSize(1400, 800) == 1) {
							new ServiceUserFrame(1300, 700, id, username);
						}
						else {
							new ServiceUserFrame(1300, 800, id, username);
						}
					}
				}.execute();
			}
		});
	}
	
	protected void addComponents() {

		add(firstNameLabel);
		add(lastNameLabel);
		add(new JLabel());
		add(firstName);
		add(lastName);
		add(new JLabel());
		add(addressLabel);
		add(citiesLabel);
		add(countriesLabel);
		add(address);
		add(citiesBox);
		add(countriesBox);
		add(new JLabel());
		add(new JLabel());
		add(new JLabel());
		add(backButton);
		add(new JLabel());
		add(confirmButton);
	}
}