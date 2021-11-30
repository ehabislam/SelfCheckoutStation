package gui;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;

import org.lsmr.selfcheckout.Card.CardData;
import org.lsmr.selfcheckout.PriceLookupCode;
import org.lsmr.selfcheckout.devices.OverloadException;

import unSorted.CardDataStub;
import unSorted.GUIDispatch;
import unSorted.MemberDatabase;
import unSorted.SelfCheckoutStationInstance;
import unSorted.Session;
import utility.Pair;

public class StationPanel extends JPanel {
	
	private static final long serialVersionUID = 1L;
	
	Session transaction;
	SelfCheckoutStationInstance station;
	JPanel startPane;
	private JPanel buttonPanel, itemListPanel, reactionPanel, balancePanel, optionsPanel, receiptPanel;
	private JSplitPane mainPane, left, right, paymentPane;
	DefaultListModel<String> model;
	JLabel balance, paymentBalance=new JLabel();
	public final int id;

	public StationPanel(int id, SelfCheckoutStationInstance station) {
		this.id = id;
		this.station = station;
		start();
	}

	// call when starting a new checkout session
	public Session createNewSession() {
		return new Session(station, new GUIDispatch(this));
	}

	private void start() {
		this.removeAll();
		startPane = new JPanel();
		// mainPane.setLayout(new BoxLayout(mainPane, BoxLayout.Y_AXIS));

		JButton startButton = new JButton("Start Transaction");
		startButton.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent arg0) {
				if (!station.blocked) {
					transaction = createNewSession();
					transaction.setTransactionStarted(true);
					setup();
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

		Dimension size = startButton.getPreferredSize();
		startButton.setBounds(230, 200, size.width, size.height);
		startPane.add(startButton);
		this.add(startPane);
		this.repaint();
		this.revalidate();

	}

	private void setup() {
		this.removeAll();
		buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
		reactionPanel = new JPanel();
		balancePanel = new JPanel();
		itemListPanel = new JPanel();
		itemListPanel.setLayout(new BoxLayout(itemListPanel, BoxLayout.Y_AXIS));
		left = new JSplitPane(JSplitPane.VERTICAL_SPLIT, buttonPanel, reactionPanel);
		right = new JSplitPane(JSplitPane.VERTICAL_SPLIT, itemListPanel, balancePanel);
		right.setDividerLocation(350);
		setBalancePanel();
		setItemListPanel();

		buildButtonPanel();

		mainPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, left, right);
		mainPane.setDividerLocation(320);
		mainPane.setPreferredSize(new Dimension(640, 480));
		this.add(mainPane);
		this.repaint();
		this.revalidate();

	}

