package unSorted;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.Card.CardData;
import org.lsmr.selfcheckout.Item;
import org.lsmr.selfcheckout.PriceLookupCode;
import org.lsmr.selfcheckout.devices.DisabledException;
import org.lsmr.selfcheckout.devices.ElectronicScale;
import org.lsmr.selfcheckout.devices.EmptyException;
import org.lsmr.selfcheckout.devices.OverloadException;
import org.lsmr.selfcheckout.devices.ReceiptPrinter;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
import org.lsmr.selfcheckout.devices.SimulationException;
import org.lsmr.selfcheckout.devices.listeners.BanknoteStorageUnitListener;
import org.lsmr.selfcheckout.devices.listeners.BanknoteValidatorListener;
import org.lsmr.selfcheckout.devices.listeners.BarcodeScannerListener;
import org.lsmr.selfcheckout.devices.listeners.CardReaderListener;
import org.lsmr.selfcheckout.devices.listeners.CoinStorageUnitListener;
import org.lsmr.selfcheckout.devices.listeners.CoinValidatorListener;
import org.lsmr.selfcheckout.devices.listeners.ElectronicScaleListener;
import org.lsmr.selfcheckout.devices.listeners.ReceiptPrinterListener;
import org.lsmr.selfcheckout.external.ProductDatabases;

import listeners.BaggingAreaListenerSoftware;
import listeners.BanknoteDispenserListenerSoftware;
import listeners.BanknoteStorageUnitListenerSoftware;
import listeners.BanknoteValidatorListenerSoftware;
import listeners.BarcodeScannerListenerSoftware;
import listeners.CardReaderListenerSoftware;
import listeners.CoinDispenserListenerSoftware;
import listeners.CoinStorageUnitListenerSoftware;
import listeners.CoinValidatorListenerSoftware;
import listeners.ReceiptPrinterListenerSoftware;
import utility.Pair;

public class Session {

	private SelfCheckoutStation selfCheckoutStation;
	private PaymentService paymentService;
	private ItemScanner itemScanner;
	private PluItemScanner pluScanner;
	private PlasticBags PlasticBags;
	private BagItem bagItem;
	private ReceiptPrinter printer;
	private BigDecimal balance = BigDecimal.ZERO;
	private Item item;
	private ElectronicScale scale;
	private boolean unauthorizedItem = false;
	private boolean isScanningFinished = false;
	private boolean discountGiven = false;
	private boolean badWeight = false;

	private CustomerUseCases CUS;
	private boolean transactionStarted = false;
	private StationUsage stationUsage;
	private List<Pair<String, BigDecimal>> totalProductsList = new ArrayList<Pair<String, BigDecimal>>();
	private GUIDispatch guiDispatch;
	private int stationId;

	public boolean isTransactionStarted() {
		return transactionStarted;
	}

	public void setTransactionStarted(boolean transactionStarted) {
		this.transactionStarted = transactionStarted;
	}

	public boolean isDiscountGiven() {
		return discountGiven;
	}

	public void setDiscountGiven(boolean wasDiscountGiven) {
		this.discountGiven = wasDiscountGiven;
	}

	public boolean isUnauthorizedItem() {
		return unauthorizedItem;
	}

	public void setUnauthorizedItem(boolean unauthorizedItem) {
		this.unauthorizedItem = unauthorizedItem;
	}

	public Session(SelfCheckoutStationInstance selfCheckoutStationInstance, GUIDispatch guiDispatch) {
		this.guiDispatch = guiDispatch;
		stationId = selfCheckoutStationInstance.id;
		selfCheckoutStation = selfCheckoutStationInstance.scs;
		stationUsage = selfCheckoutStationInstance.stationUsage;
		paymentService = new PaymentService(selfCheckoutStation);
		itemScanner = new ItemScanner(selfCheckoutStation);
		pluScanner = new PluItemScanner();
		bagItem = new BagItem(selfCheckoutStation);
		PlasticBags = new PlasticBags();
		CUS = new CustomerUseCases(this);
		this.printer = selfCheckoutStation.printer;
		registerListeners();
	}

	public PlasticBags getPlasticBags() {
		return this.PlasticBags;
	}

