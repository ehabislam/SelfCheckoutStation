import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.lsmr.selfcheckout.Banknote;
import org.lsmr.selfcheckout.Coin;
import org.lsmr.selfcheckout.devices.BanknoteDispenser;
import org.lsmr.selfcheckout.devices.BanknoteStorageUnit;
import org.lsmr.selfcheckout.devices.CoinDispenser;
import org.lsmr.selfcheckout.devices.CoinStorageUnit;
import org.lsmr.selfcheckout.devices.OverloadException;
import org.lsmr.selfcheckout.devices.ReceiptPrinter;
import org.lsmr.selfcheckout.devices.SimulationException;

import gui.GUIHelper;
import gui.StationPanel;
import listeners.BanknoteDispenserListenerSoftware;
import listeners.BanknoteStorageUnitListenerSoftware;
import listeners.CoinDispenserListenerSoftware;
import listeners.CoinStorageUnitListenerSoftware;
import listeners.ReceiptPrinterListenerSoftware;
import unSorted.GUIDispatch;
import unSorted.SelfCheckoutStationInstance;
import unSorted.Session;
import unSorted.StationUsage;

public class StationUsageTests {
	Session session;
	SelfCheckoutStationInstance station = new SelfCheckoutStationInstance(2);
	BanknoteDispenser BNdisp; 
	StationPanel panel;
	GUIDispatchStub dispatch;
	CoinDispenser coinDisp;
	BanknoteStorageUnit BNstorageUnit;
	CoinStorageUnit coinStorageUnit;
	ReceiptPrinter receiptPrinter;
	BanknoteDispenserListenerSoftware BNDispListener;
	CoinDispenserListenerSoftware coinDispListener;
	BanknoteStorageUnitListenerSoftware BNstorageUnitListener;
	CoinStorageUnitListenerSoftware coinStorageUnitListener;
	ReceiptPrinterListenerSoftware receiptPrinterListener;
	
	@Before
	public void setUp() throws Exception {
		
		panel = new StationPanel(1, station);
		dispatch = new GUIDispatchStub(panel);
		session = new Session(station, dispatch);
		BNdisp = new BanknoteDispenser(10);
		coinDisp = new CoinDispenser(10);
		BNstorageUnit = new BanknoteStorageUnit(10);
		coinStorageUnit = new CoinStorageUnit(10);
		receiptPrinter = new ReceiptPrinter();
		BNDispListener = new BanknoteDispenserListenerSoftware(session);
		coinDispListener = new CoinDispenserListenerSoftware(session);
		BNstorageUnitListener = new BanknoteStorageUnitListenerSoftware(session);
		coinStorageUnitListener = new CoinStorageUnitListenerSoftware(session);
		receiptPrinterListener = new ReceiptPrinterListenerSoftware(session);
	}
	
	@After
	public void tearDown() throws Exception {
	}
	
	//Test for when a banknote is removed and the dispenser does not need refilling
	@Test
	public void sufficientBanknoteDispenser() throws SimulationException, OverloadException {
		Banknote BN = new Banknote(5, Currency.getInstance("CAD"));
		Banknote[] Banknotes = {BN,BN,BN,BN,BN}; 
		Set<Integer> dispensersToService = new HashSet();
		
		BNdisp.load(Banknotes);
		BNDispListener.banknoteRemoved(BNdisp, BN);
		assertEquals(dispensersToService, session.getStationUsage().getBanknoteDispensersToService());
	}
	
	//Test for when a banknote is removed and the dispenser does need refilling
	@Test
	public void lowBanknoteDispenser() throws SimulationException, OverloadException {
		Banknote BN = new Banknote(5, Currency.getInstance("CAD"));
		Banknote[] Banknotes = {BN, BN}; 
		Set<Integer> dispensersToService = new HashSet();
		dispensersToService.add(BN.getValue());
		
		BNdisp.load(Banknotes);

		BNDispListener.banknoteRemoved(BNdisp, BN);
		assertEquals(dispensersToService, session.getStationUsage().getBanknoteDispensersToService());
	}
	
