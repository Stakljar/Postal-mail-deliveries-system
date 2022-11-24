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
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import hr.ferit.pomds.db.DeliveryDatabaseOperations;
import hr.ferit.pomds.db.UserDatabaseOperations;
import hr.ferit.pomds.gui.frames.DeliveriesManagementFrame;
import hr.ferit.pomds.gui.frames.HomeFrame;
import hr.ferit.pomds.utils.ComponentDecorator;
import hr.ferit.pomds.utils.DeliveryState;
import hr.ferit.pomds.utils.WindowSizeChecker;

public class FinishDeliveryPanel extends DeliveryDateChangePanel {

	private static final long serialVersionUID = 2418593785185612421L;
	
	private String deliveryId;
	private String employeeId;
	private Date takeoverDate;
	
	private JRadioButton successfullDelivery;
	private JRadioButton unsuccessfullDelivery;
	private ButtonGroup buttonGroup;
	
	private JLabel deliveryCompletionDateLabel;
	private JButton finishDeliveryButton;
	
	private GridBagConstraints gridBagConstraints = new GridBagConstraints();
	
	public FinishDeliveryPanel(String deliveryId, String employeeId, Date takeoverDate) {
		
		super();
		this.deliveryId = deliveryId;
		this.employeeId = employeeId;
		this.takeoverDate = takeoverDate;

		successfullDelivery = new JRadioButton("Uspješna dostava");
		unsuccessfullDelivery = new JRadioButton("Neuspješna dostava");
		buttonGroup = new ButtonGroup();
		finishDeliveryButton = new JButton("Završi dostavu");
		deliveryCompletionDateLabel = new JLabel("Datum završetka:");
		
		setLayout(new GridBagLayout());
		setBorder(BorderFactory.createLineBorder(Color.BLUE));
		setBackground(ComponentDecorator.getPrimaryBackgroundColor());
		addComponents();
		configureComponents();
	}
	
