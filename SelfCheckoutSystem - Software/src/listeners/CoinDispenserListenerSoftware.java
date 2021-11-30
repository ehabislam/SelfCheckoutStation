package listeners;

import java.math.BigDecimal;
import java.util.Set;

import org.lsmr.selfcheckout.Coin;
import org.lsmr.selfcheckout.devices.AbstractDevice;
import org.lsmr.selfcheckout.devices.CoinDispenser;
import org.lsmr.selfcheckout.devices.listeners.AbstractDeviceListener;
import org.lsmr.selfcheckout.devices.listeners.CoinDispenserListener;

import unSorted.Session;
import unSorted.StationUsage;

public class CoinDispenserListenerSoftware implements CoinDispenserListener {
	
	private Session session;
	
	public CoinDispenserListenerSoftware(Session session) {
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
	public void coinsFull(CoinDispenser dispenser) {
		// TODO Auto-generated method stub

	}

	@Override
	public void coinsEmpty(CoinDispenser dispenser) {
		// TODO Auto-generated method stub

	}

	@Override
	public void coinAdded(CoinDispenser dispenser, Coin coin) {
		// TODO Auto-generated method stub

	}

	@Override
	public void coinRemoved(CoinDispenser dispenser, Coin coin) {
		StationUsage stationUsage = session.getStationUsage();
		
		//station needs servicing after transaction
		if (stationUsage.coinDispenserNeedsRefill(dispenser)) {		
			
			//add the current dispenser's denomination to the set of dispensers in need of refill
			Set<BigDecimal> dispensersToService = stationUsage.getCoinDispensersToService();	
			BigDecimal coinDenomination = coin.getValue();
			dispensersToService.add(coinDenomination);
			
			//update control panel UI which should remove the current dispenser from the maintenance list
			session.getGuiDispatch().updateMaintenanceList(session.getStationId());

		}

	}

	@Override
	public void coinsLoaded(CoinDispenser dispenser, Coin... coins) {
		StationUsage stationUsage = session.getStationUsage();
		
		//ensure enough coins were actually loaded
		if (!stationUsage.coinDispenserNeedsRefill(dispenser)) {		
			
			//remove the dispenser from set of dispensers that need refilling
			BigDecimal coinDenomination = coins[0].getValue();
			stationUsage.getCoinDispensersToService().remove(coinDenomination);

			//update control panel UI which should remove the current dispenser from the maintenance list
			session.getGuiDispatch().updateMaintenanceList(session.getStationId());
		}

	}

	@Override
	public void coinsUnloaded(CoinDispenser dispenser, Coin... coins) {
		// TODO Auto-generated method stub

	}

}
