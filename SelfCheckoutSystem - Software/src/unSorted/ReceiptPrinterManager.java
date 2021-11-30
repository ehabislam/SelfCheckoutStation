package unSorted;

import org.lsmr.selfcheckout.devices.SelfCheckoutStation;

public class ReceiptPrinterManager {
	
	SelfCheckoutStation station;
	StationUsage stationUsage;
	
	/**
	 * Class that manages adding paper/ink to printer.
	 * 
	 * @param session
	 */
	public ReceiptPrinterManager(SelfCheckoutStation station, StationUsage stationUsage) {

		this.station = station;
		this.stationUsage = stationUsage;
		
	}
	
	/**
	 * Adds ink to printer
	 * 
	 * @param quantity of ink to add
	 */
	public void addInk(int quantity) {
		
		this.station.printer.addInk(quantity);
		this.stationUsage.setCharactersOfInkRemaining(this.stationUsage.getCharactersOfInkRemaining() + quantity);
		
	}
	
	/**
	 * Adds paper to printer.
	 * 
	 * @param quantity of paper to add.
	 */
	public void addPaper(int quantity) {
		
		this.station.printer.addPaper(quantity);
		this.stationUsage.setLinesOfPaperRemaining(this.stationUsage.getLinesOfPaperRemaining() + quantity);
		
	}

}
