import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.lsmr.selfcheckout.Coin;
import org.lsmr.selfcheckout.devices.DisabledException;

import gui.StationPanel;
import unSorted.PaymentService;
import unSorted.SelfCheckoutStationInstance;
import unSorted.Session;

public class CoinPaymentServiceTest {

	private Session session;
	private SelfCheckoutStationInstance station;
	private PaymentService paymentService;

	@Before
	public void setUp() throws Exception {
		station = new SelfCheckoutStationInstance(1);
		session = new Session(station, new GUIDispatchStub(new StationPanel(1,station)));
		paymentService = session.getPaymentService();
	}

	@Test
	public void test1ValidCoinWithSpace() throws DisabledException {
		Coin coin = new Coin(new BigDecimal(1), Currency.getInstance(Locale.CANADA));


		for (int i = 0; i < 1000; i++) {

			station.scs.coinTray.collectCoins();

			station.scs.coinValidator.accept(coin);
		}


		// ensures that the wallet value is correct with the margin of error (namely 5%
		// since random chance of failure is 1%)
		boolean walletWithinMargin = paymentService.getWallet().compareTo(new BigDecimal(950)) >= 0;

		assertTrue(walletWithinMargin);

	}

	@Test
	public void test2ValidCoinsWithSpace() throws DisabledException {
		Coin dollar = new Coin(new BigDecimal(1), Currency.getInstance(Locale.CANADA));
		Coin toonie = new Coin(new BigDecimal(2), Currency.getInstance(Locale.CANADA));

		for (int i = 0; i < 250; i++) {

			station.scs.coinTray.collectCoins();

			station.scs.coinValidator.accept(dollar);
		}


		// ensures that the wallet value is correct with the margin of error (namely 4%
		// since random chance of failure is 1%)
		boolean walletWithinMargin = paymentService.getWallet().compareTo(new BigDecimal(240)) >= 0;
		assertTrue(walletWithinMargin);

		BigDecimal walletAfterDollars = paymentService.getWallet();

		for (int i = 0; i < 250; i++) {
			station.scs.coinTray.collectCoins();
			station.scs.coinValidator.accept(toonie);
		}


		// focuses on the value added to wallet through only toonie insertion
		BigDecimal expectedWallet = walletAfterDollars.add(new BigDecimal(240));
		walletWithinMargin = paymentService.getWallet().compareTo(expectedWallet) >= 0;

		assertTrue(walletWithinMargin);

	}

	@Test
	public void testInvalidCurrencyCoin() throws DisabledException {
		Coin coin = new Coin(new BigDecimal(1), Currency.getInstance(Locale.CHINA));
		station.scs.coinValidator.accept(coin);
		assertEquals(BigDecimal.ZERO, paymentService.getWallet());
	}

	@Test
	public void testInvalidDenominationCoin() throws DisabledException {
		Coin coin = new Coin(new BigDecimal(10), Currency.getInstance(Locale.CANADA));
		station.scs.coinValidator.accept(coin);
		assertEquals(BigDecimal.ZERO, paymentService.getWallet());
	}

}
