package hr.ferit.pomds.gui.frames;

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
import javax.swing.JTabbedPane;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import hr.ferit.pomds.data.Delivery;
import hr.ferit.pomds.db.DeliveryDatabaseOperations;
import hr.ferit.pomds.gui.dialogues.DefaultDialog;
import hr.ferit.pomds.gui.panels.ActivityPanel;
import hr.ferit.pomds.gui.panels.credentials.PasswordChangePanel;
import hr.ferit.pomds.gui.panels.scrollable.ActiveDeliveriesSequencePanel;
import hr.ferit.pomds.gui.panels.scrollable.PendingDeliveriesSequencePanel;
import hr.ferit.pomds.utils.ComponentDecorator;
import hr.ferit.pomds.utils.DeliveryState;
import hr.ferit.pomds.utils.UserType;
import hr.ferit.pomds.utils.WindowSizeChecker;

public class DeliveriesManagementFrame extends DefaultFrame {

	private static final long serialVersionUID = 5304041843208013718L;
	
	private ActiveDeliveriesSequencePanel activeDeliveriesPanel;
	private PendingDeliveriesSequencePanel pendingDeliveriesPanel;
	private ActivityPanel activityPanel;
	private JTabbedPane deliveries;
	private JMenuBar menuBar;
	private JMenu menu;
	private JMenuItem passwordChangeOption;
	private JMenuItem logoutOption;
	private JButton finishedDeliveriesButton;
	private String id;
	private Timer timer;
	
	private GridBagConstraints gridBagConstraints = new GridBagConstraints();
	
	public DeliveriesManagementFrame(int width, int height, String id, String username) {
		
		super("Sustav dostava poštanskih pošiljki - trenutna sesija: djelatnik (" + username + ")");
		this.id = id;
		activityPanel = new ActivityPanel();
		deliveries = new JTabbedPane();
		activeDeliveriesPanel = new ActiveDeliveriesSequencePanel(id);
		pendingDeliveriesPanel = new PendingDeliveriesSequencePanel(id);
		menuBar = new JMenuBar();
		menu = new JMenu("Račun");
		passwordChangeOption = new JMenuItem("Promjena lozinke");
		logoutOption = new JMenuItem("Odjava");
		finishedDeliveriesButton = new JButton("Pregled završenih dostava");
		timer = new Timer(5 * 60 * 1000, new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if(deliveries.getSelectedComponent() == activeDeliveriesPanel) {
					readDeliveriesFromDB(DeliveryState.ACTIVE);
				}
				else {
					readDeliveriesFromDB(DeliveryState.PENDING);
				}
			}
		});

		setLayout(new GridBagLayout());
		getContentPane().setBackground(ComponentDecorator.getPrimaryBackgroundColor());
		setJMenuBar(menuBar);
		configureWindow(width, height, 1320, 700, JFrame.EXIT_ON_CLOSE);
		configureComponents();
		addComponents();
	}
	
	public ActivityPanel getActivityPanel() {
		
		return activityPanel;
	}
	
	private void configureComponents() {
		
		addListeners();
		
		deliveries.add("Aktivne", activeDeliveriesPanel);
		deliveries.add("Zahtijevane", pendingDeliveriesPanel);
		deliveries.setPreferredSize(new Dimension(100, 100));
		activityPanel.setPreferredSize(new Dimension(100, 100));
		ComponentDecorator.addDefaultColor(finishedDeliveriesButton, deliveries);
		ComponentDecorator.setFont(new Font(null, Font.PLAIN, 18), deliveries, finishedDeliveriesButton);
		
		timer.setRepeats(true);
		timer.start();
	}

	private void addListeners() {
		
		deliveries.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				
				if(deliveries.getSelectedComponent() == activeDeliveriesPanel) {
					readDeliveriesFromDB(DeliveryState.ACTIVE);
				}
				else {
					readDeliveriesFromDB(DeliveryState.PENDING);
				}
			}
		});

		finishedDeliveriesButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				DeliveriesManagementFrame.this.setEnabled(false);
				if(WindowSizeChecker.checkWindowSize(1400, 800) == 1) {
					new FinishedDeliveriesFrame(DeliveriesManagementFrame.this, id, 1200, 700);
				}
				else {
					new FinishedDeliveriesFrame(DeliveriesManagementFrame.this, id, 1300, 800);
				}
			}
		});
		
		passwordChangeOption.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if(WindowSizeChecker.checkWindowSize(1400, 800) == 1) {
					new DefaultDialog(DeliveriesManagementFrame.this, "Izmjena lozinke", ModalityType.APPLICATION_MODAL,
							400, 360, new PasswordChangePanel(UserType.EMPLOYEE, id));
				}
				else {
					new DefaultDialog(DeliveriesManagementFrame.this, "Izmjena lozinke", ModalityType.APPLICATION_MODAL,
							400, 400, new PasswordChangePanel(UserType.EMPLOYEE, id));
				}
			}
		});
		
		logoutOption.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				DeliveriesManagementFrame.this.dispose();

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

	public void readDeliveriesFromDB(DeliveryState state) {
		
		if(state == DeliveryState.PENDING) {
			pendingDeliveriesPanel.clearSubPanel();
			pendingDeliveriesPanel.changeLoadingVisibility(true);
		}
		else if(state == DeliveryState.ACTIVE) {
			activeDeliveriesPanel.clearSubPanel();
			activeDeliveriesPanel.changeLoadingVisibility(true);
		}
		
		new SwingWorker<Object, Object>() {

			private boolean successChecker = false;
			private List<Delivery> deliveries = new LinkedList<>();
			
			@Override
			protected Object doInBackground() throws Exception {
				
				try {
					deliveries =  DeliveryDatabaseOperations.getAllDeliveries(state);
					successChecker = true;
				}
				catch (SQLException e) {}
				return null;
			}
			
			@Override
			protected void done() {
				
				if(!successChecker) {
					JOptionPane.showMessageDialog(DeliveriesManagementFrame.this, "Pogreška prilikom učitavanje podataka.", "Pogreška",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				if(state == DeliveryState.PENDING) {
					pendingDeliveriesPanel.fillSubPanel(deliveries);
				}
				else if(state == DeliveryState.ACTIVE) {
					activeDeliveriesPanel.fillSubPanel(deliveries);
				}
			}
		}.execute();
	}
	
	private void addComponents() {
			
		menu.add(passwordChangeOption);
		menu.add(logoutOption);
		menuBar.add(menu);
		
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.weightx = 0.84;
		gridBagConstraints.weighty = 0.025;
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		gridBagConstraints.insets = new Insets(4, 24, 4, 0);
		
		add(finishedDeliveriesButton, gridBagConstraints);
		
		gridBagConstraints.gridy = 1;
		gridBagConstraints.weighty = 0.975;
		gridBagConstraints.gridwidth = 1;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.anchor = GridBagConstraints.CENTER;
		gridBagConstraints.insets = new Insets(0, 0, 0, 0);
		
		add(deliveries, gridBagConstraints);
		
		gridBagConstraints.gridx = 1;
		gridBagConstraints.weightx = 0.16;

		add(activityPanel, gridBagConstraints);
	}
}