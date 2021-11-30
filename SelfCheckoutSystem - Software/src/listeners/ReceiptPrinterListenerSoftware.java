package listeners;

import org.lsmr.selfcheckout.devices.AbstractDevice;
import org.lsmr.selfcheckout.devices.ReceiptPrinter;
import org.lsmr.selfcheckout.devices.listeners.AbstractDeviceListener;
import org.lsmr.selfcheckout.devices.listeners.ReceiptPrinterListener;

import unSorted.Session;
import unSorted.StationUsage;

public class ReceiptPrinterListenerSoftware implements ReceiptPrinterListener {

	StationUsage stationUsage;
	Session session;
	
	public ReceiptPrinterListenerSoftware(Session session) {
		this.session = session;
		this.stationUsage = session.getStationUsage();
		
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
	public void outOfPaper(ReceiptPrinter printer) {

		if (this.stationUsage.getLinesOfPaperRemaining() != 0)
			this.stationUsage.setLinesOfPaperRemaining(0);
		
	}

	@Override
	public void outOfInk(ReceiptPrinter printer) {

		if (this.stationUsage.getCharactersOfInkRemaining() != 0)
			this.stationUsage.setCharactersOfInkRemaining(0);
		
	}

	@Override
	public void paperAdded(ReceiptPrinter printer) {
		
		//Assumes paper added fills max capacity due to the nature of refill and since no amount is specified
		stationUsage.setLinesOfPaperRemaining(ReceiptPrinter.MAXIMUM_PAPER);
		session.getGuiDispatch().updateMaintenanceList(session.getStationId());	
	}

	@Override
	public void inkAdded(ReceiptPrinter printer) {
		//Assumes ink added fills max capacity due to the nature of refill and since no amount is specified
		stationUsage.setCharactersOfInkRemaining(ReceiptPrinter.MAXIMUM_INK);
		session.getGuiDispatch().updateMaintenanceList(session.getStationId());	
	}

}
