package unSorted;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.lsmr.selfcheckout.devices.DisabledException;
import org.lsmr.selfcheckout.devices.EmptyException;
import org.lsmr.selfcheckout.devices.OverloadException;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;

import listeners.BanknoteSlotListenerSoftware;
import listeners.CoinTrayListenerSoftware;

public class ChangeCounter {

	private final boolean rounding = true;
	private SelfCheckoutStation selfCheckoutStation;
	private BigDecimal balance;
	private List<BigDecimal> coinChange;
	private List<Integer> banknoteChange;

	private List<BigDecimal> coinDenominations;
	private List<Integer> banknoteDenominations;
	
	private CoinTrayListenerSoftware coinTrayListener;
	private BanknoteSlotListenerSoftware banknoteListener;
	
	private boolean pendingCoins = false;
	private boolean pendingBanknote = false;
	
	public ChangeCounter(SelfCheckoutStation selfCheckoutStation, BigDecimal balance) {
		
		this.selfCheckoutStation = selfCheckoutStation;
		
		// throw exception if balance is positive (no change, owes money)
		if (balance.signum() > 0) {
			
			throw new IllegalArgumentException();
			
		} else {
			
			this.balance = balance.abs();
			
		}
		
		// copy coinDenominations
		this.coinDenominations = new ArrayList<BigDecimal>(); //selfCheckoutStation.coinDenominations;
		for (BigDecimal denomination : selfCheckoutStation.coinDenominations)
			this.coinDenominations.add(denomination);
		
		// copy banknoteDenominations
		this.banknoteDenominations = new ArrayList<Integer>();
		for (int denomination : selfCheckoutStation.banknoteDenominations)
			this.banknoteDenominations.add(denomination);
		
		this.banknoteChange = new ArrayList<Integer>();
		this.coinChange = new ArrayList<BigDecimal>();
		
		this.coinTrayListener = new CoinTrayListenerSoftware(this);
		this.banknoteListener = new BanknoteSlotListenerSoftware(this);
		
		this.selfCheckoutStation.coinTray.register(coinTrayListener);
		this.selfCheckoutStation.banknoteOutput.register(banknoteListener);
		
		this.calculateChange();
		
	}
	
	/**
	 * Calculate banknotes and coins to give, based on the denominations used by the SelfCheckoutStation.
	 */
	private void calculateChange() {
		
		BigDecimal value = this.balance;
		
		// index of second lowest coin denomination in this.selfCheckoutStation coinDenominations list
		BigDecimal min = this.coinDenominations.size() > 1 ? this.getMinCoinDenominationIndex(this.coinDenominations) : this.coinDenominations.get(0);
		
		Collections.sort(this.banknoteDenominations, Collections.reverseOrder());
		Collections.sort(this.coinDenominations, Collections.reverseOrder());
		
		while (value.compareTo(min) >= 0) {
			
			// break with banknotes
			for (int i=0; i < this.banknoteDenominations.size(); i++) {
				
				if (this.banknoteDenominations.get(i) <= value.intValue()) {
					
					this.banknoteChange.add(this.banknoteDenominations.get(i));
					value = value.subtract(new BigDecimal(this.banknoteDenominations.get(i)));
					
					// try again with same denomination
					i--;
					
				}
				
			}
			
			value = value.setScale(3, RoundingMode.UP);
			
			// break with coins
			for (int i=0; i < this.coinDenominations.size(); i++) {
				
				BigDecimal coinDenom = this.coinDenominations.get(i).setScale(2, RoundingMode.DOWN);
				
				if (coinDenom.compareTo(value) <= 0) {
					
					this.coinChange.add(this.coinDenominations.get(i));
					value = value.subtract(coinDenom);
					
					// try again with same denomination
					i--;
					
				}
				
			}
			
			if (this.rounding) {
				
				if (min.subtract(value).abs().compareTo(value) <= 0) {
					
					this.coinChange.add(min);
					value = value.subtract(min);
					
				}
				
			}
			
		}
		
	}
	
	/**
	 * Get the minimum denomination in the List of possible denominations.
	 * 
	 * @param _coinDenominations List of coin denominations to get the minimum of.
	 * @return the minimum denomination in the list.
	 */
	private BigDecimal getMinCoinDenominationIndex(List<BigDecimal> _coinDenominations) {
		
		BigDecimal min;
		
		Collections.sort(_coinDenominations);
		min = _coinDenominations.get(0);
		
		return min;
		
	}

	/**
	 * Set pendingCoins. True if coins are dispensed.
	 * 
	 * @param x boolean value to set pendingCoins.
	 */
	public void setCoinPending(boolean x) {
		
		this.pendingCoins = x;
		
	}
	
	/**
	 * Set pendingBanknote. True if there is a banknote in the output.
	 * 
	 * @param x boolean value to set pendingBanknote
	 */
	public void setBanknotePending(boolean x) {
		
		this.pendingBanknote = x;
		
	}
	
	/**
	 * Get the list of coins to dispense.
	 * 
	 * @return A list of coins to dispense.
	 */
	public List<BigDecimal> getCoinChange() {
		
		return new ArrayList<BigDecimal>(this.coinChange);
		
	}
	
	/**
	 * Get the list of banknote to dispense.
	 * 
	 * @return A list of banknotes to dispense.
	 */
	public List<Integer> getBanknoteChange() {
		
		return new ArrayList<Integer>(this.banknoteChange);
		
	}
	
	/**
	 * Dispense the next coin in the list.
	 * 
	 * @throws DisabledException when a required coin dispenser is disabled.
	 * @throws EmptyException when a required coin dispenser is empty.
	 * @throws OverloadException when the coin tray is full.
	 */
	public void dispenseNextCoin() throws EmptyException, DisabledException, OverloadException {
		
		if (this.coinChange.size() > 0) {
			
			if (this.selfCheckoutStation.coinTray.hasSpace()) {
			
				this.selfCheckoutStation.coinDispensers.get(this.coinChange.get(0)).emit();
				this.coinChange.remove(0);
				
			}
			
		}
		
	}
	
	/**
	 * Dispense the next banknote in the list.
	 * 
	 * @throws DisabledException when a required banknote dispenser is disabled.
	 * @throws EmptyException when a required banknote dispenser is empty.
	 * @throws OverloadException when the banknote slot is currently occupied by another banknote.
	 */
	public void dispenseNextBanknote() throws EmptyException, DisabledException, OverloadException {
		
		if (this.banknoteChange.size() > 0) {
				
			if (!this.pendingBanknote) {
				
				this.selfCheckoutStation.banknoteDispensers.get(this.banknoteChange.get(0)).emit();
				this.banknoteChange.remove(0);
				
			}
				
		}

	}

}
