package com.crossbrowsertesting.selenium;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.net.MalformedURLException;
import java.net.URL;

/*
 * Selenium Test to CBT using maven
 */
public class MavenTest {
    static String buildName = System.getenv("CBT_BUILD_NAME");
    static String buildNumber = System.getenv("CBT_BUILD_NUMBER");
    static String username = System.getenv("CBT_USERNAME"); // Your username
    static String authkey = System.getenv("CBT_AUTHKEY");  // Your authkey
    static String browserName = System.getenv("CBT_BROWSER");
    static String version = System.getenv("CBT_VERSION");
    static String platform = System.getenv("CBT_PLATFORM");
    static String resolution = System.getenv("CBT_RESOLUTION");

    String testScore = "unset";
    DesiredCapabilities caps;
    RemoteWebDriver driver;

    @Before
    public void setup() throws MalformedURLException {
        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setCapability("name", buildName);
        caps.setCapability("build", buildNumber);
        caps.setCapability("browserName", browserName);
        caps.setCapability("platform", platform);
        caps.setCapability("version", version);
        caps.setCapability("screen_resolution", resolution);
        caps.setCapability("record_network", "false");

        driver = new RemoteWebDriver(new URL("http://" + username + ":" + authkey +"@hub.crossbrowsertesting.com:80/wd/hub"), caps);
    }
    @Test
    public void runSeleniumTest() throws UnirestException {
        System.out.println(driver.getSessionId());
        try {
            // load the page url
            System.out.println("Loading Url");
            driver.get("http://crossbrowsertesting.github.io/login-form.html");

            // maximize the window - DESKTOPS ONLY
            //System.out.println("Maximizing window");
            //driver.manage().window().maximize();

            // complete a short login form
            // first by entering the username
            System.out.println("Entering username");
            driver.findElementByName("username").sendKeys("tester@crossbrowsertesting.com");

            // then by entering the password
            System.out.println("Entering password");
            driver.findElementByName("password").sendKeys("test123");

            // then by clicking the login button
            System.out.println("Logging in");
            driver.findElementByCssSelector("div.form-actions > button").click();

            // let's wait here to ensure the page has loaded completely
            WebDriverWait wait = new WebDriverWait(driver, 10);
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"logged-in-message\"]/h2")));

            // Let's assert that the welcome message is present on the page.
            // if not, an exception will be raised and we'll set the score to fail in the catch block.
            String welcomeMessage = driver.findElementByXPath("//*[@id=\"logged-in-message\"]/h2").getText();
            Assert.assertEquals("Welcome tester@crossbrowsertesting.com", welcomeMessage);

            // if we get to this point, then all the assertions have passed
            // that means that we can set the score to pass in our system
            testScore = "pass";
        } catch(AssertionError ae) {

            // if we have an assertion error, take a snapshot of where the test fails
            // and set the score to "fail"
            String snapshotHash = takeSnapshot(driver.getSessionId().toString());
            setDescription(driver.getSessionId().toString(), snapshotHash, ae.toString());
            testScore = "fail";
        }
    }
    @After
    public void teardown() throws UnirestException {
        System.out.println("Test complete: " + testScore);

        // here we make an api call to actually send the score
        setScore(driver.getSessionId().toString(), testScore);

        // and quit the driver
        driver.quit();
    }

    public JsonNode setScore(String seleniumTestId, String score) throws UnirestException {
        // Mark a Selenium test as Pass/Fail
        HttpResponse<JsonNode> response = Unirest.put("http://crossbrowsertesting.com/api/v3/selenium/{seleniumTestId}")
                .basicAuth(username, authkey)
                .routeParam("seleniumTestId", seleniumTestId)
                .field("action","set_score")
                .field("score", score)
                .asJson();
        return response.getBody();
    }

    public String takeSnapshot(String seleniumTestId) throws UnirestException {
        /*
         * Takes a snapshot of the screen for the specified test.
         * The output of this function can be used as a parameter for setDescription()
         */
        HttpResponse<JsonNode> response = Unirest.post("http://crossbrowsertesting.com/api/v3/selenium/{seleniumTestId}/snapshots")
                .basicAuth(username, authkey)
                .routeParam("seleniumTestId", seleniumTestId)
                .asJson();
        // grab out the snapshot "hash" from the response
        String snapshotHash = (String) response.getBody().getObject().get("hash");

        return snapshotHash;
    }

    public JsonNode setDescription(String seleniumTestId, String snapshotHash, String description) throws UnirestException{
        /*
         * sets the description for the given seleniemTestId and snapshotHash
         */
        HttpResponse<JsonNode> response = Unirest.put("http://crossbrowsertesting.com/api/v3/selenium/{seleniumTestId}/snapshots/{snapshotHash}")
                .basicAuth(username, authkey)
                .routeParam("seleniumTestId", seleniumTestId)
                .routeParam("snapshotHash", snapshotHash)
                .field("description", description)
                .asJson();
        return response.getBody();
    }
}
