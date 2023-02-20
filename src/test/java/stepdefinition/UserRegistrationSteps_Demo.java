package stepdefinition;

import common.action.GlobalConfiguration;
import common.action.ReusableCommonMethods;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.Assert;
import org.openqa.selenium.WebDriver;
import page.objects.CreateAnAccountPage_Demo;
import page.objects.HomePage_Demo;
import page.objects.MyAccount_Demo;
import page.objects.SignInPage_Demo;

import java.util.List;
import java.util.Map;

public class UserRegistrationSteps_Demo {

    WebDriver driver = Hooks.driver;

    @When("click on the sign in link of the homepage")
    public void click_on_the_sign_in_link_of_the_homepage() {
        HomePage_Demo homePageObject = new HomePage_Demo(driver);
        homePageObject.clickOnSignInLink(driver);
        if (GlobalConfiguration.TAKE_SCREENSHOT_FOR_EACH_STEP.equalsIgnoreCase("Yes")) {
            ReusableCommonMethods.takeScreenshot(driver, Hooks.currentScenario);
        }
    }

    @When("enter the registration email address as {string} and click on create account button")
    public void enter_the_registration_email_address_as_and_click_on_create_account_button(String emailID) {
        SignInPage_Demo signInPageDemoObj = new SignInPage_Demo(driver);
        signInPageDemoObj.enterEmailAndCreateAnAccount(driver, emailID);
        if (GlobalConfiguration.TAKE_SCREENSHOT_FOR_EACH_STEP.equalsIgnoreCase("Yes")) {
            ReusableCommonMethods.takeScreenshot(driver, Hooks.currentScenario);
        }
    }

    @When("enter all the registration details as given below")
    public void enter_all_the_registration_details_as_given_below(DataTable userRegistrationData) {
        try {
            List<Map<String, String>> userRegistrationDataAsMap = userRegistrationData.asMaps();
            CreateAnAccountPage_Demo createAccountPageDemo = new CreateAnAccountPage_Demo(driver);
            //Select Title
            /*if (userRegistrationDataAsMap.get(0).get("title") != null && !userRegistrationDataAsMap.get(0).get("title").isEmpty()) {
                //createAccountPageDemo.selectTitle(driver, userRegistrationDataAsMap.get(0).get("title"));
            }*/
            //Enter First Name
            createAccountPageDemo.enterFirstName(driver, userRegistrationDataAsMap.get(0).get("firstName"));
            //Enter Last Name
            createAccountPageDemo.enterLastName(driver, userRegistrationDataAsMap.get(0).get("lastName"));
            //Enter password
            createAccountPageDemo.enterPassword(driver, userRegistrationDataAsMap.get(0).get("password"));
            //date of birth
            createAccountPageDemo.enterDateOfBirth(driver, userRegistrationDataAsMap.get(0).get("dob"));
            //enter address first name

            createAccountPageDemo.enterAddressFirstName(driver, userRegistrationDataAsMap.get(0).get("addressFirstName"));
            createAccountPageDemo.enterAddressLastName(driver, userRegistrationDataAsMap.get(0).get("addressLastName"));
            createAccountPageDemo.enterCompany(driver, userRegistrationDataAsMap.get(0).get("company"));
            createAccountPageDemo.enterAddress1(driver, userRegistrationDataAsMap.get(0).get("addressLine1"));
            createAccountPageDemo.enterAddress2(driver, userRegistrationDataAsMap.get(0).get("addressLine2"));
            createAccountPageDemo.enterCity(driver, userRegistrationDataAsMap.get(0).get("city"));
            createAccountPageDemo.selectState(driver, userRegistrationDataAsMap.get(0).get("state"));
            createAccountPageDemo.enterZipCode(driver, userRegistrationDataAsMap.get(0).get("zipCode"));
            createAccountPageDemo.selectCountry(driver, userRegistrationDataAsMap.get(0).get("country"));
            createAccountPageDemo.enterHomePhone(driver, userRegistrationDataAsMap.get(0).get("homePhone"));
            createAccountPageDemo.enterMobilePhone(driver, userRegistrationDataAsMap.get(0).get("mobilePhone"));
            createAccountPageDemo.enterAddressAlias(driver, userRegistrationDataAsMap.get(0).get("addressAlias"));

            //Click on Register button
            createAccountPageDemo.clickOnRegisterButton(driver);
            if (GlobalConfiguration.TAKE_SCREENSHOT_FOR_EACH_STEP.equalsIgnoreCase("Yes")) {
                ReusableCommonMethods.takeScreenshot(driver, Hooks.currentScenario);
            }

        } catch (Exception e) {
            Assert.fail("Exception occurred while entering all the user registration details : "+e);
        }
    }

    @Then("the registration should be successful")
    public void the_registration_should_be_successful() {
        Assert.fail("Test Failure");
        /*MyAccount_Demo myAccountObject = new MyAccount_Demo(driver);
        myAccountObject.verifyMyAccountPageDisplayed(driver);*/
    }

    @Then("this is a background step")
    public void backgroundStep() {
        System.out.println("Background Step");
        /*MyAccount_Demo myAccountObject = new MyAccount_Demo(driver);
        myAccountObject.verifyMyAccountPageDisplayed(driver);*/
    }
}
