package listeners;
import org.lsmr.selfcheckout.devices.AbstractDevice;
import org.lsmr.selfcheckout.devices.CoinStorageUnit;
import org.lsmr.selfcheckout.devices.listeners.AbstractDeviceListener;
import org.lsmr.selfcheckout.devices.listeners.CoinStorageUnitListener;

import unSorted.Session;
import unSorted.StationUsage;

public class CoinStorageUnitListenerSoftware implements CoinStorageUnitListener {

	private Session session;

	public CoinStorageUnitListenerSoftware(Session session) {
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
	public void coinsFull(CoinStorageUnit unit) {
		// TODO Auto-generated method stub

	}

	@Override
	public void coinAdded(CoinStorageUnit unit) {
		StationUsage stationUsage = session.getStationUsage();
		
		//Check that it was not already flagged as full, to avoid unnecessary rerender
		if (!stationUsage.isCoinStorageFull() && stationUsage.coinStorageNeedsEmptying(unit)) {
			stationUsage.setCoinStorageFull(true);
			session.getGuiDispatch().updateMaintenanceList(session.getStationId());
		}
	}

	@Override
	public void coinsLoaded(CoinStorageUnit unit) {
		// TODO Auto-generated method stub

	}

	@Override
	public void coinsUnloaded(CoinStorageUnit unit) {
		StationUsage stationUsage = session.getStationUsage();
		

		stationUsage.setCoinStorageFull(false);
		session.getGuiDispatch().updateMaintenanceList(session.getStationId());
		

	}

}
