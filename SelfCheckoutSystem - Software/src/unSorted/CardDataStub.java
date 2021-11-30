package unSorted;
import org.lsmr.selfcheckout.Card.CardData;

public class CardDataStub implements CardData { 
	String type, number, holder;

	public CardDataStub(String type, String number, String holder) {
		super();
		this.type = type;
		this.number = number;
		this.holder = holder;
	}

	@Override
	public String getType() {
		// TODO Auto-generated method stub
		return type;
	}

	@Override
	public String getNumber() {
		// TODO Auto-generated method stub
		return number;
	}

	@Override
	public String getCardholder() {
		// TODO Auto-generated method stub
		return holder;
	}

	@Override
	public String getCVV() {
		// TODO Auto-generated method stub
		return null;
	}

}
