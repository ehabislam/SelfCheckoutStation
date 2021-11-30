package listeners;
import java.util.ArrayList;

import org.lsmr.selfcheckout.Card.CardData;
import org.lsmr.selfcheckout.devices.AbstractDevice;
import org.lsmr.selfcheckout.devices.CardReader;
import org.lsmr.selfcheckout.devices.listeners.AbstractDeviceListener;
import org.lsmr.selfcheckout.devices.listeners.CardReaderListener;

import unSorted.MemberDatabase;
import unSorted.MembershipCard;
import unSorted.Session;

public class CardReaderListenerSoftware implements CardReaderListener {

	private Session session;
	public MembershipCard mCard;

	public CardReaderListenerSoftware(Session session) {

		this.session = session;
	}

	@Override
	public void enabled(AbstractDevice<? extends AbstractDeviceListener> device) {
		// TODO Auto-generated method stub

	}

	@Override
	public void disabled(AbstractDevice<? extends AbstractDeviceListener> device) {
		// TODO Auto-generated method stub

	}

	@Override
	public void cardInserted(CardReader reader) {
		// TODO Auto-generated method stub

	}

	@Override
	public void cardRemoved(CardReader reader) {
		// TODO Auto-generated method stub

	}

	@Override
	public void cardTapped(CardReader reader) {
		// TODO Auto-generated method stub

	}

	@Override
	public void cardSwiped(CardReader reader) {
		// TODO Auto-generated method stub

	}

	@Override
	public void cardDataRead(CardReader reader, CardData data) {
		// handle membership card
		ArrayList<String> l = new ArrayList<String>();
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

		} else {
			session.handleCardPayment(data);
		}

	}

}
