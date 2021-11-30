package unSorted;

import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
/**
 * Contains a variable containing the weight of all the scanned bagged items 
 * 
 * This weight shall be checked at the end of scanning to see if it is equal to the total weight of 
 * all the items that were supposed to be bagged. 
 * 
 * @author Seher (Modified by Abhay for iteration 3)
 *
 */
public class BagItem {
	private SelfCheckoutStation scs;
	private double baggingWeight;

	public BagItem(SelfCheckoutStation scs) {
		this.scs = scs;
	}

	public double getBaggingWeight() {
		return baggingWeight;
	}

	public void setBaggingWeight(double baggingWeight) {
		this.baggingWeight = baggingWeight;
	}

}
