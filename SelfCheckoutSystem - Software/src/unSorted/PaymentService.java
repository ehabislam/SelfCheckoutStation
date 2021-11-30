package unSorted;
import java.math.BigDecimal;

import org.lsmr.selfcheckout.Card.CardData;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
import org.lsmr.selfcheckout.external.CardIssuer;

public class PaymentService {

	private SelfCheckoutStation selfCheckoutStation;
	private BigDecimal wallet = BigDecimal.ZERO;

	public PaymentService(SelfCheckoutStation selfCheckoutStation) {
		this.selfCheckoutStation = selfCheckoutStation;
	}

	public boolean processCardPayment(CardData cardData, BigDecimal amount) {
		CardIssuer cardIssuer = CardIssuersDatabase.CARD_ISSUERS.get(cardData.getType());

		// card type is supported
		if (cardIssuer != null) {
			String cardNumber = cardData.getNumber();
			int holdNumber = cardIssuer.authorizeHold(cardNumber, amount);

			// hold successfully authorized
			if (holdNumber != -1) {
				return cardIssuer.postTransaction(cardNumber, holdNumber, amount);
			}
		}
		return false;
	}

	public BigDecimal getWallet() {
		return wallet;
	}

	public void setWallet(BigDecimal wallet) {
		this.wallet = wallet;
	}

}
