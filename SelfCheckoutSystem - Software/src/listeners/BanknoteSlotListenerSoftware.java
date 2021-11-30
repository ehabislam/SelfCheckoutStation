package listeners;
import org.lsmr.selfcheckout.devices.AbstractDevice;
import org.lsmr.selfcheckout.devices.BanknoteSlot;
import org.lsmr.selfcheckout.devices.listeners.AbstractDeviceListener;
import org.lsmr.selfcheckout.devices.listeners.BanknoteSlotListener;

import unSorted.ChangeCounter;

public class BanknoteSlotListenerSoftware implements BanknoteSlotListener {

	private ChangeCounter changeCounter;

	public BanknoteSlotListenerSoftware(ChangeCounter counter) {

		this.changeCounter = counter;

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
	public void banknoteInserted(BanknoteSlot slot) {
		// TODO Auto-generated method stub

	}

	@Override
	public void banknoteEjected(BanknoteSlot slot) {

		this.changeCounter.setBanknotePending(true);

	}

	@Override
	public void banknoteRemoved(BanknoteSlot slot) {

		this.changeCounter.setBanknotePending(false);

	}

}
