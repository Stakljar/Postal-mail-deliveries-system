package hr.ferit.pomds.gui.panels.items;

import java.awt.Dialog.ModalityType;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.sql.Date;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import hr.ferit.pomds.data.Delivery;
import hr.ferit.pomds.gui.dialogues.DefaultDialog;
import hr.ferit.pomds.gui.panels.delivery_dates.FinishDeliveryPanel;
import hr.ferit.pomds.utils.AdaptedDateFormat;
import hr.ferit.pomds.utils.ComponentDecorator;
import hr.ferit.pomds.utils.WindowSizeChecker;

public class ActiveDeliveryPanel extends DeliveryPanel {

	private static final long serialVersionUID = 7853159853287247532L;
	
	private JLabel takeOverDateLabel;
	private Date takeOverDate;
	
	public ActiveDeliveryPanel(Delivery delivery, String employeeId) {
		
		super(delivery, employeeId);
		this.takeOverDate = delivery.takeoverDate();
		takeOverDateLabel = new JLabel("<html>Datum preuzimanja:<br>" + (delivery.takeoverDate() == null ?  delivery.takeoverDate() : 
			AdaptedDateFormat.getDateFormat().format(delivery.takeoverDate())) + "</div></html>");
		deliveryAction = new JButton("Završi dostavu");
		
		configureComponents();
		addComponents();
	}
	
	@Override
	protected void configureComponents() {
		
		super.configureComponents();
		takeOverDateLabel.setForeground(ComponentDecorator.getPrimaryTextColor());
	}
	
	@Override
	protected void addListeners() {
		
		addComponentListener(new ComponentAdapter() {
			
			@Override
			public void componentResized(ComponentEvent e) {
				
				if(SwingUtilities.getWindowAncestor(ActiveDeliveryPanel.this).getWidth() > 1600) {
					ComponentDecorator.setFont(new Font(null, Font.PLAIN, 20), senderInformation, recipientInformation, takeOverDateLabel);
					ComponentDecorator.setFont(new Font(null, Font.BOLD, 22), senderLabel, recipientLabel);
					mailInformation.setFont(new Font(null, Font.PLAIN, 22));
					deliveryAction.setFont(new Font(null, Font.PLAIN, 18));
				}
				else if(SwingUtilities.getWindowAncestor(ActiveDeliveryPanel.this).getWidth() > 1460) {
					ComponentDecorator.setFont(new Font(null, Font.PLAIN, 18), senderInformation, recipientInformation, takeOverDateLabel);
					ComponentDecorator.setFont(new Font(null, Font.BOLD, 20), senderLabel, recipientLabel);
					mailInformation.setFont(new Font(null, Font.PLAIN, 20));
					deliveryAction.setFont(new Font(null, Font.PLAIN, 16));
				}
				else {
					ComponentDecorator.setFont(new Font(null, Font.PLAIN, 16), senderInformation, recipientInformation, takeOverDateLabel);
					ComponentDecorator.setFont(new Font(null, Font.BOLD, 18), senderLabel, recipientLabel);
					mailInformation.setFont(new Font(null, Font.PLAIN, 18));
					deliveryAction.setFont(new Font(null, Font.PLAIN, 14));
				}
			}
		});

		deliveryAction.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if(WindowSizeChecker.checkWindowSize(1400, 800) == 1) {
					new DefaultDialog(SwingUtilities.getWindowAncestor(ActiveDeliveryPanel.this), "Završetak dostave", ModalityType.APPLICATION_MODAL,
							530, 240, new FinishDeliveryPanel(deliveryId, employeeId, takeOverDate));
				}
				else {
					new DefaultDialog(SwingUtilities.getWindowAncestor(ActiveDeliveryPanel.this), "Završetak dostave", ModalityType.APPLICATION_MODAL,
							530, 280, new FinishDeliveryPanel(deliveryId, employeeId, takeOverDate));
				}
			}
		});
	}

	protected void addComponents() {

		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.weightx = 1;
		gridBagConstraints.weighty = 1;
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		gridBagConstraints.insets = new Insets(10, 10, 0, 0);
		
		add(senderLabel, gridBagConstraints);
		
		gridBagConstraints.gridy = 1;
		gridBagConstraints.insets = new Insets(10, 10, 10, 0);
		
		add(senderInformation, gridBagConstraints);
		
		gridBagConstraints.gridy = 2;
		gridBagConstraints.insets = new Insets(0, 10, 10, 0);
		
		add(takeOverDateLabel, gridBagConstraints);
		
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.anchor = GridBagConstraints.CENTER;
		gridBagConstraints.insets = new Insets(0, 10, 40, 10);
		
		add(mailInformation, gridBagConstraints);
		
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.anchor = GridBagConstraints.EAST;
		gridBagConstraints.insets = new Insets(10, 0, 10, 10);
		
		add(recipientLabel, gridBagConstraints);
		
		gridBagConstraints.gridy = 1;
		gridBagConstraints.insets = new Insets(10, 0, 10, 10);
		
		add(recipientInformation, gridBagConstraints);
		
		gridBagConstraints.gridy = 2;
		gridBagConstraints.insets = new Insets(10, 0, 20, 10);
		
		add(deliveryAction, gridBagConstraints);
	}
}
