package listeners;

import java.util.Set;

import org.lsmr.selfcheckout.Banknote;
import org.lsmr.selfcheckout.devices.AbstractDevice;
import org.lsmr.selfcheckout.devices.BanknoteDispenser;
import org.lsmr.selfcheckout.devices.listeners.AbstractDeviceListener;
import org.lsmr.selfcheckout.devices.listeners.BanknoteDispenserListener;

import unSorted.Session;
import unSorted.StationUsage;

public class BanknoteDispenserListenerSoftware implements BanknoteDispenserListener {
	
	private Session session;
	
	public BanknoteDispenserListenerSoftware(Session session) {
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
	public void banknotesFull(BanknoteDispenser dispenser) {
		// TODO Auto-generated method stub

	}

	@Override
	public void banknotesEmpty(BanknoteDispenser dispenser) {
		// TODO Auto-generated method stub

	}

	@Override
	public void banknoteAdded(BanknoteDispenser dispenser, Banknote banknote) {
		// TODO Auto-generated method stub

	}

	@Override
	public void banknoteRemoved(BanknoteDispenser dispenser, Banknote banknote) {
		StationUsage stationUsage = session.getStationUsage();
		
		//station needs servicing after transaction
		if (stationUsage.banknoteDispenserNeedsRefill(dispenser)) {		
			
			//add the current dispenser's denomination to the set of dispensers in need of refill
			Set<Integer> dispensersToService = stationUsage.getBanknoteDispensersToService();	
			dispensersToService.add(banknote.getValue());

			//update control panel UI which should add the current dispenser to the maintenance list
			session.getGuiDispatch().updateMaintenanceList(session.getStationId());
			
		}

	}

	@Override
	public void banknotesLoaded(BanknoteDispenser dispenser, Banknote... banknotes) {
		StationUsage stationUsage = session.getStationUsage();
		
		//ensure enough banknotes were actually loaded
		if (!stationUsage.banknoteDispenserNeedsRefill(dispenser)) {		
			
			//remove the dispenser from set of dispensers that need refilling
			int bankdoteDenomination = banknotes[0].getValue();
			stationUsage.getBanknoteDispensersToService().remove(bankdoteDenomination);
			
			//update control panel UI which should remove the current dispenser from the maintenance list
			session.getGuiDispatch().updateMaintenanceList(session.getStationId());

		}
		

	}

	@Override
	public void banknotesUnloaded(BanknoteDispenser dispenser, Banknote... banknotes) {
		// TODO Auto-generated method stub

	}

}
