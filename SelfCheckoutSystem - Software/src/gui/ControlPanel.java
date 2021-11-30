package gui;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import org.lsmr.selfcheckout.Banknote;
import org.lsmr.selfcheckout.PriceLookupCode;
import org.lsmr.selfcheckout.devices.OverloadException;
import org.lsmr.selfcheckout.devices.SimulationException;

import attendantConsole.AttendantConsole;
import unSorted.GUIDispatch;
import unSorted.SelfCheckoutStationInstance;
import unSorted.Session;
import unSorted.StationUsage;

/*
 * Represents a singleton for the Control Panel.
 * Designed with the assumption that the store only has and needs
 * one control panel.
 */
public class ControlPanel {
	private static ControlPanel instance;
	private AttendantConsole ac = new AttendantConsole();
	private String attendantName;
	private JTabbedPane mainPanel;
	private JLayeredPane loginPanel, lookupTab;
	private ArrayList<JLabel> labels = new ArrayList<JLabel>();
	private JFrame mainFrame = new JFrame("Control Panel");
	private JFrame loginFrame = new JFrame("Login Screen");
	private JTextField userName = new JTextField(16);
	private JTextField userPassword = new JTextField(16);
	private Map<Integer, JList<String>> stationMaintenance = new HashMap<>();
	
	public static ControlPanel getInstance() {
		if (instance == null) {
			instance = new ControlPanel();
		} 
		return instance;
	}
	
