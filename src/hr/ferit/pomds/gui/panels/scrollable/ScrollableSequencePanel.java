package hr.ferit.pomds.gui.panels.scrollable;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;

public abstract class ScrollableSequencePanel extends JPanel{

	private static final long serialVersionUID = 1110746104019562312L;
	protected JScrollPane scrollPane;
	protected JPanel subPanel;
	private JPanel topPanel;
	protected JPopupMenu menu;
	protected JMenuItem refresh;
	private JLabel loadingLabel;
	protected String id;
	
	private GridBagConstraints gridBagConstraints = new GridBagConstraints();
	
	public ScrollableSequencePanel(String id) {
		
		super();
		this.id = id;
		subPanel = new JPanel();
		topPanel = new JPanel();
		scrollPane = new JScrollPane(topPanel);
		menu = new JPopupMenu();
		refresh = new JMenuItem("Osvježi", new ImageIcon("resources\\images\\refresh.jpg"));
		loadingLabel = new JLabel("Učitavanje...");

		setBorder(BorderFactory.createLineBorder(new Color(0, 18, 117)));
		setLayout(new GridBagLayout());
		setBackground(Color.WHITE);
		setOpaque(true);
		setComponentPopupMenu(menu);
		configureComponents();
		addComponents();
	}
	
	protected  void addListeners() {
		
		scrollPane.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mousePressed(MouseEvent e) {
				
				if(e.getButton() == 3) {
					 menu.show(e.getComponent(), e.getX(), e.getY());
				}
			}
		});	
	}
	
	protected abstract <T> void fillSubPanel(List<T> items);
	
	public void configureComponents() {
		
		addListeners();
	
		topPanel.setLayout(new GridBagLayout());
		topPanel.setOpaque(true);
		subPanel.setBackground(new Color(240, 240, 255));
		subPanel.setLayout(new BoxLayout(subPanel, BoxLayout.Y_AXIS));
		subPanel.setOpaque(true);
		scrollPane.setOpaque(true);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		loadingLabel.setFont(new Font(null, Font.PLAIN, 16));
		loadingLabel.setVisible(false);
	}
	
	public void clearSubPanel() {
		
		subPanel.removeAll();
		subPanel.revalidate();
		subPanel.repaint();
	}

	public void changeLoadingVisibility(boolean visible) {
		
		loadingLabel.setVisible(visible);
	}
	
	private void addComponents() {
		
		menu.add(refresh);
		
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.weightx = 1;
		gridBagConstraints.weighty = 1;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = GridBagConstraints.NORTH;
		
		topPanel.add(subPanel, gridBagConstraints);
		
		gridBagConstraints.fill = GridBagConstraints.NONE;
		gridBagConstraints.anchor = GridBagConstraints.CENTER;
		
		add(loadingLabel, gridBagConstraints);
		
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		
		add(scrollPane, gridBagConstraints);
	}
}
