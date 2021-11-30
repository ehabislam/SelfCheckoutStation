package listeners;
import org.lsmr.selfcheckout.devices.AbstractDevice;
import org.lsmr.selfcheckout.devices.BanknoteStorageUnit;
import org.lsmr.selfcheckout.devices.listeners.AbstractDeviceListener;
import org.lsmr.selfcheckout.devices.listeners.BanknoteStorageUnitListener;

import unSorted.Session;
import unSorted.StationUsage;

public class BanknoteStorageUnitListenerSoftware implements BanknoteStorageUnitListener {

	private Session session;

	public BanknoteStorageUnitListenerSoftware(Session session) {
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
	public void banknotesFull(BanknoteStorageUnit unit) {
		// TODO Auto-generated method stub

	}

	@Override
	public void banknoteAdded(BanknoteStorageUnit unit) {
		StationUsage stationUsage = session.getStationUsage();
		
		//Check that it was not already flagged as full, to avoid unnecessary rerender
		if (!stationUsage.isBanknoteStorageFull() && stationUsage.banknoteStorageNeedsEmptying(unit)) {
			stationUsage.setBanknoteStorageFull(true);
			session.getGuiDispatch().updateMaintenanceList(session.getStationId());
		}

	}

	@Override
	public void banknotesLoaded(BanknoteStorageUnit unit) {
		// TODO Auto-generated method stub

	}

	@Override
	public void banknotesUnloaded(BanknoteStorageUnit unit) {
		StationUsage stationUsage = session.getStationUsage();
		

		stationUsage.setBanknoteStorageFull(false);
		session.getGuiDispatch().updateMaintenanceList(session.getStationId());
		

	}

}
