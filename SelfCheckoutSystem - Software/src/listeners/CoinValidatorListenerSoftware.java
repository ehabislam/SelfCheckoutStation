package listeners;
import java.math.BigDecimal;

import org.lsmr.selfcheckout.devices.AbstractDevice;
import org.lsmr.selfcheckout.devices.CoinValidator;
import org.lsmr.selfcheckout.devices.listeners.AbstractDeviceListener;
import org.lsmr.selfcheckout.devices.listeners.CoinValidatorListener;

import unSorted.PaymentService;
import unSorted.Session;

public class CoinValidatorListenerSoftware implements CoinValidatorListener {

	private Session session;
	private PaymentService paymentService;

	public CoinValidatorListenerSoftware(Session session) {
		this.session = session;
		this.paymentService = this.session.getPaymentService();
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
	public void validCoinDetected(CoinValidator validator, BigDecimal value) {

		paymentService.setWallet(paymentService.getWallet().add(value));

		BigDecimal updatedBalance = session.getBalance().subtract(value);
		session.setBalance(updatedBalance);
		session.getGuiDispatch().updateItemListPanel();
	}

	@Override
	public void invalidCoinDetected(CoinValidator validator) {
		// TODO Auto-generated method stub

	}

}
