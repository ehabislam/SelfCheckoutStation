package gui;

import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Currency;
import java.util.Locale;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.lsmr.selfcheckout.Banknote;
import org.lsmr.selfcheckout.Card;
import org.lsmr.selfcheckout.Coin;
import org.lsmr.selfcheckout.Item;
import org.lsmr.selfcheckout.devices.DisabledException;
import org.lsmr.selfcheckout.external.CardIssuer;

import unSorted.CardIssuersDatabase;
import unSorted.GUIDispatch;
import unSorted.SelfCheckoutStationInstance;

public class HardwareSimulator {

	
	ArrayList<SelfCheckoutStationInstance> stations;
	private static HardwareSimulator instance = new HardwareSimulator();
	private JFrame hardwareSim = new JFrame("Hardware Simulator");
	private JPanel buttonHolder = new JPanel();
	int counter=0;

	public HardwareSimulator() {
		stations= GUIHelper.stationList;
		hardwareSim.setContentPane(buttonHolder);
		hardwareSim.setPreferredSize(new Dimension(320, 240));
		buttonHolder.setLayout(new BoxLayout(buttonHolder, BoxLayout.Y_AXIS));
		buildSimulator();
		hardwareSim.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		hardwareSim.setResizable(false);
		hardwareSim.pack();
		hardwareSim.setVisible(true);
	}

	public static HardwareSimulator getInstance() {
		return instance;
	}
	
	private void buildSimulator() {
		
		JButton barcodeScannerButton = new JButton("Scan an item");
		barcodeScannerButton.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent arg0) {
				Item item = stations.get(0).items.get(counter);
				stations.get(0).scs.mainScanner.scan(item);
				
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
		
		
		JButton bagItemButton = new JButton("Add item to bagging scale");
		 bagItemButton.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent arg0) {
				Item item = stations.get(0).items.get(counter);
				stations.get(0).scs.baggingArea.add(item);
				counter++;
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
	
		 
		 JButton coinButton = new JButton("Add a coin to the coinSlot");
		 coinButton.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent arg0) {
				Coin coin = new Coin(new BigDecimal(1), Currency.getInstance(Locale.CANADA));
				try {
					stations.get(0).scs.coinValidator.accept(coin);
					
				} catch (DisabledException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
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
		 
		 JButton cashButton = new JButton("Add a banknote to the banknoteSlot");
		 cashButton.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent arg0) {
				Banknote banknote = new Banknote(5, Currency.getInstance(Locale.CANADA));
				try {
					stations.get(0).scs.banknoteValidator.accept(banknote);
					
				} catch (DisabledException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
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
		 
		 JButton cardButton = new JButton("Insert a card in the cardReader");
		 cardButton.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent arg0) {
				Calendar expiry = Calendar.getInstance();
				expiry.set(2025, 10, 22);
				CardIssuer ci = new CardIssuer("CIBC");
				ci.addCardData("1111222233334444","Person1", expiry, "123", BigDecimal.valueOf(500));
				CardIssuersDatabase.CARD_ISSUERS.put("CIBC",ci);
				Card card = new Card("CIBC", "1111222233334444", "Person1", null, null, true, false);
				try {
					stations.get(0).scs.cardReader.tap(card);
				
				} catch (IOException e) {
					
					e.printStackTrace();
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
		 
		buttonHolder.add(barcodeScannerButton);
		buttonHolder.add(bagItemButton);
	
		buttonHolder.add(coinButton);
		buttonHolder.add(cashButton);
		buttonHolder.add(cardButton);
	}

}