	//Test for when the banknote dispenser is loaded with enough banknotes
	@Test
	public void BanknoteDispenserLoadedProperly() throws SimulationException, OverloadException {
		Banknote BN = new Banknote(5, Currency.getInstance("CAD"));
		Banknote[] Banknotes = {BN, BN}; 
		Set<Integer> dispensersToService = new HashSet();
		
		BNdisp.load(Banknotes);

		//remove one banknote to put dispener into dispenser to service
		BNDispListener.banknoteRemoved(BNdisp, BN);
		
		//add the banknote to the dispenser
		BNdisp.load(BN);
		BNDispListener.banknotesLoaded(BNdisp, BN);
		assertEquals(dispensersToService, session.getStationUsage().getBanknoteDispensersToService());
	}
	
	//Test for when the banknote dispenser is loaded but the quantity loaded is not enough
	@Test
	public void BanknoteDispenserLoadedinsufficiently() throws SimulationException, OverloadException {
		Banknote BN = new Banknote(5, Currency.getInstance("CAD"));
		Banknote[] Banknotes = {BN}; 
		Set<Integer> dispensersToService = new HashSet();
		dispensersToService.add(BN.getValue());
		
		BNdisp.load(Banknotes);

		
		//remove one banknote to put dispener into dispenser to service
		BNDispListener.banknoteRemoved(BNdisp, BN);
		//add the banknote to the dispenser
		BNdisp.load(BN);
		BNDispListener.banknotesLoaded(BNdisp, BN);
		assertEquals(dispensersToService, session.getStationUsage().getBanknoteDispensersToService());
	}
	
	//Test for when a coin is removed and the dispenser does not need refilling
	@Test
	public void sufficientCoinDispenser() throws SimulationException, OverloadException {
		Set<BigDecimal> dispensersToService = new HashSet();
	
		Coin coin = new Coin(BigDecimal.valueOf(0.1), Currency.getInstance("CAD"));
		Coin[] coins = {coin, coin, coin, coin , coin};
		coinDisp.load(coins);
		
		
		coinDispListener.coinRemoved(coinDisp, coin);
		assertEquals(dispensersToService, session.getStationUsage().getCoinDispensersToService());
	}
	
	//Test for when a coin is removed and the dispenser does need refilling
	@Test
	public void lowCoinDispenser() throws SimulationException, OverloadException {
		Set<BigDecimal> dispensersToService = new HashSet();
		Coin coin = new Coin(BigDecimal.valueOf(0.1), Currency.getInstance("CAD"));
		Coin[] coins = {coin};
		dispensersToService.add(coin.getValue());
		coinDisp.load(coins);
		
		coinDispListener.coinRemoved(coinDisp, coin);
		assertEquals(dispensersToService, session.getStationUsage().getCoinDispensersToService());
	}
	
	//Test for when the coin dispenser is loaded with enough coins
	@Test
	public void CoinDispenserRefilledProperly() throws SimulationException, OverloadException {
		Set<BigDecimal> dispensersToService = new HashSet();
	
		Coin coin = new Coin(BigDecimal.valueOf(0.1), Currency.getInstance("CAD"));
		Coin[] coins = {coin, coin};
		coinDisp.load(coins);
		
		//remove one coin to put the dispener into dispenser to service
		coinDispListener.coinRemoved(coinDisp, coin);
		coinDisp.load(coin);
		coinDispListener.coinsLoaded(coinDisp, coin);
		assertEquals(dispensersToService, session.getStationUsage().getCoinDispensersToService());
	}
	//Test for when the coin dispenser is loaded but the quantity of coins loaded is not enough 
	@Test
	public void CoinDispenserRefilledInsufficiently() throws SimulationException, OverloadException {
		Set<BigDecimal> dispensersToService = new HashSet();
	
		Coin coin = new Coin(BigDecimal.valueOf(0.1), Currency.getInstance("CAD"));
		Coin[] coins = {coin};
		coinDisp.load(coins);
		dispensersToService.add(coin.getValue());
		
		//remove one coin to put the dispener into dispenser to service

		coinDispListener.coinRemoved(coinDisp, coin);
		coinDisp.load(coin);
		coinDispListener.coinsLoaded(coinDisp, coin);
		assertEquals(dispensersToService, session.getStationUsage().getCoinDispensersToService());
	}
	
