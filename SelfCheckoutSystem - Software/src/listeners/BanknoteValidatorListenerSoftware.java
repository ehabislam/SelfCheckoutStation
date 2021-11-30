package listeners;
import java.math.BigDecimal;
import java.util.Currency;

import org.lsmr.selfcheckout.devices.AbstractDevice;
import org.lsmr.selfcheckout.devices.BanknoteValidator;
import org.lsmr.selfcheckout.devices.listeners.AbstractDeviceListener;
import org.lsmr.selfcheckout.devices.listeners.BanknoteValidatorListener;

import unSorted.PaymentService;
import unSorted.Session;

public class BanknoteValidatorListenerSoftware implements BanknoteValidatorListener {

	private Session session;
	private PaymentService paymentService;

	public BanknoteValidatorListenerSoftware(Session session) {
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
	public void validBanknoteDetected(BanknoteValidator validator, Currency currency, int value) {
		BigDecimal banknoteValue = new BigDecimal(value);
		paymentService.setWallet(paymentService.getWallet().add(banknoteValue));

		BigDecimal updatedBalance = session.getBalance().subtract(banknoteValue);
		session.setBalance(updatedBalance);
		session.getGuiDispatch().updateItemListPanel();
	}

	@Override
	public void invalidBanknoteDetected(BanknoteValidator validator) {
		// TODO Auto-generated method stub

	}

}
