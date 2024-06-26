package com.br.luminous.system;


import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class EnergyBillSystemTest {
    private WebDriver driver;
    private WebDriverWait wait;
    private final String currentTestAddressId = "4";
    private final String currentTestFrontEndUrl = "http://localhost:3000/";
    @BeforeEach
    void setup() {
        WebDriverManager.firefoxdriver().setup();

        driver = new FirefoxDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10L));
        driver.get(this.currentTestFrontEndUrl + "login");

        WebElement emailInput = driver.findElement(By.name("email"));
        WebElement passwordInput = driver.findElement(By.name("password"));

        emailInput.sendKeys("testador@test.com");
        passwordInput.sendKeys("123456");

        WebElement submitButton = driver.findElement(By.className("primary-button"));
        submitButton.click();

        wait.until(ExpectedConditions.urlToBe(this.currentTestFrontEndUrl + "login/tip"));

        WebElement continueButton = driver.findElement(By.className("continue"));
        continueButton.click();

        WebElement address = driver.findElement(By.id(this.currentTestAddressId));
        address.click();

        WebElement bills = driver.findElement(By.id("bills")).findElement(By.tagName("a"));
        bills.click();
    }

    @Test
    public void shouldCreateAEnergyBill() {

        List<WebElement> liList = driver.findElements(By.cssSelector("ul.default-item-list li"));
        var elementsQTD = liList.size();

        WebElement btnCadastrar = driver.findElement(By.className("btn-cadastrar"));
        btnCadastrar.click();

        String billPath = "C:\\Users\\sonho\\Downloads\\fatura_energia.pdf";

        WebElement fileInput = driver.findElement(By.name("documentBillPath"));
        fileInput.sendKeys(billPath);

        WebElement referenceDate = driver.findElement(By.id("referenceDate"));
        referenceDate.sendKeys("2024-04-24");

        WebElement dueDate = driver.findElement(By.id("dueDate"));
        dueDate.sendKeys("2024-05-24");

        WebElement energyConsumptionReais = driver.findElement(By.id("consumptionReais"));
        energyConsumptionReais.sendKeys("100.00");

        WebElement energyConsumption_kWh = driver.findElement(By.id("consumptionkWh"));
        energyConsumption_kWh.sendKeys("200.00");

        WebElement btnFatura = driver.findElement(By.className("btn-fatura"));
        btnFatura.click();

        String registeredUrl = this.currentTestFrontEndUrl + "energyBill/?address=" + this.currentTestAddressId;
        wait.until(ExpectedConditions.urlToBe(registeredUrl));
        assertEquals(driver.getCurrentUrl(), registeredUrl);
        List<WebElement> finalListItems = wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(By.cssSelector(
                "ul.default-item-list li"), elementsQTD));
        int elementsQTDFinal = finalListItems.size();
        assertEquals(elementsQTD+1, elementsQTDFinal);
    }

    @Test
    public void shouldNotCreateAEnergyBillWithoutABillFile() {

        WebElement btnCadastrar = driver.findElement(By.className("btn-cadastrar"));
        btnCadastrar.click();

        WebElement referenceDate = driver.findElement(By.id("referenceDate"));
        referenceDate.sendKeys("2024-04-24");

        WebElement dueDate = driver.findElement(By.id("dueDate"));
        dueDate.sendKeys("2024-05-24");

        WebElement energyConsumptionReais = driver.findElement(By.id("consumptionReais"));
        energyConsumptionReais.sendKeys("100.00");

        WebElement energyConsumption_kWh = driver.findElement(By.id("consumptionkWh"));
        energyConsumption_kWh.sendKeys("200.00");

        WebElement btnFatura = driver.findElement(By.className("btn-fatura"));
        btnFatura.click();
        String registeredUrl = this.currentTestFrontEndUrl + "energyBill/?address=" + this.currentTestAddressId;
        assertNotEquals(driver.getCurrentUrl(), registeredUrl);
    }
}
