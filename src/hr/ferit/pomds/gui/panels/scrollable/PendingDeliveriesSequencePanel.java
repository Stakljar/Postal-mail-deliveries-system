package hr.ferit.pomds.gui.panels.scrollable;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.SwingUtilities;

import hr.ferit.pomds.data.Delivery;
import hr.ferit.pomds.gui.frames.DeliveriesManagementFrame;
import hr.ferit.pomds.gui.panels.items.PendingDeliveryPanel;
import hr.ferit.pomds.utils.DeliveryState;

public class PendingDeliveriesSequencePanel extends ScrollableSequencePanel {

	private static final long serialVersionUID = 4134341416451507522L;

	public PendingDeliveriesSequencePanel(String id) {
		
		super(id);
	}
	
	protected void addListeners() {

		super.addListeners();
		
		refresh.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				((DeliveriesManagementFrame) SwingUtilities.getWindowAncestor(PendingDeliveriesSequencePanel.this)).
					readDeliveriesFromDB(DeliveryState.PENDING);
			}
		});
	}
	
	public <T> void fillSubPanel(List<T> deliveries){
		
		clearSubPanel();
		changeLoadingVisibility(false);
		for (T delivery : deliveries) {
			subPanel.add(new PendingDeliveryPanel((Delivery) delivery, id));
		}
	}
}
