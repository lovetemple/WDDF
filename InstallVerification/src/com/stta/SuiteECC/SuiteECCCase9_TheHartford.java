package com.stta.SuiteECC;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Reporter;
import org.testng.SkipException;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import com.stta.utility.Read_XLS;
import com.stta.utility.SuiteUtility;

public class SuiteECCCase9_TheHartford extends SuiteECCBase{
	Read_XLS FilePath = null;
	String SheetName = null;
	String TestCaseName = null;	
	String ToRunColumnNameTestCase = null;
	String ToRunColumnNameTestData = null;
	String TestDataToRun[]=null;
	static boolean TestCasePass=true;
	static int DataSet=-1;	
	static boolean Testskip=false;
	static boolean Testfail=false;
	SoftAssert s_assert =null;	
	
	@BeforeTest
	public void checkCaseToRun() throws IOException{
		init();			
		//To set .xls file's path In FilePath Variable.
		FilePath = TestCaseListExcelECC;		
		TestCaseName = this.getClass().getSimpleName();	
		//SheetName to check CaseToRun flag against test case.
		SheetName = "TestCasesList";
		//Name of column In TestCasesList Excel sheet.
		ToRunColumnNameTestCase = "CaseToRun";
		//Name of column In Test Case Data sheets.
		ToRunColumnNameTestData = "DataToRun";
		Add_Log.info(TestCaseName+" : Execution started.");
		
		if(!SuiteUtility.checkToRunUtility(FilePath, SheetName,ToRunColumnNameTestCase,TestCaseName)){
			Add_Log.info(TestCaseName+" : CaseToRun = N for So Skipping Execution.");
			SuiteUtility.WriteResultUtility(FilePath, SheetName, "Pass/Fail/Skip", TestCaseName, "SKIP");
			throw new SkipException(TestCaseName+"'s CaseToRun Flag Is 'N' Or Blank. So Skipping Execution Of "+TestCaseName);
		}	
		//To retrieve DataToRun flags of all data set lines from related test data sheet.
		TestDataToRun = SuiteUtility.checkToRunUtilityOfData(FilePath, TestCaseName, ToRunColumnNameTestData);
	}
	
