package hr.ferit.pomds.gui.panels.scrollable;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.SwingUtilities;

import hr.ferit.pomds.data.Delivery;
import hr.ferit.pomds.gui.frames.ServiceUserFrame;
import hr.ferit.pomds.gui.panels.items.UserDeliveryPanel;

public class UserDeliveriesSequencePanel extends ScrollableSequencePanel {

	private static final long serialVersionUID = 6532789563795234112L;
	
	public UserDeliveriesSequencePanel(String id) {
		
		super(id);
	}
	
	protected void addListeners() {

		super.addListeners();
		
		refresh.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				((ServiceUserFrame) SwingUtilities.getWindowAncestor(UserDeliveriesSequencePanel.this)).readDeliveriesFromDB();
			}
		});
	}

	public <T> void fillSubPanel(List<T> deliveries){
		
		clearSubPanel();
		changeLoadingVisibility(false);
		for (T delivery : deliveries) {
			subPanel.add(new UserDeliveryPanel((Delivery) delivery, id));
		}
	}
}
