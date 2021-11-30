import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.lsmr.selfcheckout.Banknote;
import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.BarcodedItem;
import org.lsmr.selfcheckout.Coin;
import org.lsmr.selfcheckout.Item;
import org.lsmr.selfcheckout.PLUCodedItem;
import org.lsmr.selfcheckout.PriceLookupCode;
import org.lsmr.selfcheckout.devices.DisabledException;
import org.lsmr.selfcheckout.devices.OverloadException;

import gui.StationPanel;
import unSorted.BagItem;
import unSorted.CardDataStub;
import unSorted.CustomerUseCases;
import unSorted.ItemScanner;
import unSorted.MembershipCard;
import unSorted.PaymentService;
import unSorted.SelfCheckoutStationInstance;
import unSorted.Session;
import utility.Pair;

public class SessionTest {
	Session session;
	SelfCheckoutStationInstance station = new SelfCheckoutStationInstance(1);
	ItemScanner is;
	BagItem bagItem;
	PaymentService paymentService;

	@Before
	public void setUp() throws Exception {
		session = new Session(station,new GUIDispatchStub(new StationPanel(1,station)));
		is = session.getItemScanner();
		bagItem = session.getBagItem();
		paymentService = session.getPaymentService();

	}

	/**
	 * Everything goes well in scanning, bagging, and payment with banknotes
	 * @throws DisabledException
	 */
	@Test
	public void testValidSystemWithBankNote() throws DisabledException {

		for (int j = 0; j < 100; j++) {
			is.itemScanned = false;
			Item item = station.itemsSystemTest.get(j);
			station.scs.mainScanner.scan(item);
			station.scs.baggingArea.add(item);
		}

		boolean checkList = is.getCheckoutProducts().size() >= 80 && is.getCheckoutProducts().size() <= 100;
		boolean checkPrice = session.getBalance().compareTo(new BigDecimal(4000)) >= 0
				&& session.getBalance().compareTo(new BigDecimal(5050)) <= 0;
		boolean checkWeight = bagItem.getBaggingWeight() >= 4040 && bagItem.getBaggingWeight() <= 5050;
		if (!checkList) {
			fail("ItemScanner failed to add the items to the checkout list");
		}
		if (!checkPrice) {
			fail("ItemScanner failed to update the price of the items");
		}
		if (!checkWeight) {
			System.out.println(bagItem.getBaggingWeight());

			fail("BagItem failed to add weight of item to total weight");
		}

		Banknote fiver = new Banknote(5, Currency.getInstance(Locale.CANADA));
		Banknote tenner = new Banknote(10, Currency.getInstance(Locale.CANADA));
		for (int i = 0; i < 50; i++) {
			station.scs.banknoteValidator.accept(fiver);
			if (!station.scs.banknoteInput.hasSpace()) {
				station.scs.banknoteInput.removeDanglingBanknote();
			}

		}

		for (int i = 0; i < 50; i++) {
			station.scs.banknoteValidator.accept(tenner);
			if (!station.scs.banknoteInput.hasSpace()) {
				station.scs.banknoteInput.removeDanglingBanknote();
			}
		}

		boolean checkPayment = session.getBalance().compareTo(new BigDecimal(3290)) >= 0
				&& session.getBalance().compareTo(new BigDecimal(4350)) <= 0;
		if (!checkPayment) {

			fail("Payment failed");
		}
	}