	private ControlPanel() {
		mainPanel = new JTabbedPane();
		mainFrame.setContentPane(mainPanel);
		mainFrame.setPreferredSize(new Dimension(640, 480));
		buildLoginFrame();
		buildStationTab(1);
		buildStationTab(2);
		buildLookupTab();
		
		mainFrame.addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(java.awt.event.WindowEvent windowEvent) {
				JOptionPane.showMessageDialog(mainPanel.getParent(),
						"Cannot close attendant window while simulation is running!", "Error",
						JOptionPane.ERROR_MESSAGE);
			}
		});

		mainFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		mainFrame.setResizable(false);
		mainFrame.setTitle("Attendant control panel");
		mainFrame.pack();
		mainFrame.setVisible(false);

		loginFrame.addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(java.awt.event.WindowEvent windowEvent) {
				JOptionPane.showMessageDialog(mainPanel.getParent(),
						"Cannot close attendant window while simulation is running!", "Error",
						JOptionPane.ERROR_MESSAGE);
			}
		});
		loginFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		loginFrame.setResizable(false);
		loginFrame.setTitle("Login Screen");
		loginFrame.pack();
		loginFrame.setVisible(true);
	}

	private void buildStationTab(int stationNumber) {
		JLayeredPane mainTab = new JLayeredPane();
		mainTab.setLayout(new GridLayout(0, 2));
		JButton blockButton = new JButton("Block Station");
		blockButton.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent arg0) {
				if (blockButton.getText().equalsIgnoreCase("Block Station")) {
					blockButton.setText("Unblock Station");
					ac.blockStation(GUIHelper.getStationList().get(stationNumber-1));
				} else if (blockButton.getText().equalsIgnoreCase("Unblock Station")) {
					blockButton.setText("Block Station");
					ac.unblockStation(GUIHelper.getStationList().get(stationNumber-1));
				}

			}


			public void mouseEntered(MouseEvent arg0) {
			}

			public void mouseExited(MouseEvent arg0) {
			}

			public void mousePressed(MouseEvent arg0) {

			}

			public void mouseReleased(MouseEvent arg0) {
			}

		});
		JButton startButton = new JButton("Start Station");
		startButton.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent arg0) {
				if (startButton.getText().equalsIgnoreCase("Start Station")) {
					startButton.setText("Stop Station");
					ac.startStation(GUIHelper.getStationList().get(stationNumber-1));
				} else if (startButton.getText().equalsIgnoreCase("Stop Station")) {
					startButton.setText("Start Station");
					ac.stopStation(GUIHelper.getStationList().get(stationNumber-1));
				}

			}

			public void mouseEntered(MouseEvent arg0) {
			}

			public void mouseExited(MouseEvent arg0) {
			}

			public void mousePressed(MouseEvent arg0) {

			}

			public void mouseReleased(MouseEvent arg0) {
			}

		});

		JButton logoutButton = new JButton("Logout");
		logoutButton.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent arg0) {
				int dialogResult = JOptionPane.showConfirmDialog(null, "Are you sure you want to logout?", "Warning",
						JOptionPane.YES_NO_OPTION);
				if (dialogResult == JOptionPane.YES_OPTION) {
					mainFrame.setVisible(false);
					loginFrame.setVisible(true);
					userName.setText("");
					userPassword.setText("");
				}
			}

			public void mouseEntered(MouseEvent arg0) {
			}

			public void mouseExited(MouseEvent arg0) {
			}

			public void mousePressed(MouseEvent arg0) {

			}

			public void mouseReleased(MouseEvent arg0) {
			}

		});
		
		JLabel attendantNameText = new JLabel("Welcome");
		labels.add(attendantNameText);
		mainTab.add(attendantNameText);
		
		DefaultListModel<String> listModel = new DefaultListModel<>();
		JList list = new JList(listModel);
		JScrollPane scroller = new JScrollPane(list);
		
		mainTab.add(scroller);
		mainTab.add(startButton);
		mainTab.add(blockButton);
		mainTab.add(logoutButton);
		
		mainPanel.addTab("Station"+stationNumber, mainTab);
		stationMaintenance.put(stationNumber, list);
	}

	private void buildLoginFrame() {
		loginPanel = new JLayeredPane();
		loginPanel.setLayout(new GridLayout(0, 1));
		loginFrame.setContentPane(loginPanel);
		loginFrame.setPreferredSize(new Dimension(320, 240));

		JLabel userNameLabel = new JLabel("Enter username:");
		JLabel passwordLabel = new JLabel("Enter password:");

		JButton loginButton = new JButton("Login");
		loginButton.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent arg0) {
				if (ac.verifyLogIn(userName.getText(), userPassword.getText())) {
					attendantName=ac.getAttendantName();
					for (JLabel l : labels) {
						l.setText("Welcome " + attendantName);
					}
					mainFrame.revalidate();
					mainFrame.setVisible(true);
					loginFrame.setVisible(false);

				} else {
					JOptionPane.showMessageDialog(null, "Login Failed!", "Warning", JOptionPane.ERROR_MESSAGE);
					userName.setText("");
					userPassword.setText("");
				}
			}

			public void mouseEntered(MouseEvent arg0) {
			}

			public void mouseExited(MouseEvent arg0) {
			}

			public void mousePressed(MouseEvent arg0) {

			}

			public void mouseReleased(MouseEvent arg0) {
			}

		});

		loginPanel.add(userNameLabel);
		loginPanel.add(userName);
		loginPanel.add(passwordLabel);
		loginPanel.add(userPassword);
		loginPanel.add(loginButton);
	}

	private void buildLookupTab() {
		lookupTab =new JLayeredPane();
		lookupTab.setLayout(new GridLayout(0, 1));
		JLabel enter = new JLabel("Enter the PLU Code:");
		JTextField productLookup = new JTextField(16);
		JButton submit = new JButton("Search");
		JLabel productprice = new JLabel("Product Price/kg: $");
		JLabel productDescription = new JLabel("Product Description: ");
		JLabel error = new JLabel("");
		

		submit.addMouseListener(new MouseListener() {

			public void mouseClicked(MouseEvent arg0) {
				SelfCheckoutStationInstance station = GUIHelper.getStationList().get(0);
				Session transaction= new Session(station,new GUIDispatch(new StationPanel(1,station)));
					if (productLookup.getText().length() == 4 || productLookup.getText().length() == 5) {
						error.setText("");
						ArrayList<Object> product_list = transaction.getCUS()
								.lookupProduct(new PriceLookupCode(productLookup.getText()));

						if (product_list.size() != 0) {
							BigDecimal productPrice = (BigDecimal) product_list.get(0);
							String Description = (String) product_list.get(1);
							MathContext m = new MathContext(3); // 3 precision
							productprice.setText("Product Price/kg: $" + productPrice.round(m));
							productDescription.setText("Product Description: " + Description);
						} else {
							productprice.setText("Product not found!");
							productDescription.setText("Product not found!");
						}
					} else {
						productprice.setText("");
						productDescription.setText("");
						error.setText("Invalid PLU code entered!");
					}
				
			}

			public void mouseEntered(MouseEvent arg0) {
			}

			public void mouseExited(MouseEvent arg0) {
			}

			public void mousePressed(MouseEvent arg0) {

			}

			public void mouseReleased(MouseEvent arg0) {
			}

		});

		lookupTab.add(enter);
		lookupTab.add(productLookup);
		lookupTab.add(submit);
		lookupTab.add(error);
		lookupTab.add(productprice);
		lookupTab.add(productDescription);
		lookupTab.setPreferredSize(new Dimension(300, 200));
		mainPanel.addTab("Lookup", lookupTab);
	}

	public JFrame getMainFrame() {
		return mainFrame;
	}

	public void setMainFrame(JFrame mainFrame) {
		this.mainFrame = mainFrame;
	}

	public Map<Integer, JList<String>> getStationMaintenance() {
		return stationMaintenance;
	}
	
	public void rerenderMaintenanceList(int stationId) {
		SelfCheckoutStationInstance station = GUIHelper.getStationList().get(stationId - 1);
		StationUsage stationUsage = station.stationUsage;
		
		DefaultListModel<String> listModel = new DefaultListModel<>();
		stationUsage.getBanknoteDispensersToService().forEach((denomination) -> {
			listModel.addElement("The $" + denomination+ " banknote dispenser needs a refill");
		});
		
		stationUsage.getCoinDispensersToService().forEach((denomination) -> {
			String truncatedDenomination = denomination.toString().substring(0, 4);
			listModel.addElement("The $" + truncatedDenomination + " coin dispenser needs a refill");
		});
		
		if (stationUsage.isBanknoteStorageFull()) listModel.addElement("The banknote storage needs to be emptied.");
		if (stationUsage.isCoinStorageFull()) listModel.addElement("The coin storage needs to be emptied.");
		
		if (stationUsage.receiptPrinterNeedsInk()) listModel.addElement("The receipt printer needs an ink refill.");
		if (stationUsage.receiptPrinterNeedsPaper()) listModel.addElement("The receipt printer needs a paper refill.");
	
		stationMaintenance.get(stationId).setModel(listModel);
	}
	
}
