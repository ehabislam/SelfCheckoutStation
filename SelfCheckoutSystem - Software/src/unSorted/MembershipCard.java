package unSorted;
import java.math.BigDecimal;
import java.math.MathContext;

import org.lsmr.selfcheckout.Card.CardData;

public class MembershipCard {

	// TODO: decide if pin/tap, make member database, enter number where?, design
	// interface?

	private String dateOfJoining;
	private int numberOfPoints;
	private int pointsMultiplier;
	private BigDecimal discountPercentage;
	private String memberLevel;

	public MembershipCard(CardData cardData, String level, String date) {
		memberLevel = level;
		dateOfJoining = date;
		determineDiscount();
		determinePoints();

	}

	private void determineDiscount() {
		if (memberLevel.equalsIgnoreCase("BASIC"))
			discountPercentage = new BigDecimal(0.01);
		else if (memberLevel.equalsIgnoreCase("SILVER"))
			discountPercentage = new BigDecimal(0.02);
		if (memberLevel.equalsIgnoreCase("GOLD"))
			discountPercentage = new BigDecimal(0.05);

	}

	private void determinePoints() {
		if (memberLevel.equalsIgnoreCase("BASIC"))
			pointsMultiplier = 1;
		else if (memberLevel.equalsIgnoreCase("SILVER"))
			pointsMultiplier = 2;
		if (memberLevel.equalsIgnoreCase("GOLD"))
			pointsMultiplier = 5;

	}

	public BigDecimal calculateDiscount(BigDecimal balance) {
		//System.out.println(balance);
		BigDecimal number = balance.subtract(balance.multiply(discountPercentage));
		MathContext m = new MathContext(3); // 4 precision
		number = number.round(m);
		//System.out.println(number);
		return number;
		

	}

	public String calculatePoints(BigDecimal balance, String points) {
		numberOfPoints = Integer.parseInt(points);
		numberOfPoints += balance.doubleValue() * pointsMultiplier;
		return String.valueOf(numberOfPoints);

	}

	public String getDateOfJoining() {
		return dateOfJoining;
	}

	public int getNumberOfPoints() {
		return numberOfPoints;
	}

	public int getPointsMultiplier() {
		return pointsMultiplier;
	}

	public BigDecimal getDiscountPercentage() {
		return discountPercentage;
	}

	public String getMemberLevel() {
		return memberLevel;
	}

}