	/**
	 * Everything goes well in scanning, bagging, and payment with coins
	 * @throws DisabledException
	 */
	@Test
	public void testValidSystemWithCoins() throws DisabledException {

		for (int j = 0; j < 100; j++) {
			is.itemScanned = false;
			Item item = station.itemsSystemTest.get(j);
			station.scs.mainScanner.scan(item);
			station.scs.baggingArea.add(item);
		}

		boolean checkList = is.getCheckoutProducts().size() >= 80 && is.getCheckoutProducts().size() <= 100;
		boolean checkPrice = session.getBalance().compareTo(new BigDecimal(4040)) >= 0
				&& session.getBalance().compareTo(new BigDecimal(5050)) <= 0;
		boolean checkWeight = bagItem.getBaggingWeight() >= 4040 && bagItem.getBaggingWeight() <= 5050;
		if (!checkList) {
			fail("ItemScanner failed to add the items to the checkout list");
		}
		if (!checkPrice) {
			fail("ItemScanner failed to update the price of the items");
		}
		if (!checkWeight) {

			fail("BagItem failed to add weight of item to total weight");
		}

		Coin coin = new Coin(new BigDecimal(1), Currency.getInstance(Locale.CANADA));
		for (int i = 0; i < 1000; i++) {
			station.scs.coinTray.collectCoins();
			station.scs.coinValidator.accept(coin);
		}
		boolean checkPayment = session.getBalance().compareTo(new BigDecimal(3050)) >= 0
				&& session.getBalance().compareTo(new BigDecimal(4050)) <= 0;
		if (!checkPayment) {

			fail("Payment failed");
		}
	}

	/**
	 * Testing a full transaction being done with invalid banknotes
	 * @throws Exception
	 */
	@Test
	public void fullsessionWithInvalidBanknoteTest() throws Exception {

		for (int i = 0; i < 100; i++) {
			Item item = station.itemsSystemTest.get(i);
			station.scs.mainScanner.scan(item);
			station.scs.baggingArea.add(item);
		}

		BigDecimal upper = new BigDecimal(5050);
		BigDecimal lower = new BigDecimal(4000);

		if (!(session.getBalance().compareTo(lower) >= 0 && session.getBalance().compareTo(upper) <= 0)) {
			fail("Item Scanner failure - price not updated");
		}
		if (!(bagItem.getBaggingWeight() >= lower.intValue() && bagItem.getBaggingWeight() <= upper.intValue())) {
			fail("Bag Item failure - weight not updated");
		}

		BigDecimal totalcost = session.getBalance();

		Banknote fiver = new Banknote(5, Currency.getInstance(Locale.CANADA));
		Banknote Invalidnote = new Banknote(11, Currency.getInstance(Locale.CANADA));

		BigDecimal price = BigDecimal.ZERO;
		for (int i = 0; i < 50; i++) {
			station.scs.banknoteInput.accept(fiver);
			price = totalcost.subtract(BigDecimal.valueOf(fiver.getValue()));
			if (!station.scs.banknoteInput.hasSpace()) {
				station.scs.banknoteInput.removeDanglingBanknote();
			}
		}
		BigDecimal afterwallet = paymentService.getWallet();

		station.scs.banknoteInput.accept(Invalidnote);

		if (!(is.getCheckoutProducts().size() >= 80 && is.getCheckoutProducts().size() <= 100)) {
			fail("Item Scanner failure - some items not scanned");
		}

		boolean checkPayment = session.getBalance().compareTo(new BigDecimal(3800)) >= 0
				&& session.getBalance().compareTo(new BigDecimal(4800)) <= 0;
		if (!checkPayment) {

			fail("Payment failed");
		}

		assertTrue(afterwallet.compareTo(new BigDecimal(200)) >= 0 && afterwallet.compareTo(new BigDecimal(250)) <= 0);

	}

