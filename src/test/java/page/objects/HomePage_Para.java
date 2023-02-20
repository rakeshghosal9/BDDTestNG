package page.objects;

import common.action.ReusableCommonMethods;
import org.junit.Assert;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class HomePage_Para {

    WebDriver driver;

    @FindBy(xpath = "//a[text()='Register']")
    WebElement registerLink;

    public HomePage_Para(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public void clickOnRegisterLink(WebDriver driver) {
        try {
            Assert.assertTrue("Register Link is not clicked",
                    ReusableCommonMethods.clickOnWebElement(driver, registerLink));

        } catch (Exception e) {
            Assert.fail("Exception occurred while clicking on Register Link on Home Page : "+e);
        }
    }
}
