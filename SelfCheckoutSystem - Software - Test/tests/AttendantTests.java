import static org.junit.Assert.*;

import java.math.BigDecimal;

import org.junit.Test;
import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.BarcodedItem;
import org.lsmr.selfcheckout.Item;
import org.lsmr.selfcheckout.devices.OverloadException;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
import org.lsmr.selfcheckout.products.BarcodedProduct;
import org.lsmr.selfcheckout.products.PLUCodedProduct;

import attendantConsole.AttendantConsole;
import gui.StationPanel;
import listeners.BaggingAreaListenerSoftware;
import unSorted.CustomerUseCases;
import unSorted.SelfCheckoutStationInstance;
import unSorted.Session;

public class AttendantTests {

	@Test
	public void ProductLookupTest_Barcode() {
		
		SelfCheckoutStationInstance stationInstance = new SelfCheckoutStationInstance(0);
		Session session = new Session(stationInstance,new GUIDispatchStub(new StationPanel(1,stationInstance)));
		CustomerUseCases customerUseCases = new CustomerUseCases(session);
		BaggingAreaListenerSoftware listener = new BaggingAreaListenerSoftware(session, customerUseCases);;
		SelfCheckoutStation station = stationInstance.scs;
		

		// as defined in SelfCheckoutStationInstance
		BarcodedProduct barcodedProduct = stationInstance.barcodedProducts.get(0);
		BigDecimal price = barcodedProduct.getPrice();

		assertTrue(((BigDecimal) customerUseCases.lookupProduct(barcodedProduct.getBarcode()).get(0))
				.doubleValue() == price.doubleValue());

	}

	@Test
	public void ProductLookupTest_PLU() {
		
		SelfCheckoutStationInstance stationInstance = new SelfCheckoutStationInstance(0);
		Session session = new Session(stationInstance,new GUIDispatchStub(new StationPanel(1,stationInstance)));
		CustomerUseCases customerUseCases = new CustomerUseCases(session);
		BaggingAreaListenerSoftware listener = new BaggingAreaListenerSoftware(session, customerUseCases);;
		SelfCheckoutStation station = stationInstance.scs;
		

		// as defined in SelfCheckoutStationInstance
		PLUCodedProduct product = stationInstance.pluProducts.get(0);
		BigDecimal price = product.getPrice();

		assertTrue(((BigDecimal) customerUseCases.lookupProduct(product.getPLUCode()).get(0))
				.doubleValue() == price.doubleValue());

	}

}
