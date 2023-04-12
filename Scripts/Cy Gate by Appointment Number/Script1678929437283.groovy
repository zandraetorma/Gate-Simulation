import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject
import com.kms.katalon.core.checkpoint.Checkpoint as Checkpoint
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.Keys as Keys
import org.openqa.selenium.By as By
import org.openqa.selenium.interactions.Actions as Actions
import org.openqa.selenium.JavascriptExecutor as JavascriptExecutor
import org.openqa.selenium.WebDriver as WebDriver
import org.openqa.selenium.WebElement as WebElement
import com.kms.katalon.core.util.KeywordUtil as KeywordUtil
import com.kms.katalon.core.webui.driver.DriverFactory as DriverFactory
import com.kms.katalon.core.webui.common.WebUiCommonHelper as WebUiCommonHelper
import java.awt.Robot as Robot
import java.awt.event.KeyEvent as KeyEvent
import com.kms.katalon.util.CryptoUtil
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import org.openqa.selenium.By
import org.openqa.selenium.WebElement
import java.util.Random
import org.openqa.selenium.JavascriptExecutor
import java.time.Duration
import java.lang.String
import com.kms.katalon.core.exception.StepFailedException
//import com.kms.katalon.core.webui.exception.WebElementNotFoundException
import com.kms.katalon.core.logging.KeywordLogger
import com.kms.katalon.core.util.KeywordUtil
import java.time.ZonedDateTime;


// terminate chrome driver
Runtime.getRuntime().exec('taskkill /im chromedriver.exe /f')
// terminate google chrome
Runtime.getRuntime().exec('taskkill /im chrome.exe /f')



//Configurable parameters
//----------------------------------------------------------------------------------------------------------
//This thread is used to open multiple browsers for each lane.
Thread t1 = new Thread(new Runnable() {
	@Override
	public void run() {
		
		ArrayList<String> outLaneID = new ArrayList<String>();
		outLaneID.add("TO1 (OUT)");
		//Parameter values (LaneID/Name, Test Data Name, LaneType (IN, BOTH), outLaneID is configured in ArrayList above)
		Lane("TI1 (IN)","Cy Gate Lane 1","IN", outLaneID);
	}
});

Thread t2 = new Thread(new Runnable() {
	@Override
	public void run() {
		
		ArrayList<String> outLaneID = new ArrayList<String>();
		outLaneID.add("TO1 (OUT)");
		//Parameter values (LaneID/Name, Test Data Name, LaneType (IN, BOTH), outLaneID is configured in ArrayList above)
		Lane("TI2 (IN)","Cy Gate Lane 2","IN", outLaneID);
	}
});

Thread t3 = new Thread(new Runnable() {
	@Override
	public void run() {

		ArrayList<String> outLaneID = new ArrayList<String>();
		outLaneID.add("TO1 (OUT)");
		//Parameter values (LaneID/Name, Test Data Name, LaneType (IN, BOTH), outLaneID is configured in ArrayList above)
		Lane("TI3 (IN)","Cy Gate Lane 3","IN", outLaneID);
	}
});

Thread t4 = new Thread(new Runnable() {
	@Override
	public void run() {

		ArrayList<String> outLaneID = new ArrayList<String>();
		outLaneID.add("O1 (OUT)");
		outLaneID.add("O2 (OUT)");
		//Parameter values (LaneID/Name, Test Data Name, LaneType (IN, BOTH), outLaneID is configured in ArrayList above)
		Lane("I1 (IN)","Cy Gate Lane 4","IN", outLaneID);
	}
});

Thread t5 = new Thread(new Runnable() {
	@Override
	public void run() {

		ArrayList<String> outLaneID = new ArrayList<String>();
		outLaneID.add("O1 (OUT)");
		outLaneID.add("O2 (OUT)");
		//Parameter values (LaneID/Name, Test Data Name, LaneType (IN, BOTH), outLaneID is configured in ArrayList above)
		Lane("I2 (IN)","Cy Gate Lane 5","IN", outLaneID);
	}
});

