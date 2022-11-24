package hr.ferit.pomds.gui.panels.service_user_information;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.LinkedList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import hr.ferit.pomds.data.Country;
import hr.ferit.pomds.data.ServiceUser;
import hr.ferit.pomds.data.Town;
import hr.ferit.pomds.db.UserDatabaseOperations;
import hr.ferit.pomds.gui.frames.HomeFrame;
import hr.ferit.pomds.utils.ComponentDecorator;
import hr.ferit.pomds.utils.ComponentStateChanger;
import hr.ferit.pomds.utils.ConfiguredListeners;
import hr.ferit.pomds.utils.WindowSizeChecker;
import hr.ferit.pomds.utils.format_check.EmptySpaceChecker;
import hr.ferit.pomds.utils.format_check.FormatChecker;
import hr.ferit.pomds.utils.format_check.LimitChecker;

public class ServiceUserProfilePanel extends ServiceUserInfoPanel {

	private static final long serialVersionUID = 774238560523145624L;

	private JLabel usernameLabel;
	private JLabel informationLabel;
	
	private JTextField username;
	private JMenu menu;
	private JMenuItem edit;
	
	private ServiceUser serviceUser;
	
	public ServiceUserProfilePanel(ServiceUser serviceUser) {
		
		super();
		this.serviceUser = serviceUser;
		
		menu = new JMenu("Opcije");
		edit = new JMenuItem("Uredi");
		usernameLabel = new JLabel("Korisničko ime:");
		informationLabel = new JLabel("Podaci:"); 
		username = new JTextField(serviceUser.username());
		
		firstName = new JTextField(serviceUser.firstName());
		lastName = new JTextField(serviceUser.lastName());
		address = new JTextField(serviceUser.address());
		
		setLayout(new GridLayout(10, 3, 20, 2));
		
		loadCountries();
		configureComponents();
		addComponents();
	}

	public JMenu getMenu() {
	
		return menu;
	}
	
