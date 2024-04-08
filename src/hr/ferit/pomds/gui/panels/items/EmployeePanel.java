package hr.ferit.pomds.gui.panels.items;

import java.awt.Color;
import java.awt.Dialog.ModalityType;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.sql.SQLException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import hr.ferit.pomds.data.Employee;
import hr.ferit.pomds.db.UserDatabaseOperations;
import hr.ferit.pomds.gui.dialogues.DefaultDialog;
import hr.ferit.pomds.gui.frames.EmployeesManagementFrame;
import hr.ferit.pomds.gui.frames.HomeFrame;
import hr.ferit.pomds.gui.panels.credentials.EditEmployeeUsernamePanel;
import hr.ferit.pomds.utils.ComponentDecorator;
import hr.ferit.pomds.utils.ConfiguredListeners;
import hr.ferit.pomds.utils.WindowSizeChecker;

public class EmployeePanel extends JPanel {

	private static final long serialVersionUID = 2952867236512045250L;

	private JLabel employeeUsername;
	private JButton editButton;
	private JButton deleteButton;
	private String employeeId;
	private String managerId;
	private String username;
	
	private GridBagConstraints gridBagConstraints  = new GridBagConstraints();
	
	public EmployeePanel(Employee employee, String managerId) {
		
		super();
		this.employeeId = employee.id();
		this.username = employee.username();
		this.managerId = managerId;
		employeeUsername = new JLabel(employee.username());
		editButton = new JButton("Uredi");
		deleteButton = new JButton("Izbriši");
		
		setLayout(new GridBagLayout());
		setOpaque(true);
		setBorder(BorderFactory.createLineBorder(ComponentDecorator.getPrimaryBorderColor()));
		setBackground(Color.WHITE);
		addComponents();
		configureComponents();
	}

	private void configureComponents() {
		
		addListeners();
		
		employeeUsername.setForeground(ComponentDecorator.getPrimaryTextColor());
		ComponentDecorator.addDefaultColor(editButton, deleteButton);
		ComponentDecorator.setFont(new Font(null, Font.PLAIN, 14), new JButton(), editButton, deleteButton);
		employeeUsername.setFont(new Font(null, Font.PLAIN, 16));
	}
	
	private void addListeners() {
		
		ConfiguredListeners.addMouseListenerForHandCursor(deleteButton);
		ConfiguredListeners.addMouseListenerForHandCursor(editButton);
		deleteButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if(JOptionPane.showOptionDialog(SwingUtilities.getWindowAncestor(EmployeePanel.this),
						"Želite li uistinu izbrisati ovaj djelatnikov račun?",
						"Brisanje djelatnikovog računa", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null,
						new String[] {"      Da      ", "      Ne      "}, null) != 0) {
					return;
				}
				
				((JFrame) SwingUtilities.getWindowAncestor(EmployeePanel.this)).setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
				deleteButton.setEnabled(false);
				editButton.setEnabled(false);
				
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
							if(UserDatabaseOperations.deleteUserAccount(employeeId, "employee") == 1) {
								employeeDeletedChecker = true;
							}
							successChecker = true;
						} catch (SQLException e1) {}
						return null;	
					}
					
					@Override
					protected void done() {
						
						deleteButton.setEnabled(true);
						editButton.setEnabled(true);
						((JFrame) SwingUtilities.getWindowAncestor(EmployeePanel.this)).setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
						if(successChecker == false) {
							JOptionPane.showMessageDialog(SwingUtilities.windowForComponent(EmployeePanel.this),
									"Pogreška prilikom brisanja korisnikovog korisničkog računa.", "Pogreška", JOptionPane.ERROR_MESSAGE);
							return;
						}
						if(deletedChecker == true) {
							SwingUtilities.getWindowAncestor(EmployeePanel.this).dispose();
							if(WindowSizeChecker.checkWindowSize(1400, 800) == 1) {
								new HomeFrame(1000, 600);
							}
							else {
								new HomeFrame(1000, 700);
							}
							return;
						}
						if(employeeDeletedChecker == true) {
							JOptionPane.showMessageDialog(SwingUtilities.windowForComponent(EmployeePanel.this),
									"Korisnikov korisnički račun je već izbrisan.", "Informacija", JOptionPane.INFORMATION_MESSAGE);
						}
						((EmployeesManagementFrame) SwingUtilities.getWindowAncestor(EmployeePanel.this)).readEmployeesFromDB();
					}
					
				}.execute();
			}
		});
		
		editButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if(WindowSizeChecker.checkWindowSize(1400, 800) == 1) {
					new DefaultDialog(SwingUtilities.getWindowAncestor(EmployeePanel.this),
							"Promjena djelatnikovog korisničkog imena", ModalityType.APPLICATION_MODAL, 340, 200,
							new EditEmployeeUsernamePanel(employeeId, username, managerId));
				}
				else {
					new DefaultDialog(SwingUtilities.getWindowAncestor(EmployeePanel.this),
							"Promjena djelatnikovog korisničkog imena", ModalityType.APPLICATION_MODAL, 400, 250,
							new EditEmployeeUsernamePanel(employeeId, username, managerId));
				}
			}
		});
		
		addComponentListener(new ComponentAdapter() {
			
			@Override
			public void componentResized(ComponentEvent e) {
				
				if(SwingUtilities.getWindowAncestor(EmployeePanel.this).getWidth() > 1300) {
					ComponentDecorator.setFont(new Font(null, Font.PLAIN, 18), new JButton(), editButton, deleteButton);
					employeeUsername.setFont(new Font(null, Font.PLAIN, 20));
				}
				else if(SwingUtilities.getWindowAncestor(EmployeePanel.this).getWidth() > 900) {
					ComponentDecorator.setFont(new Font(null, Font.PLAIN, 16), new JButton(), editButton, deleteButton);
					employeeUsername.setFont(new Font(null, Font.PLAIN, 18));
				}
				else {
					ComponentDecorator.setFont(new Font(null, Font.PLAIN, 14), new JButton(), editButton, deleteButton);
					employeeUsername.setFont(new Font(null, Font.PLAIN, 16));
				}
			}
		});
	}
	
	private void addComponents() {
		
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.weightx = 1;
		gridBagConstraints.weighty = 1;

		add(employeeUsername, gridBagConstraints);
		
		gridBagConstraints.gridx = 1;
		gridBagConstraints.weightx = 0;
		gridBagConstraints.insets = new Insets(10, 10, 10, 10);
		
		add(editButton, gridBagConstraints);
		
		gridBagConstraints.gridx = 2;
		
		add(deleteButton, gridBagConstraints);
	}

}