package page.objects;

import common.action.ReusableCommonMethods;
import org.junit.Assert;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class HomePage_Demo {

    WebDriver driver;

    @FindBy(xpath = "//a[@title='Log in to your customer account']")
    WebElement signInLink;

    public HomePage_Demo(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public void clickOnSignInLink(WebDriver driver) {
        try {
            Assert.assertTrue("SignIn Link is not clicked",
                    ReusableCommonMethods.clickOnWebElement(driver, signInLink));

        } catch (Exception e) {
            Assert.fail("Exception occurred while clicking on Signin Link on Home Page : "+e);

        }
    }
}
