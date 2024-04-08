package hr.ferit.pomds.gui.frames;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.Timer;

import hr.ferit.pomds.data.Delivery;
import hr.ferit.pomds.data.ServiceUser;
import hr.ferit.pomds.db.DeliveryDatabaseOperations;
import hr.ferit.pomds.db.UserDatabaseOperations;
import hr.ferit.pomds.gui.dialogues.DefaultDialog;
import hr.ferit.pomds.gui.dialogues.ServiceUserProfileDialog;
import hr.ferit.pomds.gui.panels.NewDeliveryPanel;
import hr.ferit.pomds.gui.panels.credentials.ConfirmPasswordPanel;
import hr.ferit.pomds.gui.panels.credentials.PasswordChangePanel;
import hr.ferit.pomds.gui.panels.scrollable.UserDeliveriesSequencePanel;
import hr.ferit.pomds.gui.panels.service_user_information.ServiceUserProfilePanel;
import hr.ferit.pomds.utils.ComponentDecorator;
import hr.ferit.pomds.utils.UserType;
import hr.ferit.pomds.utils.WindowSizeChecker;

public class ServiceUserFrame extends DefaultFrame {
	
	private static final long serialVersionUID = 7319150505184159852L;
	
	private UserDeliveriesSequencePanel userDeliveriesPanel;
	private JMenuBar menuBar;
	private JMenu menu;
	private JMenuItem profileOption;
	private JMenuItem passwordChangeOption;
	private JMenuItem deleteOption;
	private JMenuItem logoutOption;
	private JButton newDeliveryButton;
	private String id;
	private Timer timer;
	
	private GridBagConstraints gridBagConstraints = new GridBagConstraints();