	public BagItem getBagItem() {
		return bagItem;
	}

	private void registerListeners() {
		CoinStorageUnitListener coinStorageUnitListener = new CoinStorageUnitListenerSoftware(this);
		selfCheckoutStation.coinStorage.register(coinStorageUnitListener);

		BanknoteStorageUnitListener banknoteStorageUnitListener = new BanknoteStorageUnitListenerSoftware(this);
		selfCheckoutStation.banknoteStorage.register(banknoteStorageUnitListener);
		
		BarcodeScannerListener barcodeScannerlistener = new BarcodeScannerListenerSoftware(this);
		selfCheckoutStation.mainScanner.register(barcodeScannerlistener);
		selfCheckoutStation.handheldScanner.register(barcodeScannerlistener);

		ElectronicScaleListener baggingAreaListener = new BaggingAreaListenerSoftware(this, this.CUS);
		selfCheckoutStation.baggingArea.register(baggingAreaListener);

		CardReaderListener cardReaderListener = new CardReaderListenerSoftware(this);
		selfCheckoutStation.cardReader.register(cardReaderListener);

		CoinValidatorListener coinValidatorListener = new CoinValidatorListenerSoftware(this);
		selfCheckoutStation.coinValidator.register(coinValidatorListener);

		BanknoteValidatorListener banknoteValidatorListener = new BanknoteValidatorListenerSoftware(this);
		selfCheckoutStation.banknoteValidator.register(banknoteValidatorListener);

		BanknoteDispenserListenerSoftware banknoteDispenserListenerSoftware = new BanknoteDispenserListenerSoftware(
				this);
		selfCheckoutStation.banknoteDispensers.forEach((denomination, dispenser) -> {
			dispenser.register(banknoteDispenserListenerSoftware);
		});

		CoinDispenserListenerSoftware coinDispenserListenerSoftware = new CoinDispenserListenerSoftware(this);
		selfCheckoutStation.coinDispensers.forEach((denomination, dispenser) -> {
			dispenser.register(coinDispenserListenerSoftware);
		});

		ReceiptPrinterListener receiptPrinterListenerSoftware = new ReceiptPrinterListenerSoftware(this);
		selfCheckoutStation.printer.register(receiptPrinterListenerSoftware);
	}

	public void handleCardPayment(CardData cardData) {
		boolean isPaymentSuccessful = paymentService.processCardPayment(cardData, balance);
		if (isPaymentSuccessful) {
			balance = BigDecimal.ZERO;
			getGuiDispatch().updateItemListPanel();
		} else {
			// placeholder for notifying customer their transaction failed
			System.out.println("The transaction failed");
		}
	}

	public void shouldRemoveProduct() {
		System.out.println("Would you like to add the product to the bag (answer with y or n)?");
		@SuppressWarnings("resource")
		Scanner input = new Scanner(System.in);
		String in = input.nextLine();
		if (in == "n") {
			scale.remove(item);
		} else if (in == "y") {
			System.out.println("Product added succesfully");
		} else {
			System.out.println("That is not a valid input.");
			shouldRemoveProduct();
		}
	}

	public String lookUpPLUProduct(String pluString) {
		String description;
		PriceLookupCode pluCode = new PriceLookupCode(pluString );
		if (ProductDatabases.PLU_PRODUCT_DATABASE.containsKey(pluCode)) {			
			description = ProductDatabases.PLU_PRODUCT_DATABASE.get(pluCode).getDescription();
		} else {
			description = "This is not a valid PLU code!";
		}
		return description;
	}

	public String lookUpBarcodedProduct(String barcodeString) {
		String description ;
		Barcode barcode = new Barcode(barcodeString);
		if (ProductDatabases.BARCODED_PRODUCT_DATABASE.containsKey(barcode)) {
			description = ProductDatabases.BARCODED_PRODUCT_DATABASE.get(barcode).getDescription();
		} else {
			description = "This is not a valid barcode!";
		}
		 return description;
	}

	public PaymentService getPaymentService() {
		return paymentService;
	}

	public ItemScanner getItemScanner() {
		return itemScanner;
	}

