package stepdefinition;


import com.report.GenerateReport;
import common.action.GlobalConfiguration;
import common.action.MariaDBConnection;
import common.action.ReusableCommonMethods;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.Assert;
import org.junit.Assume;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.URL;
import java.nio.file.NoSuchFileException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Hooks {

    public static WebDriver driver;
    public static Scenario currentScenario;

    @Before
    public void launchBrowser(Scenario scenario) {

        try {
            //check if the rerunFile is given in command line
            if (System.getProperty("rerunFile") != null && !System.getProperty("rerunFile").isEmpty()) {
                JSONParser parser = new JSONParser();
                Object obj = null;
                //Validate if the rerunFile given is valid or not, if not fail the Test Case
                try {
                    obj = parser.parse(new FileReader(System.getProperty("user.dir") +
                            "\\src\\test\\resources\\FailedScenarios\\" + System.getProperty("rerunFile")));
                } catch (NoSuchFileException NE) {
                    Assert.fail("Rerun file is given in command line, but no such file exists in " +
                            "\\src\\test\\resources\\FailedScenarios folder");
                }
                //if the rerunFile is valid then parse the file and get the scenarios
                JSONObject failedScenarios = (JSONObject) obj;
                //Validate is the current scenario is present in the rerunFile, if present only then run
                //else skip the test case
                if ((validateCurrentScenarioPresentInFailureScenarioList
                        (System.getProperty("user.dir") + "\\src\\test\\resources\\FailedScenarios\\" +
                                System.getProperty("rerunFile"), scenario.getName().trim(), failedScenarios)) == false) {
                    //Code to skip the test case
                    Assume.assumeTrue("Skipping the scenario", false);
                }
                //Check if rerunKey is given in the command line
            } else if (System.getProperty("rerunKey") != null && !System.getProperty("rerunKey").isEmpty()) {
                String query = "SELECT SCENARIO_NAME FROM " + GenerateReport.failedScenariosTableName + " " +
                        " WHERE RERUN_KEY = '" + System.getProperty("rerunKey") + "';";
                //Fetch the failed scenarios from Maria DB using the rerunKey
                HashMap<String, String> failedScenarios = MariaDBConnection.getFailedScenariosByRerunKey(query);
                //Validate is the current scenario is present in the rerunFile, if present only then run
                //else skip the test case
                if (failedScenarios.get(scenario.getName().trim()) == null) {
                    Assume.assumeTrue("Skipping the scenario", false);
                }
            }
            if (driver != null) {
                driver = null;
            }
            currentScenario = scenario;
            String URL = null;
            String browser = null;
            //Initialize all the global configuration by calling the constructor of GlobalConfiguration class
            GlobalConfiguration globalObject = new GlobalConfiguration();
            //Read environment property file in which we need to mention URL and Browser and other environment
            //related factors
            Properties prop = readEnvironmentFile();
            //if the environment given is not present, fail the test case
            if (prop == null) {
                Assert.fail("No environment is provided in command line or if provided it's not matching with any " +
                        "properties file at \\src\\test\\resources\\environments location");
            } else {
                //Load URL, Browser and other details if needed. Add code to read other properties.
                URL = prop.getProperty("URL");
                browser = prop.getProperty("BROWSER");
                prop.clear();
            }
            System.out.println("Executing Hooks");
            //Launch browser
            if (driver == null) {
                if (browser.equalsIgnoreCase("chrome")) {
                    stepsToLaunchChromeBrowser(URL);
                } else if (browser.equalsIgnoreCase("Firefox")) {
                    stepsToLaunchFirefoxBrowser(URL);
                } else if (browser.equalsIgnoreCase("Edge")) {
                    stepsToLaunchEdgeBrowser(URL);
                    //Supported browsers are Chrome,Firefox and Edge
                    //In case of random execution, any of the browser will be picked up
                } else if (browser.equalsIgnoreCase("random")) {
                    String[] supportedBrowsers = {"Chrome", "Firefox", "Edge"};
                    String selectedBrowser = supportedBrowsers[ReusableCommonMethods.
                            getRandomIntegerBetweenZeroAndGivenMaxInteger(supportedBrowsers.length - 1)];
                    System.out.println("Browser Selected : " + selectedBrowser);
                    if (selectedBrowser.equalsIgnoreCase("Chrome")) {
                        stepsToLaunchChromeBrowser(URL);
                    } else if (selectedBrowser.equalsIgnoreCase("Firefox")) {
                        stepsToLaunchFirefoxBrowser(URL);
                    } else {
                        stepsToLaunchEdgeBrowser(URL);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Exception occurred while launching browser : " + e);
        }
    }

    @After
    public void closeBrowser(Scenario sc) {
        try {
            //if scenario is failed and driver is not null, then capture screenshot
            if (sc.isFailed()) {
                if (GlobalConfiguration.TAKE_SCREENSHOT_ON_FAILURE.equalsIgnoreCase("Yes")
                        && driver != null) {
                    ReusableCommonMethods.takeScreenshot(driver, sc);
                }
            }
        } catch (Exception e) {
            System.out.println("Exception occurred : " + e);
        }
        if (driver != null) {
            driver.quit();
        }
    }

    /**
     * This method will read the properties file based on the environment provided in command line
     *
     * @return Properties
     */
    public Properties readEnvironmentFile() {
        try {
            String environment = System.getProperty("env");
            if (environment != null) {
                FileInputStream fis = null;
                Properties prop = null;
                try {
                    fis = new FileInputStream(System.getProperty("user.dir") +
                            "\\src\\test\\resources\\environments\\" + environment + ".properties");
                    prop = new Properties();
                    prop.load(fis);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (fis != null) {
                        fis.close();
                    }
                }
                return prop;
            } else {
                return null;
            }
        } catch (Exception e) {
            System.out.println("Exception occurred while reading property file : " + e);
            return null;
        }
    }

    /**
     * This method will launch Chrome browser locally
     *
     * @return WebDriver
     */
    public WebDriver launchChromeBrowser() {
        try {
            //Commenting out as for Selenium Manager it's not required
            /*System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir") +
                    "\\src\\test\\resources\\drivers\\chromedriver\\chromedriver.exe");*/
            driver = new ChromeDriver();
            driver.manage().window().maximize();
            return driver;
        } catch (Exception e) {
            System.out.println("Exception occurred while launching chrome browser : " + e);
            return null;
        }
    }

    /**
     * This method will launch a Firefox Browser Locally
     *
     * @return WebDriver
     */

    public WebDriver launchFirefoxBrowser() {
        try {
            System.setProperty("webdriver.gecko.driver", System.getProperty("user.dir") +
                    "\\src\\test\\resources\\drivers\\firefoxdriver\\geckodriver.exe");
            FirefoxOptions options = new FirefoxOptions();
            String strFFBinaryPath = "C:\\Program Files (x86)\\Mozilla Firefox\\firefox.exe";
            options.setBinary(strFFBinaryPath);
            driver = new FirefoxDriver(options);
            driver.manage().window().maximize();
            return driver;
        } catch (Exception e) {
            System.out.println("Exception occurred while launching firefox browser : " + e);
            return null;
        }
    }

    /**
     * This method will launch an Edge Browser locally
     *
     * @return WebDriver
     */

    public WebDriver launchEdgeBrowser() {
        try {
            System.setProperty("webdriver.edge.driver", System.getProperty("user.dir") +
                    "\\src\\test\\resources\\drivers\\edgedriver\\msedgedriver.exe");
            // Instantiate a EdgeDriver class.
            WebDriver driver = new EdgeDriver();
            // Maximize the browser
            driver.manage().window().maximize();
            return driver;
        } catch (Exception e) {
            System.out.println("Exception occurred while launching edge browser : " + e);
            return null;
        }
    }

    /**
     * This method will launch a Chrome browser on Selenium Grid
     *
     * @return WebDriver
     */
    public WebDriver launchRemoteChromeDriver() {
        try {
            String nodeURL = GlobalConfiguration.GRID_URL;
            ChromeOptions chromeOptions = new ChromeOptions();
            chromeOptions.setCapability("platformName", "Windows 10");
            WebDriver driver = new RemoteWebDriver(new URL(nodeURL), chromeOptions);
            driver.manage().window().maximize();
            return driver;
        } catch (Exception e) {
            System.out.println("Exception occurred while launching remote browser : " + e);
            return null;
        }

    }

    /**
     * This method will launch an Edge browser on Selenium Grid
     *
     * @return WebDriver
     */

    public WebDriver launchRemoteEdgeDriver() {
        try {
            String nodeURL = GlobalConfiguration.GRID_URL;
            EdgeOptions options = new EdgeOptions();
            //options.setCapability("platformName", "Windows 10");
            WebDriver driver = new RemoteWebDriver(new URL(nodeURL), options);
            driver.manage().window().maximize();
            return driver;
        } catch (Exception e) {
            System.out.println("Exception occurred while launching remote browser : " + e);
            return null;
        }
    }

    /**
     * This method will launch a Firefox browser on Selenium Grid
     *
     * @return WebDriver
     */

    public WebDriver launchRemoteFirefoxDriver() {
        try {
            String nodeURL = GlobalConfiguration.GRID_URL;
            FirefoxOptions options = new FirefoxOptions();
            String strFFBinaryPath = "C:\\Program Files (x86)\\Mozilla Firefox\\firefox.exe";
            options.setBinary(strFFBinaryPath);
            //options.setCapability("platformName", "Windows 10");
            WebDriver driver = new RemoteWebDriver(new URL(nodeURL), options);
            driver.manage().window().maximize();
            return driver;
        } catch (Exception e) {
            System.out.println("Exception occurred while launching remote browser : " + e);
            return null;
        }
    }

    /**
     * This method will launch a Chrome browser on Sauce Lab
     *
     * @return WebDriver
     */

    public WebDriver launchChromeOnSauceLab() {
        try {
            ChromeOptions browserOptions = new ChromeOptions();
            browserOptions.setPlatformName("Windows 11");
            browserOptions.setBrowserVersion("latest");
            Map<String, Object> sauceOptions = new HashMap<>();
            sauceOptions.put("build", "Test Build");
            sauceOptions.put("name", "Sample Test");
            browserOptions.setCapability("sauce:options", sauceOptions);
            URL url = new URL(GlobalConfiguration.SAUCE_LAB_URL);
            driver = new RemoteWebDriver(url, browserOptions);
            return driver;
        } catch (Exception e) {
            System.out.println("Exception occurred : " + e);
            return null;
        }
    }

    /**
     * This method will launch an Edge browser on Sauce Lab
     *
     * @return WebDriver
     */

    public WebDriver launchEdgeOnSauceLab() {
        try {
            EdgeOptions browserOptions = new EdgeOptions();
            browserOptions.setPlatformName("Windows 11");
            browserOptions.setBrowserVersion("latest");
            Map<String, Object> sauceOptions = new HashMap<>();
            sauceOptions.put("build", "Test Build");
            sauceOptions.put("name", "Sample Test");
            browserOptions.setCapability("sauce:options", sauceOptions);
            URL url = new URL(GlobalConfiguration.SAUCE_LAB_URL);
            driver = new RemoteWebDriver(url, browserOptions);
            return driver;
        } catch (Exception e) {
            System.out.println("Exception occurred : " + e);
            return null;
        }
    }

    /**
     * This method will launch a Firefox browser on Sauce Lab
     *
     * @return WebDriver
     */

    public WebDriver launchFirefoxOnSauceLab() {
        try {
            FirefoxOptions browserOptions = new FirefoxOptions();
            browserOptions.setPlatformName("Windows 11");
            browserOptions.setBrowserVersion("latest");
            Map<String, Object> sauceOptions = new HashMap<>();
            sauceOptions.put("build", "Test Build");
            sauceOptions.put("name", "Sample Test");
            browserOptions.setCapability("sauce:options", sauceOptions);
            URL url = new URL(GlobalConfiguration.SAUCE_LAB_URL);
            driver = new RemoteWebDriver(url, browserOptions);
            return driver;
        } catch (Exception e) {
            System.out.println("Exception occurred : " + e);
            return null;
        }
    }

    /**
     * This method will launch Chrome browser based on the Execution Type (Local,Remote,SauceLab)
     *
     * @param URL
     */
    public void stepsToLaunchChromeBrowser(String URL) {
        if (GlobalConfiguration.EXECUTION_TYPE.equalsIgnoreCase("REMOTE")) {
            driver = launchRemoteChromeDriver();
        } else if (GlobalConfiguration.EXECUTION_TYPE.equalsIgnoreCase("SAUCELAB")) {
            driver = launchChromeOnSauceLab();
        } else {
            driver = launchChromeBrowser();
        }
        driver.get(URL);
    }

    /**
     * This method will launch Firefox browser based on the Execution Type (Local,Remote,SauceLab)
     *
     * @param URL
     */
    public void stepsToLaunchFirefoxBrowser(String URL) {
        if (GlobalConfiguration.EXECUTION_TYPE.equalsIgnoreCase("REMOTE")) {
            driver = launchRemoteFirefoxDriver();
        } else if (GlobalConfiguration.EXECUTION_TYPE.equalsIgnoreCase("SAUCELAB")) {
            System.out.println("Launching firefox browser on Sauce Lab");
            driver = launchFirefoxOnSauceLab();
        } else {
            driver = launchFirefoxBrowser();
        }
        driver.get(URL);
    }

    /**
     * This method will launch Edge browser based on the Execution Type (Local,Remote,SauceLab)
     *
     * @param URL
     */
    public void stepsToLaunchEdgeBrowser(String URL) {
        if (GlobalConfiguration.EXECUTION_TYPE.equalsIgnoreCase("REMOTE")) {
            driver = launchRemoteEdgeDriver();
        } else if (GlobalConfiguration.EXECUTION_TYPE.equalsIgnoreCase("SAUCELAB")) {
            System.out.println("Launching edge browser on Sauce Lab");
            driver = launchEdgeOnSauceLab();
        } else {
            driver = launchEdgeBrowser();
        }
        driver.get(URL);
    }

    /**
     * This method will check if the current scenario is present in failure list in case rerun of failure is
     * selected from failure JSON file
     *
     * @param failedScenarioFileName
     * @param currentScenario
     * @param failedScenarios
     * @return boolean
     */
    public boolean validateCurrentScenarioPresentInFailureScenarioList
    (String failedScenarioFileName, String currentScenario, JSONObject failedScenarios) {
        try {
            if (failedScenarios.get(currentScenario) != null &&
                    failedScenarios.get(currentScenario).toString().trim().equalsIgnoreCase("Failed")) {
                System.out.println("Scenario " + currentScenario + " is present");
                return true;
            } else {
                System.out.println("Scenario " + currentScenario + " is not present");
                return false;
            }
        } catch (Exception e) {
            System.out.println("Exception occurred while reading failed scenarios : " + e);
            return false;
        }
    }

}
