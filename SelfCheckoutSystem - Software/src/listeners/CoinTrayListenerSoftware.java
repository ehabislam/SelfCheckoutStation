package listeners;
import org.lsmr.selfcheckout.devices.AbstractDevice;
import org.lsmr.selfcheckout.devices.CoinTray;
import org.lsmr.selfcheckout.devices.listeners.AbstractDeviceListener;
import org.lsmr.selfcheckout.devices.listeners.CoinTrayListener;

import unSorted.ChangeCounter;

public class CoinTrayListenerSoftware implements CoinTrayListener {

	private ChangeCounter changeCounter;

	public CoinTrayListenerSoftware(ChangeCounter counter) {

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
	public void coinAdded(CoinTray tray) {

		this.changeCounter.setCoinPending(true);

	}

}
