package unSorted;

import java.math.BigDecimal;

/**
 * Contains the information about the plastic bags used by the Customer. 
 * <br>
 * Initialized in Session class.
 * @author Abhay Sharma
 */
public class PlasticBags {
	private double NumberOfBagsUsed = 0;
	private BigDecimal price_per_bag = new BigDecimal(1.6);
	
	/**
	 * Constructor for this class
	 */
	public PlasticBags() {
	}
	
	public void setprice_per_bag(BigDecimal price) {
		this.price_per_bag = price;
	}
	
	public BigDecimal get_price_per_bag() {
		return this.price_per_bag;
	}
	/**
	 * 
	 * @return the total price of all the bags used.
	 */
	public BigDecimal get_total_bags_price() {
		BigDecimal total_price;
		total_price = new BigDecimal(NumberOfBagsUsed).multiply(price_per_bag);
		return total_price;
	}
	
	public void setNumberOfBagsUsed(double number) {
		this.NumberOfBagsUsed = number;
	}
	
	public double getNumberOfBagsUsed() {
		return this.NumberOfBagsUsed;
	}
}
