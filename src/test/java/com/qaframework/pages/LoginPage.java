package com.qaframework.pages;

import com.qaframework.config.Config;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class LoginPage extends BasePage {

  // SauceDemo expone data-test estables en todo el DOM: se priorizan sobre CSS/XPath estructural.
  private static final By USERNAME = By.cssSelector("[data-test='username']");
  private static final By PASSWORD = By.cssSelector("[data-test='password']");
  private static final By LOGIN_BUTTON = By.cssSelector("[data-test='login-button']");

  public LoginPage(WebDriver driver) {
    super(driver);
  }

  public LoginPage open() {
    driver.get(Config.uiBaseUrl());
    visible(USERNAME);
    return this;
  }

  public void openPath(String path) {
    driver.get(Config.uiBaseUrl() + "/" + path.replaceFirst("^/", ""));
  }

  public void login(String username, String password) {
    type(USERNAME, username);
    type(PASSWORD, password);
    click(LOGIN_BUTTON);
  }

  public boolean isDisplayed() {
    return isDisplayed(USERNAME) && isDisplayed(LOGIN_BUTTON);
  }
}
