package attendantConsole;

import java.util.ArrayList;
import java.util.Scanner;

import org.lsmr.selfcheckout.Item;
import org.lsmr.selfcheckout.devices.ElectronicScale;
import org.lsmr.selfcheckout.devices.OverloadException;

import listeners.BaggingAreaListenerSoftware;
import unSorted.SelfCheckoutStationInstance;

public class AttendantConsole {
	boolean loggedIn = false;
	String loggedInName = "";

	public AttendantConsole() {
		String employeeNames[] = new String[] { "John Doe", "Jane Doe", "Doug Adams", "Jen Frank", "Larry Thomson" };
		String employeeUsernames[] = new String[] { "john.doe", "jane.doe", "doug.adams", "jen.frank1", "larry.thom" };
		String employeePasswords[] = new String[] { "john23#", "jane89@", "adams5*7", "frankie56!", "lars9$9" };
		String employeeStatus[] = new String[] { "Cashier", "Bagger", "Manager", "Cashier", "Bagger" };
		String employeeID[] = new String[] { "jd123", "jd998", "da467", "jf023", "lt345" };

		for (int i = 0; i < 5; i++) {
			ArrayList<String> l = new ArrayList<String>();
			l.add(employeeNames[i]);
			l.add(employeePasswords[i]);
			l.add(employeeStatus[i]);
			l.add(employeeID[i]);

			EmployeeDatabase.EMPLOYEE_DATABASE.put(employeeUsernames[i], l);

		}

	}

	public boolean verifyLogIn(String userName, String password) {
		if (!loggedIn) {
			if (EmployeeDatabase.EMPLOYEE_DATABASE.containsKey(userName)) {

				ArrayList<String> attributes = new ArrayList<String>();
				attributes = EmployeeDatabase.EMPLOYEE_DATABASE.get(userName);
				if (password.equalsIgnoreCase(attributes.get(1))) {
					loggedIn = true;
					loggedInName = attributes.get(0);

				}

			}
		}
		return loggedIn;
	}

	public String getAttendantName() {
		return loggedInName;

	}

	public void startStation(SelfCheckoutStationInstance station) {
		station.scs.screen.getFrame().setVisible(true);
		station.scs.cardReader.enable();
		station.scs.coinSlot.enable();
		station.scs.mainScanner.enable();
		station.scs.scale.enable();
		station.scs.banknoteInput.enable();

	}

	public void stopStation(SelfCheckoutStationInstance station) {
		station.scs.screen.disable();
		station.scs.cardReader.disable();
		station.scs.coinSlot.disable();
		station.scs.mainScanner.disable();
		station.scs.scale.disable();
		station.scs.banknoteInput.disable();
		station.scs.screen.getFrame().setVisible(false);

	}

	public void blockStation(SelfCheckoutStationInstance station) {
		station.blocked = true;

	}

	public void unblockStation(SelfCheckoutStationInstance station) {
		station.blocked = false;

	}
}
