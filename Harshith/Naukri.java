package mine_day4;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.rmi.UnknownHostException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class Naukri {
	FileOutputStream fos;
	WebDriver d;
	
	@BeforeMethod
	public void setup() {
		d= new ChromeDriver();
		d.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(10));
		d.get("https://www.naukri.com/");
		d.manage().timeouts().implicitlyWait(Duration.ofSeconds(20));
	}
	@AfterMethod
	public void teardown() {

		d.quit();
	}

	@Test
	public void naukri() {
		try {
			List<WebElement> list=d.findElements(By.xpath("//a[@href]"));

			List<String> l1=new ArrayList<String>();

			
			//to add pass or fail
//			List<String> listpass=new ArrayList<String>();
			for(WebElement ele:list)
			{
				l1.add(ele.getAttribute("href"));

			}
			


			XSSFWorkbook workbook=new XSSFWorkbook();
			XSSFSheet sh1= workbook.createSheet("hyperlinks");
			for(int i=0;i<l1.size();i++) {
				String link=l1.get(i);

				sh1.createRow(i).createCell(0).setCellValue(link);
			}
			
			

			 fos = new FileOutputStream("./Test-data/hyperlinks.xlsx");
			workbook.write(fos);
			fos.close();


			
			
		}catch(Exception e) {
			e.printStackTrace();
		}

	}
	
	@Test(priority=2)
	public void checkURL() throws FileNotFoundException, IOException, InterruptedException,UnknownHostException {
		
		XSSFWorkbook workbook=new XSSFWorkbook(new FileInputStream(new File("./Test-data/hyperlinks.xlsx")));
		
		List<String> list=new ArrayList<String>();
		
		List<String> l1 = new ArrayList<String>();
		
		int row=workbook.getSheet("hyperlinks").getPhysicalNumberOfRows();
		//int col=workbook.getSheet("hyperlinks").getRow(0).getPhysicalNumberOfCells();
		
		
		
		
		for(int i=0;i<row;i++) {
			list.add(workbook.getSheet("hyperlinks").getRow(i).getCell(0).getStringCellValue());
		}
		
		try {
		for(int i=0;i<row;i++) {
			Response resp=RestAssured.get(list.get(i));
			int code=resp.getStatusCode();
			System.out.println(code);
			if((code>=200 && code<=205) || (code>=300 && code<=305)) {
				l1.add("Passed");
			}
			else {
				l1.add("Failed");
			}
		}
		}finally {
			System.out.println("");
		}
		
		XSSFSheet sh1= workbook.getSheet("hyperlinks");
		
		for(int i=0;i<list.size();i++) {
			if(l1.get(i)=="Passed") {
				 CellStyle style = workbook.createCellStyle();  
				 style.setFillBackgroundColor(IndexedColors.GREEN.getIndex());
				 style.setFillPattern(FillPatternType.BIG_SPOTS);  
				 
				XSSFCell cell=sh1.getRow(i).createCell(1);
				cell.setCellValue(l1.get(i));
				cell.setCellStyle(style); 
			}
			else {
				CellStyle style = workbook.createCellStyle();  
				 style.setFillBackgroundColor(IndexedColors.RED.getIndex());
				 style.setFillPattern(FillPatternType.BIG_SPOTS);   
				 
					XSSFCell cell=sh1.getRow(i).createCell(1);
					cell.setCellValue(l1.get(i));
					cell.setCellStyle(style); 
			}
			
		}
		
		fos = new FileOutputStream("./Test-data/hyperlinks.xlsx");
		workbook.write(fos);
		fos.close();

		
	}

}
