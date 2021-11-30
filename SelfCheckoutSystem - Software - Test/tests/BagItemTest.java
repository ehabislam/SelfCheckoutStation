import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.lsmr.selfcheckout.Item;

import gui.StationPanel;
import unSorted.BagItem;
import unSorted.ItemScanner;
import unSorted.SelfCheckoutStationInstance;
import unSorted.Session;



public class BagItemTest {
	Session session;
	SelfCheckoutStationInstance station = new SelfCheckoutStationInstance(1);
	BagItem bagItem;
	ItemScanner itemScanner;

	@Before
	public void setUp() throws Exception {
		session = new Session(station,new GUIDispatchStub(new StationPanel(1,station)));
		itemScanner = session.getItemScanner();
		bagItem = session.getBagItem();

	}

	@Test
	public void testBagOneItem() {
		itemScanner.itemScanned = true;
		Item item = station.items.get(0);
		station.scs.baggingArea.add(item);

		assertEquals(station.items.get(0).getWeight(), bagItem.getBaggingWeight(), 0.000001);

	}

	@Test
	public void testBagAllItems() {

		double expectedWeight = 0;
		for (int i = 0; i < station.items.size(); i++) {
			itemScanner.itemScanned = true;
			Item item = station.items.get(i);
			station.scs.baggingArea.add(item);
			expectedWeight += station.items.get(i).getWeight();
		}
		assertEquals(expectedWeight, bagItem.getBaggingWeight(), 0.000001);

	}

	@Test
	public void testScanFailed() {
		itemScanner.itemScanned = false;
		Item item = station.items.get(0);
		station.scs.baggingArea.add(item);
		assertTrue(session.isUnauthorizedItem());
	}
	
	

}
