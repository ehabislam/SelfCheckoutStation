import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.lsmr.selfcheckout.Banknote;
import org.lsmr.selfcheckout.devices.DisabledException;

import gui.StationPanel;
import unSorted.PaymentService;
import unSorted.SelfCheckoutStationInstance;
import unSorted.Session;

public class BanknotePaymentServiceTest {

	private Session session;
	private SelfCheckoutStationInstance station;
	private PaymentService paymentService;

	@Before
	public void setUp() throws Exception {
		station = new SelfCheckoutStationInstance(1);
		session = new Session(station,new GUIDispatchStub(new StationPanel(1,station)));
		paymentService = session.getPaymentService();
	}

	@Test
	public void invalidDenomTest() throws DisabledException {
		Banknote banknote = new Banknote(11, Currency.getInstance(Locale.CANADA));
		station.scs.banknoteValidator.accept(banknote);
		assertEquals(BigDecimal.ZERO, paymentService.getWallet());
	}

	@Test
	public void ValidbanknoteTest() throws DisabledException {
		Banknote banknote = new Banknote(5, Currency.getInstance(Locale.CANADA));

		for (int i = 0; i < 50; i++) {
			station.scs.banknoteValidator.accept(banknote);
			if (!station.scs.banknoteInput.hasSpace()) {
				station.scs.banknoteInput.removeDanglingBanknote();
			}

		}
		assertEquals(paymentService.getWallet().floatValue(), 250.0, 20.0);
	}

	@Test
	public void DiffNotesTest() throws DisabledException {
		Banknote fiver = new Banknote(5, Currency.getInstance(Locale.CANADA));
		Banknote tenner = new Banknote(10, Currency.getInstance(Locale.CANADA));

		Set<Integer> banknoteSet = new HashSet<>();
		for (int i = 0; i < 50; i++) {
			station.scs.banknoteValidator.accept(fiver);
			if (!station.scs.banknoteInput.hasSpace()) {
				station.scs.banknoteInput.removeDanglingBanknote();
			}

		}

		assertEquals(paymentService.getWallet().floatValue(), 250.0, 20.0);

		BigDecimal oldwallet = paymentService.getWallet();

		for (int i = 0; i < 10; i++) {
			station.scs.banknoteValidator.accept(tenner);
			if (!station.scs.banknoteInput.hasSpace()) {
				station.scs.banknoteInput.removeDanglingBanknote();
			}
		}

		BigDecimal expectedWallet = oldwallet.add(new BigDecimal(100));

		assertEquals(paymentService.getWallet().floatValue(), expectedWallet.floatValue(), 20.0);

	}

	@Test
	public void invalidCurrencyTest() throws DisabledException {
		Banknote banknote = new Banknote(5, Currency.getInstance(Locale.CHINA));
		station.scs.banknoteValidator.accept(banknote);
		assertEquals(BigDecimal.ZERO, paymentService.getWallet());
	}

}
