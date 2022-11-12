package hr.ferit.pomds.utils.format_check;

import java.awt.Color;
import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import hr.ferit.pomds.utils.TextOperations;

public class LimitChecker implements FormatChecker {

	private Integer[] textSizes;
	
	public LimitChecker(Integer[] textSizes) {
		
		this.textSizes = textSizes;
	}
	
	public boolean isFormatCorrect(JPanel panel, JTextField... textFields) {
		
		boolean checker = true;
		for (int i = 0; i < textFields.length; i++) {
			if(!TextOperations.isTextSizeUnderLimit(textFields[i].getText(), textSizes[i])) {
				checker = false;
				textFields[i].setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.RED),
						BorderFactory.createEmptyBorder(0, 4, 0, 0)));
			}
		}
		if(checker == false) {	
			JOptionPane.showMessageDialog(panel, "Unos predugačak.", "Pogreška", JOptionPane.ERROR_MESSAGE);
		}
		return checker;
	}
}
