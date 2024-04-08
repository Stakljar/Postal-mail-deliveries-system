package hr.ferit.pomds.gui.panels.scrollable;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.SwingUtilities;

import hr.ferit.pomds.data.Employee;
import hr.ferit.pomds.gui.frames.EmployeesManagementFrame;
import hr.ferit.pomds.gui.panels.items.EmployeePanel;

public class EmployeesSequencePanel extends ScrollableSequencePanel {

	private static final long serialVersionUID = 1149214712463567100L;
	
	public EmployeesSequencePanel(String id) {
		
		super(id);
	}
	
	protected void addListeners() {

		super.addListeners();
		
		refresh.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				((EmployeesManagementFrame) SwingUtilities.getWindowAncestor(EmployeesSequencePanel.this)).readEmployeesFromDB();
			}
		});
	}

	public <T> void fillSubPanel(List<T> employees){
		
		clearSubPanel();
		changeLoadingVisibility(false);
		for (T employee : employees) {
			subPanel.add(new EmployeePanel((Employee) employee, id));
		}
	}
}
