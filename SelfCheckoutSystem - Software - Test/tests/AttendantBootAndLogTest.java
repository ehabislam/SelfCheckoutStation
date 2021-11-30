import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import attendantConsole.AttendantConsole;
import attendantConsole.EmployeeDatabase;
import unSorted.SelfCheckoutStationInstance ;



public class AttendantBootAndLogTest {
	AttendantConsole at;
	SelfCheckoutStationInstance station = new SelfCheckoutStationInstance(1);
	
	


	@Before
	public void setUp() throws Exception {
		at = new AttendantConsole();
		
	}
		
	
	
	@Test
	public void AttendantLoginTest_wrongPWRD() {
		Object key = EmployeeDatabase.EMPLOYEE_DATABASE.keySet().toArray()[0];
		String un = key.toString();
		String pw = "wrongpwd";
		assertFalse(at.verifyLogIn(un, pw));

	}
	
	
	
	@Test
	public void AttendantLoginTest_EnotInDB() {
		String un = "tester";
		String pw = "testpwd";
		assertFalse(at.verifyLogIn(un, pw));

	}
	
	
	 

	
	@Test
	public void AttendantLoginTest_true() {
		Object key = EmployeeDatabase.EMPLOYEE_DATABASE.keySet().toArray()[0];
		ArrayList<String> array = EmployeeDatabase.EMPLOYEE_DATABASE.get(key);
		String un = key.toString();
		String pw = array.get(1);
		assertTrue(at.verifyLogIn(un, pw));
			
	} 
	
	
	
	@Test
	public void AttendantLoginTest_alreadyLoggedIn() {
		//login as employee
		Object key = EmployeeDatabase.EMPLOYEE_DATABASE.keySet().toArray()[0];
		ArrayList<String> array = EmployeeDatabase.EMPLOYEE_DATABASE.get(key);
		String un = key.toString();
		String pw = array.get(1);
		assertTrue(at.verifyLogIn(un, pw));
		
		//try logging in again as a different attendant
		Object key2 = EmployeeDatabase.EMPLOYEE_DATABASE.keySet().toArray()[1];
		ArrayList<String> array2 = EmployeeDatabase.EMPLOYEE_DATABASE.get(key2);
		String un2 = key.toString();
		String pw2 = array2.get(1);
		at.verifyLogIn(un2, pw2);
		
		// make sure that the original employee is still logged in
		// and that the 2nd employee was not able to login as 
		// first was already logged in
		assertTrue(at.getAttendantName() == array.get(0));
		
		
			
	}
	
	@Test
	public void attendantNameTest_init() {
		assertTrue("" == at.getAttendantName());
	}
	
	
	@Test
	public void attendantNameTest_after() {
		
		//login using ED
		Object key = EmployeeDatabase.EMPLOYEE_DATABASE.keySet().toArray()[0];
		ArrayList<String> array = EmployeeDatabase.EMPLOYEE_DATABASE.get(key);
		String un = key.toString();
		String pw = array.get(1);
		
		//make sure we are logged in
		assertTrue(at.verifyLogIn(un, pw));
		
		//test that the curr employee's name is set as loggedIn name 
		assertTrue(array.get(0) == at.getAttendantName());
	
	
	}
	
	
	
	
	
	
	
	@Test
	public void AttendantStartupTest() {
		
		//make sure all disabled
		station.scs.cardReader.disable();
		station.scs.screen.getFrame().setVisible(false);
		station.scs.cardReader.disable();
		station.scs.mainScanner.disable();
		station.scs.scale.disable();
		station.scs.banknoteInput.disable();
	
		//start station
		at.startStation(station);
		
		//all should now be enabled
		assertFalse(station.scs.cardReader.isDisabled());
		assertFalse(station.scs.cardReader.isDisabled());
		assertFalse(station.scs.mainScanner.isDisabled());
		assertFalse(station.scs.scale.isDisabled());
		assertFalse(station.scs.banknoteInput.isDisabled());
		assertTrue(station.scs.screen.getFrame().isVisible());
		
		
	}
	
	
	
	
	@Test
	public void AttendantShutdownTest() {
		
		//start station up
		at.startStation(station);
		
		//stop station
		at.stopStation(station);
		
		
		// make sure that the stop has disabled all
		assertTrue(station.scs.cardReader.isDisabled());
		assertTrue(station.scs.cardReader.isDisabled());
		assertTrue(station.scs.mainScanner.isDisabled());
		assertTrue(station.scs.scale.isDisabled());
		assertTrue(station.scs.banknoteInput.isDisabled());
		assertFalse(station.scs.screen.getFrame().isVisible());
		
	}
	
	
	
	
	@Test
	public void AttendantStationBlockTest() {
		// make sure station is not blocked
		station.blocked = false;
		
		// block station
		at.blockStation(station);

		// make sure blocked
		assertTrue(station.blocked);
	}
	
	@Test
	public void AttendantStationUnblockTest() {
		// make sure station is blocked
		station.blocked = true;
		
		// block station
		at.unblockStation(station);
		
		//make sure its now unblocked
		assertFalse(station.blocked);
	}
	
	
	
	
	
}