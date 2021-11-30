package unSorted;
/**
 * 
 * @author Harry(Dongheok) Lee
 *
 */
public interface CustomerOwnBag {

	/**
	 * Checks if the customer brought a bag.
	 * 
	 * @param bag: the bag that customer is using
	 */
	public static boolean isOwnBagPlaced(String bag) {
		if (bag == "own bag") {
			return true;

		} else if (bag == "plastic bag") {
			return false;
		} else {
			throw new IllegalArgumentException("Please put a proper bag");
		}
	}

}
