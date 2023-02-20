package page.objects;

import common.action.ReusableCommonMethods;
import org.junit.Assert;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class CreateAnAccountPage_Demo {

    WebDriver driver;

    public CreateAnAccountPage_Demo(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    @FindBy(xpath = "//input[@id='id_gender1']")
    WebElement title_Mr;

    @FindBy(xpath = "//input[@id='id_gender2']")
    WebElement title_Mrs;

    @FindBy(xpath = "//input[@id='customer_firstname']")
    WebElement customerFirstName;

    @FindBy(xpath = "//input[@id='customer_lastname']")
    WebElement customerLastName;

    @FindBy(xpath = "//input[@id='passwd']")
    WebElement password;

    @FindBy(xpath = "//select[@id='days']")
    WebElement dob_days;

    @FindBy(xpath = "//select[@id='months']")
    WebElement dob_months;

    @FindBy(xpath = "//select[@id='years']")
    WebElement dob_years;

    @FindBy(xpath = "//input[@id='firstname']")
    WebElement addressFirstName;

    @FindBy(xpath = "//input[@id='lastname']")
    WebElement addressLastName;

    @FindBy(xpath = "//input[@id='company']")
    WebElement company;

    @FindBy(xpath = "//input[@id='address1']")
    WebElement address1;

    @FindBy(xpath = "//input[@id='address2']")
    WebElement address2;

    @FindBy(xpath = "//input[@id='city']")
    WebElement city;

    @FindBy(xpath = "//select[@id='id_state']")
    WebElement state;

    @FindBy(xpath = "//input[@id='postcode']")
    WebElement postCode;

    @FindBy(xpath = "//select[@id='id_country']")
    WebElement country;

    @FindBy(xpath = "//input[@id='phone']")
    WebElement homePhoneNumber;

    @FindBy(xpath = "//input[@id='phone_mobile']")
    WebElement mobilePhoneNumber;

    @FindBy(xpath = "//input[@id='alias']")
    WebElement addressAlias;

    @FindBy(xpath = "//button[@id='submitAccount']")
    WebElement registerButton;

    public void selectTitle(WebDriver driver, String title) {
        if (title.equalsIgnoreCase("Mr.")) {
            ReusableCommonMethods.clickOnWebElement(driver, title_Mr);
        } else if (title.equalsIgnoreCase("Mrs.")) {
            ReusableCommonMethods.clickOnWebElement(driver, title_Mrs);

        } else {
            Assert.fail("Title [" + title + "] is not available as option, valid values are [Mr. and Mrs.]");
        }
    }

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

    public void enterDateOfBirth(WebDriver driver, String dob) {
        String[] dobDetails = dob.split("-");
        Assert.assertTrue("Day of date of birth is not selected",
                ReusableCommonMethods.selectDropdownValue(dob_days,dobDetails[0].trim()));
        Assert.assertTrue("Day of date of birth is not selected",
                ReusableCommonMethods.selectDropdownValue(dob_months,dobDetails[1].trim()));
        Assert.assertTrue("Day of date of birth is not selected",
                ReusableCommonMethods.selectDropdownValue(dob_years,dobDetails[2].trim()));
    }

    public void enterAddressFirstName(WebDriver driver, String addressFirstNameValue) {
        Assert.assertTrue("Address First Name not entered successfully",
                ReusableCommonMethods.enterValueInTextBox(addressFirstName, addressFirstNameValue, driver));
    }

    public void enterAddressLastName(WebDriver driver, String addressLastNameValue) {
        Assert.assertTrue("Address Last Name not entered successfully",
                ReusableCommonMethods.enterValueInTextBox(addressLastName, addressLastNameValue, driver));
    }

    public void enterCompany(WebDriver driver, String companyValue) {
        Assert.assertTrue("Company not entered successfully",
                ReusableCommonMethods.enterValueInTextBox(company, companyValue, driver));
    }

    public void enterAddress1(WebDriver driver, String address1Value) {
        Assert.assertTrue("Address 1 not entered successfully",
                ReusableCommonMethods.enterValueInTextBox(address1, address1Value, driver));
    }

    public void enterAddress2(WebDriver driver, String address2Value) {
        Assert.assertTrue("Address 2 not entered successfully",
                ReusableCommonMethods.enterValueInTextBox(address2, address2Value, driver));
    }

    public void enterCity(WebDriver driver, String cityValue) {
        Assert.assertTrue("City not entered successfully",
                ReusableCommonMethods.enterValueInTextBox(city, cityValue, driver));
    }

    public void selectState(WebDriver driver, String stateValue) {
        Assert.assertTrue("State not entered successfully",
                ReusableCommonMethods.selectDropdownValue(state,stateValue));
    }

    public void enterZipCode(WebDriver driver, String zipValue) {
        Assert.assertTrue("Zip Code not entered successfully",
                ReusableCommonMethods.enterValueInTextBox(postCode, zipValue, driver));
    }

    public void selectCountry(WebDriver driver, String countryValue) {
        Assert.assertTrue("Country not entered successfully",
                ReusableCommonMethods.selectDropdownValue(country,countryValue));
    }

    public void enterHomePhone(WebDriver driver, String homePhoneValue) {
        Assert.assertTrue("Home Phone not entered successfully",
                ReusableCommonMethods.enterValueInTextBox(homePhoneNumber, homePhoneValue, driver));
    }

    public void enterMobilePhone(WebDriver driver, String mobilePhoneValue) {
        Assert.assertTrue("Mobile Phone not entered successfully",
                ReusableCommonMethods.enterValueInTextBox(mobilePhoneNumber, mobilePhoneValue, driver));
    }

    public void enterAddressAlias(WebDriver driver, String addressAliasValue) {
        Assert.assertTrue("Address Alias Value not entered successfully",
                ReusableCommonMethods.enterValueInTextBox(addressAlias, addressAliasValue, driver));
    }

    public void clickOnRegisterButton(WebDriver driver)
    {
        Assert.assertTrue("Register button is not clicked",
                ReusableCommonMethods.clickOnWebElement(driver, registerButton));
    }

}
