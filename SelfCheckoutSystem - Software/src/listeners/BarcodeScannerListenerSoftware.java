package listeners;
import java.math.BigDecimal;

import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.devices.AbstractDevice;
import org.lsmr.selfcheckout.devices.BarcodeScanner;
import org.lsmr.selfcheckout.devices.listeners.AbstractDeviceListener;
import org.lsmr.selfcheckout.devices.listeners.BarcodeScannerListener;
import org.lsmr.selfcheckout.external.ProductDatabases;
import org.lsmr.selfcheckout.products.BarcodedProduct;

import unSorted.ItemScanner;
import unSorted.Session;
import utility.Pair;

public class BarcodeScannerListenerSoftware implements BarcodeScannerListener {

	private Session session;

	public BarcodeScannerListenerSoftware(Session session) {
		this.session = session;
	}

	@Override
	public void enabled(AbstractDevice<? extends AbstractDeviceListener> device) {
		// TODO Auto-generated method stub

	}

	@Override
	public void disabled(AbstractDevice<? extends AbstractDeviceListener> device) {
		// TODO Auto-generated method stub

	}

	@Override
	public void barcodeScanned(BarcodeScanner barcodeScanner, Barcode barcode) {
		if (!session.getisScanningFinished()) {
			ItemScanner itemScanner = session.getItemScanner();
			// if no unbagged item exist, scan new item
			if (!itemScanner.itemScanned) {
				itemScanner.itemScanned = true; // new item needs to be bagged if specified by the customer.

				if (ProductDatabases.BARCODED_PRODUCT_DATABASE.containsKey(barcode)) {
					BarcodedProduct bp = ProductDatabases.BARCODED_PRODUCT_DATABASE.get(barcode);

					if (ProductDatabases.INVENTORY.containsKey(bp)) {
						int i = ProductDatabases.INVENTORY.get(bp);
						if (i != 0) {
//							BigDecimal updatedPrice = itemScanner.getCurrentPrice().add(bp.getPrice());
//							itemScanner.setCurrentPrice(updatedPrice);
							
							// Adding the product to the uniform list in Session
							Pair<String, BigDecimal> nameToPricePair = new Pair<>(bp.getDescription(), bp.getPrice());
							session.getTotalProductsList().add(nameToPricePair);

							itemScanner.getCheckoutProducts().add(bp);
							BigDecimal updatedPrice = session.getBalance().add(bp.getPrice());
							session.setBalance(updatedPrice);
							session.getGuiDispatch().updateItemListPanel();
							
							//-------------//
							//This block was added by Abhay.
							//Setting that the item is scanned in case the item should not be bagged. 
							//Adds a scanned item to Unbagged Scanned Item List if it should not be scanned. 
							if(!session.getItemScanner().getShouldbeBagged()) {
								session.getItemScanner().getUnbagged_Scanned_Item_List().add(bp);
								session.getItemScanner().itemScanned = false;
								session.getItemScanner().setshouldbeBagged(true);
							}
							//------------//
							
							i--;

							ProductDatabases.INVENTORY.replace(bp, i);
						}
					}
				}
			}
		}
	}
}