	/**
	 * Testing a full transaction being done with invalid coins
	 * @throws Exception
	 */
	@Test
	public void fullsessionWithInvalidCoinTest() throws Exception {
		for (int i = 0; i < 100; i++) {
			Item item = station.itemsSystemTest.get(i);
			station.scs.mainScanner.scan(item);
			station.scs.baggingArea.add(item);
		}

		BigDecimal upper = new BigDecimal(5050);
		BigDecimal lower = new BigDecimal(4000);

		if (!(session.getBalance().compareTo(lower) >= 0 && session.getBalance().compareTo(upper) <= 0)) {
			fail("Item Scanner failure - price not updated");
		}
		if (!(bagItem.getBaggingWeight() >= lower.intValue() && bagItem.getBaggingWeight() <= upper.intValue())) {

			fail("Bag Item failure - weight not updated");
		}

		BigDecimal totalcost = session.getBalance();
		Coin dollar = new Coin(new BigDecimal(1), Currency.getInstance(Locale.CANADA));
		Coin Invalidcoin = new Coin(new BigDecimal(3), Currency.getInstance(Locale.CANADA));

		BigDecimal price = BigDecimal.ZERO;
		for (int i = 0; i < 50; i++) {
			station.scs.coinValidator.accept(dollar);
			price = totalcost.subtract(dollar.getValue());
		}

		BigDecimal afterwallet = paymentService.getWallet();

		station.scs.coinSlot.accept(Invalidcoin);

		if (!(is.getCheckoutProducts().size() >= 80 && is.getCheckoutProducts().size() <= 100)) {
			fail("Item Scanner failure - some items not scanned");
		}

		boolean checkPayment = session.getBalance().compareTo(new BigDecimal(4000)) >= 0
				&& session.getBalance().compareTo(new BigDecimal(5000)) <= 0;
		if (!checkPayment) {

			fail("Payment failed");
		}

		assertTrue(afterwallet.compareTo(new BigDecimal(45)) >= 0 && afterwallet.compareTo(new BigDecimal(50)) <= 0);

	}

	@Test
	public void scanBeforeBagging() {
		Item item = station.itemsSystemTest.get(0);
		while (session.getItemScanner().getCheckoutProducts().size() < 1) {
			station.scs.mainScanner.scan(item);
		}
		station.scs.mainScanner.scan(station.itemsSystemTest.get(1));

		assertEquals(session.getItemScanner().getCheckoutProducts().size(), 1);
		assertEquals(session.getBalance(), new BigDecimal(1));
		assertEquals(session.getItemScanner().itemScanned, true);
	}

	/**
	 * Testing to see if the product has been added to the total products list after
	 * being added via a PLU code
	 * 
	 * @throws OverloadException
	 */
	@Test
	public void testTotalProductsListUpdatedAfterPLUProductAdded() throws OverloadException {
		assertTrue(session.getTotalProductsList().isEmpty());
		CustomerUseCases customerUseCases = session.getCUS();

		PriceLookupCode testPluCode = new PriceLookupCode("1111");
		PLUCodedItem pluCodedItem = new PLUCodedItem(testPluCode, 2.25);
		session.getCheckoutStation().scale.add(pluCodedItem);

		// Entering known test PLU code string (apples at 6 per kg cost)
		BigDecimal pluCost = customerUseCases.entersPLUCode("1111");
		BigDecimal expectedCost = new BigDecimal(13.5);
		assertEquals(expectedCost.doubleValue(), pluCost.doubleValue(), 0.0001);

		// Testing to see if the pair was added to totalProductsList
		// Tests the key and the value of the pair individually
		Pair<String, BigDecimal> expectedPair = new Pair<>("Apples", expectedCost);
		assertEquals(expectedPair.getKey(), session.getTotalProductsList().get(0).getKey());
		assertEquals(expectedPair.getValue().doubleValue(),
				session.getTotalProductsList().get(0).getValue().doubleValue(), 0.0001);
	}

