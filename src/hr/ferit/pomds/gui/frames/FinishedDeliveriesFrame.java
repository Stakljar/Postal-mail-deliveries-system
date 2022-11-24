package hr.ferit.pomds.gui.frames;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import hr.ferit.pomds.data.Delivery;
import hr.ferit.pomds.db.DeliveryDatabaseOperations;
import hr.ferit.pomds.db.UserDatabaseOperations;
import hr.ferit.pomds.utils.AdaptedDateFormat;
import hr.ferit.pomds.utils.ComponentDecorator;
import hr.ferit.pomds.utils.DeliveryState;
import hr.ferit.pomds.utils.WindowSizeChecker;

public class FinishedDeliveriesFrame extends DefaultFrame {

	private static final long serialVersionUID = 1049158238571741752L;
	
	private JFrame owner;
	private JTable table;
	private JScrollPane scrollPane;
	private DefaultTableModel tableModel;
	private JLabel deliveriesDeletionLabel;
	private JButton deliveriesDeletion;
	private JPopupMenu menu;
	private JMenuItem refresh;
	private String employeeId;
	private Timer timer;
	
	private GridBagConstraints gridBagConstraints = new GridBagConstraints();
	
	public FinishedDeliveriesFrame(JFrame owner, String id, int width, int height) {
		
		super("Sustav dostava poštanskih pošiljki");
		this.owner = owner;
		this.employeeId = id;
		deliveriesDeletionLabel = new JLabel("Izbriši označene dostave:");
		deliveriesDeletion = new JButton("Izbriši");
		menu = new JPopupMenu();
		refresh = new JMenuItem("Osvježi", new ImageIcon("resources\\images\\refresh.jpg"));

		tableModel = new DefaultTableModel() {

			private static final long serialVersionUID = 1123651456512641494L;
			
			@Override
			public boolean isCellEditable(int row, int column) {
				
				return false;
			}
		
		};
		
		DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
			
			private static final long serialVersionUID = 8417576151165712481L;

			@Override
			public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
				
				Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				if(isSelected) {
					setForeground(new Color(0, 73, 181));
					setBorder(BorderFactory.createLineBorder(Color.BLACK));
				}
				else {
					setForeground(Color.BLACK);
					setBorder(null);
				}
				return component;
			}
		};
		
		table = new JTable(tableModel) {
			
			private static final long serialVersionUID = 8593288235872641224L;

			@Override
			public String getToolTipText(MouseEvent event) {
				
				Point point = event.getPoint();
				int rowIndex = rowAtPoint(point);
				int colIndex = columnAtPoint(point);
				Object value = null;
				try {
					value = getValueAt(rowIndex, colIndex);
				}
				catch(ArrayIndexOutOfBoundsException e) {}
				return value == null ? null: value.toString();
			}
			
			@Override
			public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
				
				Component component = super.prepareRenderer(renderer, row, column);
		        if(getModel().getValueAt(row, 9) == null) {
		        	component.setBackground(new Color(255, 194, 194));
		        }
		        else {
		        	component.setBackground(new Color(194, 255, 201));
		        }
				return component;
			}
			
			@Override
			public TableCellRenderer getCellRenderer(int row, int column) {

				return renderer;
		    }
		};
		
		timer = new Timer(5 * 60 * 1000, new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				setupTable();
			}
		});
		table.setDefaultRenderer(JLabel.class, renderer);
		scrollPane = new JScrollPane(table);
		
		setLayout(new GridBagLayout());
		getContentPane().setBackground(ComponentDecorator.getPrimaryBackgroundColor());
		configureWindow(width, height, 1000, 700, JFrame.DISPOSE_ON_CLOSE);
		configureComponents();
		addComponents();
		setupTable();
	}

	private void configureComponents() {
		
		addListeners();
		
		scrollPane.setOpaque(true);
		
		tableModel.addColumn("ID");
		tableModel.addColumn("Pošiljateljevo korisničko ime");
		tableModel.addColumn("Pošiljateljevo ime i prezime");
		tableModel.addColumn("Primateljevo korisničko ime");
		tableModel.addColumn("Primateljevo ime i prezime");
		tableModel.addColumn("Izvorišna adresa");
		tableModel.addColumn("Odredišna adresa");
		tableModel.addColumn("Pošiljka");
		tableModel.addColumn("Datum preuzimanja");
		tableModel.addColumn("Datum završetka");
		
		table.removeColumn(table.getColumnModel().getColumn(0));
		table.setFillsViewportHeight(true);
		table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		table.getTableHeader().setFont(new Font(null, Font.BOLD, 16));
		table.setFont(new Font(null, Font.PLAIN, 16));
		table.setRowHeight(22);
		
		ComponentDecorator.addDefaultColor(new JButton(), deliveriesDeletion);
		deliveriesDeletionLabel.setForeground(new Color(2, 40, 210));
		deliveriesDeletion.setBackground(new Color(232, 238, 252));
		deliveriesDeletionLabel.setFont(new Font(null, Font.PLAIN, 20));
		deliveriesDeletion.setFont(new Font(null, Font.PLAIN, 20));
		setTableColumnWidths();
		
		timer.setRepeats(true);
		timer.start();
	}

	private void setTableColumnWidths() {
		
		for(int i = 0; i < table.getColumnCount(); i++) {
			
			if(i == 1 || i == 3) {
				table.getColumnModel().getColumn(i).setPreferredWidth(150);
			}
			else if(i == 4 || i == 5){
				table.getColumnModel().getColumn(i).setPreferredWidth(200);
			}
			else if(i == 6) {
				table.getColumnModel().getColumn(i).setPreferredWidth(170);
			}
			else {
				table.getColumnModel().getColumn(i).setPreferredWidth(100);
			}
		}
	}
	
	private void setupTable() {
		
		table.setEnabled(false);
		deliveriesDeletion.setEnabled(false);
		FinishedDeliveriesFrame.this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		new SwingWorker<Object, Object>(){

			private List<Delivery> deliveries;
			
			@Override
			protected Object doInBackground() throws Exception {
				
				deliveries = loadDeliveries();
				return null;
			}
			
			@Override
			protected void done() {
				
				table.setEnabled(true);
				deliveriesDeletion.setEnabled(true);
				FinishedDeliveriesFrame.this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				fillTable(deliveries);
			}
			
		}.execute();
	}
	
	private List<Delivery> loadDeliveries() {
		
		List<Delivery> deliveries = new LinkedList<>();
		try {
			deliveries = DeliveryDatabaseOperations.getAllDeliveries(DeliveryState.FINISHED);
		} catch (SQLException e) {
			SwingUtilities.invokeLater(new Runnable() {
				
				@Override
				public void run() {
					JOptionPane.showMessageDialog(owner, "Pogreška prilikom učitavanja podataka.", "Pogreška", JOptionPane.ERROR_MESSAGE);
				}
			});
		}
		return deliveries;
	}
	
	private void fillTable(List<Delivery> deliveries) {
		
		for(int i = 0; i < tableModel.getRowCount(); i++) {
			tableModel.removeRow(i--);
		}
		for(Delivery delivery: deliveries) {
			String fragile = "";
			String type = "";
			if(delivery.mail().isFragile()) {
				fragile += ", krhka";
			}
			if(delivery.mail().type().toLowerCase().equals("package")) {
				type = "Paket:";
			}
			else {
				type = "Pismo";
			}
			tableModel.addRow(new Object[]{delivery.id(), delivery.sender().username(), 
					delivery.sender().id() == null ? null :  delivery.sender().firstName() + " " + delivery.sender().lastName(),
					delivery.recipient().username(), delivery.recipient().id() == null ? null : delivery.recipient().firstName()
					+ " " + delivery.recipient().lastName(), delivery.sender().id() == null ? null : delivery.sender()
					.address() + ", " + delivery.sender().town().name() + " " + delivery.sender().town().postalCode()
					+ ", " + delivery.sender().town().country(), delivery.recipient().id() == null ? null : 
					delivery.recipient().address() + ", " + delivery.recipient().town().name()
					+ " " + delivery.recipient().town().postalCode() + ", " + delivery.recipient().town().country(), 
					type + " " + (delivery.mail().name() == null ? "" : delivery.mail().name()) + fragile,
					(delivery.takeoverDate() == null ?  delivery.takeoverDate() : AdaptedDateFormat.getDateFormat().
					format(delivery.takeoverDate())), (delivery.completionDate() == null ?  delivery.completionDate() : 
					AdaptedDateFormat.getDateFormat().format(delivery.completionDate()))});
		}
	}

	private void addListeners() {
		
		refresh.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
			
				setupTable();
			}
		});
		
		table.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mousePressed(MouseEvent e) {
				
				if(e.getButton() == 3) {
					 menu.show(table, e.getX(), e.getY());
				}
			}
		});
		
		this.addWindowListener(new WindowAdapter() {
			
			@Override
			public void windowClosed(WindowEvent e) {
				
				timer.stop();
				owner.setFocusable(true);
				owner.setEnabled(true);
				owner.toFront();
			}
		});
		
		table.getTableHeader().addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseClicked(MouseEvent e) {
				
				table.setEnabled(false);
				deliveriesDeletion.setEnabled(false);
				FinishedDeliveriesFrame.this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
				int columnIndex = table.getTableHeader().columnAtPoint(e.getPoint());
				
				new SwingWorker<Object, Object>(){

					private List<Delivery> deliveries;
					
					@Override
					protected Object doInBackground() throws Exception {
						
						deliveries = loadDeliveries();
						Comparator<Delivery> comparator;
						if(columnIndex == 0) {
							comparator = new Comparator<Delivery>() {

								@Override
								public int compare(Delivery o1, Delivery o2) {
									return (o1.sender().username().toLowerCase()
											.compareTo(o2.sender().username().toLowerCase()));
								}
							};
						}
						else if(columnIndex == 1) {
							comparator = new Comparator<Delivery>() {

								@Override
								public int compare(Delivery o1, Delivery o2) {
									return (o1.sender().firstName() + " " + o1.sender().lastName()).toLowerCase()
											.compareTo((o2.sender().firstName() + " " + o2.sender().lastName()).toLowerCase());
								}
							};
						}
						else if(columnIndex == 2) {
							comparator = new Comparator<Delivery>() {

								@Override
								public int compare(Delivery o1, Delivery o2) {
									return (o1.recipient().username().toLowerCase()
											.compareTo(o2.recipient().username().toLowerCase()));
								}
							};
						}
						else if(columnIndex == 3) {
							comparator = new Comparator<Delivery>() {

								@Override
								public int compare(Delivery o1, Delivery o2) {
									return (o1.recipient().firstName() + " " + o1.recipient().lastName()).toLowerCase()
											.compareTo((o2.recipient().firstName() + " " + o2.recipient().lastName()).toLowerCase());
								}
							};
						}
						else if(columnIndex == 4) {
							comparator = new Comparator<Delivery>() {

								@Override
								public int compare(Delivery o1, Delivery o2) {
									return (o1.sender().address() + " " + o1.sender().town().name() + " " + o1.sender().town().postalCode()
											+ ", " + o1.sender().town().country()).toLowerCase()
											.compareTo((o2.sender().address() + " " + o2.sender().town().name() + " " + o2.sender().town().postalCode()
													+ ", " + o2.sender().town().country()).toLowerCase());
								}
							};
						}
						else if(columnIndex == 5) {
							comparator = new Comparator<Delivery>() {

								@Override
								public int compare(Delivery o1, Delivery o2) {
									return (o1.recipient().address() + " " + o1.recipient().town().name() + " " + o1.recipient().town().postalCode()
											+ ", " + o1.recipient().town().country()).toLowerCase()
											.compareTo((o2.recipient().address() + " " + o2.recipient().town().name() + " " + o2.recipient().town().postalCode()
													+ ", " + o2.recipient().town().country()).toLowerCase());
								}
							};
						}
						else if(columnIndex == 6) {
							comparator = new Comparator<Delivery>() {

								@Override
								public int compare(Delivery o1, Delivery o2) {
									
									String fragileFirst = "";
									String fragileSecond = "";
									if(o1.mail().isFragile()) {
										fragileFirst += ", krhko";
									}
									if(o2.mail().isFragile()) {
										fragileSecond += ", krhko";
									}
									return (o1.mail().type() + " " + o1.mail().name() + fragileFirst).toLowerCase()
											.compareTo((o2.mail().type() + " " + o2.mail().name() + fragileSecond).toLowerCase());
								}
							};
						}
						else if(columnIndex == 7) {
							comparator = new Comparator<Delivery>() {

								@Override
								public int compare(Delivery o1, Delivery o2) {
									return o1.takeoverDate().compareTo(o2.takeoverDate());
						
								}
							};
						}
						else {
							comparator = new Comparator<Delivery>() {

								@Override
								public int compare(Delivery o1, Delivery o2) {
									return o1.completionDate().compareTo(o2.completionDate());

								}
							};
						}
						deliveries.sort(comparator);
						return null;
					}
					
					@Override
					protected void done() {
						
						fillTable(deliveries);
						table.setEnabled(true);
						deliveriesDeletion.setEnabled(true);
						FinishedDeliveriesFrame.this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
					}
					
				}.execute();
			}
		});
		
		deliveriesDeletion.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if(table.getSelectedRowCount() == 0) {
					JOptionPane.showMessageDialog(FinishedDeliveriesFrame.this, "Nema odabranih redaka.", "Brisanje dostava",
							JOptionPane.INFORMATION_MESSAGE);
					return;
				}
				
				if(JOptionPane.showOptionDialog(FinishedDeliveriesFrame.this,
						"Da li ste sigurni da želite izbrisati odabrane dostave?",
						"Brisanje dostava", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null,
						new String[] {"      Da      ", "      Ne      "}, null) != 0) {
					return;
				}
				
				table.setEnabled(false);
				deliveriesDeletion.setEnabled(false);
				FinishedDeliveriesFrame.this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
				
				new SwingWorker<Object, Object>(){
					
					private boolean successChecker = false;
					private boolean deletedChecker = false;
					
					@Override
					protected Object doInBackground() throws Exception {
						
						try {
							if(UserDatabaseOperations.isUserAlreadyDeleted(employeeId, "employee")) {
								deletedChecker = true;
								successChecker = true;
								return null;
							}
							for(int i = 0; i < tableModel.getRowCount(); i++) {
								if(table.isRowSelected(i)){
									DeliveryDatabaseOperations.deleteDelivery((String) table.getModel().getValueAt(i, 0));
								}
							}
							successChecker = true;
						} catch (SQLException e1) {}
						return null;
					}
					
					@Override
					protected void done() {
						
						table.setEnabled(true);
						deliveriesDeletion.setEnabled(true);
						FinishedDeliveriesFrame.this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
						if(!successChecker) {
							JOptionPane.showMessageDialog(FinishedDeliveriesFrame.this, "Pogreška prilikom brisanja podataka.", "Pogreška",
									JOptionPane.ERROR_MESSAGE);
							return;
						}
						if(deletedChecker) {
							FinishedDeliveriesFrame.this.dispose();
							owner.dispose();
							if(WindowSizeChecker.checkWindowSize(1400, 800) == 1) {
								new HomeFrame(1000, 600);
							}
							else {
								new HomeFrame(1000, 700);
							}
							return;
						}
						setupTable();
					}
				}.execute();
			}
		});
	}
	
	private void addComponents() {
		
		menu.add(refresh);
		
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.weightx = 1;
		gridBagConstraints.weighty = 1;
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		
		add(scrollPane, gridBagConstraints);
		
		gridBagConstraints.gridwidth = 1;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.weighty = 0.1;
		gridBagConstraints.fill = GridBagConstraints.NONE;
		gridBagConstraints.insets = new Insets(0, 0, 0, 30);
		gridBagConstraints.anchor = GridBagConstraints.EAST;
		
		add(deliveriesDeletionLabel, gridBagConstraints);
		
		gridBagConstraints.gridx = 1;
		gridBagConstraints.insets = new Insets(0, 30, 0, 0);
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		
		add(deliveriesDeletion, gridBagConstraints);
	}
}