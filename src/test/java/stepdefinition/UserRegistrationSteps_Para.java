package stepdefinition;

import common.action.GlobalConfiguration;
import common.action.ReusableCommonMethods;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.Assert;
import org.openqa.selenium.WebDriver;
import page.objects.*;

import java.util.List;
import java.util.Map;

public class UserRegistrationSteps_Para {

    WebDriver driver = Hooks.driver;

    @When("click on the register link of the homepage")
    public void click_on_the_register_link_of_the_homepage() {
        HomePage_Para homePageObject = new HomePage_Para(driver);
        homePageObject.clickOnRegisterLink(driver);
        if (GlobalConfiguration.TAKE_SCREENSHOT_FOR_EACH_STEP.equalsIgnoreCase("Yes")) {
            ReusableCommonMethods.takeScreenshot(driver, Hooks.currentScenario);
        }
    }

    @When("enter all the signup details as given below")
    public void enter_all_the_registration_details_as_given_below(DataTable userRegistrationData) {
        try {
            List<Map<String, String>> userRegistrationDataAsMap = userRegistrationData.asMaps();
            UserRegistration_Para createAccountPageDemo = new UserRegistration_Para(driver);
            //Enter First Name
            createAccountPageDemo.enterFirstName(driver, userRegistrationDataAsMap.get(0).get("firstName"));
            //Enter Last Name
            createAccountPageDemo.enterLastName(driver, userRegistrationDataAsMap.get(0).get("lastName"));
            //date of birth
            createAccountPageDemo.enterAddress1(driver, userRegistrationDataAsMap.get(0).get("addressLine1"));
            createAccountPageDemo.enterCity(driver, userRegistrationDataAsMap.get(0).get("city"));
            createAccountPageDemo.selectState(driver, userRegistrationDataAsMap.get(0).get("state"));
            createAccountPageDemo.enterZipCode(driver, userRegistrationDataAsMap.get(0).get("zipCode"));
            createAccountPageDemo.enterMobilePhone(driver, userRegistrationDataAsMap.get(0).get("mobilePhone"));
            createAccountPageDemo.enterSSN(driver,userRegistrationDataAsMap.get(0).get("SSN"));
            createAccountPageDemo.enterUsername(driver,ReusableCommonMethods.generateRandomAlphabaticString(10));
            //Enter password
            createAccountPageDemo.enterPassword(driver, userRegistrationDataAsMap.get(0).get("password"));
            createAccountPageDemo.enterConfirmPassword(driver, userRegistrationDataAsMap.get(0).get("password"));
            //Click on Register button
            //createAccountPageDemo.clickOnRegisterButton(driver);
            if (GlobalConfiguration.TAKE_SCREENSHOT_FOR_EACH_STEP.equalsIgnoreCase("Yes")) {
                ReusableCommonMethods.takeScreenshot(driver, Hooks.currentScenario);
            }

        } catch (Exception e) {
            Assert.fail("Exception occurred while entering all the user registration details : "+e);
        }
    }

}
