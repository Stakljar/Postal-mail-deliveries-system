package hr.ferit.pomds.gui.panels.items;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import hr.ferit.pomds.data.Delivery;
import hr.ferit.pomds.utils.AdaptedDateFormat;
import hr.ferit.pomds.utils.ComponentDecorator;

public class UserDeliveryPanel extends JPanel {

	private static final long serialVersionUID = 7418757325626526185L;
	
	private JLabel interactedUserTypeLabel;
	private JLabel interactedUserInformation;
	private JLabel mailInformation;
	private JLabel takeOverDate;
	private JLabel status;
	private JLabel completionDate;
	private Delivery delivery;
	private String id;
	
	private GridBagConstraints gridBagConstraints = new GridBagConstraints();
	
	public UserDeliveryPanel(Delivery delivery, String userId) {
		
		super();
		this.delivery = delivery;
		this.id = userId;
		if(delivery.recipient().id() != null && delivery.recipient().id().equals(userId)) {
			interactedUserTypeLabel = new JLabel("Pošiljatelj:");
			interactedUserInformation = new JLabel(delivery.sender().toString(40));
		}
		else {
			interactedUserTypeLabel = new JLabel("Primatelj:");
			interactedUserInformation = new JLabel(delivery.recipient().toString(40));
		}
		mailInformation = new JLabel(delivery.mail().toString(30));
		takeOverDate = new JLabel("<html>Datum preuzimanja:<br>" + (delivery.takeoverDate() == null ?  delivery.takeoverDate() : 
			AdaptedDateFormat.getDateFormat().format(delivery.takeoverDate()))  + "</html>");
		completionDate = new JLabel("<html>Datum završetka:<br>" + (delivery.completionDate() == null ?  delivery.completionDate() : 
			AdaptedDateFormat.getDateFormat().format(delivery.completionDate()))  + "</html>");
		status = new JLabel();
		
		setLayout(new GridBagLayout());
		setOpaque(true);
		setBorder(BorderFactory.createLineBorder(ComponentDecorator.getPrimaryBorderColor()));
		setBackground(Color.WHITE);
		configureComponents();
		addComponents();
	}

	private void configureComponents() {
		
		addListeners();
		
		ComponentDecorator.setForeground(ComponentDecorator.getPrimaryTextColor(), interactedUserTypeLabel, interactedUserInformation, 
				mailInformation, takeOverDate, status, completionDate);
		interactedUserTypeLabel.setForeground(ComponentDecorator.getTernaryTextColor());
		ComponentDecorator.setFont(new Font(null, Font.PLAIN, 16), interactedUserInformation, 
				mailInformation, takeOverDate, status, completionDate);
		interactedUserTypeLabel.setFont(new Font(null, Font.BOLD, 18));
		mailInformation.setFont(new Font(null, Font.PLAIN, 18));
		if(delivery.recipient().id() != null && delivery.recipient().id().equals(id)) {
			setBackground(new Color(247, 247, 255));
		}
		if(delivery.completionDate() != null) {
			status.setText("<html><div style='text-align: center';><b>Status:</b><br><div style='color: #00ff00';><b>ZAVRŠENA</b></div></div></html>");
		}
		else if(delivery.isUnsuccessful() == true) {
			status.setText("<html><div style='text-align: center';><b>Status:</b><br><div style='color: #ff0000';><b>NEUSPJEŠNA</b></div></div></html>");
			completionDate.setVisible(false);
		}
		else if(delivery.takeoverDate() != null) {
			status.setText("<html><div style='text-align: center';><b>Status:</b><br><div style='color: #ffb700';><b>U TIJEKU</b></div></div></html>");
			completionDate.setVisible(false);
		}
		else {
			status.setText("<html><div style='text-align: center';><b>Status:</b><br><div style='color: #d3d600';><b>NA ČEKANJU</b></div></div></html>");
			completionDate.setVisible(false);
			takeOverDate.setVisible(false);
		}
	}
	
	private void addListeners() {
		
		addComponentListener(new ComponentAdapter() {
			
			@Override
			public void componentResized(ComponentEvent e) {
				
				if(SwingUtilities.getWindowAncestor(UserDeliveryPanel.this).getWidth() > 1600) {
					ComponentDecorator.setFont(new Font(null, Font.PLAIN, 20), new JLabel(), interactedUserInformation, 
							takeOverDate, completionDate, status);
					interactedUserTypeLabel.setFont(new Font(null, Font.BOLD, 22));
					mailInformation.setFont(new Font(null, Font.PLAIN, 22));
				}
				else if(SwingUtilities.getWindowAncestor(UserDeliveryPanel.this).getWidth() > 1400) {
					ComponentDecorator.setFont(new Font(null, Font.PLAIN, 18), new JLabel(), interactedUserInformation, 
							takeOverDate, completionDate, status);
					interactedUserTypeLabel.setFont(new Font(null, Font.BOLD, 20));
					mailInformation.setFont(new Font(null, Font.PLAIN, 20));
				}
				else {
					ComponentDecorator.setFont(new Font(null, Font.PLAIN, 16), new JLabel(), interactedUserInformation, 
							takeOverDate, completionDate, status);
					interactedUserTypeLabel.setFont(new Font(null, Font.BOLD, 18));
					mailInformation.setFont(new Font(null, Font.PLAIN, 18));
				}
			}
		});
	}

	private void addComponents() {

		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.weightx = 1;
		gridBagConstraints.weighty = 0.1;
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		gridBagConstraints.insets = new Insets(10, 10, 0, 0);
		
		add(interactedUserTypeLabel, gridBagConstraints);
		
		gridBagConstraints.gridy = 1;
		
		gridBagConstraints.insets = new Insets(10, 10, 10, 0);
		
		add(interactedUserInformation, gridBagConstraints);
		
		gridBagConstraints.gridy = 2;
		gridBagConstraints.insets = new Insets(0, 10, 10, 0);
		
		add(takeOverDate, gridBagConstraints);
		
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.anchor = GridBagConstraints.CENTER;
		gridBagConstraints.insets = new Insets(0, 40, 40, 40);
		
		add(status, gridBagConstraints);
		
		gridBagConstraints.gridx = 2;
		gridBagConstraints.anchor = GridBagConstraints.EAST;
		gridBagConstraints.insets = new Insets(0, 0, 40, 100);
		
		add(mailInformation, gridBagConstraints);
		
		gridBagConstraints.gridy = 2;
		gridBagConstraints.insets = new Insets(0, 0, 10, 90);
		
		add(completionDate, gridBagConstraints);
	}
}
