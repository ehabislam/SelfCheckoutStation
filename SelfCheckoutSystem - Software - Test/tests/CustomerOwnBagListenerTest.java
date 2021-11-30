
/**
 * 
 * @author Harry(Dongheok) Lee
 *
 */
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import gui.StationPanel;
import unSorted.CustomerOwnBagListener;
import unSorted.SelfCheckoutStationInstance;
import unSorted.Session;

public class CustomerOwnBagListenerTest {

	Session session;
	private SelfCheckoutStationInstance station;
	CustomerOwnBagListener listener = new CustomerOwnBagListener(session);;

	/**
	 * Initializes the listener and the session
	 * 
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {
		CustomerOwnBagListener listener = new CustomerOwnBagListener(session);
		station = new SelfCheckoutStationInstance(1);
		session = new Session(station,new GUIDispatchStub(new StationPanel(1,station)));
	}

	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Tests when the customer brought their own bag
	 */
	@Test
	public void testCheckOwnBagPlacedWithOwnBag() {
		CustomerOwnBagListener listener = new CustomerOwnBagListener(session);
		listener.checkOwnBagPlaced("own bag");
		BigDecimal bd = new BigDecimal(0.0);
		assertEquals(session.getBalance(), bd);
	}

	/**
	 * Tests when the customer uses plastic bag
	 */
	@Test
	public void testCheckOwnBagPlacedWithoutOwnBag() {
		CustomerOwnBagListener listener = new CustomerOwnBagListener(session);
		listener.checkOwnBagPlaced("plastic bag");
		BigDecimal bd = new BigDecimal(0.5);
		assertEquals(session.getBalance(), bd);
	}

	/**
	 * Tests when the customer puts something else other than the bag
	 */
	@Test
	public void testCheckOwnBagPlacedNoBag() {
		CustomerOwnBagListener listener = new CustomerOwnBagListener(session);
		try {
			listener.checkOwnBagPlaced("apple");
		} catch (Exception e) {
			assertTrue("Please put a proper bag", e instanceof IllegalArgumentException);
		}
	}

}
