package com.stta.SuiteOne;

import java.io.IOException;
import org.testng.SkipException;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import com.stta.utility.Read_XLS;
import com.stta.utility.SuiteUtility;

//SuiteOneCaseOne Class Inherits From SuiteOneBase Class.
//So, SuiteOneCaseOne Class Is Child Class Of SuiteOneBase Class And SuiteBase Class.
public class TempSuiteOneCaseOne extends SuiteOneBase{
	Read_XLS FilePath = null;
	String SheetName = null;
	String TestCaseName = null;	
	String ToRunColumnNameTestCase = null;	
	
	@BeforeTest
	public void checkCaseToRun() throws IOException{
		//Called init() function from SuiteBase class to Initialize .xls Files
		init();	
		//To set SuiteOne.xls file's path In FilePath Variable.
		FilePath = TestCaseListExcelOne;		
		TestCaseName = this.getClass().getSimpleName();	
		//SheetName to check CaseToRun flag against test case.
		SheetName = "TestCasesList";
		//Name of column In Excel sheet.
		ToRunColumnNameTestCase = "CaseToRun";
		
		//To check test case's CaseToRun = Y or N In related excel sheet.
		//If CaseToRun = N or blank, Test case will skip execution. Else It will be executed.
		if(!SuiteUtility.checkToRunUtility(FilePath, SheetName, ToRunColumnNameTestCase, TestCaseName)){
			//To report TestCase as 'Skipped' In TestCaseList sheet of SuiteXXX.xls If CaseToRun = N.
			SuiteUtility.WriteResultUtility(FilePath, SheetName, "Pass/Fail/Skip", TestCaseName, "Skipped");
			throw new SkipException(TestCaseName+"'s CaseToRun Flag Is 'N' Or Blank. So Skipping Execution Of "+TestCaseName);
		}	
		//To report TestCase as 'Executed' In TestCaseList sheet of SuiteXXX.xls If CaseToRun = Y. 
		SuiteUtility.WriteResultUtility(FilePath, SheetName, "Pass/Fail/Skip", TestCaseName, "Executed");
	}
	
	//Accepts 4 column's String data In every Iteration.
	@Test(dataProvider="SuiteOneCaseOneData")
	public void SuiteOneCaseOneTest(String DataCol1,String DataCol2,String DataCol3,String ExpectedResult){
		System.out.println("Value Of DataCol1 = "+DataCol1);
		System.out.println("Value Of DataCol2 = "+DataCol2);
		System.out.println("Value Of DataCol3 = "+DataCol3);
		System.out.println("Value Of ExpectedResult = "+ExpectedResult);		
	}	
	
	//This data provider method will return 4 column's data one by one In every Iteration.
	@DataProvider
	public Object[][] SuiteOneCaseOneData(){
		//To retrieve data from Data 1 Column,Data 2 Column,Data 3 Column and Expected Result column of SuiteOneCaseOne data Sheet.
		//Last two columns (DataToRun and Pass/Fail/Skip) are Ignored programatically when reading test data.
		return SuiteUtility.GetTestDataUtility(FilePath, TestCaseName);
	}
}