	/**
	 * Testing to see if the product has been added to the total products list
	 * after being added via scanning
	 */
	@Test
	public void testTotalProductsListUpdatedAfterBarcodedItemScanned() {
		String barcodeString = "5";
		Barcode barcode = new Barcode(barcodeString);
		double weight = 5.0;
		BarcodedItem item = new BarcodedItem(barcode, weight);
		BigDecimal expectedCost = new BigDecimal(5);

		assertTrue(session.getTotalProductsList().isEmpty());

		/**
		 * Testing to see if the product is added to totalProductsList after a
		 * BarcodedItem is scanned. Scanning over and over until the item is actually
		 * scanned in case of scanning failures
		 */
		while (session.getTotalProductsList().isEmpty()) {
			station.scs.mainScanner.scan(item);			
		}
		
		Pair<String, BigDecimal> expectedPair = new Pair<>("Milk", expectedCost);
		assertEquals(expectedPair.getKey(), session.getTotalProductsList().get(0).getKey());
		assertEquals(expectedPair.getValue().doubleValue(),
				session.getTotalProductsList().get(0).getValue().doubleValue(), 0.0001);
	}

	@Test
	public void testTotalProductsListAfterPLUProductAddedAndBarcodeItemScanned() throws OverloadException {
		assertTrue(session.getTotalProductsList().isEmpty());

		// PLU lookup stuff
		CustomerUseCases customerUseCases = session.getCUS();
		PriceLookupCode testPluCode = new PriceLookupCode("1111");
		PLUCodedItem pluCodedItem = new PLUCodedItem(testPluCode, 2.25);
		session.getCheckoutStation().scale.add(pluCodedItem);

		BigDecimal expectedPluCost = new BigDecimal(13.5);
		Pair<String, BigDecimal> expectedPluPair = new Pair<>("Apples", expectedPluCost);

		// Barcode stuff
		String barcodeString = "5";
		Barcode barcode = new Barcode(barcodeString);
		double barcodeItemWeight = 5.0;
		BarcodedItem barcodedItem = new BarcodedItem(barcode, barcodeItemWeight);
		BigDecimal expectedBarcodeCost = new BigDecimal(5);

		Pair<String, BigDecimal> expectedBarcodePair = new Pair<>("Milk", expectedBarcodeCost);

		// Entering PLU item
		BigDecimal actualPluCost = customerUseCases.entersPLUCode("1111");
		assertEquals(expectedPluCost.doubleValue(), actualPluCost.doubleValue(), 0.0001);
		assertEquals(expectedPluPair.getKey(), session.getTotalProductsList().get(0).getKey());
		assertEquals(expectedPluPair.getValue().doubleValue(),
				session.getTotalProductsList().get(0).getValue().doubleValue(), 0.0001);

		// Scanning barcoded item
		while (session.getTotalProductsList().size() == 1) {
			station.scs.mainScanner.scan(barcodedItem);			
		}
		assertEquals(expectedBarcodePair.getKey(), session.getTotalProductsList().get(1).getKey());
		assertEquals(expectedBarcodePair.getValue().doubleValue(),
				session.getTotalProductsList().get(1).getValue().doubleValue(), 0.0001);
	}

	/**
	 * A test case to check whether the generated receipt content is as expected,
	 * this is a receipt where there has been no discount given to the customer
	 * 
	 * @throws OverloadException
	 */
	@Test
	public void testGenerateReceiptContentNoDiscount() throws OverloadException {
		// PLU lookup stuff
		CustomerUseCases customerUseCases = session.getCUS();
		PriceLookupCode testPluCode = new PriceLookupCode("1111");
		PLUCodedItem pluCodedItem = new PLUCodedItem(testPluCode, 2.25);
		session.getCheckoutStation().scale.add(pluCodedItem);

		BigDecimal expectedPluCost = new BigDecimal(13.5);
		Pair<String, BigDecimal> expectedPluPair = new Pair<>("Apples", expectedPluCost);

		// Barcode stuff
		String barcodeString = "5";
		Barcode barcode = new Barcode(barcodeString);
		double barcodeItemWeight = 5.0;
		BarcodedItem barcodedItem = new BarcodedItem(barcode, barcodeItemWeight);
		BigDecimal expectedBarcodeCost = new BigDecimal(5);

		Pair<String, BigDecimal> expectedBarcodePair = new Pair<>("Milk", expectedBarcodeCost);

		// Entering PLU item
		BigDecimal actualPluCost = customerUseCases.entersPLUCode("1111");
		assertEquals(expectedPluCost.doubleValue(), actualPluCost.doubleValue(), 0.0001);
		assertEquals(expectedPluPair.getKey(), session.getTotalProductsList().get(0).getKey());
		assertEquals(expectedPluPair.getValue().doubleValue(),
				session.getTotalProductsList().get(0).getValue().doubleValue(), 0.0001);

		// Scanning barcoded item
		while (session.getTotalProductsList().size() == 1) {
			station.scs.mainScanner.scan(barcodedItem);			
		}
		assertEquals(expectedBarcodePair.getKey(), session.getTotalProductsList().get(1).getKey());
		assertEquals(expectedBarcodePair.getValue().doubleValue(),
				session.getTotalProductsList().get(1).getValue().doubleValue(), 0.0001);

		String actualContent = session.generateReceiptContent();
		String expectedContent = "List of Scanned Items: \n"
				+ "Apples                                                13.50\n"
				+ "Milk                                                   5.00\n"
				+ "Total Price:                                          18.50\n";
		
		assertEquals(actualContent, expectedContent);
	}

