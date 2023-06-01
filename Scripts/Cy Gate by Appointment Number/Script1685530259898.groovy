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
import com.kms.katalon.core.logging.KeywordLogger
import com.kms.katalon.core.util.KeywordUtil
import java.time.ZonedDateTime;
import com.kms.katalon.core.configuration.RunConfiguration
import org.apache.poi.ss.usermodel.*
import org.apache.poi.xssf.usermodel.*
import java.io.*
import org.apache.commons.lang.StringUtils
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;




// terminate chrome driver
Runtime.getRuntime().exec('taskkill /im chromedriver.exe /f')
// terminate google chrome
Runtime.getRuntime().exec('taskkill /im chrome.exe /f')



//Configurable parameters
//----------------------------------------------------------------------------------------------------------
//This thread is used to open multiple browsers for each lane.


// Attributes inside this Global class will be accessed throughout the code
public class Global {
    // Declare logDict as a global variable, used to store report logs, then later on insert it to report excels
    private static Map<String, Map<String, Object>> logDict = new HashMap<>();
	// Use this to count how many threads is running(Both Lane and OutGate), used later on to only start putting value to report excels when all process is done.
	private static int activeThreadCount = 0
}


String sheetName      = "CY Gate Lane"
String reportFilePath = "C:/Katalon Automation Tool/Gate-Simulation/Test Data/Report Lane"
//Get number of sheet from CY Gate 1 file
String numberOfExcelString = findTestData(sheetName + " 1").getValue(2, 6)
int numberOfExcel = Integer.parseInt(numberOfExcelString)
println("Number of excel - " + numberOfExcel)
 
//thread queueing, set to 1 at a time
ExecutorService executor = Executors.newFixedThreadPool(1);

for (int i = 1; i <= numberOfExcel; i++) {
	// Excel name + loop number = current CY Gate lane
	String laneTestData = sheetName + " " + i
	String currentReportFilePath = reportFilePath + " " + i + ".xlsx"
	String laneType = findTestData(laneTestData).getValue(2, 8)
	String laneID = findTestData(laneTestData).getValue(1, 8) + " (" + laneType + ")"
	// Could be multiple outgate "T01,T02,T03"
	String outlaneString = findTestData(laneTestData).getValue(3, 8)
 
	ArrayList<String> outLaneID = new ArrayList<String>();
 
	//Split outlaneString into array with "," , adding value " (OUT)" then add it to outLaneID array
	for (String id : outlaneString.split(",")) {
		outLaneID.add( id + " (OUT)");
	}

	Thread t1 = new Thread(new Runnable() {
		@Override
		public void run() {
			Lane(laneID, laneTestData, laneType, outLaneID, currentReportFilePath, executor);
		}
	});
	// Started a thread process, activeThreadCount increment by 1
	Global.activeThreadCount ++
	t1.start()
}

print ("this is Before the sleep, will check for activeThreadCount is there is still running threads.")

while (Global.activeThreadCount > 0) {
	// checks every 60 seconds
	println("Still have " + Global.activeThreadCount + " threads running, will sleep for 60 seconds then check again")
	WebUI.delay(10)
}

println("All threads done, proceeding to update excel reports")
println("logDict " + Global.logDict)

