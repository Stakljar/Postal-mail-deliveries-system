package hr.ferit.pomds.utils.format_check;

import javax.swing.JComboBox;

public class DateChecker {

	public static boolean isDateFormatCorrect(JComboBox<Integer> daysComboBox, JComboBox<String> monthsComboBox,
			JComboBox<Integer> yearsComboBox) {
		
		if(monthsComboBox.getSelectedIndex() == 1) {
			if((Integer)yearsComboBox.getSelectedItem() % 4 == 0) {
				if((Integer)daysComboBox.getSelectedItem() > 29) {
					return false;
				}
			}
			else {
				if((Integer)daysComboBox.getSelectedItem() > 28) {
					return false;
				}
			}
		}
		else if((monthsComboBox.getSelectedIndex() <= 6 && monthsComboBox.getSelectedIndex() % 2 != 0) ||
				(monthsComboBox.getSelectedIndex() > 6 && monthsComboBox.getSelectedIndex() % 2 == 0)) {
			if((Integer)daysComboBox.getSelectedItem() > 30) {
				return false;
			}
		}
		return true;
	}
}