	protected void configureComponents() {
		
		super.configureComponents();
		
		try {
			countriesBox.setSelectedItem(countries.stream().filter(it -> it.alphaTwoCode()
					.equals(serviceUser.town().postalCode().substring(0, 2))).findFirst().orElse(null));
			loadCities();
		} catch (NullPointerException e) {
			countriesBox.setSelectedItem(null);
		}
		
		addListeners();
		
		ComponentDecorator.addDefaultColor(confirmButton, citiesBox, countriesBox);
		ComponentDecorator.setForeground(ComponentDecorator.getPrimaryTextColor(), usernameLabel, informationLabel, firstNameLabel,
				lastNameLabel, addressLabel, citiesLabel, countriesLabel);
		ComponentDecorator.setFont(new Font(null, Font.PLAIN, 14), confirmButton, countriesBox);
		ComponentDecorator.setFont(new Font(null, Font.PLAIN, 16), firstNameLabel,
				lastNameLabel, addressLabel, citiesLabel, countriesLabel, username, firstName, lastName,
				address, citiesBox, countriesBox);
		ComponentDecorator.setFont(new Font(null, Font.BOLD, 18), usernameLabel, informationLabel);
		ComponentDecorator.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.BLUE),
				BorderFactory.createEmptyBorder(0, 4, 0, 0)), username, firstName, lastName,
				address, citiesBox, countriesBox);
		
		ComponentStateChanger.setEnabled(false, username, firstName, lastName, address, citiesBox, countriesBox);
		confirmButton.setVisible(false);
	}
	
	@Override
	protected void loadCountries() {
		
		menu.setEnabled(false);
		
		new SwingWorker<Object, Object>(){

			List<Country> countries = new LinkedList<>();
			private boolean successChecker = false;
			
			@Override
			protected Object doInBackground() throws Exception {
				
				try {
					countries = UserDatabaseOperations.getAllCountries();
					successChecker = true;
				} catch (SQLException e) {}
				return null;
			}
			
			@Override
			protected void done() {
				
				if(!successChecker) {
					JOptionPane.showMessageDialog(ServiceUserProfilePanel.this, "Pogreška prilikom učitavanja država.", "Pogreška",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				for (Country country : countries) {
					countriesBox.addItem(country);
				}
				try {
					countriesBox.setSelectedItem(countries.stream().filter(it -> it.alphaTwoCode()
							.equals(serviceUser.town().country().alphaTwoCode())).findFirst().orElse(null));
				} catch (NullPointerException e) {
					countriesBox.setSelectedItem(null);
				}
				loadCities();
			}
		}.execute();
	}
	
	@Override
	protected void loadCities() {
		
		new SwingWorker<Object, Object>(){

			private boolean successChecker = false;
			
			@Override
			protected Object doInBackground() throws Exception {
				
				try {
					allCities = UserDatabaseOperations.getAllCities();
					successChecker = true;
				} catch (SQLException e) {}
				return null;
			}
			
			@Override
			protected void done() {
				
				menu.setEnabled(true);
				if(!successChecker) {
					JOptionPane.showMessageDialog(ServiceUserProfilePanel.this, "Pogreška prilikom učitavanja mjesta.", "Pogreška",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				fillCityBox();
				citiesBox.setSelectedItem(allCities.stream().filter(it -> it
						.equals(serviceUser.town())).findFirst().orElse(null));
			}
		}.execute();
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
		
		edit.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				ComponentStateChanger.setEnabled(true, username, firstName, lastName, address, citiesBox, countriesBox);
				confirmButton.setVisible(true);
			}
		});
		
		confirmButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				ComponentDecorator.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.BLUE),
						BorderFactory.createEmptyBorder(0, 4, 0, 0)), username, firstName, lastName,
						address);
				
				FormatChecker formatChecker = new EmptySpaceChecker(new LimitChecker(new Integer[] {30, 20, 30, 60}));
				if(!formatChecker.isFormatCorrect(ServiceUserProfilePanel.this, username, firstName, lastName, address)) {
					return;
				}
				
				SwingUtilities.getWindowAncestor(ServiceUserProfilePanel.this).setEnabled(false);
				
				new SwingWorker<Object, Object>() {

					private boolean successChecker = false;
					private boolean deletedChecker = false;
					private String errorMessage = "Pogreška prilikom ažuriranja podataka.";
					
					@Override
					protected Object doInBackground() throws Exception {
						
							try {
								if(UserDatabaseOperations.isUserAlreadyDeleted(serviceUser.id(), "service_user")) {
									deletedChecker = true;
									successChecker = true;
									return null;
								}
								UserDatabaseOperations.updateServiceUserInformation(serviceUser.id(), username.getText(),
										firstName.getText(), lastName.getText(), address.getText(),
										citiesBox.getSelectedItem() == null ? null : ((Town) citiesBox.getSelectedItem()).postalCode(),
												citiesBox.getSelectedItem() == null ? null :((Town) citiesBox.getSelectedItem()).country().alphaTwoCode(),
										citiesBox.getSelectedItem() == null ? null :((Town) citiesBox.getSelectedItem()).name());
								successChecker = true;
							} 
							catch (SQLIntegrityConstraintViolationException e1) {
								if(e1.getMessage().equals("Cannot add or update a child row: a foreign key constraint fails")) {
									errorMessage = "Mjesto ili država nedostaje iz baze podataka.";
								} else {
									errorMessage = "Korisničko ime već postoji.";
								}
							} catch (SQLException e1) {}
						return null;
					}
					
					@Override
					protected void done() {
						
						SwingUtilities.getWindowAncestor(ServiceUserProfilePanel.this).setEnabled(true);
						if(!successChecker) {
							JOptionPane.showMessageDialog(ServiceUserProfilePanel.this, errorMessage, "Pogreška", JOptionPane.ERROR_MESSAGE);
							return;
						}
						if(deletedChecker) {
							SwingUtilities.getWindowAncestor(ServiceUserProfilePanel.this).dispose();
							SwingUtilities.getWindowAncestor(ServiceUserProfilePanel.this).getOwner().dispose();
							if(WindowSizeChecker.checkWindowSize(1400, 800) == 1) {
								new HomeFrame(1000, 600);
							}
							else {
								new HomeFrame(1000, 700);
							}
							return;
						}
						if(citiesBox.getSelectedItem() == null) {
							countriesBox.setSelectedItem(null);
						}
						ComponentStateChanger.setEnabled(false, username, firstName, lastName, address, citiesBox, countriesBox);
						confirmButton.setVisible(false);
						JOptionPane.showMessageDialog(ServiceUserProfilePanel.this, "Podaci su uspješno izmijenjeni.", "Informacija",
								JOptionPane.INFORMATION_MESSAGE);
					}
				}.execute();
			}
		});
	}
	
	protected void addComponents() {
		
		menu.add(edit);
		
		add(usernameLabel);
		add(new JLabel());
		add(new JLabel());
		add(username);
		add(new JLabel());
		add(new JLabel());
		add(new JLabel());
		add(new JLabel());
		add(new JLabel());
		add(informationLabel);
		add(new JLabel());
		add(new JLabel());
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
		add(new JLabel());
		add(new JLabel());
		add(confirmButton);
	}
}