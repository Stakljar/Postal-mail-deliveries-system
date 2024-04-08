package hr.ferit.pomds.gui.panels.delivery_dates;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Calendar;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import hr.ferit.pomds.db.DeliveryDatabaseOperations;
import hr.ferit.pomds.db.UserDatabaseOperations;
import hr.ferit.pomds.gui.frames.DeliveriesManagementFrame;
import hr.ferit.pomds.gui.frames.HomeFrame;
import hr.ferit.pomds.utils.ComponentDecorator;
import hr.ferit.pomds.utils.DeliveryState;
import hr.ferit.pomds.utils.WindowSizeChecker;

public class AcceptDeliveryPanel extends DeliveryDateChangePanel {

	private static final long serialVersionUID = 5851665614816781142L;
	
	private String deliveryId;
	private String employeeId;
	private JLabel takeoverDateLabel;
	private JButton acceptDeliveryButton;
	
	private GridBagConstraints gridBagConstraints = new GridBagConstraints();
	
	public AcceptDeliveryPanel(String deliveryId, String employeeId) {
		
		super();
		this.deliveryId = deliveryId;
		this.employeeId = employeeId;
		takeoverDateLabel = new JLabel("Datum preuzimanja:");
		acceptDeliveryButton = new JButton("Prihvati dostavu");
		
		setLayout(new GridBagLayout());
		setBorder(BorderFactory.createLineBorder(Color.BLUE));
		setBackground(ComponentDecorator.getPrimaryBackgroundColor());
		configureComponents();
		addComponents();
	}
	
	private void configureComponents() {
		
		addListeners();
		
		days.setSelectedItem(calendar.get(Calendar.DAY_OF_MONTH));
		months.setSelectedIndex(calendar.get(Calendar.MONTH));
		years.setSelectedItem(calendar.get(Calendar.YEAR));
		
		ComponentDecorator.addDefaultColor(acceptDeliveryButton, days, months, years);
		ComponentDecorator.setForeground(ComponentDecorator.getPrimaryTextColor(), dayLabel, monthLabel, yearLabel, takeoverDateLabel);
		acceptDeliveryButton.setFont(new Font(null, Font.PLAIN, 14));
		ComponentDecorator.setFont(new Font(null, Font.PLAIN, 16), dayLabel, monthLabel, yearLabel, days, months, years);
		takeoverDateLabel.setFont(new Font(null, Font.BOLD, 16));
		ComponentDecorator.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.BLUE),
				BorderFactory.createEmptyBorder(0, 4, 0, 0)), days, months, years);
	}
	
	private void addListeners() {
		
		acceptDeliveryButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				calendar.set(Calendar.DAY_OF_MONTH, (int) days.getSelectedItem());
				calendar.set(Calendar.MONTH, months.getSelectedIndex());
				calendar.set(Calendar.YEAR, (int) years.getSelectedItem());
				
				if(!checkValidDate(new Date(calendar.getTime().getTime()))) {
					JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(AcceptDeliveryPanel.this),
							"Neispravan datum.", "Pogreška", JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				SwingUtilities.getWindowAncestor(AcceptDeliveryPanel.this).setEnabled(false);
				
				new SwingWorker<Object, Object>() {

					private boolean successChecker = false;
					private boolean deletedChecker = false;
					private boolean alreadyModifiedChecker = false;
					
					@Override
					protected Object doInBackground() throws Exception {

						try {
							if(UserDatabaseOperations.isUserAlreadyDeleted(employeeId, "employee")) {
								deletedChecker = true;
								successChecker = true;
								return null;
							}
							else if(DeliveryDatabaseOperations.changeDeliveryDate(DeliveryState.PENDING, deliveryId, new Date(calendar.getTime()
									.getTime())) == 1) {
								alreadyModifiedChecker = true;
							}
							successChecker = true;
						}
						catch (SQLException e) {}
						return null;
					}
					
					@Override
					protected void done() {
						
						colorizeDateInputs(BorderFactory.createLineBorder(Color.BLUE), Color.BLUE);
						SwingUtilities.getWindowAncestor(AcceptDeliveryPanel.this).setEnabled(true);
						if(!successChecker) {
							JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(AcceptDeliveryPanel.this),
								"Pogreška prilikom mijenjanja podataka.", "Pogreška", JOptionPane.ERROR_MESSAGE);
							return;
						}
						SwingUtilities.getWindowAncestor(AcceptDeliveryPanel.this).dispose();
						if(deletedChecker) {
							SwingUtilities.getWindowAncestor(AcceptDeliveryPanel.this).getOwner().dispose();
							if(WindowSizeChecker.checkWindowSize(1400, 800) == 1) {
								new HomeFrame(1000, 600);
							}
							else {
								new HomeFrame(1000, 700);
							}
							return;
						}
						((DeliveriesManagementFrame) SwingUtilities.getWindowAncestor(AcceptDeliveryPanel.this).getOwner())
							.readDeliveriesFromDB(DeliveryState.PENDING);
						if(alreadyModifiedChecker) {
							JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(AcceptDeliveryPanel.this).getOwner(),
								"Dostava je već modificirana.", "Informacija", JOptionPane.INFORMATION_MESSAGE);
							return;
						}
						String activity = "Dostava: " + deliveryId + " prihvaćena.";
						((DeliveriesManagementFrame) SwingUtilities.getWindowAncestor(AcceptDeliveryPanel.this).getOwner())
							.getActivityPanel().addText(activity);
					}
				}.execute();
			}
		});
	}
	
	private void addComponents() {

		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.weightx = 1;
		gridBagConstraints.weighty = 1;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new Insets(0, 20, 0, 20);
		
		add(takeoverDateLabel, gridBagConstraints);	
		
		gridBagConstraints.gridy = 1;
		gridBagConstraints.weighty = 0.5;
		
		add(dayLabel, gridBagConstraints);
		
		gridBagConstraints.gridx = 1;
		
		add(monthLabel, gridBagConstraints);
		
		gridBagConstraints.gridx = 2;
		
		add(yearLabel, gridBagConstraints);
		
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.weighty = 1;
		
		add(days, gridBagConstraints);
		
		gridBagConstraints.gridx = 1;
		
		add(months, gridBagConstraints);
		
		gridBagConstraints.gridx = 2;
		
		add(years, gridBagConstraints);
		
		gridBagConstraints.gridy = 4;
		
		add(acceptDeliveryButton, gridBagConstraints);
		
	}
}