	private void configureComponents() {
		
		addListeners();
		
		buttonGroup.add(successfullDelivery);
		buttonGroup.add(unsuccessfullDelivery);
		days.setSelectedItem(calendar.get(Calendar.DAY_OF_MONTH));
		months.setSelectedIndex(calendar.get(Calendar.MONTH));
		years.setSelectedItem(calendar.get(Calendar.YEAR));
		successfullDelivery.setSelected(true);
		
		ComponentDecorator.addDefaultColor(finishDeliveryButton, days, months, years);
		ComponentDecorator.setBackground(ComponentDecorator.getPrimaryBackgroundColor(), successfullDelivery, unsuccessfullDelivery);
		ComponentDecorator.setForeground(ComponentDecorator.getPrimaryTextColor(), dayLabel, monthLabel, yearLabel,
				deliveryCompletionDateLabel);
		ComponentDecorator.setForeground(Color.BLUE, successfullDelivery, unsuccessfullDelivery);
		ComponentDecorator.setFont(new Font(null, Font.PLAIN, 16), dayLabel, monthLabel, yearLabel, days, months, years,
				successfullDelivery, unsuccessfullDelivery);
		deliveryCompletionDateLabel.setFont(new Font(null, Font.BOLD, 16));
		finishDeliveryButton.setFont(new Font(null, Font.PLAIN, 14));
		ComponentDecorator.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.BLUE),
				BorderFactory.createEmptyBorder(0, 4, 0, 0)), days, months, years);
	}
	
	private void addListeners() {
		
		unsuccessfullDelivery.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				
				if(unsuccessfullDelivery.isSelected()) {
					changeState(false);
				}
				else {
					changeState(true);
				}
			}
		});
		
		finishDeliveryButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if(successfullDelivery.isSelected()) {
					calendar.set(Calendar.DAY_OF_MONTH, (int) days.getSelectedItem());
					calendar.set(Calendar.MONTH, months.getSelectedIndex());
					calendar.set(Calendar.YEAR, (int) years.getSelectedItem());
					
					if(!checkValidDate(new Date(calendar.getTime().getTime()))) {
						JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(FinishDeliveryPanel.this),
								"Neispravan datum.", "Pogreška", JOptionPane.ERROR_MESSAGE);
						return;
					}
				}
				
				SwingUtilities.getWindowAncestor(FinishDeliveryPanel.this).setEnabled(false);
				
				new SwingWorker<Object, Object>() {

					private boolean successChecker = false;
					private boolean deletedChecker = false;
					private boolean deliverySuccessStateChecker = false;
					private boolean alreadyFinishedChecker = false;
					
					@Override
					protected Object doInBackground() throws Exception {

						if(successfullDelivery.isSelected()) {
							try {
								if(UserDatabaseOperations.isUserAlreadyDeleted(employeeId, "employee")) {
									deletedChecker = true;
									successChecker = true;
									return null;
								}
								else if(DeliveryDatabaseOperations.changeDeliveryDate(DeliveryState.ACTIVE, deliveryId, new Date(calendar.getTime()
										.getTime())) == 1) {
									alreadyFinishedChecker = true;
								}
								successChecker = true;
								deliverySuccessStateChecker = true;
							} catch (SQLException e1) {e1.printStackTrace();}
						}
						else {
							try {
								if(UserDatabaseOperations.isUserAlreadyDeleted(employeeId, "employee")) {
									deletedChecker = true;
									successChecker = true;
									return null;
								}
								else if(DeliveryDatabaseOperations.failDelivery(deliveryId) == 1) {
									alreadyFinishedChecker = true;
								}
								successChecker = true;
							} catch (SQLException e1) {}
						}
						return null;
					}
					
					@Override
					protected void done() {
						
						colorizeDateInputs(BorderFactory.createLineBorder(Color.BLUE), Color.BLUE);
						SwingUtilities.getWindowAncestor(FinishDeliveryPanel.this).setEnabled(true);
						if(!successChecker) {
							JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(FinishDeliveryPanel.this),
									"Pogreška prilikom mijenjanja podataka.", "Pogreška", JOptionPane.ERROR_MESSAGE);
							return;
						}
						if(deletedChecker) {
							SwingUtilities.getWindowAncestor(FinishDeliveryPanel.this).dispose();
							SwingUtilities.getWindowAncestor(FinishDeliveryPanel.this).getOwner().dispose();
							if(WindowSizeChecker.checkWindowSize(1400, 800) == 1) {
								new HomeFrame(1000, 600);
							}
							else {
								new HomeFrame(1000, 700);
							}
							return;
						}
						SwingUtilities.getWindowAncestor(FinishDeliveryPanel.this).dispose();	
						((DeliveriesManagementFrame) SwingUtilities.getWindowAncestor(FinishDeliveryPanel.this).getOwner())
							.readDeliveriesFromDB(DeliveryState.ACTIVE);
						if(alreadyFinishedChecker) {
							JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(FinishDeliveryPanel.this).getOwner(),
									"Dostava je već završena.", "Informacija", JOptionPane.INFORMATION_MESSAGE);
							return;
						}
						String activity = "Dostava: " + deliveryId + (deliverySuccessStateChecker ? " završena." : " neuspješna.");
						((DeliveriesManagementFrame) SwingUtilities.getWindowAncestor(FinishDeliveryPanel.this).getOwner())
							.getActivityPanel().addText(activity);
					}
				}.execute();
			}
		});
	}
	
	private void changeState(boolean value) {
		
		deliveryCompletionDateLabel.setVisible(value);
		dayLabel.setVisible(value);
		monthLabel.setVisible(value);
		yearLabel.setVisible(value);
		days.setVisible(value);
		months.setVisible(value);
		years.setVisible(value);
	}
	
	@Override
	protected boolean checkValidDate(Date date) {

		if (!super.checkValidDate(date)){
			return false;
		}
		if(date.compareTo(takeoverDate) < 0) {
			colorizeDateInputs(BorderFactory.createLineBorder(Color.RED), Color.RED);
			return false;
		}
		return true;
	}

	private void addComponents() {
		
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.weightx = 1;
		gridBagConstraints.weighty = 1;
		gridBagConstraints.anchor = GridBagConstraints.CENTER;
		
		add(successfullDelivery, gridBagConstraints);
		
		gridBagConstraints.gridx = 1;
		
		add(unsuccessfullDelivery, gridBagConstraints);
		
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new Insets(0, 20, 0, 20);
		
		add(deliveryCompletionDateLabel, gridBagConstraints);	
		
		gridBagConstraints.gridy = 2;
		gridBagConstraints.weighty = 0.5;

		add(dayLabel, gridBagConstraints);
		
		gridBagConstraints.gridx = 1;
		
		add(monthLabel, gridBagConstraints);
		
		gridBagConstraints.gridx = 2;
		
		add(yearLabel, gridBagConstraints);
		
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 3;
		gridBagConstraints.weighty = 1;
		
		add(days, gridBagConstraints);
		
		gridBagConstraints.gridx = 1;
		
		add(months, gridBagConstraints);
		
		gridBagConstraints.gridx = 2;
		
		add(years, gridBagConstraints);
		
		gridBagConstraints.gridy = 4;
		
		add(finishDeliveryButton, gridBagConstraints);
	}
}
