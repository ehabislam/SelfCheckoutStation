package unSorted;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.Card.CardData;
import org.lsmr.selfcheckout.PriceLookupCode;
import org.lsmr.selfcheckout.devices.OverloadException;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
import org.lsmr.selfcheckout.external.ProductDatabases;
import org.lsmr.selfcheckout.products.BarcodedProduct;
import org.lsmr.selfcheckout.products.PLUCodedProduct;

import utility.Pair;

/**
 * This class contains driver methods for Iteration3 Customer use cases.
 *
 * @author Abhay Sharma and Ehab Islam
 *
 */
public class CustomerUseCases {
	private Session session;
	private boolean isBeingRemoved = false;

	/**
	 * Constructor for this class
	 *
	 * @param session
	 */
	public CustomerUseCases(Session session) {
		this.session = session;
	}

	/**
	 * It3 : Use Case 1 : Customer returns to adding items. <br>
	 * Basic Idea : The customer returns scanning items after he/she previously
	 * clicked the finished button <br>
	 * GUI add-ins : Add a button in the scanning screen for Finishing Scanning, Add
	 * another button on the payment menu for return to scanning window.
	 *
	 * @author Ehab and Abhay
	 */
	public void returnToAddingItems() {
		session.restartScanning();
	}

	/**
	 * IT3 : Use Case 3 : Customer looks up product <br>
	 *
	 * @param barcode : Barcode of the product whose information is needed.
	 * @return A list whose first element is the price of the object and second
	 *         element is the description Returns empty list if no object with this
	 *         bar code exists.
	 * @author Ehab and Abhay
	 */
	public ArrayList<Object> lookupProduct(Barcode barcode) {
		ArrayList<Object> list = new ArrayList<Object>();
		if (ProductDatabases.BARCODED_PRODUCT_DATABASE.containsKey(barcode)) {
			BarcodedProduct bp = ProductDatabases.BARCODED_PRODUCT_DATABASE.get(barcode);
			BigDecimal price = new BigDecimal(bp.getPrice().doubleValue());
			String description = bp.getDescription();
			list.add(price);
			list.add(description);
			return list;
		}
		return list; // not product found
	}

	/**
	 * IT3 : Use Case 3 : Customer looks up product <br>
	 *
	 * @param code : PLUcode of the product whose information is needed.
	 * @return A list whose first element is the price of the object and second
	 *         element is the description Returns empty list if no object with this
	 *         bar code exists.
	 * @author Ehab and Abhay
	 */
	public ArrayList<Object> lookupProduct(PriceLookupCode code) {
		ArrayList<Object> list = new ArrayList<Object>();
		if (ProductDatabases.PLU_PRODUCT_DATABASE.containsKey(code)) {
			PLUCodedProduct bp = ProductDatabases.PLU_PRODUCT_DATABASE.get(code);
			BigDecimal price = new BigDecimal(bp.getPrice().doubleValue());
			String description = bp.getDescription();
			list.add(price);
			list.add(description);
			return list;
		}
		return list; // not product found
	}

	/**
	 * IT3 : Use Case 4 : Customer enters PLU code for a product
	 *
	 * @param pluCode : Plu code of the item to be purchased.
	 * @return The total cost of the price according to the price-to-weight ratio if
	 *         the product exists in the database and the weight on the scale is
	 *         above zero, otherwise null
	 * @throws OverloadException
	 */
	public BigDecimal entersPLUCode(String pluCode) throws OverloadException {
		SelfCheckoutStation station = session.getCheckoutStation();
		PluItemScanner pluScanner = session.getPluItemScanner();
		PriceLookupCode plu = new PriceLookupCode(pluCode);
		double currentWeight = station.scale.getCurrentWeight();
		if (ProductDatabases.PLU_PRODUCT_DATABASE.containsKey(plu) && currentWeight > 0) {
			PLUCodedProduct pluProduct = ProductDatabases.PLU_PRODUCT_DATABASE.get(plu);

			// assume only plu item on scale
			BigDecimal pluCost = pluProduct.getPrice().multiply(new BigDecimal(currentWeight));

			// Update the balance (add pluCost to current balance)
			BigDecimal currentBalance = session.getBalance();
			BigDecimal updatedBalance = currentBalance.add(pluCost);
			session.setBalance(updatedBalance);

			// Adding to totalProductsList
			List<Pair<String, BigDecimal>> totalProductsList = session.getTotalProductsList();
			Pair<String, BigDecimal> nameToPricePair = new Pair<>(pluProduct.getDescription(), pluCost);
			totalProductsList.add(nameToPricePair);

			// Update GUI
			session.getGuiDispatch().updateItemListPanel();

			return pluCost;
		}
		return null;
	}