	private void buildButtonPanel() {
		
		JButton lookUp = new JButton("Quick Lookup");
		JButton enterPLU = new JButton("Enter item by PLU code");

		JButton buyBags = new JButton("Buy Bags");
		JButton help = new JButton("Ask for help");
		JButton notBag = new JButton("Do not bag item");
		JButton RemoveanItem = new JButton("Remove Item");
		
		lookUp.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent arg0) {
				if (!station.blocked) {
					buildReactionPanel("Lookup");
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
		
		enterPLU.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent arg0) {
				if (!station.blocked) {
					buildReactionPanel("PLU");
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

		buyBags.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent arg0) {
				if (!station.blocked) {
					buildReactionPanel("Bags");
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

		notBag.addMouseListener(new MouseListener() {
			// Team-A sets item to not be bagged
			public void mouseClicked(MouseEvent arg0) {
				
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

		RemoveanItem.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent arg0) {
				buildReactionPanel("RemoveanItem");

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

		
		buttonPanel.add(lookUp);
		buttonPanel.add(enterPLU);

		buttonPanel.add(buyBags);
		buttonPanel.add(help);
		buttonPanel.add(notBag);
		buttonPanel.add(RemoveanItem);

	}

	private void buildReactionPanel(String state) {
		reactionPanel.removeAll();
		switch (state) {
		case "Lookup":
			reactionPanel.add(lookUp());

			break;
		case "PLU":

			reactionPanel.add(PLU());
			break;
		case "Membership":
			reactionPanel.add(member());
			break;
		case "Bags":
			reactionPanel.add(bags());
			break;
		case "RemoveanItem":
			reactionPanel.add(RemoveItem());
			break;

		}
		reactionPanel.revalidate();

	}

	private JLayeredPane lookUp() {
		JLayeredPane lookupTab = new JLayeredPane();
		lookupTab.setLayout(new GridLayout(0, 1));
		JLabel enter = new JLabel("Enter the PLU Code:");
		JTextField productLookup = new JTextField(16);
		JButton submit = new JButton("Search");
		JLabel productprice = new JLabel("Product Price/kg: $");
		JLabel productDescription = new JLabel("Product Description: ");
		JLabel error = new JLabel("");
		JButton addItem = new JButton("Add item");

		addItem.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent arg0) {
				// add itemname here
				if (!station.blocked) {
					// model.addElement("three");
				}
			}

			public void mouseEntered(MouseEvent arg0) {
			}

			public void mouseExited(MouseEvent arg0) {
			}

			public void mousePressed(MouseEvent arg0) {

			}

			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub

			}
		});

		submit.addMouseListener(new MouseListener() {

			public void mouseClicked(MouseEvent arg0) {

				if (!station.blocked) {
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
		return lookupTab;
	}

	private JLayeredPane PLU() {
		JLayeredPane PLUTab = new JLayeredPane();
		PLUTab.setLayout(new GridLayout(0, 1));
		JLabel enter = new JLabel("Enter the PLU Code:");
		JTextField productLookup = new JTextField(16);
		JLabel error = new JLabel("");
		JButton addItem = new JButton("Add Item");

		addItem.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent arg0) {

				// Implementation of Use case 3 by team- A-------//
				try {

					// ------------
					if (productLookup.getText().length() == 4 || productLookup.getText().length() == 5) {
						error.setText("");
						transaction.getCUS().entersPLUCode(productLookup.getText());
						ArrayList<Object> product_list = transaction.getCUS()
								.lookupProduct(new PriceLookupCode(productLookup.getText()));
//						if (product_list.size() != 0) {
////							String Description = (String) product_list.get(1);
////							model.addElement(Description);
//						} else {
//							error.setText("Invalid PLU code length!");
//						}
						// ------------
					} else {
						error.setText("Invalid PLU code length!");
					}
				} catch (OverloadException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				// ---------------------------//
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

		PLUTab.add(enter);
		PLUTab.add(productLookup);
		PLUTab.add(addItem);
		PLUTab.add(error);
		PLUTab.setPreferredSize(new Dimension(300, 200));
		return PLUTab;
	}

	private JLayeredPane member() {
		JLayeredPane memberTab = new JLayeredPane();
		memberTab.setLayout(new GridLayout(0, 1));
		JLabel enter = new JLabel("Enter MembershipCard Number:");
		JTextField memberLookup = new JTextField(16);
		JLabel error = new JLabel("");
		JButton submit = new JButton("Submit");

		submit.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent arg0) {

				// Implementation of Use case 5 by team- A-------//
				String memberCode = memberLookup.getText();
				if (MemberDatabase.MEMBER_DATABASE.containsKey(memberCode)) {
					error.setText("");
					CardData card = new CardDataStub("CO-OP", memberCode, "");
					transaction.getCUS().membershipCardReader(card);
				} else {
					error.setText("Member card not Found!");
				}

				// ---------------------------//
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
		memberTab.add(enter);
		memberTab.add(memberLookup);
		memberTab.add(error);
		memberTab.add(submit);
		memberTab.setPreferredSize(new Dimension(200, 150));
		return memberTab;
	}

	private JLayeredPane RemoveItem() {
		JLayeredPane memberTab = new JLayeredPane();
		memberTab.setLayout(new GridLayout(0, 1));
		JLabel removeItemLabel = new JLabel("Enter the position of the item you want removed");
		JLabel error = new JLabel("");
		JTextField removeTextField = new JTextField(16);
		JButton removeButton = new JButton("Remove");

		removeButton.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				String removeLabelText = removeTextField.getText();
				int removePosition;
				try {
					removePosition = Integer.parseInt(removeLabelText) - 1;					
				} catch (NumberFormatException e) {
					removePosition = -1;
				}
				if (removeLabelText.length() > 0 && removePosition >= 0) {
					try {						
						Pair<String, BigDecimal> pairToRemove = transaction.getTotalProductsList().get(removePosition);
						transaction.getTotalProductsList().remove(removePosition);
						BigDecimal updatedBalance = transaction.getBalance().subtract(pairToRemove.getValue());
						transaction.setBalance(updatedBalance);
						updateItemListPanel();
					} catch (IndexOutOfBoundsException e) {
						error.setText("Please enter a valid position number!");
					}
				} else {
					error.setText("Please enter a valid position number!");
				}
			}

			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub

			}
		});

		memberTab.add(removeItemLabel);
		memberTab.add(removeTextField);
		memberTab.add(removeButton);
		memberTab.add(error);
		memberTab.setPreferredSize(new Dimension(300, 200));
		return memberTab;
	}

	private JLayeredPane bags() {
		JLayeredPane bagTab = new JLayeredPane();
		bagTab.setLayout(new GridLayout(0, 1));
		JLabel enter = new JLabel("Enter the number of bags:");
		JTextField bagsNumber = new JTextField(16);
		JButton submit = new JButton("Submit");
		submit.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				transaction.getCUS().enter_number_of_plastic_bags(Integer.parseInt(bagsNumber.getText()));
			}

			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub

			}
		});
		bagTab.add(enter);
		bagTab.add(bagsNumber);
		bagTab.add(submit);
		bagTab.setPreferredSize(new Dimension(300, 200));
		return bagTab;
	}

	private void setBalancePanel() {
		balancePanel.setPreferredSize(new Dimension(300, 130));
		balance = new JLabel("Balance:");

		
		
		JButton proceed = new JButton("Proceed to checkout");
		proceed.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent arg0) {
				if (!station.blocked)
					balance.setText("Balance: " + transaction.getBalance());
				transaction.setisScanningFinished(true);
				toPayment();
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
		balancePanel.setLayout(new BoxLayout(balancePanel, BoxLayout.Y_AXIS));
		balancePanel.add(balance);
		balancePanel.add(proceed);

	}

	private void setItemListPanel() {
		JLabel shopping = new JLabel("Your Order");
		model = new DefaultListModel<>();
		JList<String> list = new JList<>(model);
		JScrollPane scroller = new JScrollPane(list);
		itemListPanel.add(shopping);
		itemListPanel.add(scroller);
	}

	/**
	 * Update the item list panel whenever there is an update in the
	 * totalProductsList in Session
	 */
	public void updateItemListPanel() {
		String orderStringFormat = "%-10d%-30.30s%30s%.2f";
		List<Pair<String, BigDecimal>> productsList = transaction.getTotalProductsList();
		model.clear();
		for (int i = 0; i < productsList.size(); i++) {
			Pair<String, BigDecimal> pair = productsList.get(i);
			String description = pair.getKey();
			BigDecimal price = pair.getValue();
			String orderString = String.format(orderStringFormat, i + 1, description, station.currency.getSymbol(), price);
			model.addElement(orderString);
		}
		balance.setText("Balance: $"+transaction.getBalance().toString());
		paymentBalance.setText("Balance: $"+transaction.getBalance().toString());
	}

	private void toPayment() {
		this.removeAll();
		optionsPanel = new JPanel();
		optionsPanel.setLayout(new GridLayout(0, 1));
		receiptPanel = new JPanel();

		receiptPanel.setLayout(new GridLayout(0, 1));
		paymentButtons();
		paymentPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, optionsPanel, receiptPanel);
		paymentPane.setDividerLocation(220);
		paymentPane.setPreferredSize(new Dimension(600, 400));
		this.add(paymentPane);
		this.repaint();
		this.revalidate();
	}

	private void paymentButtons() {
		paymentBalance = new JLabel("Balance $: " + transaction.getBalance().toString());
		JPanel buttons = new JPanel();
		JPanel panel = new JPanel();
		JPanel giftPanel = new JPanel();
		JSplitPane options = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, buttons, panel);

		optionsPanel.setPreferredSize(new Dimension(550, 250));

		options.setPreferredSize(new Dimension(500, 250));
		buttons.setLayout(new BoxLayout(buttons, BoxLayout.Y_AXIS));
		JButton membership = new JButton("Enter Membership Card");
		JButton cash = new JButton("Pay by Cash");
		JButton card = new JButton("Pay by Card");
		JButton giftCard = new JButton("Pay by giftCard");
		JLabel enterGift = new JLabel("Enter the Giftcard number: ");
		JTextField giftCardNumber = new JTextField(16);
		JButton submit = new JButton("Submit");
		giftPanel.add(enterGift);
		giftPanel.add(giftCardNumber);
		giftPanel.add(submit);
		giftPanel.setPreferredSize(new Dimension(200, 150));
		buttons.add(membership);
		buttons.add(cash);
		buttons.add(card);
		buttons.add(giftCard);

		JButton cancel = new JButton("Return to adding items");
		JLabel paymentStatus = new JLabel("Payment pending...");
		JButton receipt = new JButton("Print Receipt");

		JPanel thispanel = this;
		
		cash.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent arg0) {
				if (!station.blocked) {
					paymentStatus.setText("Please pay by adding coins and banknotes");
					if(transaction.getBalance().compareTo(BigDecimal.ZERO)<=0) {
						paymentStatus.setText("Payment Successful");
						 paymentBalance.setText("Balance: " + transaction.getBalance().toString());
					}
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
		
		card.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent arg0) {
				if (!station.blocked) {
					paymentStatus.setText("Please use the cardReader");
					if(transaction.getBalance().compareTo(BigDecimal.ZERO)<=0) {
						paymentStatus.setText("Payment Successful");
					}
					
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

		cancel.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent arg0) {
				if (!station.blocked) {
					thispanel.removeAll();
					thispanel.add(mainPane);
					thispanel.repaint();
					thispanel.revalidate();
					transaction.getCUS().returnToAddingItems();
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

		submit.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent arg0) {
				if (!station.blocked) {
					transaction.getCUS().giftcardPayment(giftCardNumber.getText());
					paymentBalance.setText(("Balance $: " + transaction.getBalance().toString()));
				}
				if(transaction.getBalance().equals(BigDecimal.ZERO)) {
					paymentStatus.setText("Payment Successful");
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
		membership.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent arg0) {
				if (!station.blocked) {
					panel.removeAll();
					panel.add(member());
					panel.repaint();
					panel.revalidate();

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

		receipt.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent arg0) {
				if (!station.blocked) {
					start();
					//transaction.print(transaction.generateReceiptContent());
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

		giftCard.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent arg0) {
				panel.removeAll();
				panel.add(giftPanel);
				panel.repaint();
				panel.revalidate();

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
		buttons.add(cancel);
		buttons.add( paymentBalance);

		optionsPanel.add(options);

		receiptPanel.add(paymentStatus);
		receiptPanel.add(receipt);
		receiptPanel.setPreferredSize(new Dimension(550, 200));
	}
}
