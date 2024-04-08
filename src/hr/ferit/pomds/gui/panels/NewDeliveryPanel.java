package hr.ferit.pomds.gui.panels;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import hr.ferit.pomds.data.ServiceUser;
import hr.ferit.pomds.db.DeliveryDatabaseOperations;
import hr.ferit.pomds.db.UserDatabaseOperations;
import hr.ferit.pomds.gui.frames.HomeFrame;
import hr.ferit.pomds.gui.frames.ServiceUserFrame;
import hr.ferit.pomds.utils.ComponentDecorator;
import hr.ferit.pomds.utils.WindowSizeChecker;

public class NewDeliveryPanel extends JPanel {

	private static final long serialVersionUID = 1043752398612414657L;
	
	private JLabel mailLabel;
	private JLabel mailTypeLabel;
	private JLabel mailNameLabel;
	private JLabel fragileMailLabel;
	private JLabel filterLabel;
	private JLabel recipientLabel;
	
	private JComboBox<String> mailType;
	private JTextField mailName;
	private JComboBox<String> fragileMail;
	
	private JTextField recipientSearch;
	private JButton searchButton;
	private JComboBox<String> filter;
	private DefaultListModel<ServiceUser> defaultListModel;
	private JList<ServiceUser> recipients;
	private JScrollPane scrollPane;
	private JButton sendButton;
	private String id;
	
	private GridBagConstraints gridBagConstraints = new GridBagConstraints();
	
