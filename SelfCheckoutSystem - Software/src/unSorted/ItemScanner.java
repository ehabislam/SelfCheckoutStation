package unSorted;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
import org.lsmr.selfcheckout.products.BarcodedProduct;

public class ItemScanner {

	private SelfCheckoutStation selfCheckoutStation;
	private BigDecimal currentPrice = BigDecimal.ZERO;
	private List<BarcodedProduct> checkoutProducts = new ArrayList<BarcodedProduct>();
	public boolean itemScanned = false;
	private List<BarcodedProduct> Unbagged_Scanned_Item_List = new ArrayList<BarcodedProduct>();
	
	//TODO: there should be a button that needs to be presses before scanning an item to specify that a particular
	//item needs to be not bagged. 
	public boolean ShouldbeBagged = true;
	
	public ItemScanner(SelfCheckoutStation selfCheckoutStation) {
		this.selfCheckoutStation = selfCheckoutStation;
	}

	public BigDecimal getCurrentPrice() {
		return currentPrice;
	}

	public void setCurrentPrice(BigDecimal currentPrice) {
		this.currentPrice = currentPrice;
	}

	public List<BarcodedProduct> getCheckoutProducts() {
		return checkoutProducts;
	}

	public List<BarcodedProduct> getUnbagged_Scanned_Item_List() {
		return Unbagged_Scanned_Item_List;
	}
	/**
	 * 
	 * This function should only be called when the customer presses a button to tell that 
	 * this particular item should not be bagged. 
	 * @author Abhay Sharma
	 */
	public void shouldnotBeBagged() {
		this.ShouldbeBagged = false;
	}
	
	public boolean getShouldbeBagged() {
		return this.ShouldbeBagged;
	}
	public void setshouldbeBagged(boolean val) {
		this.ShouldbeBagged = val;
	}
	
}
