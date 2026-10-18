package com.qaframework.pages;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class CheckoutPage extends BasePage {

  private static final By FIRST_NAME = By.cssSelector("[data-test='firstName']");
  private static final By LAST_NAME = By.cssSelector("[data-test='lastName']");
  private static final By POSTAL_CODE = By.cssSelector("[data-test='postalCode']");
  private static final By CONTINUE = By.cssSelector("[data-test='continue']");
  private static final By CANCEL = By.cssSelector("[data-test='cancel']");
  private static final By FINISH = By.cssSelector("[data-test='finish']");
  private static final By COMPLETE_HEADER = By.cssSelector("[data-test='complete-header']");
  private static final By SUBTOTAL = By.cssSelector("[data-test='subtotal-label']");
  private static final By TAX = By.cssSelector("[data-test='tax-label']");
  private static final By TOTAL = By.cssSelector("[data-test='total-label']");

  // Los labels vienen como "Item total: $39.98", "Tax: $3.20", "Total: $43.18".
  private static final Pattern AMOUNT = Pattern.compile("\\$([\\d.]+)");

  public CheckoutPage(WebDriver driver) {
    super(driver);
  }

  public CheckoutPage waitForInformationStep() {
    waitForUrlContaining("checkout-step-one.html");
    visible(FIRST_NAME);
    return this;
  }

  public void fillInfo(String firstName, String lastName, String postalCode) {
    type(FIRST_NAME, firstName);
    type(LAST_NAME, lastName);
    type(POSTAL_CODE, postalCode);
  }

  public void continueToOverview() {
    click(CONTINUE);
  }

  public CheckoutPage waitForOverview() {
    waitForUrlContaining("checkout-step-two.html");
    visible(TOTAL);
    return this;
  }

  public void finish() {
    click(FINISH);
  }

  public void cancel() {
    click(CANCEL);
  }

  public boolean isOrderCompleteDisplayed() {
    return isDisplayed(COMPLETE_HEADER);
  }

  public String completeHeader() {
    return text(COMPLETE_HEADER);
  }

  public BigDecimal subtotal() {
    return amountOf(SUBTOTAL);
  }

  public BigDecimal tax() {
    return amountOf(TAX);
  }

  public BigDecimal total() {
    return amountOf(TOTAL);
  }

  private BigDecimal amountOf(By locator) {
    String label = text(locator);
    Matcher matcher = AMOUNT.matcher(label);
    if (!matcher.find()) {
      throw new IllegalStateException("No se encontró un monto en \"" + label + "\"");
    }
    return new BigDecimal(matcher.group(1));
  }
}
