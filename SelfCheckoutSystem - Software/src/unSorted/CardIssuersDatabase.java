package unSorted;
import java.util.HashMap;
import java.util.Map;

import org.lsmr.selfcheckout.external.CardIssuer;

public class CardIssuersDatabase {

	private CardIssuersDatabase() {
	}

	public static final Map<String, CardIssuer> CARD_ISSUERS = new HashMap<>();
}
