import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.Card.CardData;
import org.lsmr.selfcheckout.PLUCodedItem;
import org.lsmr.selfcheckout.PriceLookupCode;
import org.lsmr.selfcheckout.devices.OverloadException;
import org.lsmr.selfcheckout.external.ProductDatabases;
import org.lsmr.selfcheckout.products.PLUCodedProduct;

import gui.StationPanel;
import unSorted.CardDataStub;
import unSorted.CustomerUseCases;
import unSorted.GiftcardDatabase;
import unSorted.SelfCheckoutStationInstance;
import unSorted.Session;

public class CustomerUseCasesTest {

	private Session session;
	private SelfCheckoutStationInstance station;

	@BeforeClass
	public static void setUpBeforeClass() {
		PriceLookupCode pluCode = new PriceLookupCode("12345");
		BigDecimal price = new BigDecimal(10.50);
		PLUCodedProduct pluCodedProduct = new PLUCodedProduct(pluCode, "Test PLU Product", price);
		ProductDatabases.PLU_PRODUCT_DATABASE.put(pluCode, pluCodedProduct);
	}

	@Before
	public void setUp() throws Exception {
		station = new SelfCheckoutStationInstance(1);
		session = new Session(station,new GUIDispatchStub(new StationPanel(1,station)));
	}
	
	@Test
	public void EnterBags() {
		int no_bags = 10;
		BigDecimal expectedCost = session.getPlasticBags().get_price_per_bag().multiply(new BigDecimal(no_bags));
		session.getCUS().enter_number_of_plastic_bags(no_bags);
		BigDecimal bagCost = session.getPlasticBags().get_total_bags_price();
		//System.out.println(bagCost);
		assertTrue((expectedCost.compareTo(bagCost))==0);
	}
	
	@Test
	public void MembershipCardReaderTestInvalidNumber() {
		session.setBalance(new BigDecimal(100));
		BigDecimal afterDiscount = new BigDecimal(100);
		String membercode = "12345678187654321";
		CardData card = new CardDataStub("CO-OP", membercode, "");
		session.getCUS().membershipCardReader(card);
		//System.out.println(session.getBalance());
		assertTrue(afterDiscount.compareTo(session.getBalance())==0);
	}
	
	@Test
	public void MembershipCardReaderTestWrongCard() {
		session.setBalance(new BigDecimal(100));
		BigDecimal afterDiscount = new BigDecimal(100);
		String membercode = "1234567887654321";
		CardData card = new CardDataStub("Walmart", membercode, "");
		session.getCUS().membershipCardReader(card);
		//System.out.println(session.getBalance());
		assertTrue(afterDiscount.compareTo(session.getBalance())==0);
	}
	
	@Test
	public void MembershipCardReaderTest() {
		session.setBalance(new BigDecimal(100));
		BigDecimal afterDiscount = new BigDecimal(99);
		String membercode = "1234567887654321";
		CardData card = new CardDataStub("CO-OP", membercode, "");
		session.getCUS().membershipCardReader(card);
		//System.out.println(session.getBalance());
		assertTrue(afterDiscount.compareTo(session.getBalance())==0);
	}
	
	@Test
	public void giftcardPaymenInvalidCard() {
		String giftcardcode = "22321";
		boolean test = session.getCUS().giftcardPayment(giftcardcode);
		assertTrue(!test);


	}
	
	@Test
	public void giftcardPaymenCardBalanceZero() {
		session.setBalance(new BigDecimal(100));
		String giftcardcode = "54321";
		BigDecimal cardBalance = GiftcardDatabase.GIFTCARD_DATABASE.get(giftcardcode);
		BigDecimal newCardBalance = BigDecimal.ZERO;
		BigDecimal sessionBalance = session.getBalance().subtract(cardBalance);
		session.getCUS().giftcardPayment(giftcardcode);
		assertTrue(sessionBalance.compareTo(session.getBalance())==0);
		assertTrue(newCardBalance.compareTo(GiftcardDatabase.GIFTCARD_DATABASE.get(giftcardcode))==0);
		//System.out.println(session.getBalance());

	}

	
	@Test
	public void giftcardPaymentBalanceEqualToCard() {
		session.setBalance(new BigDecimal(100));
		String giftcardcode = "11111";
		BigDecimal cardBalance = GiftcardDatabase.GIFTCARD_DATABASE.get(giftcardcode);
		BigDecimal newCardBalance = cardBalance.subtract(session.getBalance());
		BigDecimal sessionBalance = session.getBalance().subtract(cardBalance);
		session.getCUS().giftcardPayment(giftcardcode);
		assertTrue(sessionBalance.compareTo(session.getBalance())==0);
		assertTrue(newCardBalance.compareTo(GiftcardDatabase.GIFTCARD_DATABASE.get(giftcardcode))==0);
		//System.out.println(session.getBalance());

	}