// Start adding data in dictionary "logDict" to respective excel reports
// Loop is per CY Gate/Lane
for (gateEntry in Global.logDict.entrySet()) {
    String currentReportFilePath = gateEntry.key
    Map<String, Map<String, Object>> appointments = gateEntry.value
    
	// Per CY Gate/Lane has its own report excel, this part will open that report excel based on currentReportFilePath
	FileInputStream file = new FileInputStream(new File(currentReportFilePath))
	XSSFWorkbook workbook = new XSSFWorkbook(file)
	XSSFSheet sheet = workbook.getSheetAt(0)

	// This loop is per appointment inside this CY Gate/Lane
    for (appointmentEntry in appointments.entrySet()) {
        String appointmentNum = appointmentEntry.key
        Map<String, Object> appointmentData = appointmentEntry.value
        
		// This check the current appointment if has data rowNumber, if not will proceed to next appointment
		if (appointmentData.containsKey("row")) {
			int rowNumber = appointmentData["row"]
			Row row = sheet.createRow(rowNumber)
			row.createCell(0).setCellValue(appointmentNum)
		
			// If has gateVisit in dictionary, then retrieve data from dictionary, lastly insert it to report excel, same process with other datas below
			if (appointmentData.containsKey("gateVisit")) {
				String gateVisit = appointmentData["gateVisit"]
				row.createCell(1).setCellValue(gateVisit)
			}
		
			if (appointmentData.containsKey("appInputTimeFormatted")) {
				String appInputTimeFormatted = appointmentData["appInputTimeFormatted"]
				row.createCell(2).setCellValue(appInputTimeFormatted)
			}
	
			if (appointmentData.containsKey("preInGateTimeFormatted")) {
				String preInGateTimeFormatted = appointmentData["preInGateTimeFormatted"]
				row.createCell(3).setCellValue(preInGateTimeFormatted)
			}
	
	
			if (appointmentData.containsKey("toGroundPickTimeFormatted")) {
				String toGroundPickTimeFormatted = appointmentData["toGroundPickTimeFormatted"]
				row.createCell(5).setCellValue(toGroundPickTimeFormatted)
			}
	
	
			if (appointmentData.containsKey("gateVisitInputTimeformatted")) {
				String gateVisitInputTimeformatted = appointmentData["gateVisitInputTimeformatted"]
				row.createCell(7).setCellValue(gateVisitInputTimeformatted)
			}
	
			if (appointmentData.containsKey("gateCompletedTimeformatted")) {
				String gateCompletedTimeformatted = appointmentData["gateCompletedTimeformatted"]
				row.createCell(8).setCellValue(gateCompletedTimeformatted)
			}	
		}
    }

	// After looping through all appointment datas in this CY Gate/Lane, time to save the current report excel
	FileOutputStream fileOut = new FileOutputStream(currentReportFilePath)
	workbook.write(fileOut)
	fileOut.close()
	workbook.close()
	println ("Inserted report logs " + currentReportFilePath)
}

println ("Done inserting all report logs")
//Configuration ends here
//----------------------------------------------------------------------------------------------------------







