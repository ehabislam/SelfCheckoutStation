import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.lsmr.selfcheckout.Banknote;
import org.lsmr.selfcheckout.Coin;
import org.lsmr.selfcheckout.devices.DisabledException;
import org.lsmr.selfcheckout.devices.EmptyException;
import org.lsmr.selfcheckout.devices.OverloadException;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
import org.lsmr.selfcheckout.devices.SimulationException;

import unSorted.ChangeCounter;
import unSorted.SelfCheckoutStationInstance;

public class ChangeCounterTest {

	@Test
	public void BanknoteOnlyTest() throws EmptyException, DisabledException {

		SelfCheckoutStationInstance instance = new SelfCheckoutStationInstance(1);
		ChangeCounter counter = new ChangeCounter(instance.scs, new BigDecimal(-30));

		List<Banknote> receivedBanknotes = new ArrayList<Banknote>();

		int banknoteCount = counter.getBanknoteChange().size();

		try {

			for (int banknoteDenom : instance.banknoteDenominations) {

				for (int i = 0; i < SelfCheckoutStation.BANKNOTE_DISPENSER_CAPACITY; i++)
					instance.scs.banknoteDispensers.get(banknoteDenom)
							.load(new Banknote(banknoteDenom, instance.currency));

			}

		} catch (SimulationException e) {
			e.printStackTrace();
		} catch (OverloadException e) {
			e.printStackTrace();
		}

		while (banknoteCount > 0) {

			try {

				counter.dispenseNextBanknote();

			} catch (OverloadException e) {

				continue;

			}

			banknoteCount = counter.getBanknoteChange().size();

			receivedBanknotes.add(instance.scs.banknoteOutput.removeDanglingBanknote());

		}

		Banknote[] correctBanknotes = { new Banknote(20, instance.currency), new Banknote(10, instance.currency) };
		for (int i = 0; i < receivedBanknotes.size(); i++) {

			assertEquals(receivedBanknotes.get(i).getValue(), correctBanknotes[i].getValue());
			assertEquals(receivedBanknotes.get(i).getCurrency(), correctBanknotes[i].getCurrency());

		}

	}

	@Test
	public void CoinOnlyTest() throws EmptyException, DisabledException {

		SelfCheckoutStationInstance instance = new SelfCheckoutStationInstance(1);
		ChangeCounter counter = new ChangeCounter(instance.scs, new BigDecimal(-3));

		List<Coin> receivedCoins = new ArrayList<Coin>();

		int coinCount = counter.getCoinChange().size();

		try {

			for (BigDecimal coinDenom : instance.coinDenominations) {

				for (int i = 0; i < SelfCheckoutStation.COIN_DISPENSER_CAPACITY; i++)
					instance.scs.coinDispensers.get(coinDenom).load(new Coin(coinDenom, instance.currency));

			}

		} catch (SimulationException e) {
			e.printStackTrace();
		} catch (OverloadException e) {
			e.printStackTrace();
		}

		while (coinCount > 0) {

			try {

				counter.dispenseNextCoin();

			} catch (OverloadException e) {

				continue;

			}
			coinCount = counter.getCoinChange().size();

			for (Coin coin : instance.scs.coinTray.collectCoins()) {

				if (coin != null) {

					receivedCoins.add(coin);

				}

			}

		}

		Coin[] correctCoins = { new Coin(new BigDecimal(2), instance.currency),
				new Coin(new BigDecimal(1), instance.currency) };

		for (int i = 0; i < receivedCoins.size(); i++) {

			assertEquals(receivedCoins.get(i).getValue(), correctCoins[i].getValue());
			assertEquals(receivedCoins.get(i).getCurrency(), correctCoins[i].getCurrency());

		}

	}