	@Test
	public void giftcardPaymentBalancelessThanCard() {
		session.setBalance(new BigDecimal(100));
		String giftcardcode = "11223";
		BigDecimal cardBalance = GiftcardDatabase.GIFTCARD_DATABASE.get(giftcardcode);
		BigDecimal newCardBalance = cardBalance.subtract(session.getBalance());
		BigDecimal sessionBalance = BigDecimal.ZERO;
		session.getCUS().giftcardPayment(giftcardcode);
		assertTrue(sessionBalance.compareTo(session.getBalance())==0);
		assertTrue(newCardBalance.compareTo(GiftcardDatabase.GIFTCARD_DATABASE.get(giftcardcode))==0);
		//System.out.println(session.getBalance());

	}

	@Test
	public void giftcardPaymentBalancegreaterThanCard() {
		session.setBalance(new BigDecimal(100));
		String giftcardcode = "22222";
		BigDecimal cardBalance = GiftcardDatabase.GIFTCARD_DATABASE.get(giftcardcode);
		BigDecimal newCardBalance = BigDecimal.ZERO;
		BigDecimal sessionBalance = session.getBalance().subtract(cardBalance);
		session.getCUS().giftcardPayment(giftcardcode);
		assertTrue(sessionBalance.compareTo(session.getBalance())==0);
		assertTrue(newCardBalance.compareTo(GiftcardDatabase.GIFTCARD_DATABASE.get(giftcardcode))==0);
		//System.out.println(session.getBalance());

	}

//	@Test
//	public void testReturnToAddingItems() {
//		fail("Not yet implemented");
//	}

	@Test
	public void testLookupProductIfBarcodeExists() {
		Barcode barcode = station.barcodes.get(0);
		BigDecimal price = station.prices[0];
		String description = station.descriptions[0];
		CustomerUseCases customeruses = session.getCUS();
		// System.out.println(barcode.toString());
		ArrayList<Object> testreturn;
		customeruses.lookupProduct(barcode);
		testreturn = customeruses.lookupProduct(barcode);
		BigDecimal testprice = (BigDecimal) testreturn.get(0);
		String testdescription = (String) testreturn.get(1);
		assertTrue((price.compareTo(testprice) == 0) && (description.equals(testdescription)));
	}

	@Test
	public void testLookupProductIfBarcodeDoesNotExist() {
		Barcode barcode = new Barcode("11122233211223332");
		CustomerUseCases customeruses = session.getCUS();
		// System.out.println(barcode.toString());
		ArrayList<Object> testreturn;
		customeruses.lookupProduct(barcode);
		testreturn = customeruses.lookupProduct(barcode);
		assertTrue(testreturn.size() == 0);
	}

	@Test
	public void testLookupProductIfPLUcodeExists() {
		PriceLookupCode plucode = station.PLUcodes.get(0);
		BigDecimal price = station.pluPrices[0];
		String description = station.pluDescriptions[0];
		CustomerUseCases customeruses = session.getCUS();
		// System.out.println(barcode.toString());
		ArrayList<Object> testreturn;
		customeruses.lookupProduct(plucode);
		testreturn = customeruses.lookupProduct(plucode);
		BigDecimal testprice = (BigDecimal) testreturn.get(0);
		String testdescription = (String) testreturn.get(1);
		assertTrue((price.compareTo(testprice) == 0) && (description.equals(testdescription)));
	}

	@Test
	public void testLookupProductIfPLUcodeDoesNotExist() {
		PriceLookupCode plucode = new PriceLookupCode("99999");
		CustomerUseCases customeruses = session.getCUS();
		// System.out.println(barcode.toString());
		ArrayList<Object> testreturn;
		testreturn = customeruses.lookupProduct(plucode);
		assertEquals(0, testreturn.size());
	}

	@Test
	public void testEntersPLUCode() throws OverloadException {
//		 CustomerUseCases customeruses = session.getCUS();
//		 BigDecimal price = station.pluPrices[0];
//		 BigDecimal cost = price.multiply(new
//		 BigDecimal(session.getCheckoutStation().scale.getCurrentWeight()));
//		 PriceLookupCode code = station.PLUcodes.get(0);
//		
//		 assertTrue(cost.compareTo(customeruses.entersPLUCode(code.toString()))==0);

		assertTrue(session.getTotalProductsList().isEmpty());
		CustomerUseCases customerUseCases = session.getCUS();

		PriceLookupCode testPluCode = new PriceLookupCode("12345");
		PLUCodedItem pluCodedItem = new PLUCodedItem(testPluCode, 2.0);
		station.scs.scale.add(pluCodedItem);

		// Entering known test PLU code string
		BigDecimal pluCost = customerUseCases.entersPLUCode("12345");
		BigDecimal expectedCost = new BigDecimal(21.0);
		assertEquals(expectedCost.doubleValue(), pluCost.doubleValue(), 0.0001);
	}
}

