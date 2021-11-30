import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.BarcodedItem;
import org.lsmr.selfcheckout.Item;
import org.lsmr.selfcheckout.devices.OverloadException;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;

import gui.StationPanel;
import listeners.BaggingAreaListenerSoftware;
import unSorted.CustomerUseCases;
import unSorted.SelfCheckoutStationInstance;
import unSorted.Session;

public class BaggingAreaTest {

	@Test
	public void ConformingWeightTest() throws OverloadException {
		
		SelfCheckoutStationInstance stationInstance = new SelfCheckoutStationInstance(0);
		Session session = new Session(stationInstance, new GUIDispatchStub(new StationPanel(1,stationInstance)));
		CustomerUseCases customerUseCases = new CustomerUseCases(session);
		BaggingAreaListenerSoftware listener = new BaggingAreaListenerSoftware(session, customerUseCases);;
		SelfCheckoutStation station = stationInstance.scs;
		
//		station.baggingArea.register(listener);
//		session = new Session(stationInstance);

		Item correctItem = new BarcodedItem(new Barcode("12345"), 1);
		Item wrongItem = new BarcodedItem(new Barcode("54321"), 2);

		// scan correct item and bag correct one
		station.mainScanner.scan(correctItem);
		station.baggingArea.add(correctItem);
		
		// should conform
		assertTrue(listener.checkConform(station.baggingArea, correctItem, correctItem));

	}

	@Test
	public void NonConformingWeightTest() throws OverloadException {
		
		SelfCheckoutStationInstance stationInstance = new SelfCheckoutStationInstance(0);
		Session session = new Session(stationInstance, new GUIDispatchStub(new StationPanel(1,stationInstance)));
		CustomerUseCases customerUseCases = new CustomerUseCases(session);
		BaggingAreaListenerSoftware listener = new BaggingAreaListenerSoftware(session, customerUseCases);;
		SelfCheckoutStation station = stationInstance.scs;
		
//		station.baggingArea.register(listener);
//		session = new Session(stationInstance);

		Item correctItem = new BarcodedItem(new Barcode("12345"), 1);
		Item wrongItem = new BarcodedItem(new Barcode("54321"), 2);
		
		// scan correct item and bag wrong one
		station.mainScanner.scan(correctItem);
		station.baggingArea.add(wrongItem);

		// should not conform
		assertFalse(listener.checkConform(station.baggingArea, correctItem, wrongItem));

	}

}