	public NewDeliveryPanel(String id) {
		
		super();
		this.id = id;
		mailLabel = new JLabel("Pošiljka:");
		mailTypeLabel = new JLabel("Tip pošiljke:");
		mailNameLabel = new JLabel("Naziv pošiljke:");
		fragileMailLabel = new JLabel("Krhka:");
		
		mailType = new JComboBox<>(new String[] {"Pismo", "Paket"});
		mailName = new JTextField();
		fragileMail = new JComboBox<>(new String[] {"Ne", "Da"});
		
		recipientLabel = new JLabel("Primatelj:");
		filterLabel = new JLabel("Filtiraj po:");
		filter = new JComboBox<>(new String[] {"Korisničko ime", "Ime i prezime", "Adresa"});
		
		recipientSearch = new JTextField();
		searchButton = new JButton("Pronađi primatelja");
		sendButton = new JButton("Pošalji");
		defaultListModel = new DefaultListModel<>();
		recipients = new JList<>(defaultListModel);
		scrollPane = new JScrollPane(recipients);
		
		setLayout(new GridBagLayout());
		setOpaque(true);
		setBackground(ComponentDecorator.getPrimaryBackgroundColor());
		setBorder(BorderFactory.createCompoundBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40),
				BorderFactory.createLineBorder(ComponentDecorator.getPrimaryBorderColor())), BorderFactory.createEmptyBorder(20, 20, 20, 20)));
		addComponents();
		configureComponents();
	}

	private void configureComponents() {
		
		addListeners();
		
		changeAvailability(false);
		mailName.setPreferredSize(new Dimension(100, 26));
		recipientSearch.setPreferredSize(new Dimension(100, 26));
		scrollPane.setOpaque(true);
		scrollPane.setPreferredSize(new Dimension(100, 100));
		recipients.setFixedCellHeight(80);
		recipients.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 2));
		
		mailType.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(mailType.getSelectedIndex() == 1) {
					changeAvailability(true);
				}
				else {
					changeAvailability(false);
				}
			}
		});

		ComponentDecorator.addDefaultColor(sendButton, searchButton, mailType, fragileMail, filter);
		ComponentDecorator.setForeground(ComponentDecorator.getPrimaryTextColor(), mailLabel, mailTypeLabel, mailNameLabel, fragileMailLabel,
				filterLabel, recipientLabel);
		ComponentDecorator.setFont(new Font(null, Font.PLAIN, 16), mailTypeLabel, mailNameLabel, fragileMailLabel, filterLabel,
				mailName, recipientSearch, sendButton, searchButton, mailType, fragileMail, filter, recipients);
		ComponentDecorator.setFont(new Font(null, Font.BOLD, 18), mailLabel, recipientLabel);
		recipients.setFont(new Font(null, Font.PLAIN, 16));
		ComponentDecorator.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.BLUE),
				BorderFactory.createEmptyBorder(0, 4, 0, 0)), mailType, fragileMail, filter, mailName, recipientSearch);
		
	}
	
	private void changeAvailability(boolean value) {
		
		mailName.setEnabled(value);
		fragileMail.setEnabled(value);
		if(!value) {
			mailName.setText(null);
			fragileMail.setSelectedItem("Ne");
		}
	}

	private void addListeners() {
	
		searchButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				searchButton.setEnabled(false);
				sendButton.setEnabled(false);
				recipientSearch.setEnabled(false);
				filter.setEnabled(false);
				
				new SwingWorker<Object, Object>(){
					
					private boolean deletedChecker = false;
					private boolean successChecker = false;
					private List<ServiceUser> serviceUsers = new LinkedList<>();
					
					@Override
					protected Object doInBackground() throws Exception {
						try {
							if(UserDatabaseOperations.isUserAlreadyDeleted(id, "service_user")) {
								deletedChecker = true;
								successChecker = true;
								return null;
							}
							String criteria = filter.getSelectedItem() == "Korisničko ime" ? "username" : 
								filter.getSelectedItem() == "Ime i prezime" ? "CONCAT(first_name, ' ', last_name)" :
									"CONCAT(address, ' ', town.name, ' ', town.postal_code, ', ', country.country_name)";
							serviceUsers = UserDatabaseOperations.getServiceUsers(id, criteria, recipientSearch.getText().toLowerCase());
							successChecker = true;
						} catch (SQLException e1) {}
						return null;	
					}
					
					@Override
					protected void done() {
						
						searchButton.setEnabled(true);
						sendButton.setEnabled(true);
						recipientSearch.setEnabled(true);
						filter.setEnabled(true);
						
						defaultListModel.clear();
						if(!successChecker) {
							JOptionPane.showMessageDialog(SwingUtilities.windowForComponent(NewDeliveryPanel.this),
									"Pogreška baze podataka.", "Pogreška", JOptionPane.ERROR_MESSAGE);
							return;
						}
						if(deletedChecker) {
							SwingUtilities.getWindowAncestor(NewDeliveryPanel.this).dispose();
							SwingUtilities.getWindowAncestor(NewDeliveryPanel.this).getOwner().dispose();
							if(WindowSizeChecker.checkWindowSize(1400, 800) == 1) {
								new HomeFrame(1000, 600);
							}
							else {
								new HomeFrame(1000, 700);
							}
							return;
						}
						for(ServiceUser serviceUser: serviceUsers) {
							defaultListModel.addElement(serviceUser);
						}
					}
					
				}.execute();
				
			}
		});
		
		sendButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				mailName.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.BLUE),
						BorderFactory.createEmptyBorder(3, 2, 3, 0)));
				
				if(checkInputs() != 0) {
					return;
				}
				
				SwingUtilities.getWindowAncestor(NewDeliveryPanel.this).setEnabled(false);
				
				new SwingWorker<Object, Object>(){
					
					private boolean deletedChecker = false;
					private boolean successChecker = false;
					
					@Override
					protected Object doInBackground() throws Exception {
						try {
							if(UserDatabaseOperations.isUserAlreadyDeleted(id, "service_user")) {
								deletedChecker = true;
								successChecker = true;
								return null;
							}
							DeliveryDatabaseOperations.insertDelivery(id, recipients.getSelectedValue().id(), mailType.getSelectedItem() == "Pismo" ? "letter" : "package",
									mailType.getSelectedItem() == "Pismo" ? null : mailName.getText(), fragileMail.getSelectedItem() == "Ne" ? false : true);
							successChecker = true;
						} catch (SQLException e1) {}
						return null;	
					}
					
					@Override
					protected void done() {
						
						SwingUtilities.getWindowAncestor(NewDeliveryPanel.this).setEnabled(true);
						if(!successChecker) {
							JOptionPane.showMessageDialog(SwingUtilities.windowForComponent(NewDeliveryPanel.this),
									"Pogreška baze podataka.", "Pogreška", JOptionPane.ERROR_MESSAGE);
							return;
						}
						if(deletedChecker) {
							SwingUtilities.getWindowAncestor(NewDeliveryPanel.this).dispose();
							SwingUtilities.getWindowAncestor(NewDeliveryPanel.this).getOwner().dispose();
							if(WindowSizeChecker.checkWindowSize(1400, 800) == 1) {
								new HomeFrame(1000, 600);
							}
							else {
								new HomeFrame(1000, 700);
							}
							return;
						}
						SwingUtilities.getWindowAncestor(NewDeliveryPanel.this).dispose();
						((ServiceUserFrame) SwingUtilities.getWindowAncestor(NewDeliveryPanel.this).getOwner()).readDeliveriesFromDB();
						JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(NewDeliveryPanel.this).getOwner(),
								"Zahtjev za dostavu poslan.", "Informacija", JOptionPane.INFORMATION_MESSAGE);
					}
				}.execute();
			}
		});
	}
	
	private int checkInputs() {
		
		if(recipients.isSelectionEmpty()) {
			JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(this),
					"Odaberite primatelja.", "Pogreška", JOptionPane.ERROR_MESSAGE);
			return 1;
		}
		else if(mailName.getText().isBlank() && mailType.getSelectedItem() == "Package") {
			mailName.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.RED),
					BorderFactory.createEmptyBorder(3, 2, 3, 0)));
			JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(this),
					"Unesite valjano ime pošiljke.", "Pogreška", JOptionPane.ERROR_MESSAGE);
			return 2;
		}
		else if(mailName.getText().length() > 50) {
			mailName.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.RED),
					BorderFactory.createEmptyBorder(3, 2, 3, 0)));
			JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(this),
					"Ime pošiljke predugačko.", "Pogreška", JOptionPane.ERROR_MESSAGE);
			return 3;
		}
		return 0;
	}

	private void addComponents() {

		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.weightx = 0.25;
		gridBagConstraints.weighty = 0.02;
		gridBagConstraints.ipady = 4;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new Insets(2, 20, 2, 20);
		
		add(mailLabel, gridBagConstraints);
		
		gridBagConstraints.gridy = 1;
		
		add(mailTypeLabel, gridBagConstraints);
		
		gridBagConstraints.gridx = 1;
		gridBagConstraints.weightx = 0.5;
		
		add(mailNameLabel, gridBagConstraints);
		
		gridBagConstraints.gridx = 2;
		gridBagConstraints.weightx = 0.25;
		
		add(fragileMailLabel, gridBagConstraints);
		
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 2;
		
		add(mailType, gridBagConstraints);
		
		gridBagConstraints.gridx = 1;
		gridBagConstraints.ipady = 6;
		
		add(mailName, gridBagConstraints);
		
		gridBagConstraints.gridx = 2;
		gridBagConstraints.ipady = 4;
		
		add(fragileMail, gridBagConstraints);
		
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 3;
		gridBagConstraints.insets = new Insets(40, 20, 2, 20);
		
		add(recipientLabel, gridBagConstraints);
		
		gridBagConstraints.gridy = 4;
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.ipady = 6;
		gridBagConstraints.insets = new Insets(2, 20, 2, 20);
		
		add(recipientSearch, gridBagConstraints);
		
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridwidth = 1;
		gridBagConstraints.ipady = 4;
		
		add(searchButton, gridBagConstraints);
		
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 5;
		
		add(filterLabel, gridBagConstraints);
		
		gridBagConstraints.gridy = 6;
		
		add(filter, gridBagConstraints);
		
		gridBagConstraints.gridy = 7;
		gridBagConstraints.weighty = 0.84;
		gridBagConstraints.gridwidth = 3;
		gridBagConstraints.ipady = 0;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.insets = new Insets(12, 20, 12, 20);

		add(scrollPane, gridBagConstraints);
		
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = 8;
		gridBagConstraints.weighty = 0.03;
		gridBagConstraints.gridwidth = 1;
		gridBagConstraints.ipady = 4;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new Insets(2, 20, 2, 20);
		
		add(sendButton, gridBagConstraints);
	}
}