//Functions
//----------------------------------------------------------------------------------------------------------
//This function is for the lane/browser to open and create visit by appointment number
static void Lane(String laneID, String laneTestData, String laneType, ArrayList<String> outLaneID, String currentReportFilePath, ExecutorService executor) {
	// Add to logDict with Key = currentReportFilePath, and its value = empty dictionary.
	Global.logDict.putAt(currentReportFilePath, new HashMap<>())
	
	int rowsOfData = getRowNumber(laneTestData, 4)
	String firstData  = findTestData(laneTestData).getValue(4,1)
	

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
		int i = 1;

		while (i <= rowsOfData) {
			//Get values from current row	
			String startTime  = findTestData(laneTestData).getValue(5,i)
			
			
			boolean isExpired = WaitUntilWithinTimeSlot(startTime)
			String appointmentNum  = findTestData(laneTestData).getValue(4,i)

			
			// Put another empty dictionary inside current appointmentNum key.
			Global.logDict.getAt(currentReportFilePath).putAt(appointmentNum, new HashMap<>())

			// Update dictionary inside of current appointmentNum key with "row" value
			Global.logDict.getAt(currentReportFilePath).getAt(appointmentNum).putAt("row", i)
			
			boolean isError = true

			if (isExpired) {
				// Update dictionary inside of current appointmentNum key with "gateVisit" value
				Global.logDict.getAt(currentReportFilePath).getAt(appointmentNum).putAt("gateVisit", "EXPIRED")
				i++;
				continue
			}
			println(appointmentNum + " create started, not expired")
			String timeWaitInMin  = findTestData(laneTestData).getValue(12,i)
			
			WebUI.setText(findTestObject('Object Repository/Gate Simulation/CY Gate by Appointment/appointment_num'), appointmentNum)
			//WebUI.delay(2)
			Thread.sleep(2000);
			WebUI.sendKeys(findTestObject('Object Repository/Gate Simulation/CY Gate by Appointment/appointment_num'), Keys.chord(Keys.DOWN))
			WebUI.sendKeys(findTestObject('Object Repository/Gate Simulation/CY Gate by Appointment/appointment_num'), Keys.chord(Keys.ENTER))
			
			//Get time After input appointment no. , before click proceed to Pre In Gate
			// Get the current date and time
			LocalDateTime appInputTime = LocalDateTime.now()
			
			
			// Convert LocalDateTime to a string representation
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss")
			String appInputTimeFormatted = appInputTime.format(formatter)
			
			// Update dictionary inside of current appointmentNum key with "appInputTimeFormatted" value
			Global.logDict.getAt(currentReportFilePath).getAt(appointmentNum).putAt("appInputTimeFormatted", appInputTimeFormatted)
			
			while(isElementPresent(findTestObject('Object Repository/Gate Simulation/CY Gate by Appointment/container_1'), 20) == false)
			{
				WebUI.click(findTestObject('Object Repository/Gate Simulation/CY Gate by Appointment/clear_button'))
				WebUI.setText(findTestObject('Object Repository/Gate Simulation/CY Gate by Appointment/appointment_num'), appointmentNum)
				//WebUI.delay(2)
				Thread.sleep(2000);
				WebUI.sendKeys(findTestObject('Object Repository/Gate Simulation/CY Gate by Appointment/appointment_num'), Keys.chord(Keys.DOWN))
				WebUI.sendKeys(findTestObject('Object Repository/Gate Simulation/CY Gate by Appointment/appointment_num'), Keys.chord(Keys.ENTER))

			}
			WebUI.click(findTestObject('Object Repository/Gate Simulation/CY Gate by Appointment/proceed_button'))
			
			//Get time After click proceed to Pre In Gate, going to In Gate
			// Get the current date and time
			LocalDateTime preInGateTime = LocalDateTime.now()

			// Convert LocalDateTime to a string representation
			String preInGateTimeFormatted = preInGateTime.format(formatter)
			
			// Update dictionary inside of current appointmentNum key with "preInGateTimeFormatted" value
			Global.logDict.getAt(currentReportFilePath).getAt(appointmentNum).putAt("preInGateTimeFormatted", preInGateTimeFormatted)


			if (isElementPresent(findTestObject('Object Repository/Gate Simulation/CY Gate by Appointment/proceed_in_gate_button'), 120))
					{
						WebUI.click(findTestObject('Object Repository/Gate Simulation/CY Gate by Appointment/proceed_in_gate_button'))
						
						//Get time when ready To Ground/Pick
						// Get the current date and time
						LocalDateTime toGroundPickTime = LocalDateTime.now()
						
						// Convert LocalDateTime to a string representation
						String toGroundPickTimeFormatted = toGroundPickTime.format(formatter)
						
						// Update dictionary inside of current appointmentNum key with "toGroundPickTimeFormatted" value
						Global.logDict.getAt(currentReportFilePath).getAt(appointmentNum).putAt("toGroundPickTimeFormatted", toGroundPickTimeFormatted)				

						//WebUI.delay(3)
						Thread.sleep(3000);
						String gateVisit =  WebUI.getAttribute(findTestObject('Object Repository/Gate Simulation/CY Gate by Appointment/gate_visit_num'), "value")
	
						// Update dictionary inside of current appointmentNum key with "gateVisit" value
						Global.logDict.getAt(currentReportFilePath).getAt(appointmentNum).putAt("gateVisit", gateVisit)
						
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
						
						// Started a thread process, activeThreadCount increment by 1
						Global.activeThreadCount ++;
						println(appointmentNum + " started outgate")
						println("thread is freed up, running outgate thread")
						isError = false
						executor.execute(new Runnable() {
							@Override
							public void run() {
						 		OutGate(currentReportFilePath, appointmentNum, gateVisit, laneType, i, containerList, isIM, laneID, outLaneID, laneTestData)
						 	}
						 });

					}
					
				WebUI.click(findTestObject('Object Repository/Gate Simulation/CY Gate by Appointment/clear_button'))
				
				// if isError is False, only will apply delay for time wait in minute 
				if (isError == false)
				{
					WebUI.delay((Double.parseDouble(timeWaitInMin) * 60).toLong())
					println(appointmentNum + " create finished will proceed to next row")
				}
			i++;
		}
		

		WebUI.closeBrowser()
		DriverFactory.getWebDriver().quit()
	}
	// Ending a thread process, activeThreadCount decrement by 1
	Global.activeThreadCount --;
}

//This will login and navigate to CY Gate UI
static void Login(laneTestData) {
	WebUI.openBrowser('')
	WebUI.navigateToUrl(findTestData(laneTestData).getValue(2, 3))
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
			
			
			WebUI.setText(findTestObject('Object Repository/Gate Simulation/Login/Username'), findTestData(laneTestData).getValue(2, 1))
			WebUI.setEncryptedText(findTestObject('Object Repository/Gate Simulation/Login/Password'), findTestData(laneTestData).getValue(2, 2))
			WebUI.click(findTestObject('Object Repository/Gate Simulation/Login/LoginSubmit'))
			WebUI.verifyElementPresent(findTestObject('Object Repository/Gate Simulation/Login/HomePage'), 60)
			WebUI.navigateToUrl(findTestData(laneTestData).getValue(2, 4))
			WebUI.waitForPageLoad(100)
	
		}
		
	
		
		
	else
		{
		WebUI.setText(findTestObject('Object Repository/Gate Simulation/Login/Username'), findTestData(laneTestData).getValue(2, 1))
		WebUI.setEncryptedText(findTestObject('Object Repository/Gate Simulation/Login/Password'), findTestData(laneTestData).getValue(2, 2))
		WebUI.click(findTestObject('Object Repository/Gate Simulation/Login/LoginSubmit'))
		WebUI.verifyElementPresent(findTestObject('Object Repository/Gate Simulation/Login/HomePage'), 60)
		WebUI.navigateToUrl(findTestData(laneTestData).getValue(2, 4))
		WebUI.waitForPageLoad(100)
		}
	
	
}