	/**
	 * A test case to check whether the generated receipt content is as expected,
	 * this is a receipt where that has been a discount applied to the total balance
	 * @throws OverloadException 
	 */
	@Test
	public void testGenerateReceiptContentWithDiscount() throws OverloadException {
		// Discount card
		CardDataStub testCardData = station.membershipCards.get(0);
		MembershipCard testCard = new MembershipCard(testCardData, "Basic", "2020-05-08");
		
		// PLU lookup stuff
		CustomerUseCases customerUseCases = session.getCUS();
		PriceLookupCode testPluCode = new PriceLookupCode("1111");
		PLUCodedItem pluCodedItem = new PLUCodedItem(testPluCode, 2.25);
		session.getCheckoutStation().scale.add(pluCodedItem);

		BigDecimal expectedPluCost = new BigDecimal(13.5);
		Pair<String, BigDecimal> expectedPluPair = new Pair<>("Apples", expectedPluCost);

		// Barcode stuff
		String barcodeString = "5";
		Barcode barcode = new Barcode(barcodeString);
		double barcodeItemWeight = 5.0;
		BarcodedItem barcodedItem = new BarcodedItem(barcode, barcodeItemWeight);
		BigDecimal expectedBarcodeCost = new BigDecimal(5);

		Pair<String, BigDecimal> expectedBarcodePair = new Pair<>("Milk", expectedBarcodeCost);

		// Entering PLU item
		BigDecimal actualPluCost = customerUseCases.entersPLUCode("1111");
		assertEquals(expectedPluCost.doubleValue(), actualPluCost.doubleValue(), 0.0001);
		assertEquals(expectedPluPair.getKey(), session.getTotalProductsList().get(0).getKey());
		assertEquals(expectedPluPair.getValue().doubleValue(),
				session.getTotalProductsList().get(0).getValue().doubleValue(), 0.0001);

		// Scanning barcoded item
		while (session.getTotalProductsList().size() == 1) {
			station.scs.mainScanner.scan(barcodedItem);			
		}
		assertEquals(expectedBarcodePair.getKey(), session.getTotalProductsList().get(1).getKey());
		assertEquals(expectedBarcodePair.getValue().doubleValue(),
				session.getTotalProductsList().get(1).getValue().doubleValue(), 0.0001);
		
		// Applying discount
		session.getCUS().membershipCardReader(testCardData);

		String actualContent = session.generateReceiptContent();
		String expectedContent = "List of Scanned Items: \n"
				+ "Apples                                                13.50\n"
				+ "Milk                                                   5.00\n"
				+ "Total Price:                                          18.50\n"
				+ "Discount Amount:                                       0.20\n"
				+ "Amount After Discount:                                18.30\n";
		
		assertEquals(actualContent, expectedContent);
	}
}
