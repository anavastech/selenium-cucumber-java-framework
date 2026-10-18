package com.qaframework.pages;

import com.qaframework.config.Config;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Base de todos los page objects: centraliza las esperas explícitas para que ninguna página use
 * Thread.sleep ni dependa de implicit waits. Los page objects exponen acciones y lecturas; los
 * asserts viven en los steps.
 */
public abstract class BasePage {

  protected final WebDriver driver;
  protected final WebDriverWait wait;

  protected BasePage(WebDriver driver) {
    this.driver = driver;
    this.wait = new WebDriverWait(driver, Config.explicitWait());
  }

  public String currentUrl() {
    return driver.getCurrentUrl();
  }

  protected WebElement visible(By locator) {
    return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
  }

  /**
   * Click con reintento: el menú lateral de SauceDemo tiene animación de apertura y React re-renderiza
   * botones al cambiar de estado, así que un click puede caer sobre un elemento en movimiento o ya
   * reemplazado. Reintentamos hasta el timeout en vez de fallar al primer intento.
   */
  protected void click(By locator) {
    wait.ignoring(ElementClickInterceptedException.class)
        .ignoring(StaleElementReferenceException.class)
        .until(
            d -> {
              WebElement element = ExpectedConditions.elementToBeClickable(locator).apply(d);
              if (element == null) {
                return false;
              }
              element.click();
              return true;
            });
  }

  protected void type(By locator, String text) {
    WebElement element = visible(locator);
    element.clear();
    element.sendKeys(text);
  }

  protected String text(By locator) {
    return visible(locator).getText().trim();
  }

  protected boolean isPresent(By locator) {
    return !driver.findElements(locator).isEmpty();
  }

  protected boolean isDisplayed(By locator) {
    List<WebElement> elements = driver.findElements(locator);
    return !elements.isEmpty() && elements.getFirst().isDisplayed();
  }

  protected List<String> texts(By locator) {
    return driver.findElements(locator).stream().map(e -> e.getText().trim()).toList();
  }

  protected void waitForUrlContaining(String fragment) {
    wait.until(ExpectedConditions.urlContains(fragment));
  }

  /** Slug que SauceDemo usa en sus data-test: "Sauce Labs Bike Light" -> "sauce-labs-bike-light". */
  protected static String slug(String productName) {
    return productName.trim().toLowerCase().replaceAll("[^a-z0-9.()]+", "-");
  }
}
