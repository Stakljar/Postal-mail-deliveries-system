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
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.Timer;

import hr.ferit.pomds.data.Employee;
import hr.ferit.pomds.db.UserDatabaseOperations;
import hr.ferit.pomds.gui.dialogues.DefaultDialog;
import hr.ferit.pomds.gui.panels.credentials.NewEmployeePanel;
import hr.ferit.pomds.gui.panels.credentials.PasswordChangePanel;
import hr.ferit.pomds.gui.panels.scrollable.EmployeesSequencePanel;
import hr.ferit.pomds.utils.ComponentDecorator;
import hr.ferit.pomds.utils.UserType;
import hr.ferit.pomds.utils.WindowSizeChecker;

public class EmployeesManagementFrame extends DefaultFrame {

	private static final long serialVersionUID = 4321414871848501433L;
	
	private EmployeesSequencePanel employeesPanel;
	private JMenuBar menuBar;
	private JMenu menu;
	private JMenuItem passwordChangeOption;
	private JMenuItem logoutOption;
	private JButton addNewEmployeeButton;
	private JLabel employeesList;
	private String id;
	private Timer timer;
	
	private GridBagConstraints gridBagConstraints = new GridBagConstraints();
	
	public EmployeesManagementFrame(int width, int height, String id, String username) {
		
		super("Sustav dostava poštanskih pošiljki - trenutna sesija: upravitelj (" + username + ")");
		this.id = id;
		employeesPanel = new EmployeesSequencePanel(id);
		menuBar = new JMenuBar();
		menu = new JMenu("Račun");
		passwordChangeOption = new JMenuItem("Promjena lozinke");
		logoutOption = new JMenuItem("Odjava");
		addNewEmployeeButton = new JButton("Dodavanje novog djelatnika");
		employeesList = new JLabel("Popis djelatnika");
		timer = new Timer(5 * 60 * 1000, new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				readEmployeesFromDB();
			}
		});
		
		readEmployeesFromDB();
		setLayout(new GridBagLayout());
		getContentPane().setBackground(ComponentDecorator.getPrimaryBackgroundColor());
		setJMenuBar(menuBar);
		configureWindow(width, height, 600, 700, JFrame.EXIT_ON_CLOSE);
		configureComponents();
		addComponents();
	}
	
	private void configureComponents() {
		
		addListeners();
		
		employeesPanel.setPreferredSize(new Dimension(100, 100));
		ComponentDecorator.addDefaultColor(addNewEmployeeButton);
		addNewEmployeeButton.setFont(new Font(null, Font.PLAIN, 18));
		employeesList.setForeground(ComponentDecorator.getPrimaryTextColor());
		employeesList.setFont(new Font(null, Font.BOLD, 20));	
		
		timer.setRepeats(true);
		timer.start();
	}
	
	private void addListeners() {
		
		addNewEmployeeButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if(WindowSizeChecker.checkWindowSize(1400, 800) == 1) {
					new DefaultDialog(EmployeesManagementFrame.this, "Dodavanje novog djelatnika", ModalityType.APPLICATION_MODAL,
							400, 280, new NewEmployeePanel(EmployeesManagementFrame.this, id));
				}
				else {
					new DefaultDialog(EmployeesManagementFrame.this, "Dodavanje novog djelatnika", ModalityType.APPLICATION_MODAL,
							400, 300, new NewEmployeePanel(EmployeesManagementFrame.this, id));
				}
			}
		});
		
		passwordChangeOption.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if(WindowSizeChecker.checkWindowSize(1400, 800) == 1) {
					new DefaultDialog(EmployeesManagementFrame.this, "Promjena lozinke", ModalityType.APPLICATION_MODAL,
							400, 360, new PasswordChangePanel(UserType.MANAGER, id));
				}
				else {
					new DefaultDialog(EmployeesManagementFrame.this, "Promjena lozinke", ModalityType.APPLICATION_MODAL,
							400, 400, new PasswordChangePanel(UserType.MANAGER, id));
				}
			}
		});
		
		logoutOption.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				EmployeesManagementFrame.this.dispose();
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

	public void readEmployeesFromDB() {
		
		employeesPanel.clearSubPanel();
		employeesPanel.changeLoadingVisibility(true);
		
		new SwingWorker<Object, Object>() {

			private boolean successChecker = false;
			private List<Employee> employees = new LinkedList<>();
			
			@Override
			protected Object doInBackground() throws Exception {

				try {
					employees =  UserDatabaseOperations.getAllEmployees();
					successChecker = true;
				}
				catch (SQLException e) {}
				return null;
			}
			
			@Override
			protected void done() {
				
				if(!successChecker) {
					JOptionPane.showMessageDialog(EmployeesManagementFrame.this, "Pogreška prilikom učitavanja podataka.", "Pogreška",
							JOptionPane.ERROR_MESSAGE);
				}
				employeesPanel.fillSubPanel(employees);
			}
		}.execute();
	}
	
	private void addComponents() {
		
		menu.add(passwordChangeOption);
		menu.add(logoutOption);
		menuBar.add(menu);
		
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.weightx = 1;
		gridBagConstraints.weighty = 0.025;
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		gridBagConstraints.insets = new Insets(10, 30, 5, 0);
		
		add(employeesList, gridBagConstraints);
		
		gridBagConstraints.gridx = 1;
		gridBagConstraints.anchor = GridBagConstraints.EAST;
		gridBagConstraints.insets = new Insets(10, 0, 5, 30);
		
		add(addNewEmployeeButton , gridBagConstraints);
		
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.weighty = 0.975;
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.anchor = GridBagConstraints.CENTER;
		gridBagConstraints.insets = new Insets(0, 30, 40, 30);
		
		add(employeesPanel, gridBagConstraints);
	}

}