	@Test
	public void CoinAndBanknoteTest() throws EmptyException, DisabledException {

		SelfCheckoutStationInstance instance = new SelfCheckoutStationInstance(1);
		ChangeCounter counter = new ChangeCounter(instance.scs, new BigDecimal(-23.25));

		List<Coin> receivedCoins = new ArrayList<Coin>();

		int coinCount = counter.getCoinChange().size();

		try {

			for (BigDecimal coinDenom : instance.coinDenominations) {

				for (int i = 0; i < SelfCheckoutStation.COIN_DISPENSER_CAPACITY; i++)
					instance.scs.coinDispensers.get(coinDenom).load(new Coin(coinDenom, instance.currency));

			}

		} catch (SimulationException e) {
			e.printStackTrace();
		} catch (OverloadException e) {
			e.printStackTrace();
		}

		while (coinCount > 0) {

			try {

				counter.dispenseNextCoin();

			} catch (OverloadException e) {

				continue;

			}

			coinCount = counter.getCoinChange().size();

			for (Coin coin : instance.scs.coinTray.collectCoins()) {

				if (coin != null) {

					receivedCoins.add(coin);

				}

			}

		}

		Coin[] correctCoins = { new Coin(new BigDecimal(2), instance.currency),
				new Coin(new BigDecimal(1), instance.currency), new Coin(new BigDecimal(0.25), instance.currency) };

		for (int i = 0; i < receivedCoins.size(); i++) {

			assertEquals(receivedCoins.get(i).getValue(), correctCoins[i].getValue());
			assertEquals(receivedCoins.get(i).getCurrency(), correctCoins[i].getCurrency());

		}

		List<Banknote> receivedBanknotes = new ArrayList<Banknote>();
		int banknoteCount = counter.getBanknoteChange().size();

		try {

			for (int banknoteDenom : instance.banknoteDenominations) {

				for (int i = 0; i < SelfCheckoutStation.BANKNOTE_DISPENSER_CAPACITY; i++)
					instance.scs.banknoteDispensers.get(banknoteDenom)
							.load(new Banknote(banknoteDenom, instance.currency));

			}

		} catch (SimulationException e) {
			e.printStackTrace();
		} catch (OverloadException e) {
			e.printStackTrace();
		}

		while (banknoteCount > 0) {

			try {

				counter.dispenseNextBanknote();

			} catch (OverloadException e) {

				continue;

			}
			banknoteCount = counter.getBanknoteChange().size();

			receivedBanknotes.add(instance.scs.banknoteOutput.removeDanglingBanknote());

		}

		Banknote[] correctBanknotes = { new Banknote(20, instance.currency) };
		for (int i = 0; i < receivedBanknotes.size(); i++) {

			assertEquals(receivedBanknotes.get(i).getValue(), correctBanknotes[i].getValue());
			assertEquals(receivedBanknotes.get(i).getCurrency(), correctBanknotes[i].getCurrency());

		}

	}

	@Test
	public void RoundChangeTest() throws EmptyException, DisabledException {

		SelfCheckoutStationInstance instance = new SelfCheckoutStationInstance(1);
		ChangeCounter counter = new ChangeCounter(instance.scs, new BigDecimal(-0.99));

		List<Coin> receivedCoins = new ArrayList<Coin>();

		int coinCount = counter.getCoinChange().size();

		try {

			for (BigDecimal coinDenom : instance.coinDenominations) {

				for (int i = 0; i < SelfCheckoutStation.COIN_DISPENSER_CAPACITY; i++)
					instance.scs.coinDispensers.get(coinDenom).load(new Coin(coinDenom, instance.currency));

			}

		} catch (SimulationException e) {
			e.printStackTrace();
		} catch (OverloadException e) {
			e.printStackTrace();
		}

		while (coinCount > 0) {

			try {

				counter.dispenseNextCoin();

			} catch (OverloadException e) {

				continue;

			}

			coinCount = counter.getCoinChange().size();

			for (Coin coin : instance.scs.coinTray.collectCoins()) {

				if (coin != null) {

					receivedCoins.add(coin);

				}

			}

		}

		Coin[] correctCoins = { new Coin(new BigDecimal(0.5), instance.currency),
				new Coin(new BigDecimal(0.25), instance.currency),
				new Coin(instance.coinDenominations[1], instance.currency),
				new Coin(instance.coinDenominations[1], instance.currency),
				new Coin(instance.coinDenominations[0], instance.currency) };

		for (int i = 0; i < receivedCoins.size(); i++) {

			assertEquals(receivedCoins.get(i).getValue(), correctCoins[i].getValue());
			assertEquals(receivedCoins.get(i).getCurrency(), correctCoins[i].getCurrency());

		}

	}

	@Test
	public void InvalidBalanceTest_ThrowException() {

		SelfCheckoutStationInstance instance = new SelfCheckoutStationInstance(1);

		try {

			ChangeCounter counter = new ChangeCounter(instance.scs, new BigDecimal(300));
			fail("Must throw IllegalArgumentException");

		} catch (Exception e) {

			assertTrue(e instanceof IllegalArgumentException);

		}

	}

	@Test
	public void NoChangeTest() {

		SelfCheckoutStationInstance instance = new SelfCheckoutStationInstance(1);
		ChangeCounter counter = new ChangeCounter(instance.scs, new BigDecimal(0));
		assertEquals(counter.getCoinChange().size(), 0);
		assertEquals(counter.getBanknoteChange().size(), 0);

	}

	@Test
	public void GetCoinChangeTest() {

		SelfCheckoutStationInstance instance = new SelfCheckoutStationInstance(1);
		ChangeCounter counter = new ChangeCounter(instance.scs, new BigDecimal(-1.50));

		Coin[] correctCoins = { new Coin(new BigDecimal(1), instance.currency),
				new Coin(new BigDecimal(0.5), instance.currency) };

		for (int i = 0; i < counter.getCoinChange().size(); i++) {

			assertEquals(counter.getCoinChange().get(i), correctCoins[i].getValue());

		}

	}

	@Test
	public void GetBanknoteChangeTest() {

		SelfCheckoutStationInstance instance = new SelfCheckoutStationInstance(1);
		ChangeCounter counter = new ChangeCounter(instance.scs, new BigDecimal(0));

		Banknote[] correctBanknotes = { new Banknote(20, instance.currency), new Banknote(5, instance.currency) };

		for (int i = 0; i < counter.getBanknoteChange().size(); i++) {

			assertEquals(counter.getBanknoteChange().get(i).intValue(), correctBanknotes[i].getValue());

		}

	}

}
