package page.objects;

import common.action.ReusableCommonMethods;
import org.junit.Assert;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import java.sql.Driver;

public class SignInPage_Demo {

    public WebDriver driver;

    @FindBy(xpath = "//input[@id='email_create']")
    WebElement createAccountEmailAddress;

    @FindBy(xpath = "//button[@id='SubmitCreate']")
    WebElement createAnAccountButton;

    public SignInPage_Demo(WebDriver driver)
    {
        this.driver = driver;
        //This initElements method will create  all WebElements
        PageFactory.initElements(driver, this);
    }

    public void enterEmailAndCreateAnAccount(WebDriver driver,String emailID) {
        try {
            if(emailID.equalsIgnoreCase("auto-generated"))
            {
                emailID = ReusableCommonMethods.generateRandomAlphabaticString(10);
                emailID = emailID+"@gmail.com";
                System.out.println("Email ID generated : "+emailID);
            }
            Assert.assertTrue("Email ID is not entered in create and account text box",
                    ReusableCommonMethods.enterValueInTextBox(createAccountEmailAddress, emailID, driver));
            Assert.assertTrue("Create an account button is not clicked successfully",
                    ReusableCommonMethods.clickOnWebElement(driver, createAnAccountButton));

        } catch (Exception e) {
            Assert.fail("Exception occurred while entering email and created an account" + e);

        }
    }

}