	/**
	 *
	 * This function removes the item with the provided barcode from the Scanned
	 * Item list as well as it removes the weight of the scanned item from the
	 * BagItem class
	 */
	public void RemoveFromBaggingArea() {

	}

	/**
	 * IT3 : Use Case 8 : Customer enters number of plastic bags used TODO:
	 * Reimplement this
	 *
	 * @param number_of_bags_used : The quantity of bags used
	 * @return The total price of all the bags used
	 */
	public BigDecimal enter_number_of_plastic_bags(int number_of_bags_used) {
		PlasticBags bags = this.session.getPlasticBags();
		bags.setNumberOfBagsUsed(number_of_bags_used);
		// System.out.println(bags.get_total_bags_price());
		session.setBalance(session.getBalance().add(bags.get_total_bags_price().multiply(new BigDecimal(0.5))));
		session.getGuiDispatch().updateItemListPanel();
		return bags.get_total_bags_price();
	}

	/**
	 * IT3 : Use Case 5 : Customer enters their membership card information
	 *
	 * @param data CardData of the entered membership card.
	 */
	public void membershipCardReader(CardData data) {
		ArrayList<String> l = new ArrayList<String>();
		MembershipCard mCard;
		if (data.getType().equalsIgnoreCase("CO-OP")) {
			if (MemberDatabase.MEMBER_DATABASE.containsKey(data.getNumber())) {
				String memberLevel = MemberDatabase.MEMBER_DATABASE.get(data.getNumber()).get(0);
				String date = MemberDatabase.MEMBER_DATABASE.get(data.getNumber()).get(1);
				mCard = new MembershipCard(data, memberLevel, date);
				String points = mCard.calculatePoints(session.getBalance(),
						MemberDatabase.MEMBER_DATABASE.get(data.getNumber()).get(2));
				l.add(memberLevel);
				l.add(date);
				l.add(points);
				MemberDatabase.MEMBER_DATABASE.replace(data.getNumber(), l);
				session.setBalance(mCard.calculateDiscount(session.getBalance()));
				session.setDiscountGiven(true);
			}

		}
	}

	public boolean giftcardPayment(String giftcardNumber) {
		if (GiftcardDatabase.GIFTCARD_DATABASE.containsKey(giftcardNumber)) {
			BigDecimal cardbalance = GiftcardDatabase.GIFTCARD_DATABASE.get(giftcardNumber);
			BigDecimal sessionbalance = session.getBalance();
			if ((cardbalance.compareTo(BigDecimal.ZERO) == 1)) {
				if (sessionbalance.compareTo(cardbalance) == 1) {
					session.setBalance(sessionbalance.subtract(cardbalance));
					GiftcardDatabase.GIFTCARD_DATABASE.replace(giftcardNumber, BigDecimal.ZERO);
				} else if (sessionbalance.compareTo(cardbalance) == -1) {
					session.setBalance(BigDecimal.ZERO);
					GiftcardDatabase.GIFTCARD_DATABASE.replace(giftcardNumber, cardbalance.subtract(sessionbalance));
				} else {
					session.setBalance(BigDecimal.ZERO);
					GiftcardDatabase.GIFTCARD_DATABASE.replace(giftcardNumber, BigDecimal.ZERO);
				}
			}
			return true;
		} else {
			return false;
		}
	}

	public void setisBeingRemoved(boolean val) {
		this.isBeingRemoved = val;
	}

	public boolean getisBeingRemoved() {
		return this.isBeingRemoved;
	}
}