	//Test for when a banknote is added to the storage unit and there is still enough space
	@Test
	public void sufficientBanknoteStorageSpace() throws SimulationException, OverloadException {
		Banknote BN = new Banknote(5, Currency.getInstance("CAD"));
		Banknote[] Banknotes = {BN,BN,BN,BN,BN}; 
		BNstorageUnit.load(Banknotes);
		BNstorageUnitListener.banknoteAdded(BNstorageUnit);
		assertFalse(session.getStationUsage().isBanknoteStorageFull());
	}
	
	@Test
	public void BanknoteStorageSpace() throws SimulationException, OverloadException {
		Banknote BN = new Banknote(5, Currency.getInstance("CAD"));
		Banknote[] Banknotes = {BN,BN,BN,BN,BN}; 
		BNstorageUnit.load(Banknotes);
		BNstorageUnitListener.banknoteAdded(BNstorageUnit);
		assertFalse(session.getStationUsage().isBanknoteStorageFull());
	}
	
	@Test
	public void BanknoteStorageEmptiedProperly() throws SimulationException, OverloadException {
		Banknote BN = new Banknote(5, Currency.getInstance("CAD"));
		Banknote[] Banknotes = {BN,BN,BN,BN,BN}; 
		BNstorageUnit.load(Banknotes);
		
		BNstorageUnitListener.banknoteAdded(BNstorageUnit);
		BNstorageUnitListener.banknotesUnloaded(BNstorageUnit);
		assertFalse(session.getStationUsage().isBanknoteStorageFull());
	}
	
	//Test for when a banknote is added to the storage unit and there is no longer enough space
	@Test
	public void lowBanknoteStorageSpace() throws SimulationException, OverloadException {
		Banknote BN = new Banknote(5, Currency.getInstance("CAD"));
		Banknote[] Banknotes = {BN,BN,BN,BN,BN,BN,BN,BN,BN}; 
		BNstorageUnit.load(Banknotes);
		BNstorageUnitListener.banknoteAdded(BNstorageUnit);
		assertTrue(session.getStationUsage().isBanknoteStorageFull());
	}
	
	//Test for emptying the banknote storage
	@Test
	public void emptiedBanknotestorage(){
		BNstorageUnitListener.banknotesUnloaded(BNstorageUnit);
		assertFalse(session.getStationUsage().isBanknoteStorageFull());
	}
	
	//Test for when a coin is added to the storage unit and there is still enough space
	@Test
	public void sufficientCoinStorageSpace() throws SimulationException, OverloadException {
		Coin coin = new Coin(BigDecimal.valueOf(0.1), Currency.getInstance("CAD"));
		Coin[] coins = {coin, coin, coin, coin};
		coinStorageUnit.load(coins);
		coinStorageUnitListener.coinAdded(coinStorageUnit);
		assertFalse(session.getStationUsage().isCoinStorageFull());
	}
	
	//Test for when a coin is added to the storage unit and there is no longer enough space
	@Test
	public void lowCoinStorageSpace() throws SimulationException, OverloadException {
		Coin coin = new Coin(BigDecimal.valueOf(0.1), Currency.getInstance("CAD"));
		Coin[] coins = {coin , coin, coin, coin, coin , coin, coin, coin , coin};
		coinStorageUnit.load(coins);
		coinStorageUnitListener.coinAdded(coinStorageUnit);
		assertTrue(session.getStationUsage().isCoinStorageFull());
	}
	
	//Test for emptying the coin storage
	@Test
	public void emptiedCoinStorage() {
		coinStorageUnitListener.coinsUnloaded(coinStorageUnit);
		assertFalse(session.getStationUsage().isCoinStorageFull());
	}

	//Test for adding ink 
	@Test
	public void inkIsAdded() {
		receiptPrinterListener.inkAdded(receiptPrinter);
		assertFalse(session.getStationUsage().receiptPrinterNeedsInk());
	}
	
	//Test for refilling paper
	@Test 
	public void PaperIsRefilled() {
		receiptPrinterListener.paperAdded(receiptPrinter);
		assertFalse(session.getStationUsage().receiptPrinterNeedsPaper());
	}
}
