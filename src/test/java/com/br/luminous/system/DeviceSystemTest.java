package com.br.luminous.system;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class DeviceSystemTest {

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    void setup() {
        WebDriverManager.firefoxdriver().setup();

        driver = new FirefoxDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10L));
        driver.get("http://localhost:3000/login");

        WebElement emailInput = driver.findElement(By.name("email"));
        WebElement passwordInput = driver.findElement(By.name("password"));

        emailInput.sendKeys("testador@test.com");
        passwordInput.sendKeys("123456");

        WebElement submitButton = driver.findElement(By.className("primary-button"));
        submitButton.click();

        WebElement continueButton = driver.findElement(By.className("continue"));
        continueButton.click();

        WebElement address = driver.findElement(By.id("1"));
        address.click();

        WebElement bills = driver.findElements(By.className("intergration-card")).get(2).findElement(By.tagName("a"));
        bills.click();
    }

    /*
     * CT038 - Usuário informa todos os campos válidos
     */
    @Test
    public void shouldCreateDeviceGivenValidRequest() {

    }

    /*
    * CT039 - Usuário informa campos nulos na criação de um equipamento
    */
    @Test
    public void shouldNotCreateDeviceGivenNullFields() {

    }
}