	public ServiceUserFrame(int width, int height, String id, String username) {
		
		super("Sustav dostava poštanskih pošiljki - trenutna sesija: korisnik usluge (" + username + ")");
		this.id = id;
		userDeliveriesPanel = new UserDeliveriesSequencePanel(id);
		menuBar = new JMenuBar();
		menu = new JMenu("Račun");
		profileOption = new JMenuItem("Profil");
		passwordChangeOption = new JMenuItem("Promjena lozinke");
		deleteOption = new JMenuItem("Brisanje računa");
		logoutOption = new JMenuItem("Odjava");
		newDeliveryButton= new JButton("Slanje novog zahtjeva za dostavu");
		timer = new Timer(5 * 60 * 1000, new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				readDeliveriesFromDB();
			}
		});
		
		setLayout(new GridBagLayout());
		setJMenuBar(menuBar);
		getContentPane().setBackground(ComponentDecorator.getPrimaryBackgroundColor());
		configureWindow(width, height, 1200, 700, JFrame.EXIT_ON_CLOSE);
		configureComponents();
		addComponents();
		readDeliveriesFromDB();
	}
	
	private void configureComponents() {
		
		addListeners();
		
		userDeliveriesPanel.setPreferredSize(new Dimension(100, 100));
		deleteOption.setForeground(Color.RED);
		ComponentDecorator.addDefaultColor(newDeliveryButton);
		newDeliveryButton.setFont(new Font(null, Font.PLAIN, 18));
		
		timer.setRepeats(true);
		timer.start();
	}
	
	private void addListeners() {
		
		newDeliveryButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if(WindowSizeChecker.checkWindowSize(1400, 800) == 1) {
					new DefaultDialog(ServiceUserFrame.this, "Novi zahtjev za dostavu",
							ModalityType.APPLICATION_MODAL, 1000, 720, new NewDeliveryPanel(id));
				}
				else {
					new DefaultDialog(ServiceUserFrame.this, "Novi zahtjev za dostavu",
							ModalityType.APPLICATION_MODAL, 1100, 820, new NewDeliveryPanel(id));
				}
			}
		});
		
		profileOption.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				menu.setEnabled(false);
				newDeliveryButton.setEnabled(false);
				
				new SwingWorker<Object, Object>(){

					private ServiceUser serviceUser;
					private boolean successChecker = false;
					private boolean deletedChecker = false;
					
					@Override
					protected Object doInBackground() throws Exception {

						try {
							if(UserDatabaseOperations.isUserAlreadyDeleted(id, "service_user")) {
								deletedChecker = true;
								successChecker = true;
								return null;
							}
							serviceUser = UserDatabaseOperations.getServiceUser(id);
							successChecker = true;
						} catch (SQLException e) {e.printStackTrace();}
						return null;
					}
					
					@Override
					protected void done() {
						
						menu.setEnabled(true);
						newDeliveryButton.setEnabled(true);
						if(!successChecker) {
							JOptionPane.showMessageDialog(ServiceUserFrame.this,
									"Pogreška baze podataka.", "Pogreška", JOptionPane.ERROR_MESSAGE);
							return;
						}
						if(deletedChecker) {
							ServiceUserFrame.this.dispose();
							if(WindowSizeChecker.checkWindowSize(1400, 800) == 1) {
								new HomeFrame(1000, 600);
							}
							else {
								new HomeFrame(1000, 700);
							}
							return;
						}
						if(WindowSizeChecker.checkWindowSize(1400, 800) == 1) {
							new ServiceUserProfileDialog(ServiceUserFrame.this, "Profil",
									ModalityType.APPLICATION_MODAL, 800, 480, new ServiceUserProfilePanel(serviceUser));
						}
						else {
							new ServiceUserProfileDialog(ServiceUserFrame.this, "Profil",
									ModalityType.APPLICATION_MODAL, 900, 540, new ServiceUserProfilePanel(serviceUser));
						}
					}
					
				}.execute();
			}
		});
		
		passwordChangeOption.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if(WindowSizeChecker.checkWindowSize(1400, 800) == 1) {
					new DefaultDialog(ServiceUserFrame.this, "Izmjena lozinke", ModalityType.APPLICATION_MODAL,
							400, 360, new PasswordChangePanel(UserType.SERVICE_USER, id));
				}
				else {
					new DefaultDialog(ServiceUserFrame.this, "Izmjena lozinke", ModalityType.APPLICATION_MODAL,
							400, 400, new PasswordChangePanel(UserType.SERVICE_USER, id));
				}
			}
		});
		
		deleteOption.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if(JOptionPane.showOptionDialog(ServiceUserFrame.this,
						"Želite li uistinu izbrisati ovaj račun?",
						"Brisanje računa", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null,
						new String[] {"      Da      ", "      Ne      "}, null) != 0) {
					return;
				}
				if(WindowSizeChecker.checkWindowSize(1400, 800) == 1) {
					new DefaultDialog(ServiceUserFrame.this, "Potvrda lozinke", ModalityType.APPLICATION_MODAL,
							400, 120, new ConfirmPasswordPanel(id));
				}
				else {
					new DefaultDialog(ServiceUserFrame.this, "Potvrda lozinke", ModalityType.APPLICATION_MODAL,
							400, 140, new ConfirmPasswordPanel(id));
				}
			}
		});
		
		logoutOption.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				ServiceUserFrame.this.dispose();
				if(WindowSizeChecker.checkWindowSize(1400, 800) == 1) {
					new HomeFrame(1000, 600);
				}
				else {
					new HomeFrame(1000, 700);
				}
			}
		});
		
		addWindowListener(new WindowAdapter() {
			
			@Override
			public void windowClosed(WindowEvent e) {
				
				timer.stop();
			}
		});
	}
	
	public void readDeliveriesFromDB() {
		
		userDeliveriesPanel.clearSubPanel();
		userDeliveriesPanel.changeLoadingVisibility(true);
		
		new SwingWorker<Object, Object>() {

			private boolean successChecker = false;
			private List<Delivery> userDeliveries = new LinkedList<>();
			
			@Override
			protected Object doInBackground() throws Exception {

				try {
					userDeliveries =  DeliveryDatabaseOperations.getUserDeliveries(id);
					successChecker = true;
				}
				catch (Exception e) {}
				return null;
			}
			
			@Override
			protected void done() {
				
				if(!successChecker) {
					JOptionPane.showMessageDialog(ServiceUserFrame.this, "Pogreška prilikom učitavanja podataka.", "Pogreška",
							JOptionPane.ERROR_MESSAGE);
				}
				userDeliveriesPanel.fillSubPanel(userDeliveries);
			}
		}.execute();
	}

	private void addComponents() {
		
		menu.add(profileOption);
		menu.add(passwordChangeOption);
		menu.add(deleteOption);
		menu.add(logoutOption);
		menuBar.add(menu);
		
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.weightx = 1;
		gridBagConstraints.weighty = 0.025;
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		gridBagConstraints.insets = new Insets(4, 24, 4, 0);
		
		add(newDeliveryButton, gridBagConstraints);
		
		gridBagConstraints.gridy = 1;
		gridBagConstraints.weighty = 0.975;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.anchor = GridBagConstraints.CENTER;
		gridBagConstraints.insets = new Insets(0, 0, 0, 0);
		
		add(userDeliveriesPanel, gridBagConstraints);
	}
}