Thread t6 = new Thread(new Runnable() {
	@Override
	public void run() {

		ArrayList<String> outLaneID = new ArrayList<String>();
		outLaneID.add("O1 (OUT)");
		outLaneID.add("O2 (OUT)");
		//Parameter values (LaneID/Name, Test Data Name, LaneType (IN, BOTH), outLaneID is configured in ArrayList above)
		Lane("I3 (IN)","Cy Gate Lane 6","IN", outLaneID);
	}
});


//Add the thread you want to start
t1.start()
//t2.start()
//t3.start()
//t4.start()
//t5.start()
//t6.start()


//Configuration ends here
//----------------------------------------------------------------------------------------------------------




//Functions
//----------------------------------------------------------------------------------------------------------
//This function is for the lane/browser to open and create visit by appointment number
static void Lane(String laneID, String laneTestData, String laneType, ArrayList<String> outLaneID) {
	int rowsOfData  = findTestData(laneTestData).getRowNumbers()
	String firstData  = findTestData(laneTestData).getValue(1,1)
	if(firstData)
	{
		
		   Login(laneTestData);
		   WebUI.setText(findTestObject('Object Repository/Gate Simulation/CY Gate by Appointment/lane_num'), laneID)
		// Will continuous downkey until lane value matches
		while (true) {
			WebUI.sendKeys(findTestObject('Object Repository/Gate Simulation/CY Gate by Appointment/lane_num'), Keys.chord(Keys.DOWN))
			String laneAttribute =  WebUI.getAttribute(findTestObject('Object Repository/Gate Simulation/CY Gate by Appointment/lane_num_suggest'), "aria-label")
			
			// Get current selected value
			if(laneID == laneAttribute)
			{
				WebUI.sendKeys(findTestObject('Object Repository/Gate Simulation/CY Gate by Appointment/lane_num'), Keys.chord(Keys.ENTER))
				break;
			}
		  }
		   
		
		// i represent row number
		int i = 2;
		while (i <= rowsOfData) {
			//Get values from current row
			
			String startTime  = findTestData(laneTestData).getValue(6,i)
			boolean isExpired = WaitUntilWithinTimeSlot(startTime)
			String appointmentNum  = findTestData(laneTestData).getValue(5,i)
			
			if (isExpired) {
				i++;
				continue
			}
			println(appointmentNum + " create started, not expired")
			String timeWaitInMin  = findTestData(laneTestData).getValue(13,i)
			
			WebUI.setText(findTestObject('Object Repository/Gate Simulation/CY Gate by Appointment/appointment_num'), appointmentNum)
			WebUI.delay(2)
			//Thread.sleep(2000);
			WebUI.sendKeys(findTestObject('Object Repository/Gate Simulation/CY Gate by Appointment/appointment_num'), Keys.chord(Keys.DOWN))
			WebUI.sendKeys(findTestObject('Object Repository/Gate Simulation/CY Gate by Appointment/appointment_num'), Keys.chord(Keys.ENTER))
			
			while(isElementPresent(findTestObject('Object Repository/Gate Simulation/CY Gate by Appointment/container_1'), 20) == false)
			{
				
				WebUI.click(findTestObject('Object Repository/Gate Simulation/CY Gate by Appointment/clear_button'))
				WebUI.setText(findTestObject('Object Repository/Gate Simulation/CY Gate by Appointment/appointment_num'), appointmentNum)
				WebUI.delay(2)
				//Thread.sleep(2000);
				WebUI.sendKeys(findTestObject('Object Repository/Gate Simulation/CY Gate by Appointment/appointment_num'), Keys.chord(Keys.DOWN))
				WebUI.sendKeys(findTestObject('Object Repository/Gate Simulation/CY Gate by Appointment/appointment_num'), Keys.chord(Keys.ENTER))

			}
			WebUI.click(findTestObject('Object Repository/Gate Simulation/CY Gate by Appointment/proceed_button'))
			WebUI.delay(10)
			//Thread.sleep(10000);
			WebUI.click(findTestObject('Object Repository/Gate Simulation/CY Gate by Appointment/proceed_in_gate_button'))
			
			WebUI.delay(3)
			//Thread.sleep(3000);
			String gateVisit =  WebUI.getAttribute(findTestObject('Object Repository/Gate Simulation/CY Gate by Appointment/gate_visit_num'), "value")
			//KeywordUtil.logInfo(laneID + " : Gate visit " + gateVisit + "is created for appointment " + appointmentNum);
			//WebUI.takeScreenshot(["text" : "CY Gate Visit Created ", "x" : 10, "y" : 20])
			
			List<String> containers = getContainer();
			List<String> containerList = new ArrayList<String>();
			// unpack containers list from getContainer()
			String container_1 = containers[0]
			String container_2 = containers[1]
			String container_3 = containers[2]
			String container_4 = containers[3]
			
			
			// numberOfContainer will be equal to how many container are there. Trim() Remove whitespace from both sides of a string:
			if (!container_1.trim().isEmpty()) {
				containerList.add(container_1);
			}
			if (!container_2.trim().isEmpty()) {
				containerList.add(container_2);
			}
			if (!container_3.trim().isEmpty()) {
				containerList.add(container_3);
			}
			if (!container_4.trim().isEmpty()) {
				containerList.add(container_4);
			}
		
			
			// Define the target XPath
			String targetXPath = '//a[contains(text(),"Out")]'
			
			// Get all elements that match the target XPath
			List<WebElement> elements = DriverFactory.getWebDriver().findElements(By.xpath(targetXPath))
			
			// Check if at least one element is present
			boolean isIM = false;
	
			if (elements.size() > 0) {
				println(laneID + " : At least one FULL/EMPTY OUT mission is present in appointment " + appointmentNum)
				isIM = true;
			}
	
			Thread outgateThread2 = new Thread(new Runnable() {
				@Override
				public void run() {
					OutGate(appointmentNum, gateVisit, laneType, i, containerList, isIM, laneID, outLaneID, laneTestData)
				}
			});
			outgateThread2.start()
			println(appointmentNum + " started outgate")
			WebUI.click(findTestObject('Object Repository/Gate Simulation/CY Gate by Appointment/clear_button'))
			//def delayTimeInMilliseconds = (Double.parseDouble(timeWaitInMin) * 6000).toLong()
			//Thread.sleep(delayTimeInMilliseconds)
			WebUI.delay((Double.parseDouble(timeWaitInMin) * 60).toLong())
			println(appointmentNum + " create finished will proceed to next row")
			i++;
		}
		WebUI.closeBrowser()
		DriverFactory.getWebDriver().quit()
	}
}


