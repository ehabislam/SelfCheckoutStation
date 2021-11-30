package unSorted;
import java.math.BigDecimal;

/**
 * 
 * @author Harry(Dongheok) Lee
 *
 */
public class CustomerOwnBagListener implements CustomerOwnBag {

	private Session session;

	public CustomerOwnBagListener(Session session) {
		this.session = session;
	}

	/**
	 * Checks if the customer brought a bag. If the customer did not, then add extra
	 * charge of 0.5 dollars.
	 * 
	 * @param customersBag: the customer's bag that can be either their own bag or
	 *                      plastic bag
	 */
	public void checkOwnBagPlaced(String customersBag) {
		if (!CustomerOwnBag.isOwnBagPlaced(customersBag)) {
			session.setBalance(session.getBalance().add(new BigDecimal(0.5)));
		}
	}

}
