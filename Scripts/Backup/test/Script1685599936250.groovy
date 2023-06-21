import org.apache.poi.ss.usermodel.*
import org.apache.poi.xssf.usermodel.*
import java.io.*

String reportFilePath = "C:/Katalon Automation Tool/Gate-Simulation/Test Data/Report Lane"

for (int i = 1; i <= 2; i++) {
	String currentReportFilePath = reportFilePath + " " + i + ".xlsx"
	println("currentReportFilePath - " + currentReportFilePath)

	Thread t1 = new Thread(new Runnable() {
		@Override
		public void run() {
			Lane(currentReportFilePath);
		}
	});
 

	//Add the thread you want to start
	t1.start()
}


static void Lane(String currentReportFilePath) {

	FileInputStream file = new FileInputStream(new File(currentReportFilePath))
	XSSFWorkbook workbook = new XSSFWorkbook(file)
	XSSFSheet sheet = workbook.getSheetAt(0)

	int i = 1;

	while (i <= 10) {

		Row row = sheet.createRow(i) // Specify the row number where you want to insert the data
		Cell cell1 = row.createCell(0) // Specify the column number where you want to insert the data
		println("row " + row)
		cell1.setCellValue("first col")


		Cell cell = row.createCell(1) // Specify the column number where you want to insert the data
		cell.setCellFormula('IF(AND(NOT(ISBLANK(D2)), NOT(ISBLANK(C2))), TEXT(D2-C2, "hh:mm:ss"), "")');
		i++
	}


	FileOutputStream fileOut = new FileOutputStream(currentReportFilePath)
	workbook.write(fileOut)
	fileOut.close()
	workbook.close()

}