	public PluItemScanner getPluItemScanner() {
		return pluScanner;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public CustomerUseCases getCUS() {
		return CUS;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;

		// if balance good, dispense change
		if (balance.compareTo(BigDecimal.ZERO) <= 0) {
			this.dispenseChange();
		}
	}

	public void shutDownScanning() {
		setisScanningFinished(true);
	}

	public void restartScanning() {
		setisScanningFinished(false);
	}

	public boolean getisScanningFinished() {
		return this.isScanningFinished;
	}

	public void setisScanningFinished(boolean isScanningFinished) {
		this.isScanningFinished = isScanningFinished;
	}

	private void dispenseChange() {
		ChangeCounter changeCounter = new ChangeCounter(this.selfCheckoutStation, this.balance);

		int banknoteCount = changeCounter.getBanknoteChange().size();
		int coinCount = changeCounter.getCoinChange().size();

		while (banknoteCount > 0) {
			try {
				changeCounter.dispenseNextBanknote();
			} catch (EmptyException e) {
				System.out.println("A required banknote dispenser is empty.");
				break;
			} catch (DisabledException e) {
				System.out.println("A required banknote dispenser is disabled.");
				break;
			} catch (OverloadException e) {
				// The coin tray is full. System will wait until user collects the coins.
				continue;
			}
			
			banknoteCount = changeCounter.getBanknoteChange().size();
		}

		while (coinCount > 0) {
			try {
				changeCounter.dispenseNextCoin();
			} catch (EmptyException e) {
				System.out.println("A required coin dispenser is empty.");
				break;
			} catch (DisabledException e) {
				System.out.println("A required banknote dispenser is disabled.");
				break;
			} catch (OverloadException e) {
				// The banknote slot is currently occupied by a dangling banknote. System will
				// wait until user collects the banknote.
				continue;
			}
			
			coinCount = changeCounter.getCoinChange().size();
		}
	}

	public SelfCheckoutStation getCheckoutStation() {
		return this.selfCheckoutStation;
	}


	/**
	 * Prints a string to the printer. Use {@code generateReceiptContent()} to get
	 * the receipt content.
	 * 
	 * @param content to print.
	 */
	public void print(String content) {
		int counter = 0;

		try {
			while (counter < content.length()) {
				char c = content.charAt(counter);

				// if printing non whitespace char, decrement ink in stationUsage
				if (!Character.isWhitespace(c)) {

					this.stationUsage.setCharactersOfInkRemaining(this.stationUsage.getCharactersOfInkRemaining() - 1);

				} else if (c == '\n') {

					this.stationUsage.setLinesOfPaperRemaining(this.stationUsage.getLinesOfPaperRemaining() - 1);

				}

				printer.print(c);
				counter++;
			}
		} catch (SimulationException e) {
			e.printStackTrace();
			throw new SimulationException("Ink or Paper are out !");
		}
		printer.cutPaper();
		
		if (stationUsage.receiptPrinterNeedsInk() || stationUsage.receiptPrinterNeedsPaper()) {
			guiDispatch.updateMaintenanceList(stationId);
		}
	}

	/**
	 * Generate the text content of the receipt
	 *
	 * @return A string containing all of the text content of the receipt
	 */
	public String generateReceiptContent() {

		StringBuilder content = new StringBuilder();

		content.append("List of Scanned Items: \n");

		BigDecimal totalPrice = BigDecimal.ZERO;

		// Iterating through products list to add each one to the receipt
		for (Pair<String, BigDecimal> pair : getTotalProductsList()) {
			content.append(String.format("%-30.30s%29.2f\n", pair.getKey(), pair.getValue()));
			totalPrice = totalPrice.add(pair.getValue());
		}

		content.append(String.format("Total Price:%47.2f\n", totalPrice));

		if (discountGiven) {
			content.append(String.format("Discount Amount:%43.2f\n", totalPrice.subtract(getBalance())));
			content.append(String.format("Amount After Discount:%37.2f\n", getBalance()));
		}

		return new String(content);
	}


	public StationUsage getStationUsage() {
		return stationUsage;
	}

	public List<Pair<String, BigDecimal>> getTotalProductsList() {
		return totalProductsList;
	}

	public GUIDispatch getGuiDispatch() {
		return guiDispatch;
	}

	public int getStationId() {
		return stationId;
	}

}
