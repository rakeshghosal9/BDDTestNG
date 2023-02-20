package page.objects;

import common.action.ReusableCommonMethods;
import org.junit.Assert;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class UserRegistration_Para {

    WebDriver driver;

    public UserRegistration_Para(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    @FindBy(xpath = "//input[@id='customer.firstName']")
    WebElement customerFirstName;

    @FindBy(xpath = "//input[@id='customer.lastName']")
    WebElement customerLastName;

    @FindBy(xpath = "//input[@id='customer.password']")
    WebElement password;

    @FindBy(xpath = "//input[@id='customer.address.street']")
    WebElement address1;

    @FindBy(xpath = "//input[@id='customer.address.city']")
    WebElement city;

    @FindBy(xpath = "//input[@id='customer.address.state']")
    WebElement state;

    @FindBy(xpath = "//input[@id='customer.address.zipCode']")
    WebElement postCode;

    @FindBy(xpath = "//input[@id='customer.phoneNumber']")
    WebElement mobilePhoneNumber;

    @FindBy(xpath = "//input[@id='customer.ssn']")
    WebElement SSN;

    @FindBy(xpath = "//input[@id='customer.username']")
    WebElement username;

    @FindBy(xpath = "//input[@id='repeatedPassword']")
    WebElement confirmPassword;

    @FindBy(xpath = "//input[@type='submit'][@value='Register']")
    WebElement registerButton;

    public void enterFirstName(WebDriver driver, String firstName) {
        Assert.assertTrue("First Name is not entered successfully",
                ReusableCommonMethods.enterValueInTextBox(customerFirstName, firstName, driver));
    }

    public void enterLastName(WebDriver driver, String lastName) {
        Assert.assertTrue("Last Name is not entered successfully",
                ReusableCommonMethods.enterValueInTextBox(customerLastName, lastName, driver));
    }

    public void enterPassword(WebDriver driver, String passwordValue) {
        Assert.assertTrue("Password not entered successfully",
                ReusableCommonMethods.enterValueInTextBox(password, passwordValue, driver));
    }

    public void enterAddress1(WebDriver driver, String address1Value) {
        Assert.assertTrue("Address 1 not entered successfully",
                ReusableCommonMethods.enterValueInTextBox(address1, address1Value, driver));
    }

    public void enterCity(WebDriver driver, String cityValue) {
        Assert.assertTrue("City not entered successfully",
                ReusableCommonMethods.enterValueInTextBox(city, cityValue, driver));
    }

    public void selectState(WebDriver driver, String stateValue) {
        Assert.assertTrue("State not entered successfully",
                ReusableCommonMethods.enterValueInTextBox(state, stateValue, driver));
    }

    public void enterZipCode(WebDriver driver, String zipValue) {
        Assert.assertTrue("Zip Code not entered successfully",
                ReusableCommonMethods.enterValueInTextBox(postCode, zipValue, driver));
    }

    public void enterMobilePhone(WebDriver driver, String mobilePhoneValue) {
        Assert.assertTrue("Mobile Phone not entered successfully",
                ReusableCommonMethods.enterValueInTextBox(mobilePhoneNumber, mobilePhoneValue, driver));
    }

    public void clickOnRegisterButton(WebDriver driver)
    {
        Assert.assertTrue("Register button is not clicked",
                ReusableCommonMethods.clickOnWebElement(driver, registerButton));
    }

    public void enterSSN(WebDriver driver, String SSNValue) {
        Assert.assertTrue("SSN not entered successfully",
                ReusableCommonMethods.enterValueInTextBox(SSN, SSNValue, driver));
    }

    public void enterConfirmPassword(WebDriver driver, String passwordValue) {
        Assert.assertTrue("Confirm Password not entered successfully",
                ReusableCommonMethods.enterValueInTextBox(confirmPassword, passwordValue, driver));
    }

    public void enterUsername(WebDriver driver, String userNameValue) {
        Assert.assertTrue("Username not entered successfully",
                ReusableCommonMethods.enterValueInTextBox(username, userNameValue, driver));
    }

}
