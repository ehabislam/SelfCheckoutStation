import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.Calendar;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.lsmr.selfcheckout.Card.CardData;
import org.lsmr.selfcheckout.devices.CardReader;
import org.lsmr.selfcheckout.devices.listeners.CardReaderListener;
import org.lsmr.selfcheckout.external.CardIssuer;

import gui.StationPanel;
import listeners.CardReaderListenerSoftware;
import unSorted.CardIssuersDatabase;
import unSorted.SelfCheckoutStationInstance;
import unSorted.Session;



public class CardPaymentTest {
	
	public class cardDataStub implements CardData {
		
		@Override
		public String getType() {
			return "Visa";
		}

		
		@Override
		public String getNumber() {
			return "1234567812345678";
		}

		
		@Override
		public String getCardholder() {
			return "John Paul Smith";
		}

		@Override
		public String getCVV() {
			return "123";
		}
	}
	
	Session session;
	SelfCheckoutStationInstance station = new SelfCheckoutStationInstance(1);
	CardReaderListener cardReaderListener;
	CardReader cardreader;
	CardData carddata;
	Calendar expiry = Calendar.getInstance();
	

	

	@Before
	public void setUp() throws Exception {
		session = new Session(station,new GUIDispatchStub(new StationPanel(1,station)));
		cardReaderListener = new CardReaderListenerSoftware(session);
		cardreader = new CardReader();
		carddata = new cardDataStub();
		
		
		
		
		}

	@After
	public void tearDown() throws Exception {
	}
	
	
	
	@Test
	// Test a vaild transaction with a card
	public void validCardPaymentTest() {
		//set valid amount and expiry for card
		session.setBalance(BigDecimal.valueOf(100));
		expiry.set(2025, 10, 22);
		
		// add card issuer data to database
		CardIssuer ci = new CardIssuer("CIBC");
		ci.addCardData(carddata.getNumber(), carddata.getCardholder(), expiry, carddata.getCVV(), BigDecimal.valueOf(500));
		CardIssuersDatabase.CARD_ISSUERS.put("Visa",ci);
		
		//call listener
		cardReaderListener.cardDataRead(cardreader, carddata);
		
		// make sure balance is now 0 as payment has gone through
		assertEquals(session.getBalance(), BigDecimal.valueOf(0));
		
		
		
		
	}
	
	@Test
	// Test invalid issuer by setting issuer to null in issuer database
	public void invalidCardIssuerPaymentTest() {
		
		// For print test
		final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
		
        //cost of items
		session.setBalance(BigDecimal.valueOf(100));
		CardIssuer ci = new CardIssuer("HSBC");
		expiry.set(2025, 10, 22);
		
		//add customers data to database
		ci.addCardData(carddata.getNumber(), carddata.getCardholder(), expiry, carddata.getCVV(), BigDecimal.valueOf(500));
		CardIssuersDatabase.CARD_ISSUERS.put("Visa",null);
		
		//call listener
		cardReaderListener.cardDataRead(cardreader, carddata);
		
		// test that no payment went through
		assertEquals(session.getBalance(), BigDecimal.valueOf(100));
		
		// Test if failure message is printed
		assertEquals("The transaction failed", outContent.toString().trim());

		
		
		
		
	}
	@Test
	// Test hold == -1 by setting amount in bank account to less than balance
	// ie customer does not have enough money in account to pay for items
	public void invalidHoldNumberPaymentTest() {
		
		// For print test
		final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
		
        
        
		session.setBalance(BigDecimal.valueOf(100));
		CardIssuer ci = new CardIssuer("RBC");
		expiry.set(2025, 10, 22);
		
		// Note value of 99(last value) in account is less than total balance(cost)
		ci.addCardData(carddata.getNumber(), carddata.getCardholder(), expiry, carddata.getCVV(), BigDecimal.valueOf(99));
		CardIssuersDatabase.CARD_ISSUERS.put("Visa",ci);
		cardReaderListener.cardDataRead(cardreader, carddata);
		
		// test that no payment went through
		assertEquals(session.getBalance(), BigDecimal.valueOf(100));
		
		// Test if failure message is printed
		assertEquals("The transaction failed", outContent.toString().trim());
		
		
		
		
		
	}
	
	
	
	
}