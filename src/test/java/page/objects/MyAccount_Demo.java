package page.objects;

import common.action.ReusableCommonMethods;
import org.junit.Assert;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class MyAccount_Demo {

    WebDriver driver;

    @FindBy(xpath = "//h1[@class='page-heading']")
    WebElement myAccountHeader;

    public MyAccount_Demo(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public void verifyMyAccountPageDisplayed(WebDriver driver) {
        try {
            Assert.assertTrue("My Account Page is not displayed",
                    ReusableCommonMethods.waitForElementToBeVisible(myAccountHeader, driver, 60));
            System.out.println("My account page displayed successfully");

        } catch (Exception e) {
            Assert.fail("Exception occurred while validating my account page is displayed or not : " + e);

        }
    }
}
