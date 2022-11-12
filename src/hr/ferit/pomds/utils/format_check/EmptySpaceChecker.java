package hr.ferit.pomds.utils.format_check;

import java.awt.Color;
import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class EmptySpaceChecker implements FormatChecker {

	private FormatChecker next;
	
	public EmptySpaceChecker(FormatChecker next) {
		
		this.next = next;
	}
	
	public boolean isFormatCorrect(JPanel panel, JTextField... textFields) {
		
		boolean checker = true;
		for (JTextField textField : textFields) {
				if(textField.getText().isBlank()) {
					textField.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.RED),
							BorderFactory.createEmptyBorder(0, 4, 0, 0)));
					checker = false;
				}
		}
		if(checker == false) {	
			JOptionPane.showMessageDialog(panel, "Unos prazan.", "Pogreška", JOptionPane.ERROR_MESSAGE);
			return checker;
		}
		else {
			return next.isFormatCorrect(panel, textFields);
		}
	}
}
