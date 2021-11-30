package unSorted;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import org.lsmr.selfcheckout.devices.BanknoteDispenser;
import org.lsmr.selfcheckout.devices.BanknoteStorageUnit;
import org.lsmr.selfcheckout.devices.CoinDispenser;
import org.lsmr.selfcheckout.devices.CoinStorageUnit;

/**
 * This class can be used to access all the station specific things that are not
 * tied to the sessions on top of the station itself. Things like the printer
 * resources, banknote and coin storages and dispensers
 */
public class StationUsage {
	private Set<Integer> banknoteDispensersToService = new HashSet<>();
	private Set<BigDecimal> coinDispensersToService = new HashSet<>(); 
	private boolean isBanknoteStorageFull = false;
	private boolean isCoinStorageFull = false;
	private int linesOfPaperRemaining = 0;
	private int charactersOfInkRemaining = 0;
	
	public static final int LOW_PAPER_THRESHOLD = 100;
	public static final int LOW_INK_THRESHOLD = 2000;
	
	public boolean banknoteDispenserNeedsRefill(BanknoteDispenser banknoteDispenser) {
		return banknoteDispenser.size() <= (0.20 * banknoteDispenser.getCapacity());
	}
	
	public boolean coinDispenserNeedsRefill(CoinDispenser coinDispenser) {
		return coinDispenser.size() <= (0.20 * coinDispenser.getCapacity());
	}
	
	public boolean coinStorageNeedsEmptying(CoinStorageUnit coinStorage) {
		return coinStorage.getCoinCount() >= (0.80 * coinStorage.getCapacity());
	}
	
	public boolean banknoteStorageNeedsEmptying(BanknoteStorageUnit BNStorage) {
		return BNStorage.getBanknoteCount() >= (0.80 * BNStorage.getCapacity());
	}

	public Set<Integer> getBanknoteDispensersToService() {
		return banknoteDispensersToService;
	}

	public Set<BigDecimal> getCoinDispensersToService() {
		return coinDispensersToService;
	}
	
	public boolean isBanknoteStorageFull() {
		return isBanknoteStorageFull;
	}

	public void setBanknoteStorageFull(boolean isBanknoteStorageFull) {
		this.isBanknoteStorageFull = isBanknoteStorageFull;
	}

	public boolean isCoinStorageFull() {
		return isCoinStorageFull;
	}

	public void setCoinStorageFull(boolean isCoinStorageFull) {
		this.isCoinStorageFull = isCoinStorageFull;
	}

	public void setLinesOfPaperRemaining(int quantity) { this.linesOfPaperRemaining = quantity; }
	public void setCharactersOfInkRemaining(int quantity) { this.charactersOfInkRemaining = quantity; }
	
	public int getLinesOfPaperRemaining() { return this.linesOfPaperRemaining; }
	public int getCharactersOfInkRemaining() { return this.charactersOfInkRemaining; }
	
	public boolean receiptPrinterNeedsPaper() { return this.linesOfPaperRemaining <= StationUsage.LOW_PAPER_THRESHOLD; }
	public boolean receiptPrinterNeedsInk() { return this.charactersOfInkRemaining <= StationUsage.LOW_INK_THRESHOLD; }
}
