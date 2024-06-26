package com.br.luminous.system;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class DeviceSystemTest {

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
        WebElement devices = driver.findElement(By.id("devices")).findElement(By.tagName("a"));
        devices.click();
    }

    /*
     * CT038 - Usuário informa todos os campos válidos
     */
    @Test
    public void shouldCreateDeviceGivenValidRequest() {
        List<WebElement> devicesList = driver.findElements(By.cssSelector("ul.default-item-list li"));
        var devicesQtd = devicesList .size();

        WebElement btnCadastrar = driver.findElement(By.className("btn-cadastrar"));
        btnCadastrar.click();

        WebElement nameInput = driver.findElement(By.id("name"));
        nameInput.sendKeys("Computador Desktop");
        WebElement powerInput = driver.findElement(By.id("power"));
        powerInput.sendKeys("700");
        WebElement timeInput = driver.findElement(By.id("usageTime"));
        timeInput.clear();
        timeInput.sendKeys("08:00");
        WebElement submitButton = driver.findElement(By.className("primary-button"));
        submitButton.click();

        String confirmRegisterDeviceUrl = this.currentTestFrontEndUrl + "devices/?address=" + this.currentTestAddressId;
        wait.until(ExpectedConditions.urlToBe(confirmRegisterDeviceUrl));
        assertEquals(driver.getCurrentUrl(), confirmRegisterDeviceUrl);
        List<WebElement> confirmDeviceList = wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(By.cssSelector(
                "ul.default-item-list li"), devicesQtd));
        int devicesFinalCount = confirmDeviceList .size();
        assertEquals(devicesQtd+1, devicesFinalCount);
    }

    /*
    * CT039 - Usuário informa campos nulos na criação de um equipamento
    */
    @Test
    public void shouldNotCreateDeviceGivenNullFields() {
        WebElement btnCadastrar = driver.findElement(By.className("btn-cadastrar"));
        btnCadastrar.click();

        WebElement nameInput = driver.findElement(By.id("name"));
        nameInput.sendKeys(" ");
        WebElement powerInput = driver.findElement(By.id("power"));
        powerInput.sendKeys(" ");
        WebElement timeInput = driver.findElement(By.id("usageTime"));
        timeInput.clear();
        timeInput.sendKeys(" ");
        WebElement submitButton = driver.findElement(By.className("primary-button"));
        submitButton.click();

        String confirmRegisterDeviceUrl = this.currentTestFrontEndUrl + "devices/?address=" + this.currentTestAddressId;
        assertNotEquals(driver.getCurrentUrl(), confirmRegisterDeviceUrl);
    }
}
