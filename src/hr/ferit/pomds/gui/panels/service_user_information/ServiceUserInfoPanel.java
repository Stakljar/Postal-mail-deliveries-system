package hr.ferit.pomds.gui.panels.service_user_information;

import java.awt.Component;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingWorker;

import hr.ferit.pomds.data.Country;
import hr.ferit.pomds.data.Town;
import hr.ferit.pomds.db.UserDatabaseOperations;
import hr.ferit.pomds.utils.ComponentDecorator;

public abstract class ServiceUserInfoPanel extends JPanel {

	private static final long serialVersionUID = 4141982141552185105L;

	protected JLabel firstNameLabel;
	protected JLabel lastNameLabel;
	protected JLabel addressLabel;
	protected JLabel citiesLabel;
	protected JLabel countriesLabel;
	protected JButton confirmButton;
	
	protected JTextField firstName;
	protected JTextField lastName;
	protected JTextField address;
	protected JComboBox<Town> citiesBox;
	protected JComboBox<Country> countriesBox;
	protected List<Country> countries;
	protected List<Town> allCities = new LinkedList<>();
	
	public ServiceUserInfoPanel() {
		
		super();
		confirmButton = new JButton("Potvrdi");
		firstNameLabel = new JLabel("Ime:");
		lastNameLabel = new JLabel("Prezime:");
		addressLabel = new JLabel("Adresa:");
		citiesLabel = new JLabel("Mjesto:");
		countriesLabel = new JLabel("Država:");
		countries = new LinkedList<>();
		countriesBox = new JComboBox<>();
		citiesBox = new JComboBox<Town>();
		
		setOpaque(true);
		setBorder(BorderFactory.createCompoundBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40),
				BorderFactory.createLineBorder(ComponentDecorator.getPrimaryBorderColor())), BorderFactory.createEmptyBorder(20, 20, 20, 20)));
		setBackground(ComponentDecorator.getPrimaryBackgroundColor());
	}
	
	protected void configureComponents() {
		
		address.setToolTipText("Ulica ili trg, broj građevine i naselje (u slučaju da naselje nije odabrano mjesto,"
				+ " ali je dio poštanskog ureda tog mjesta)");
		countriesBox.setRenderer(new DefaultListCellRenderer() {
			
			private static final long serialVersionUID = 8423647175715041952L;

			@Override
			public Component getListCellRendererComponent(JList<?> list, Object value, int index, 
					boolean isSelected, boolean cellHasFocus){
				
				Component component = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
				
				if(isSelected) {
					try {
						((JComponent) component).setToolTipText(((JLabel) component).getText().toString());
					} catch (ClassCastException e) {}
				}
				return component;
			}
		});
		
		citiesBox.setRenderer(new DefaultListCellRenderer() {
			
			private static final long serialVersionUID = 1131204015346512612L;

			@Override
			public Component getListCellRendererComponent(JList<?> list, Object value, int index, 
					boolean isSelected, boolean cellHasFocus){
				
				Component component = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
				
				if(isSelected) {	
					((JComponent) component).setToolTipText(((JLabel) component).getText().toString());
				}
				return component;
			}
		});
	}
	
	protected void loadCountries() {
		
		confirmButton.setEnabled(false);
		countriesBox.setEnabled(false);
		citiesBox.setEnabled(false);
		
		new SwingWorker<Object, Object>(){

			List<Country> countries = new LinkedList<>();
			private boolean successChecker = false;
			
			@Override
			protected Object doInBackground() throws Exception {
				
				try {
					countries = UserDatabaseOperations.getAllCountries();
					successChecker = true;
				} catch (SQLException e) {}
				return null;
			}
			
			@Override
			protected void done() {
				
				countriesBox.setEnabled(true);
				if(!successChecker) {
					JOptionPane.showMessageDialog(ServiceUserInfoPanel.this, "Pogreška prilikom učitavanja država.", "Pogreška",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				for (Country country : countries) {
					countriesBox.addItem(country);
				}
				loadCities();
			}
			
		}.execute();
	}
	
	protected void loadCities() {
		
		new SwingWorker<Object, Object>(){

			private boolean successChecker = false;
			
			@Override
			protected Object doInBackground() throws Exception {
				
				try {
					allCities = UserDatabaseOperations.getAllCities();
					successChecker = true;
				} catch (SQLException e) {}
				return null;
			}
			
			@Override
			protected void done() {
				
				confirmButton.setEnabled(true);
				citiesBox.setEnabled(true);
				if(!successChecker) {
					JOptionPane.showMessageDialog(ServiceUserInfoPanel.this, "Pogreška prilikom učitavanja mjesta.", "Pogreška",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				fillCityBox();
			}
			
		}.execute();
	}
	
	protected void fillCityBox() {
		
		citiesBox.removeAllItems();
		List<Town> cities = new LinkedList<>();
		try {
			cities = allCities.stream().filter(it -> it.country().alphaTwoCode().
					equals(((Country) (countriesBox.getSelectedItem())).alphaTwoCode())).collect(Collectors.toList());
		} catch (NullPointerException e) {}
		for(Town city: cities) {
			citiesBox.addItem(city);
		}
	}
	
	protected abstract void addComponents();
}
