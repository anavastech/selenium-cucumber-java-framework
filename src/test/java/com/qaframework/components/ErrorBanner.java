package com.qaframework.components;

import com.qaframework.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/** Banner de error de SauceDemo: el mismo componente aparece en login y en checkout. */
public class ErrorBanner extends BasePage {

  private static final By ERROR = By.cssSelector("[data-test='error']");

  public ErrorBanner(WebDriver driver) {
    super(driver);
  }

  public String text() {
    return text(ERROR);
  }
}
