package hr.ferit.pomds.gui.panels.scrollable;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.SwingUtilities;

import hr.ferit.pomds.data.Delivery;
import hr.ferit.pomds.gui.frames.DeliveriesManagementFrame;
import hr.ferit.pomds.gui.panels.items.ActiveDeliveryPanel;
import hr.ferit.pomds.utils.DeliveryState;

public class ActiveDeliveriesSequencePanel extends ScrollableSequencePanel {

	private static final long serialVersionUID = 7124984161427182345L;
	
	public ActiveDeliveriesSequencePanel(String id) {
		
		super(id);
	}
	
	protected void addListeners() {

		super.addListeners();
		
		refresh.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				((DeliveriesManagementFrame) SwingUtilities.getWindowAncestor(ActiveDeliveriesSequencePanel.this)).
					readDeliveriesFromDB(DeliveryState.ACTIVE);
			}
		});
	}
	
	public <T> void fillSubPanel(List<T> deliveries){
		
		clearSubPanel();
		changeLoadingVisibility(false);
		for (T delivery : deliveries) {
			subPanel.add(new ActiveDeliveryPanel((Delivery) delivery, id));
		}
	}
}