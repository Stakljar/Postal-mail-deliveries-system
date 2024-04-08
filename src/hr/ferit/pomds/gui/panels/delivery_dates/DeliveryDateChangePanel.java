package hr.ferit.pomds.gui.panels.delivery_dates;

import java.awt.Color;
import java.sql.Date;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

import hr.ferit.pomds.utils.ComponentDecorator;
import hr.ferit.pomds.utils.format_check.DateChecker;

public class DeliveryDateChangePanel extends JPanel {

	private static final long serialVersionUID = 1212646187590512311L;
	
	protected JLabel dayLabel;
	protected JLabel monthLabel;
	protected JLabel yearLabel;
	
	protected JComboBox<Integer> days;
	protected JComboBox<String> months;
	protected JComboBox<Integer> years;
	
	protected Calendar calendar = Calendar.getInstance();
	
	public DeliveryDateChangePanel() {
		
		super();
		dayLabel = new JLabel("Dan:");
		monthLabel = new JLabel("Mjesec:");
		yearLabel = new JLabel("Godina:");
		days = new JComboBox<>(new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 
				13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31}); 
		months = new JComboBox<>(new String[]{"Siječanj", "Veljača", "Ožujak", "Travanj", "Svibanj", "Lipanj", "Srpanj",
				"Kolovoz", "Rujan", "Listopad", "Studeni", "Prosinac"}); 
		years = new JComboBox<>(fillYears()); 
		
		setOpaque(true);
	}
	
	private Integer[] fillYears() {
		
		List<Integer> years = new LinkedList<>();
		for(int i = 2000; i <= Calendar.getInstance().get(Calendar.YEAR); i++) {
			
			years.add(i);
		}
		return years.toArray(new Integer[years.size()]);
	}
	
	protected boolean checkValidDate(Date date) {

		if(!DateChecker.isDateFormatCorrect(days, months, years)) {
			colorizeDateInputs(BorderFactory.createLineBorder(Color.RED), Color.RED);
			return false;
		}
		return true;
	}
	
	protected void colorizeDateInputs(Border border, Color color) {
		
		ComponentDecorator.setBorder(border, days, months, years);
		ComponentDecorator.setForeground(color, dayLabel, monthLabel, yearLabel, days, months, years);
	}
}