//This will login and navigate to CY Gate UI
static void Login(laneTestData) {
	WebUI.openBrowser('')
	WebUI.navigateToUrl(findTestData(laneTestData).getValue(3, 4))
	WebUI.maximizeWindow()
	if(WebUI.verifyElementPresent(findTestObject('Object Repository/Gate Simulation/Login/ChromeAdvancedButton'), 3, FailureHandling.OPTIONAL))
		{
			WebUI.click(findTestObject('Object Repository/Gate Simulation/Login/ChromeAdvancedButton'))
			WebUI.click(findTestObject('Object Repository/Gate Simulation/Login/ChromeProceedToSite'))
			WebUI.delay(2)
		
			if (isElementPresent(findTestObject('Object Repository/Gate Simulation/Login/AcceptCookiesButton'), 20))
				{
					WebUI.click(findTestObject('Object Repository/Gate Simulation/Login/AcceptCookiesButton'))
				}
			
			
			WebUI.setText(findTestObject('Object Repository/Gate Simulation/Login/Username'), findTestData(laneTestData).getValue(3, 2))
			WebUI.setEncryptedText(findTestObject('Object Repository/Gate Simulation/Login/Password'), findTestData(laneTestData).getValue(3, 3))
			WebUI.click(findTestObject('Object Repository/Gate Simulation/Login/LoginSubmit'))
			WebUI.verifyElementPresent(findTestObject('Object Repository/Gate Simulation/Login/HomePage'), 60)
			WebUI.navigateToUrl(findTestData(laneTestData).getValue(3, 5))
			WebUI.waitForPageLoad(100)
		}
		
	
		
		
	else
		{
		WebUI.setText(findTestObject('Object Repository/Gate Simulation/Login/Username'), findTestData(laneTestData).getValue(3, 2))
		WebUI.setEncryptedText(findTestObject('Object Repository/Gate Simulation/Login/Password'), findTestData(laneTestData).getValue(3, 3))
		WebUI.click(findTestObject('Object Repository/Gate Simulation/Login/LoginSubmit'))
		WebUI.verifyElementPresent(findTestObject('Object Repository/Gate Simulation/Login/HomePage'), 60)
		WebUI.navigateToUrl(findTestData(laneTestData).getValue(3, 5))
		WebUI.waitForPageLoad(100)
		}
	
	
}


