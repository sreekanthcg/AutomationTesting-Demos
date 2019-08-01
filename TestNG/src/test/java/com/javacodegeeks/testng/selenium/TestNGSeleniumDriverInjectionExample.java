package com.javacodegeeks.testng.selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;


@ContextConfiguration("driver_context.xml")
public class TestNGSeleniumDriverInjectionExample extends AbstractTestNGSpringContextTests {
	@Autowired
	private WebDriver driver;
	
	@BeforeClass
	public void printBrowserUsed() {
		System.out.println("Driver used is: " + driver);
	}

	@Test
	public void searchGoogle() {
		final String searchKey = "TestNG";
		System.out.println("Search " + searchKey + " in google");
		driver.navigate().to("http://www.google.com");
		WebElement element = driver.findElement(By.name("q"));
		System.out.println("Enter " + searchKey);
		element.sendKeys(searchKey);
		System.out.println("submit");
		element.submit();
		(new WebDriverWait(driver, 10)).until(new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver d) {
				return d.getTitle().toLowerCase()
						.startsWith(searchKey.toLowerCase());
			}
		});
		System.out.println("Got " + searchKey + " results");
	}

	@AfterSuite
	public void quitDriver() throws Exception {
		driver.quit();
	}
}
