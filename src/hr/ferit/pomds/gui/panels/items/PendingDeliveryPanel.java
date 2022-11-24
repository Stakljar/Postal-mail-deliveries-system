package hr.ferit.pomds.gui.panels.items;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import hr.ferit.pomds.data.Delivery;
import hr.ferit.pomds.db.DeliveryDatabaseOperations;
import hr.ferit.pomds.db.UserDatabaseOperations;
import hr.ferit.pomds.gui.dialogues.DefaultDialog;
import hr.ferit.pomds.gui.frames.DeliveriesManagementFrame;
import hr.ferit.pomds.gui.frames.HomeFrame;
import hr.ferit.pomds.gui.panels.delivery_dates.AcceptDeliveryPanel;
import hr.ferit.pomds.utils.ComponentDecorator;
import hr.ferit.pomds.utils.DeliveryState;
import hr.ferit.pomds.utils.WindowSizeChecker;

public class PendingDeliveryPanel extends DeliveryPanel {

	private static final long serialVersionUID = 7412646147178419520L;
	
	private JButton deliveryDenial = new JButton("Odbij dostavu");
	
	public PendingDeliveryPanel(Delivery delivery, String employeeId) {
		
		super(delivery, employeeId);
		deliveryAction = new JButton("Prihvati dostavu");
		
		configureComponents();
		addComponents();
	}

	protected void configureComponents() {
		
		super.configureComponents();
		
		ComponentDecorator.addDefaultColor(deliveryAction, deliveryDenial);
		deliveryDenial.setFont(new Font(null, Font.PLAIN, 14));
	}
	
	@Override
	protected void addListeners() {
		
		addComponentListener(new ComponentAdapter() {
			
			@Override
			public void componentResized(ComponentEvent e) {
				
				if(SwingUtilities.getWindowAncestor(PendingDeliveryPanel.this).getWidth() > 1600) {
					ComponentDecorator.setFont(new Font(null, Font.PLAIN, 20), senderInformation, recipientInformation);
					ComponentDecorator.setFont(new Font(null, Font.BOLD, 22), senderLabel, recipientLabel);
					ComponentDecorator.setFont(new Font(null, Font.PLAIN, 18), deliveryAction, deliveryDenial);
					mailInformation.setFont(new Font(null, Font.PLAIN, 22));
				}
				else if(SwingUtilities.getWindowAncestor(PendingDeliveryPanel.this).getWidth() > 1460) {
					ComponentDecorator.setFont(new Font(null, Font.PLAIN, 18), senderInformation, recipientInformation);
					ComponentDecorator.setFont(new Font(null, Font.BOLD, 20), senderLabel, recipientLabel);
					ComponentDecorator.setFont(new Font(null, Font.PLAIN, 16), deliveryAction, deliveryDenial);
					mailInformation.setFont(new Font(null, Font.PLAIN, 20));
				}
				else {
					ComponentDecorator.setFont(new Font(null, Font.PLAIN, 16), senderInformation, recipientInformation);
					ComponentDecorator.setFont(new Font(null, Font.BOLD, 18), senderLabel, recipientLabel);
					ComponentDecorator.setFont(new Font(null, Font.PLAIN, 14), deliveryAction, deliveryDenial);
					mailInformation.setFont(new Font(null, Font.PLAIN, 18));
				}
			}
		});
		
		deliveryAction.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if(WindowSizeChecker.checkWindowSize(1400, 800) == 1) {
					new DefaultDialog(SwingUtilities.getWindowAncestor(PendingDeliveryPanel.this), "Prihvaćanje dostave", ModalityType.APPLICATION_MODAL,
							530, 200, new AcceptDeliveryPanel(deliveryId, employeeId));
				}
				else {
					new DefaultDialog(SwingUtilities.getWindowAncestor(PendingDeliveryPanel.this), "Prihvaćanje dostave", ModalityType.APPLICATION_MODAL,
							530, 240, new AcceptDeliveryPanel(deliveryId, employeeId));
				}
			}
		});
		
		deliveryDenial.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if(JOptionPane.showOptionDialog(PendingDeliveryPanel.this,
						"Da li ste sigurni da želite odbiti odabranu dostavu?",
						"Odbijanje dostave", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null,
						new String[] {"      Da      ", "      Ne      "}, null) != 0) {
					return;
				}
				
				deliveryAction.setEnabled(false);
				deliveryDenial.setEnabled(false);
				
				new SwingWorker<Object, Object>() {

					private boolean deletedChecker = false;
					private boolean successChecker = false;
					private boolean alreadyModifiedChecker = false;
					
					@Override
					protected Object doInBackground() throws Exception {
						try {
							if(UserDatabaseOperations.isUserAlreadyDeleted(employeeId, "employee")) {
								deletedChecker = true;
								successChecker = true;
								return null;
							}
							if(DeliveryDatabaseOperations.isDeliveryAlreadyChanged(deliveryId, DeliveryState.PENDING)) {
								alreadyModifiedChecker = true;
								successChecker = true;
								return null;
							}
							DeliveryDatabaseOperations.deleteDelivery(deliveryId);
							successChecker = true;
						} catch (SQLException e) {}
						return null;
					}
					
					@Override
					protected void done() {
						
						deliveryAction.setEnabled(true);
						deliveryDenial.setEnabled(true);
						if(!successChecker) {
							JOptionPane.showMessageDialog(PendingDeliveryPanel.this, "Pogreška prilikom brisanja dostave.", "Pogreška",
									JOptionPane.ERROR_MESSAGE);
							return;
						}
						if(deletedChecker) {
							SwingUtilities.getWindowAncestor(PendingDeliveryPanel.this).dispose();
							if(WindowSizeChecker.checkWindowSize(1400, 800) == 1) {
								new HomeFrame(1000, 600);
							}
							else {
								new HomeFrame(1000, 700);
							}
							return;
						}
						DeliveriesManagementFrame owner = (DeliveriesManagementFrame) SwingUtilities.
								getWindowAncestor(PendingDeliveryPanel.this);
						owner.readDeliveriesFromDB(DeliveryState.PENDING);
						if(alreadyModifiedChecker) {
							JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(PendingDeliveryPanel.this),
									"Dostava je već modificirana.", "Info", JOptionPane.INFORMATION_MESSAGE);
							return;
						}
						String activity = "Dostava: " + deliveryId + " odbijena.";
						owner.getActivityPanel().addText(activity);
					}
				}.execute();
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
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		gridBagConstraints.insets = new Insets(0, 10, 10, 10);
		
		add(deliveryDenial, gridBagConstraints);
		
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
		
		gridBagConstraints.insets = new Insets(10, 0, 10, 10);
		gridBagConstraints.gridy = 1;
		
		add(recipientInformation, gridBagConstraints);
		
		gridBagConstraints.gridy = 2;
		gridBagConstraints.insets = new Insets(10, 0, 20, 10);
		
		add(deliveryAction, gridBagConstraints);
	}
}
