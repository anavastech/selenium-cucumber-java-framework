package com.qaframework.pages;

import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class CartPage extends BasePage {

  private static final By CHECKOUT = By.cssSelector("[data-test='checkout']");
  private static final By CONTINUE_SHOPPING = By.cssSelector("[data-test='continue-shopping']");
  private static final By ITEM_NAMES = By.cssSelector("[data-test='inventory-item-name']");

  public CartPage(WebDriver driver) {
    super(driver);
  }

  public CartPage waitUntilLoaded() {
    waitForUrlContaining("cart.html");
    visible(CHECKOUT);
    return this;
  }

  public void checkout() {
    click(CHECKOUT);
  }

  public void continueShopping() {
    click(CONTINUE_SHOPPING);
  }

  public void removeFromCart(String productName) {
    click(By.cssSelector("[data-test='remove-" + slug(productName) + "']"));
  }

  public List<String> productNames() {
    return texts(ITEM_NAMES);
  }
}