static void OutGate(String appointmentNum, String gateVisit, String laneType, int rowNumber, List<String> containerList, boolean isIM, String laneID, ArrayList<String> outLaneID, String laneTestData) {
	// Simulation start
	
	// This will read logs for road queue lift on container
	File dir = new File (findTestData(laneTestData).getValue(3, 6))
	println(dir)
	println(containerList)
	boolean fileFoundPending = false;
	// container list that already found its logs
	List<String> foundContainerList = new ArrayList<String>();
	println (appointmentNum+" found cont")
	
	while (!fileFoundPending) {
		println(appointmentNum+ " while loop start")
		//List of log files found in directory
		List<String> listOfFiles = Arrays.asList(dir.list());
		println(appointmentNum+ " " +listOfFiles.size())

		
		//Loop list of log files found in directory
		println(appointmentNum+" Find log loop start")
//		for (int j = 0; j < listOfFiles.size(); j++) {
//			String currentFilename = listOfFiles.get(j);
//
			//Loop container list
		for (int i = 0; i < containerList.size(); i++) {
			String currentContainer = containerList.get(i);
		
			// If currentContainer is already found, skip this container and continue to next loop
			if (foundContainerList.contains(currentContainer)) {
				println(appointmentNum+" skip already lifted " + currentContainer)
				continue;
			}

			String currentContainerfileNameIM = "QA-GetGateVisit-" + currentContainer + "-IM.log";
			String currentContainerfileNameEX = "QA-GetGateVisit-" + currentContainer + "-EX.log";
			String currentContainerfileNameST = "QA-GetGateVisit-" + currentContainer + "-ST.log";
			
			println(currentContainerfileNameIM)
			print(currentContainerfileNameEX)

			if (listOfFiles.contains(currentContainerfileNameIM)) {
				foundContainerList.add(currentContainer);
				println (appointmentNum+" found container IM "+currentContainer)
				
			} else if (listOfFiles.contains(currentContainerfileNameEX)) {
				foundContainerList.add(currentContainer);
				println (appointmentNum+" found container EX "+currentContainer)
				
			} else if (listOfFiles.contains(currentContainerfileNameST)) {
				foundContainerList.add(currentContainer);
				println (appointmentNum+" found container ST "+currentContainer)
			}
		}
//		}
		println(appointmentNum+" Find log loop end")
		
	
		// this checks if all containers have their log files found, comtpare size of containerList and foundContainerList if same
		if (containerList.size() == foundContainerList.size()) {
			fileFoundPending = true;
			println(appointmentNum + " : All container(s) in gate visit " + gateVisit  + " is/are lifted in/off the truck : " + containerList);
			break;
		} else {
			println(appointmentNum+" not all cotainers in gate visit. sleep 2 start")
			WebUI.delay(15)
			//Thread.sleep(15000);
			println(appointmentNum+" sleep 2 end")
		}
		

	}
	
	if (isIM == true) {
	// Open browser if log file is found
		Login(laneTestData);
		WebUI.setText(findTestObject('Object Repository/Gate Simulation/CY Gate by Appointment/gate_visit_num'), gateVisit)
		WebUI.delay(1)
		WebUI.sendKeys(findTestObject('Object Repository/Gate Simulation/CY Gate by Appointment/gate_visit_num'), Keys.chord(Keys.DOWN))
		WebUI.sendKeys(findTestObject('Object Repository/Gate Simulation/CY Gate by Appointment/gate_visit_num'), Keys.chord(Keys.ENTER))
		
		if (laneType == "IN")
			{
				Collections.shuffle(outLaneID);
				WebUI.setText(findTestObject('Object Repository/Gate Simulation/CY Gate by Appointment/lane_num'), outLaneID.get(0));
				
				while (true) {
					WebUI.sendKeys(findTestObject('Object Repository/Gate Simulation/CY Gate by Appointment/lane_num'), Keys.chord(Keys.DOWN))
					String laneAttribute =  WebUI.getAttribute(findTestObject('Object Repository/Gate Simulation/CY Gate by Appointment/lane_num_suggest'), "aria-label")
					WebUI.delay(2)
					//Thread.sleep(2000);
					// Get current selected value
	
					if(outLaneID.get(0) == laneAttribute)
					{
						WebUI.sendKeys(findTestObject('Object Repository/Gate Simulation/CY Gate by Appointment/lane_num'), Keys.chord(Keys.ENTER))
						println(appointmentNum + "matched" + laneAttribute + outLaneID)
						break;
					}
					println(appointmentNum + "unmatched" + laneAttribute + outLaneID)
				}
			}
					
		
		
			WebUI.delay(10)
			//Thread.sleep(10000);
			WebUI.click(findTestObject('Object Repository/Gate Simulation/CY Gate by Appointment/proceed_button'))
			WebUI.delay(7)
			//Thread.sleep(7000);
			WebUI.closeBrowser()
			DriverFactory.getWebDriver().quit()

	}
}


