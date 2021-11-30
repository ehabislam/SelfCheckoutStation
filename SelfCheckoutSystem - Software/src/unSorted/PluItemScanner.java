package unSorted;


import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class PluItemScanner {

	private Map<String, BigDecimal> checkoutPLUProducts = new HashMap<String, BigDecimal>();
	
	public PluItemScanner() {
		
	}
	public Map<String, BigDecimal> getCheckoutProducts() {
		return checkoutPLUProducts;
	}
	
	public void setCheckoutPLUProducts(Map<String, BigDecimal> checkoutPLUProducts) {
		this.checkoutPLUProducts = checkoutPLUProducts;
	}
	
	
	
}
