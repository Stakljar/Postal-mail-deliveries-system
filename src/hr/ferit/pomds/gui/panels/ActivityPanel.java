package hr.ferit.pomds.gui.panels;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

import hr.ferit.pomds.utils.ComponentDecorator;

public class ActivityPanel extends JPanel {

	private static final long serialVersionUID = 7851571865279851221L;
	
	private JLabel title;
	private JScrollPane scrollPane;
	private JTextArea textArea;
	private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
	
	private GridBagConstraints gridBagConstraints = new GridBagConstraints();
	
	public ActivityPanel() {
		
		super();
		title = new JLabel("Nedavna lokalna aktivnost", SwingConstants.CENTER);
		textArea = new JTextArea();
		scrollPane = new JScrollPane(textArea);
		
		setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(ComponentDecorator.getPrimaryBorderColor()),
				BorderFactory.createEmptyBorder(0, 10, 10, 10)));
		setBackground(ComponentDecorator.getPrimaryBackgroundColor());
		setLayout(new GridBagLayout());
		setOpaque(true);
		configureComponents();
		addComponents();
	}
	
	private void configureComponents() {
		
		title.setForeground(ComponentDecorator.getSecondaryTextColor());
		title.setFont(new Font(null, Font.BOLD, 20));
		textArea.setFont(new Font(null, Font.PLAIN, 16));
		textArea.setEditable(false);
	}
	
	public void addText(String text) {
		
		Calendar calendar = Calendar.getInstance();
		textArea.setText(textArea.getText() + simpleDateFormat.format(calendar.getTime()) + " " + text + "\n");
	}
	
	private void addComponents() {
		
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 1;
		gridBagConstraints.weighty = 0.05;
		
		add(title, gridBagConstraints);
		
		gridBagConstraints.gridy = 1;
		gridBagConstraints.weighty = 0.95;
		
		add(scrollPane, gridBagConstraints);
	}
}
