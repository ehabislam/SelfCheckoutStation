package listeners;
import org.lsmr.selfcheckout.Item;
import org.lsmr.selfcheckout.devices.AbstractDevice;
import org.lsmr.selfcheckout.devices.ElectronicScale;
import org.lsmr.selfcheckout.devices.listeners.AbstractDeviceListener;
import org.lsmr.selfcheckout.devices.listeners.ElectronicScaleListener;

import unSorted.CustomerUseCases;
import unSorted.Session;

public class BaggingAreaListenerSoftware implements ElectronicScaleListener {

	private Session session;
	CustomerUseCases CUS;

	public BaggingAreaListenerSoftware(Session session,CustomerUseCases CUS) {
		this.session = session;
		this.CUS = CUS;

		// TODO Auto-generated constructor stub
	}
	
	public boolean checkConform(ElectronicScale scale, Item scannedItem, Item addedItem) {
		if (scannedItem.getWeight() != addedItem.getWeight()) {
			return false;
		}
		return true;
	}
	
	@Override
	public void enabled(AbstractDevice<? extends AbstractDeviceListener> device) {
		// TODO Auto-generated method stub

	}

	@Override
	public void disabled(AbstractDevice<? extends AbstractDeviceListener> device) {
		// TODO Auto-generated method stub

	}

	/**
	 * Is called everytime a new item is placed in the scale for bagging 
	 */
	@Override
	public void weightChanged(ElectronicScale scale, double weightInGrams) {
		if (session.getItemScanner().itemScanned && !this.CUS.getisBeingRemoved()) {

			session.getBagItem().setBaggingWeight(weightInGrams);
			session.getItemScanner().itemScanned = false;
		}

		else if(!this.CUS.getisBeingRemoved()){
			session.setUnauthorizedItem(true);
		}
		
		//This condition is true only if an item is being removed from the scale
		if(this.CUS.getisBeingRemoved()) {
			session.getBagItem().setBaggingWeight(weightInGrams);
			this.CUS.setisBeingRemoved(false);
		}
	}

	@Override
	public void overload(ElectronicScale scale) {
		// TODO Auto-generated method stub

	}

	@Override
	public void outOfOverload(ElectronicScale scale) {
		// TODO Auto-generated method stub

	}
	

}