	@Test(dataProvider="SuiteECCCase9_TheHartford")
	public void SuiteECCCase9_TheHartfordTest(String DataURL, String DataBrowser, String DataUsername, String DataPassword, String DataSSN, String DataBeginDate) throws InterruptedException, IOException{
		
		DataSet++;
		s_assert = new SoftAssert();
		String fileName1_statement = "";

		if(!TestDataToRun[DataSet].equalsIgnoreCase("Y")){	
			Add_Log.info(TestCaseName+" : DataToRun = N for data set line "+(DataSet+1)+" So skipping Its execution.");
			Testskip=true;
			throw new SkipException("DataToRun for row number "+DataSet+" Is No Or Blank. So Skipping Its Execution.");
		}
		Reporter.log("<br>Screenshots of " + TestCaseName);
		loadWebBrowser(DataBrowser);		
		driver.get(DataURL);
		userLoginECC(DataUsername, DataPassword);
		Add_Log.info("Logged in");
		
		// Select Menu TRAC
		WebDriverWait wait = new WebDriverWait(driver, timeOutSet);
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("html/body/center/table[2]/tbody/tr/td/table/tbody/tr[1]/td/input[8]")));
		driver.findElement(By.xpath("html/body/center/table[2]/tbody/tr/td/table/tbody/tr[1]/td/input[8]")).click();
		Add_Log.info("Selected TRAC");
		
		// Select "View Statements" 
		wait.until(ExpectedConditions.elementToBeClickable(By.name("DocumentViewStmtImage")));
		driver.findElement(By.name("DocumentViewStmtImage")).click();
		Add_Log.info("Selected 'View Statements'");
		
		// Enter Search Criteria and View Document
		wait.until(ExpectedConditions.textToBePresentInElementLocated((By.xpath("html/body")),"TRAC Statement Search"));
		wait.until(ExpectedConditions.presenceOfElementLocated(By.name("sttaxid")));
		driver.findElement(By.name("sttaxid")).sendKeys(DataSSN);
		
		// Enter Begin Date and End Date
		wait.until(ExpectedConditions.presenceOfElementLocated(By.name("startdate")));
		driver.findElement(By.name("startdate")).clear();
		wait.until(ExpectedConditions.presenceOfElementLocated(By.name("enddate")));
		driver.findElement(By.name("enddate")).clear();
		driver.findElement(By.name("startdate")).sendKeys(DataBeginDate);
		String timeStamp = new SimpleDateFormat("YYYY-MM-dd").format(Calendar.getInstance().getTime());
		driver.findElement(By.name("enddate")).sendKeys(timeStamp);
		// Click View document button
		wait.until(ExpectedConditions.presenceOfElementLocated(By.name("Submit")));
		driver.findElement(By.name("Submit")).click();
		
		wait.until(ExpectedConditions.titleContains("eCC System - TRAC PDF Statement List"));
		wait.until(ExpectedConditions.textToBePresentInElement(driver.findElement(By.xpath("html/body/center/table[2]/tbody/tr/td/table/tbody/tr[1]/td/font/b")), "Click on date to View the Statement:"));
		wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("html/body/center/table[2]/tbody/tr/td")));
		// Get all search result. Verify one of the statement is presented. i.e. 2008-06-30Statement
		String statementList = driver.findElement(By.xpath("html/body/center/table[2]/tbody/tr/td")).getText();
		if(!statementList.contains("Statement"))
		{
			s_assert.assertTrue(statementList.contains("Statement"), "Tested statement NOT FOUND: Statement");
			Add_Log.info("Tested statement NOT FOUND: Statement");
        	Testfail = true;
		}
		else
		{
			Add_Log.info("Statement Lists are displayed. Click on one of the statement");
		}
		// Click any statement e.g. the 1st statement
		wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("html/body/center/table[2]/tbody/tr/td/table/tbody/tr[3]/td[2]/font/a")));
		driver.findElement(By.xpath("html/body/center/table[2]/tbody/tr/td/table/tbody/tr[3]/td[2]/font/a")).click();
		// PDF is opened in new tab
		Set<String> AllWindowHandles = driver.getWindowHandles();
		String window1 = (String) AllWindowHandles.toArray()[0]; // Main CSR window
		String window2 = (String) AllWindowHandles.toArray()[1]; // PDF window
		
		driver.switchTo().window(window2);
		try {
				  Thread.sleep(waitFileLoad);
				} catch(InterruptedException ex) {
				  Thread.currentThread().interrupt();
				}
		String currentURL = driver.getCurrentUrl();
		if(currentURL.contains(".onlinefinancialdocs")) // This test case is only applicable on Chrome
		{
				// Verify PDF contents
				/*wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//body")));
				driver.findElement(By.xpath("//body")).click();
				Thread.sleep(clipboardTime);
				driver.findElement(By.xpath("//body")).sendKeys(Keys.chord(Keys.CONTROL, "a"));
				Thread.sleep(clipboardTime);
			    driver.findElement(By.xpath("//body")).sendKeys(Keys.chord(Keys.CONTROL, "c"));
			    Thread.sleep(clipboardTime);
			    driver.findElement(By.xpath("//body")).click();
			    Thread.sleep(clipboardTime);
			    String PDFContent = getClipBoard();
			    Thread.sleep(clipboardTime);
			    //Add_Log.info("PDFContent" + PDFContent);
			    Add_Log.info("PDF can be opened. As expected");
			    if(PDFContent.contains("Balance"))
			    {
			    	fileName1_statement = takeScreenshot(TestCaseName, "1_ViewStatement");
					Add_Log.info("PDF's content is correct. As expected");
			    }
			    else
			    {
			    	Add_Log.info("PDF content does not contain expected wording: Balance");
			    	Testfail = true;
			    	s_assert.assertTrue(PDFContent.contains("Balance"), "PDF content does not contain expected wording: Balance");
			    	fileName1_statement = takeScreenshot(TestCaseName, "1_ViewStatementFail");
			    }*/
				String PDFContent = "";
				String expectedWording = "Balance";
				PDFContent = verifyPDFContent();
				// In case PDF is taking long time to load. Wait x2 times and get clip board again
				if(!PDFContent.contains(expectedWording))
				{
					try {
						Thread.sleep(waitFileLoad);
					} catch(InterruptedException ex) {
						Thread.currentThread().interrupt();
					}
					PDFContent = verifyPDFContent();
				}
				if(PDFContent.contains(expectedWording))
				{
					fileName1_statement = takeScreenshot(TestCaseName, "1_ViewStatement");
					Add_Log.info("PDF's content is correct. As expected");
				}
				else
				{
					Add_Log.info("PDF content does not contain expected wording:" + expectedWording);
					Testfail = true;
					s_assert.assertTrue(PDFContent.contains(expectedWording), "PDF content does not contain expected wording: " + expectedWording);
					fileName1_statement = takeScreenshot(TestCaseName, "1_ViewStatementFail");
				}	
		}
		else
		{
				Add_Log.info("FAIL: Error occurs upon opening PDF");
				Testfail = true;
				s_assert.assertTrue(driver.getCurrentUrl().contains(".onlinefinancialdocs"), "Error occurs upon opening PDF");
				fileName1_statement = takeScreenshot(TestCaseName, "1_ViewStatement");
		}
		
		driver.get(DataURL);
		driver.close(); // Close PDF window
		driver.switchTo().window(window1);
		
		// Adding screenshot to report =======================================================================================================================

		reportOutput(fileName1_statement, "1. View Statement", "1. Error occurs on Statement file");
	
		//=======================================================================================================================================================
		if(Testfail){
			s_assert.assertAll();		
		}

	}
	
	//@AfterMethod method will be executed after execution of @Test method every time.
	@AfterMethod
	public void reporterDataResults(){		
		if(Testskip){
			Add_Log.info(TestCaseName+" : Reporting test data set line "+(DataSet+1)+" as SKIP In excel.");
			//If found Testskip = true, Result will be reported as SKIP against data set line In excel sheet.
			SuiteUtility.WriteResultUtility(FilePath, TestCaseName, "Pass/Fail/Skip", DataSet+1, "SKIP");
		}
		else if(Testfail){
			Add_Log.info(TestCaseName+" : Reporting test data set line "+(DataSet+1)+" as FAIL In excel.");
			//To make object reference null after reporting In report.
			s_assert = null;
			//Set TestCasePass = false to report test case as fail In excel sheet.
			TestCasePass=false;	
			//If found Testfail = true, Result will be reported as FAIL against data set line In excel sheet.
			SuiteUtility.WriteResultUtility(FilePath, TestCaseName, "Pass/Fail/Skip", DataSet+1, "FAIL");			
		}else{
			Add_Log.info(TestCaseName+" : Reporting test data set line "+(DataSet+1)+" as PASS In excel.");
			//If found Testskip = false and Testfail = false, Result will be reported as PASS against data set line In excel sheet.
			SuiteUtility.WriteResultUtility(FilePath, TestCaseName, "Pass/Fail/Skip", DataSet+1, "PASS");
		}
		//At last make both flags as false for next data set.
		Testskip=false;
		Testfail=false;
	}
	
		
	//This data provider method will return column's data one by one In every Iteration.
	@DataProvider
	public Object[][] SuiteECCCase9_TheHartford(){
		//Last two columns (DataToRun and Pass/Fail/Skip) are Ignored programatically when reading test data.
		return SuiteUtility.GetTestDataUtility(FilePath, TestCaseName);
	}	
	
	//To report result as pass or fail for test cases In TestCasesList sheet.
	@AfterTest
	public void closeBrowser(){
		closeWebBrowser();
		if(TestCasePass){
			Add_Log.info(TestCaseName+" : Reporting test case as PASS In excel.");
			SuiteUtility.WriteResultUtility(FilePath, SheetName, "Pass/Fail/Skip", TestCaseName, "PASS");
		}
		else{
			Add_Log.info(TestCaseName+" : Reporting test case as FAIL In excel.");
			SuiteUtility.WriteResultUtility(FilePath, SheetName, "Pass/Fail/Skip", TestCaseName, "FAIL");			
		}
	}
}