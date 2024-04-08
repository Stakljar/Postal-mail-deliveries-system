package hr.ferit.pomds.gui.panels;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import hr.ferit.pomds.gui.dialogues.DefaultDialog;
import hr.ferit.pomds.gui.panels.credentials.LoginPanel;
import hr.ferit.pomds.gui.panels.credentials.RegistrationPanel;
import hr.ferit.pomds.utils.ComponentDecorator;
import hr.ferit.pomds.utils.ConfiguredListeners;
import hr.ferit.pomds.utils.UserType;
import hr.ferit.pomds.utils.WindowSizeChecker;

public class HomePanel extends JPanel {

	private static final long serialVersionUID = 924237601416245227L;
	
	private JButton managerLoginButton;
	private JButton employeeLoginButton;
	private JButton registerButton;
	private JButton loginButton;
	private JLabel title;
	private JLabel info;
	private Image image;
	private GridBagConstraints gridBagConstraints = new GridBagConstraints();
	
	public HomePanel(String filePath) {
		
		super();
		managerLoginButton = new JButton("Prijava kao upravitelj");
		employeeLoginButton = new JButton("Prijava kao djelatnik");
		registerButton = new JButton("Registracija");
		loginButton = new JButton("Prijava");
		title = new JLabel("<html><div style='text-align: center'>Sustav<br>dostava<br>poštanskih<br>pošiljki</div></html>");
		info = new JLabel("<html><div style='text-align: center'>Napravio: Dražen Antunović<br>Kontakt: drazenantunovic012@gmail.com"
		  		+ "<br><u>https://www.github.com/Stakljar</u></div></html>");
		try {
			image = ImageIO.read(new File(filePath));
		} catch (IOException e) {}
		
		setLayout(new GridBagLayout());
		setOpaque(true);
		configureComponents();
		addComponents();
	}
	
	private void configureComponents() {
		
		addListeners();
		
		ComponentDecorator.addDefaultColor(managerLoginButton, employeeLoginButton, registerButton, loginButton);
		title.setForeground(new Color(72, 84, 246));
		info.setForeground(new Color(214, 217, 255));
		title.setFont(new Font("Stencil", Font.PLAIN, 76));
		ComponentDecorator.setFont(new Font(null, Font.BOLD, 16), managerLoginButton, employeeLoginButton, registerButton, loginButton);
	}
	
	private void addListeners() {
		
		managerLoginButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				openAuthorization(UserType.MANAGER);
			}
		});
		
		employeeLoginButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				openAuthorization(UserType.EMPLOYEE);
			}
		});
		
		loginButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				openAuthorization(UserType.SERVICE_USER);
			}
		});
		
		registerButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(WindowSizeChecker.checkWindowSize(1400, 800) == 1) {
					new DefaultDialog(SwingUtilities.getWindowAncestor(HomePanel.this), "Registacija", ModalityType.APPLICATION_MODAL,
							400, 280, new RegistrationPanel());
				}
				else {
					new DefaultDialog(SwingUtilities.getWindowAncestor(HomePanel.this), "Registacija", ModalityType.APPLICATION_MODAL,
							400, 300, new RegistrationPanel());
				}
			}
		});

		ConfiguredListeners.addMouseListenerForHandCursor(loginButton);
	}
	
	private void openAuthorization(UserType authorizationIntension) {
		
		String authorizationTitle = authorizationIntension == UserType.MANAGER ? "Prijava upravitelja" :
			authorizationIntension == UserType.EMPLOYEE ? "Prijava djelatnika" : "Prijava";
		if(WindowSizeChecker.checkWindowSize(1400, 800) == 1) {
			new DefaultDialog(SwingUtilities.getWindowAncestor(HomePanel.this), authorizationTitle, ModalityType.APPLICATION_MODAL,
					400, 280, new LoginPanel(authorizationIntension));
		}
		else {
			new DefaultDialog(SwingUtilities.getWindowAncestor(HomePanel.this), authorizationTitle, ModalityType.APPLICATION_MODAL,
					400, 300, new LoginPanel(authorizationIntension));
		}
	}

	private void addComponents(){

		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.weightx = 0.08;
		gridBagConstraints.weighty = 0.5;
		gridBagConstraints.insets = new Insets(14, 0, 0, 0);
		gridBagConstraints.anchor = GridBagConstraints.NORTH;
		
		add(managerLoginButton, gridBagConstraints);
		
		gridBagConstraints.gridx = 1;
		gridBagConstraints.weightx = 0.42;
		gridBagConstraints.insets = new Insets(14, 0, 0, 0);
		gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
		
		add(employeeLoginButton, gridBagConstraints);
		
		gridBagConstraints.gridx = 2;
		gridBagConstraints.anchor = GridBagConstraints.NORTHEAST;
		
		add(registerButton, gridBagConstraints);
		
		gridBagConstraints.gridx = 3;
		gridBagConstraints.weightx = 0.08;
		gridBagConstraints.insets = new Insets(14, 0, 0, 0);
		gridBagConstraints.anchor = GridBagConstraints.NORTH;
		
		add(loginButton, gridBagConstraints);

		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridwidth = 4;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.insets = new Insets(82, 0, 0, 20);
		gridBagConstraints.anchor = GridBagConstraints.EAST;
		
		add(title, gridBagConstraints);
		
		gridBagConstraints.gridy = 2;
		gridBagConstraints.insets = new Insets(0, 0, 46, 0);
		gridBagConstraints.anchor = GridBagConstraints.SOUTH;
		
		add(info, gridBagConstraints);
	}
	
	@Override
	public void paintComponent(Graphics g) {
		
		g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
	}
}