static void OutGate(String currentReportFilePath, String appointmentNum, String gateVisit, String laneType, int rowNumber, List<String> containerList, boolean isIM, String laneID, ArrayList<String> outLaneID, String laneTestData) {
	// Simulation start
	
	// This will read logs for road queue lift on container
	File dir = new File (findTestData(laneTestData).getValue(2, 5))
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
			//WebUI.delay(15)
			Thread.sleep(15000);
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
		
		
		
		
		//Get time After input gate visit no. before click proceed
		// Get the current date and time
		LocalDateTime gateVisitInputTime = LocalDateTime.now()
		
		// Convert LocalDateTime to a string representation
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss")
		String gateVisitInputTimeformatted = gateVisitInputTime.format(formatter)
		Global.logDict.getAt(currentReportFilePath).getAt(appointmentNum).putAt("gateVisitInputTimeformatted", gateVisitInputTimeformatted)
		
		//Global.logDict[currentReportFilePath][appointmentNum] = ["gateVisitInputTimeformatted": gateVisitInputTimeformatted]
		// Set the cell value
		// Row row = sheet.createRow(rowNumber) // Specify the row number where you want to insert the data
		// Cell cellGateVisitInput = row.createCell(7) // Specify the column number where you want to insert the data
		// cellGateVisitInput.setCellValue(gateVisitInputTimeformatted)
		// println("Set value for column 5: " + gateVisitInputTimeformatted)
		
		
		
		if (laneType == "IN")
			{
				Collections.shuffle(outLaneID);
				WebUI.setText(findTestObject('Object Repository/Gate Simulation/CY Gate by Appointment/lane_num'), outLaneID.get(0));
				
				while (true) {
					WebUI.sendKeys(findTestObject('Object Repository/Gate Simulation/CY Gate by Appointment/lane_num'), Keys.chord(Keys.DOWN))
					String laneAttribute =  WebUI.getAttribute(findTestObject('Object Repository/Gate Simulation/CY Gate by Appointment/lane_num_suggest'), "aria-label")
					//WebUI.delay(2)
					Thread.sleep(2000);
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
					
		
		
			//WebUI.delay(5)
			Thread.sleep(10000);
			if (isElementPresent(findTestObject('Object Repository/Gate Simulation/CY Gate by Appointment/proceed_button'), 10))
			{
				WebUI.click(findTestObject('Object Repository/Gate Simulation/CY Gate by Appointment/proceed_button'))
				
				if (isElementPresent(findTestObject('Object Repository/Gate Simulation/CY Gate by Appointment/outgate_completed'), 120))
					{
				
					//Get time After input gate visit no. before click proceed
					// Get the current date and time
					LocalDateTime gateCompletedTime = LocalDateTime.now()
					
					// Convert LocalDateTime to a string representation
					String gateCompletedTimeformatted = gateCompletedTime.format(formatter)
					
					// Set the cell value
					Global.logDict.getAt(currentReportFilePath).getAt(appointmentNum).putAt("gateCompletedTimeformatted", gateCompletedTimeformatted)
					//Global.logDict[currentReportFilePath][appointmentNum] = ["gateCompletedTimeformatted": gateCompletedTimeformatted]
					// Cell cellgateCompletedTime = row.createCell(8) // Specify the column number where you want to insert the data
					// cellgateCompletedTime.setCellValue(gateCompletedTimeformatted)
					// println("Set value for column 8: " + gateCompletedTimeformatted)
					
					
					}
			}
			
			//Thread.sleep(7000);
			WebUI.closeBrowser()
			DriverFactory.getWebDriver().quit()

	}
	// Ending a thread process, activeThreadCount decrement by 1
	Global.activeThreadCount --;
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









static int getRowNumber(String testDataName, int columnNumber) {
	// Get the test data using the provided name
	def testData = findTestData(testDataName)

	// Get the total number of rows in the test data sheet
	def totalRows = testData.getRowNumbers()
	

	// Iterate through each row and check the value in the specified column
	for (int row = 1; row <= totalRows; row++) {
		// Get the value in the specified column for the current row
		def cellValue = testData.getValue(columnNumber, row)
		

		// Check if the cell value is not empty or null
		if (cellValue == null || cellValue.isEmpty() || StringUtils.isBlank(cellValue)) {
			// Return the row number if the cell value is found
			return row-1
		}
	}

	// Return -1 if the cell value is not found in any row
	return totalRows
}




