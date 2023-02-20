package stepdefinition;

import com.utility.ReusableUtilities;
import common.action.ReusableCommonMethods;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Assert;
import org.openqa.selenium.WebDriver;
import page.objects.StockAnalysisPage;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StockAnalysisSteps {

    WebDriver driver = Hooks.driver;
    StockAnalysisPage st;
    List<String> allStocks = new ArrayList<String>();
    public static List<HashMap<String, String>> consolidatedData = new ArrayList<>();
    public String sheetNameGlobal;

    @When("landed on the google homepage")
    public void landed_on_the_google_homepage() {
        st = new StockAnalysisPage(driver);
        Assert.assertTrue("Google Homepage is not displayed", st.validateGoogleHomePageDisplayed());
    }


    @Then("capture all the stock statistics")
    public void capture_all_the_stock_statistics() {
        for (int i = 0; i < allStocks.size(); i++) {
            st.enterSearchValueAndClickOnEnterButton(allStocks.get(i));
            st.fetchStockAnalysis();
        }
        //System.out.println(consolidatedData);
        ReusableUtilities.writeExcelData(
                System.getProperty("user.dir") + "\\src\\test\\resources\\OtherData\\" + ReusableCommonMethods.getTodaysDateAndTime("dd_MM_yyyy_HH_mm_ss") + ".xlsx",
                consolidatedData,
                allStocks
        );
        st.writeTodaysChangeInLastColumn(consolidatedData,allStocks,sheetNameGlobal);
    }

    @Given("we read all the company name from excel {string} and sheet name {string}")
    public void we_read_all_the_company_name_from_excel_and_sheet_name(String workbookName, String sheetName) {

        sheetNameGlobal = sheetName;
        System.out.println("Reading the excel");
        XSSFWorkbook wb = ReusableUtilities.getWorkbookObject(System.getProperty("user.dir") + "\\src\\test\\resources\\OtherData\\" + workbookName + ".xlsx");
        System.out.println("Workbook : " + wb);
        XSSFSheet sheet = ReusableUtilities.getWorkbookSheet(sheetName, wb);
        System.out.println("Sheet : " + sheet);
        allStocks = ReusableUtilities.fetchValueFromGivenColumnInExcel(0, sheet);
    }
}
