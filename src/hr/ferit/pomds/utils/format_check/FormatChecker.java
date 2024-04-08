package hr.ferit.pomds.utils.format_check;

import javax.swing.JPanel;
import javax.swing.JTextField;

public interface FormatChecker {
		
	boolean isFormatCorrect(JPanel panel, JTextField... textFields);
}
