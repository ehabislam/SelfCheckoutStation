import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ BagItemTest.class, BanknotePaymentServiceTest.class, CoinPaymentServiceTest.class,
		ItemScannerTest.class, SessionTest.class, CustomerOwnBagListenerTest.class, ChangeCounterTest.class,
		MembershipCardTest.class, CardPaymentTest.class, AttendantBootAndLogTest.class,
		BaggingAreaTest.class, CustomerUseCasesTest.class, AttendantTests.class, ReceiptPrinterResourcesTest.class })
public class AllTests {
	private static String BASEDIR = "C:\\Users\\seher\\Desktop\\EclipseJava\\SelfCheckoutSystem\\software";

}
