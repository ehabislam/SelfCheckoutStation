import static org.junit.Assert.fail;

import java.io.IOException;
import java.math.BigDecimal;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import gui.StationPanel;
import listeners.CardReaderListenerSoftware;
import unSorted.SelfCheckoutStationInstance;
import unSorted.Session;

public class MembershipCardTest {
	Session session;
	SelfCheckoutStationInstance station = new SelfCheckoutStationInstance(1);
	CardReaderListenerSoftware cardListener;
	

	@Before
	public void setUp() throws Exception {
		session = new Session(station,new GUIDispatchStub(new StationPanel(1,station)));

		cardListener = new CardReaderListenerSoftware(session);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testOneCard() throws IOException {
		session.setBalance(new BigDecimal(100));
		cardListener.cardDataRead(station.scs.cardReader, station.membershipCards.get(0));

		if (!cardListener.mCard.getDateOfJoining().equals(station.dates[0])) {
			fail("date incorrect");
		}

		if (!cardListener.mCard.getMemberLevel().equals(station.memberLevels[0])) {
			fail("member level incorrect");
		}

		if (!(cardListener.mCard.getPointsMultiplier() == 1)) {
			fail("points multiplier incorrect");
		}

		if (!(cardListener.mCard.getNumberOfPoints() == station.points[0])) {
			fail("points incorrect");
		}
		if (session.getBalance().equals(new BigDecimal(99))) {
			fail("discount applied incorrect");
		}
	}

	@Test
	public void testMultipleCards() throws IOException {
		session.setBalance(new BigDecimal(100));
		for (int i = 0; i < 5; i++) {
			cardListener.cardDataRead(station.scs.cardReader, station.membershipCards.get(i));
			if (!cardListener.mCard.getDateOfJoining().equals(station.dates[i])) {
				fail("date incorrect");
			}
			if (!cardListener.mCard.getMemberLevel().equals(station.memberLevels[i])) {
				fail("member level incorrect");
			}

			if (!(cardListener.mCard.getPointsMultiplier() == station.multipliers[i])) {
				fail("points multiplier incorrect");
			}

			if (!(cardListener.mCard.getNumberOfPoints() == station.points[i])) {

				fail("points incorrect");
			}

			if (session.getBalance().equals(new BigDecimal(86.6849445))) {
				fail("discount applied incorrect");
			}

		}

	}

	@Test
	public void testSameCard() throws IOException {
		session.setBalance(new BigDecimal(100));
		cardListener.cardDataRead(station.scs.cardReader, station.membershipCards.get(0));
		session.setBalance(new BigDecimal(50));
		cardListener.cardDataRead(station.scs.cardReader, station.membershipCards.get(0));

		if (!(cardListener.mCard.getNumberOfPoints() == 150)) {
			fail("points incorrect");
		}
		
		session.setBalance(new BigDecimal(50));
		cardListener.cardDataRead(station.scs.cardReader, station.membershipCards.get(0));
		
		if (!(cardListener.mCard.getNumberOfPoints() == 200)) {
			fail("points incorrect");
		}
		
	}

}
