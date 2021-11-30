import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.lsmr.selfcheckout.devices.ReceiptPrinter;
import org.lsmr.selfcheckout.devices.SimulationException;

import gui.StationPanel;
import unSorted.GUIDispatch;
import unSorted.SelfCheckoutStationInstance;
import unSorted.Session;
import unSorted.StationUsage;

public class ReceiptPrinterResourcesTest {
	
	private SelfCheckoutStationInstance instance;
	private Session session;
	private int usedInk = 0;
	private int usedPaper = 0;
	
	@Before
	public void setUp() {
		instance = new SelfCheckoutStationInstance(1);
		session = new Session(instance, new GUIDispatchStub(new StationPanel(1,instance)));
	}

	@Test
	public void startsWithFullResources() {
		assertEquals(session.getStationUsage().getLinesOfPaperRemaining(), ReceiptPrinter.MAXIMUM_PAPER);
		assertEquals(session.getStationUsage().getCharactersOfInkRemaining(), ReceiptPrinter.MAXIMUM_INK);
		assertFalse(session.getStationUsage().receiptPrinterNeedsPaper());
		assertFalse(session.getStationUsage().receiptPrinterNeedsInk());
	}

	@Test
	public void addInkTest() {
		
		String toPrint = "SENG300";
		session.print(toPrint);
		this.usedInk += toPrint.length();
		
		session.getCheckoutStation().printer.addInk(this.usedInk);
		
		assertEquals(session.getStationUsage().getCharactersOfInkRemaining(), ReceiptPrinter.MAXIMUM_INK);
		
	}

	@Test
	public void inkDecrementTest() {
		
		String toPrint = "a";

		session.print(toPrint);
		this.usedInk += toPrint.length();

		assertEquals(session.getStationUsage().getCharactersOfInkRemaining(), ReceiptPrinter.MAXIMUM_INK - toPrint.length());
		
	}

	@Test
	public void addPaperTest() {
		
		String toPrint = "\n";
		
		session.print(toPrint);
		this.usedPaper += 1;

		session.getCheckoutStation().printer.addPaper(this.usedPaper);

		assertEquals(session.getStationUsage().getLinesOfPaperRemaining(), ReceiptPrinter.MAXIMUM_PAPER);
	}

	@Test
	public void paperDecrementTest() {
		
		String toPrint = "\n";
		session.print(toPrint);
		
		this.usedPaper += 1;

		session.getCheckoutStation().printer.addPaper(this.usedPaper);

		assertEquals(session.getStationUsage().getLinesOfPaperRemaining(), ReceiptPrinter.MAXIMUM_PAPER);
		
	}


	@Test
	public void paperNeededTest() {
		
		session.getCheckoutStation().printer.addInk(this.usedInk);
		session.getCheckoutStation().printer.addPaper(this.usedPaper);
		
		for (int i=0; i < ReceiptPrinter.MAXIMUM_PAPER - StationUsage.LOW_PAPER_THRESHOLD; i++) {
			
			session.print("\n");
			session.print("a");
			this.usedInk++;
			this.usedPaper++;
			
			session.getCheckoutStation().printer.addInk(1);
			
		}
		
		System.out.println(session.getStationUsage().getLinesOfPaperRemaining());
		assertTrue(session.getStationUsage().receiptPrinterNeedsPaper());
		
	}

	/**
	 * This is test of what happens when someone tries to print a receipt when there
	 * is no paper left in the printer
	 */
	@Test(expected = SimulationException.class)
	public void notEnoughPaperWhenPrinting() {
		session.getCheckoutStation().printer.addInk(ReceiptPrinter.MAXIMUM_INK);
		session.print("hello");
	}
	
	@Test(expected = SimulationException.class)
	public void notEnoughInkWhenPrinting() {
		session.getCheckoutStation().printer.addPaper(ReceiptPrinter.MAXIMUM_PAPER);
		session.print("world");
	}

}
