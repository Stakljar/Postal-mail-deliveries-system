package hr.ferit.pomds.gui.panels.items;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import hr.ferit.pomds.data.Delivery;
import hr.ferit.pomds.utils.ComponentDecorator;

public abstract class DeliveryPanel extends JPanel {

private static final long serialVersionUID = 6415461758051204832L;
	
	protected JLabel senderLabel;
	protected JLabel senderInformation;
	protected JLabel recipientLabel;
	protected JLabel recipientInformation;
	protected JLabel mailInformation;
	protected JButton deliveryAction;
	protected String deliveryId;
	protected String employeeId;
	protected GridBagConstraints gridBagConstraints = new GridBagConstraints();
	
	public DeliveryPanel(Delivery delivery, String employeeId) {
		
		super();
		this.deliveryId = delivery.id();
		this.employeeId = employeeId;

		senderLabel = new JLabel("Pošiljatelj:");
		senderInformation = new JLabel(delivery.sender().toString(20, 40, "left"));
		
		recipientLabel = new JLabel("Primatelj:");
		recipientInformation = new JLabel(delivery.recipient().toString(20, 40, "right"));
	
		mailInformation = new JLabel(delivery.mail().toString(20));
		
		setLayout(new GridBagLayout());
		setBorder(BorderFactory.createLineBorder(ComponentDecorator.getPrimaryBorderColor()));
		setBackground(Color.WHITE);
		setOpaque(true);
	}

	protected void configureComponents() {
		
		addListeners();
		
		ComponentDecorator.addDefaultColor(deliveryAction);
		ComponentDecorator.setForeground(ComponentDecorator.getPrimaryTextColor(), senderInformation, recipientInformation, mailInformation);
		ComponentDecorator.setForeground(ComponentDecorator.getTernaryTextColor(), senderLabel, recipientLabel);
		ComponentDecorator.setFont(new Font(null, Font.PLAIN, 16), senderInformation, recipientInformation);
		ComponentDecorator.setFont(new Font(null, Font.BOLD, 18), senderLabel, recipientLabel);
		mailInformation.setFont(new Font(null, Font.PLAIN, 18));
		deliveryAction.setFont(new Font(null, Font.PLAIN, 14));
	}
	
	protected void addListeners() {
		
		addComponentListener(new ComponentAdapter() {
			
			@Override
			public void componentResized(ComponentEvent e) {
				
				if(SwingUtilities.getWindowAncestor(DeliveryPanel.this).getWidth() > 1600) {
					ComponentDecorator.setFont(new Font(null, Font.PLAIN, 20), senderInformation, recipientInformation);
					ComponentDecorator.setFont(new Font(null, Font.BOLD, 22), senderLabel, recipientLabel);
					mailInformation.setFont(new Font(null, Font.PLAIN, 22));
					deliveryAction.setFont(new Font(null, Font.PLAIN, 18));
				}
				else if(SwingUtilities.getWindowAncestor(DeliveryPanel.this).getWidth() > 1460) {
					ComponentDecorator.setFont(new Font(null, Font.PLAIN, 18), senderInformation, recipientInformation);
					ComponentDecorator.setFont(new Font(null, Font.BOLD, 20), senderLabel, recipientLabel);
					mailInformation.setFont(new Font(null, Font.PLAIN, 20));
					deliveryAction.setFont(new Font(null, Font.PLAIN, 16));
				}
				else {
					ComponentDecorator.setFont(new Font(null, Font.PLAIN, 16), senderInformation, recipientInformation);
					ComponentDecorator.setFont(new Font(null, Font.BOLD, 18), senderLabel, recipientLabel);
					mailInformation.setFont(new Font(null, Font.PLAIN, 18));
					deliveryAction.setFont(new Font(null, Font.PLAIN, 14));
				}
			}
		});
	}
	
	protected abstract void addComponents();
}