public static boolean isElementPresent(TestObject testObject, int timeout) {
	try {
		List<WebElement> elements = WebUiCommonHelper.findWebElements(testObject, timeout);
		return elements.size() > 0;
	} catch (Exception e) {
		return false;
	}
}





static List<String> getContainer() {
	
	String container_1 = "";
	String container_2 = "";
	String container_3 = "";
	String container_4 = "";

	if(isElementPresent(findTestObject('Object Repository/Gate Simulation/CY Gate by Appointment/container_1'), 3))
		{
		container_1 =  WebUI.getAttribute(findTestObject('Object Repository/Gate Simulation/CY Gate by Appointment/container_1'), "value")

		}
	if(isElementPresent(findTestObject('Object Repository/Gate Simulation/CY Gate by Appointment/container_2'), 3))
		{
		container_2 =  WebUI.getAttribute(findTestObject('Object Repository/Gate Simulation/CY Gate by Appointment/container_2'), "value")
	
		}
	if(isElementPresent(findTestObject('Object Repository/Gate Simulation/CY Gate by Appointment/container_3'), 3))
		{
		container_3 =  WebUI.getAttribute(findTestObject('Object Repository/Gate Simulation/CY Gate by Appointment/container_3'), "value")
		}
	if(isElementPresent(findTestObject('Object Repository/Gate Simulation/CY Gate by Appointment/container_4'), 3))
		{
		container_4 =  WebUI.getAttribute(findTestObject('Object Repository/Gate Simulation/CY Gate by Appointment/container_4'), "value")
	
		}
	
	return Arrays.asList(container_1,container_2,container_3,container_4)

}



static boolean WaitUntilWithinTimeSlot(String startTimeString) {
	// To format string to correct DateTime format "yyyy-MM-dd'T'HH:mm:ssXXX"
	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");
	// Formatted startTime to the correct format
	ZonedDateTime startTime = ZonedDateTime.parse(startTimeString, formatter);
	// So start time + 59 minutes = end time
	ZonedDateTime endTime = startTime.plusMinutes(59);
	// Get current date and time
	ZonedDateTime currentDateTime = ZonedDateTime.now();
	//println("currentDateTime:" + currentDateTime);
	//println("start:" + startTime + " - end:" + endTime);
	// current time is after the end time, so is expired
	if (currentDateTime.isAfter(endTime)) {
		println("EXPIRED");
		return true;
	}

	// current time is after start time, and before the end time
	if (currentDateTime.isAfter(startTime) && currentDateTime.isBefore(endTime)) {
		println("Current time is within the date and time range of the appointment");
		return false;
	}

	// thread sleep for duration between current time to start time
	Duration remainingTime = Duration.between(currentDateTime, startTime);
	println("Current time is earlier than the appointment range, will sleep for " + remainingTime.toMillis() + " milliseconds, then return false");
	//Thread.sleep(remainingTime.toMillis());
	WebUI.delay(remainingTime.toSeconds().toLong())
	return false;
	
}
