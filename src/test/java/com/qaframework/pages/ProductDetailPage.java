package com.qaframework.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class ProductDetailPage extends BasePage {

  private static final By NAME = By.cssSelector("[data-test='inventory-item-name']");
  private static final By PRICE = By.cssSelector("[data-test='inventory-item-price']");
  private static final By ADD_TO_CART = By.cssSelector("[data-test='add-to-cart']");
  private static final By REMOVE = By.cssSelector("[data-test='remove']");
  private static final By BACK_TO_PRODUCTS = By.cssSelector("[data-test='back-to-products']");

  public ProductDetailPage(WebDriver driver) {
    super(driver);
  }

  /** Igual que el inventario: la URL cambia antes de que el detalle termine de renderizar. */
  public ProductDetailPage waitUntilLoaded() {
    visible(BACK_TO_PRODUCTS);
    return this;
  }

  public String name() {
    return text(NAME);
  }

  public String price() {
    return text(PRICE);
  }

  public void addToCart() {
    click(ADD_TO_CART);
  }

  public void removeFromCart() {
    click(REMOVE);
  }

  public void backToProducts() {
    click(BACK_TO_PRODUCTS);
  }

  public boolean isAddToCartDisplayed() {
    return isDisplayed(ADD_TO_CART);
  }

  public boolean isRemoveDisplayed() {
    return isDisplayed(REMOVE);
  }
}
