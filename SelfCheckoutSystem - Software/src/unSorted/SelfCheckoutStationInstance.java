package unSorted;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.BarcodedItem;
import org.lsmr.selfcheckout.PLUCodedItem;
import org.lsmr.selfcheckout.PriceLookupCode;
import org.lsmr.selfcheckout.devices.ReceiptPrinter;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
import org.lsmr.selfcheckout.external.ProductDatabases;
import org.lsmr.selfcheckout.products.BarcodedProduct;
import org.lsmr.selfcheckout.products.PLUCodedProduct;

public class SelfCheckoutStationInstance {
	public int id;
	public boolean blocked= false;
	public SelfCheckoutStation scs;
	public StationUsage stationUsage;
	public List<Barcode> barcodes = new ArrayList<Barcode>();
	public List<BarcodedItem> items = new ArrayList<BarcodedItem>();
	public List<BarcodedProduct> barcodedProducts = new ArrayList<BarcodedProduct>();
	public String descriptions[] = new String[] { "Milk", "Honey", "Sausage", "Butter", "Sugar" };
	public BigDecimal prices[] = new BigDecimal[] { new BigDecimal(5.0), new BigDecimal(10.5), new BigDecimal(3.75),
			new BigDecimal(9.86), new BigDecimal(1.54) };
	public String barcodesNum[] = new String[] { "12345", "1345", "55689", "4442323", "86645222" };
	int inventoryNum[] = new int[] { 800, 800, 800, 800, 800 };
	double weight[] = new double[] { 6.25, 7.54, 2.05, 15.67, 34.54 };
	public Currency currency = Currency.getInstance(Locale.CANADA);
	public int banknoteDenominations[] = { 5, 10, 20, 50, 100 };
	public BigDecimal coinDenominations[] = new BigDecimal[] { new BigDecimal(0.05), new BigDecimal(0.10),
			new BigDecimal(0.25), new BigDecimal(0.50), new BigDecimal(1), new BigDecimal(2) };

	public List<BarcodedItem> itemsSystemTest = new ArrayList<BarcodedItem>();

	public List<CardDataStub> membershipCards = new ArrayList<CardDataStub>();
	public String cardHolders[] = new String[] { "Person1", "Person2", "Person3", "Person4", "Person5" };
	public String cardNumbers[] = new String[] { "1234567887654321", "1357900000097531", "2468000000008642",
			"4812162024283236", "3691215182124273" };
	public String memberLevels[] = new String[] { "Basic", "Silver", "Gold", "Basic", "gold" };
	public String dates[] = new String[] { "2020-05-08", "2018-06-29", "2021-03-21", "2009-05-30", "2015-12-25" };
	public int multipliers[] = new int[] { 1, 2, 5, 1, 5 };
	public int points[] = new int[] { 100, 2 * (99), (int) (5 * 97.02), (int) 92.169, (int) (5 * 91.2384) };
	public String point = "0";

	public List<PriceLookupCode> PLUcodes = new ArrayList<PriceLookupCode>();
	public List<PLUCodedItem> pluItems = new ArrayList<PLUCodedItem>();
	public List<PLUCodedProduct> pluProducts = new ArrayList<PLUCodedProduct>();
	public String pluDescriptions[] = new String[] {"Apples", "Bananas", "Tomatoes", "Berries", "Carrots"};
	public BigDecimal pluPrices[] = new BigDecimal[] {new BigDecimal(6), new BigDecimal(4.55), new BigDecimal(3.99),
			new BigDecimal(15.6), new BigDecimal(0.5)};
	public String pluNum[] = new String[] {"1111","2222","3333","4444","5555"};

	public String giftCardNumber[] = new String[] {"11111","22222","12345", "54321","11223"};
	public BigDecimal giftCardBalance[] = new BigDecimal[] {new BigDecimal(100), new BigDecimal(50), new BigDecimal(10),
			new BigDecimal(0), new BigDecimal(2000)};
	
	public SelfCheckoutStationInstance(int id) {
		this.id = id;
		scs = new SelfCheckoutStation(currency, banknoteDenominations, coinDenominations, 135000, 1);
		stationUsage = new StationUsage();
		stationUsage.setCharactersOfInkRemaining(ReceiptPrinter.MAXIMUM_INK);
		stationUsage.setLinesOfPaperRemaining(ReceiptPrinter.MAXIMUM_PAPER);
		InitializeDatabase();
		createItemsSystemTest();
		InitializePLUDatabase();
		InitializeGiftCardDatabase();
		this.scs.printer.addInk(ReceiptPrinter.MAXIMUM_INK);
		this.scs.printer.addPaper(ReceiptPrinter.MAXIMUM_PAPER);
		
	}
	
	private void InitializeGiftCardDatabase() {
		for (int i = 0; i < 5; i++) {
			GiftcardDatabase.GIFTCARD_DATABASE.put(giftCardNumber[i], giftCardBalance[i]);
		}
	}
	
	private void InitializePLUDatabase() {
		for (int i = 0; i < 5; i++) {

			PriceLookupCode b = new PriceLookupCode(pluNum[i]);
			PLUcodes.add(b);
			PLUCodedProduct bp = new PLUCodedProduct(b, pluDescriptions[i], pluPrices[i]);
			pluProducts.add(bp);

			ProductDatabases.PLU_PRODUCT_DATABASE.put(b, bp);
			ProductDatabases.INVENTORY.put(bp, inventoryNum[i]);
			//pluItems.add(new PLUCodedItem(b, -1));
		}
	}

	private void InitializeDatabase() {
		for (int i = 0; i < 5; i++) {

			Barcode b = new Barcode(barcodesNum[i]);
			barcodes.add(b);
			BarcodedProduct bp = new BarcodedProduct(b, descriptions[i], prices[i]);
			barcodedProducts.add(bp);

			ProductDatabases.BARCODED_PRODUCT_DATABASE.put(b, bp);
			ProductDatabases.INVENTORY.put(bp, inventoryNum[i]);
			items.add(new BarcodedItem(b, weight[i]));
		}

		populateMemberDatabase();
		
	}

	public void createItemsSystemTest() {
		for (int i = 1; i <= 100; i++) {
			Barcode b = new Barcode(String.valueOf(i));
			itemsSystemTest.add(new BarcodedItem(b, i));
			BarcodedProduct bp = new BarcodedProduct(b, "Milk", new BigDecimal(i));
			ProductDatabases.BARCODED_PRODUCT_DATABASE.put(b, bp);
			ProductDatabases.INVENTORY.put(bp, i);
		}

	}

	private void populateMemberDatabase() {

		for (int i = 0; i < 5; i++) {
			List<String> l = new ArrayList<String>();
			l.add(memberLevels[i]);
			l.add(dates[i]);
			l.add(point);
			membershipCards.add(new CardDataStub("CO-OP", cardNumbers[i], cardHolders[i]));
			MemberDatabase.MEMBER_DATABASE.put(cardNumbers[i], l);

		}

	}

	
}
