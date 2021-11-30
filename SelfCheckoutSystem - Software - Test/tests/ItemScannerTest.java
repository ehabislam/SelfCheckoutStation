import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.BarcodedItem;
import org.lsmr.selfcheckout.Item;
import org.lsmr.selfcheckout.external.ProductDatabases;
import org.lsmr.selfcheckout.products.BarcodedProduct;

import gui.StationPanel;
import unSorted.GUIDispatch;
import unSorted.ItemScanner;
import unSorted.SelfCheckoutStationInstance;
import unSorted.Session;

public class ItemScannerTest {
	Session session;
	SelfCheckoutStationInstance station = new SelfCheckoutStationInstance(1);
	ItemScanner is;

	@Before
	public void setUp() throws Exception {
		session = new Session(station, new GUIDispatchStub(new StationPanel(1,station)));

		is = session.getItemScanner();

	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testProductAdded() {
		for (int i = 0; i < 500; i++) {
			is.itemScanned=false;
			Item item = station.items.get(0);
			station.scs.mainScanner.scan(item);
		}

		assertTrue(is.getCheckoutProducts().size() > 400);
		assertTrue(is.getCheckoutProducts().size() <= 500);
	}

	@Test
	public void testAllProductsAdded() {

		for (int i = 0; i < 100; i++) {
			for (int j = 0; j < 5; j++) {
				is.itemScanned=false;
				Item item = station.items.get(j);
				station.scs.mainScanner.scan(item);
			}
		}

		assertTrue(is.getCheckoutProducts().size() > 400);
		assertTrue(is.getCheckoutProducts().size() <= 500);

	}

	@Test
	public void testPriceOneItem() {

		for (int i = 0; i < 500; i++) {
			is.itemScanned=false;
			Item item = station.items.get(0);
			station.scs.mainScanner.scan(item);
		}

		assertTrue(session.getBalance().compareTo(station.prices[0].multiply(new BigDecimal(400))) >= 0);
		assertTrue(session.getBalance().compareTo(station.prices[0].multiply(new BigDecimal(500))) <= 0);
	}

	@Test
	public void testPriceAllItems() {
		BigDecimal expectedPrice = BigDecimal.ZERO;
		for (int i = 0; i < 100; i++) {
			for (int j = 0; j < 5; j++) {
				is.itemScanned=false;
				Item item = station.items.get(j);
				station.scs.mainScanner.scan(item);

			}
		}
		for (int j = 0; j < 5; j++) {
			expectedPrice = expectedPrice.add(station.prices[j]);
		}

		assertTrue(session.getBalance().compareTo(expectedPrice.multiply(new BigDecimal(80))) >= 0);
		assertTrue(session.getBalance().compareTo(expectedPrice.multiply(new BigDecimal(100))) <= 0);
	}

	@Test
	public void testItemNotInDataBase() {
		Barcode barcode = new Barcode("12679");
		Item item = new BarcodedItem(barcode, 77);
		for (int i = 0; i < 100; i++) {
			station.scs.mainScanner.scan(item);
		}
		assertEquals(0, is.getCheckoutProducts().size());

	}

	@Test
	public void testInventoryUpdated() {

		BarcodedProduct bp = ProductDatabases.BARCODED_PRODUCT_DATABASE.get(station.items.get(0).getBarcode());
		for (int i = 0; i < 50; i++) {
			is.itemScanned=false;
			station.scs.mainScanner.scan(station.items.get(0));

		}
		
		assertTrue(ProductDatabases.INVENTORY.get(bp) >= 750);
		assertTrue(ProductDatabases.INVENTORY.get(bp) <= 760);

	}
	
	@Test
	public void testAlreadyScanned() {
		is.itemScanned=true;
		for (int i = 0; i < 100; i++) {
			station.scs.mainScanner.scan(station.items.get(0));
		}
		assertEquals(0, is.getCheckoutProducts().size());

	}
	
	@Test
	public void testScannedFlag() {
		int counter=0;
		for (int i = 0; i < 100; i++) {
			is.itemScanned=false;
			station.scs.mainScanner.scan(station.items.get(0));
			if(is.itemScanned) {
				counter++;
			}
		}
		
		assertTrue(counter >=80);
		assertTrue(counter <= 100);
		

	}
	
	
	
	

}
