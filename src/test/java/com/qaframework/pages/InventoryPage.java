package com.qaframework.pages;

import java.math.BigDecimal;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.Select;

public class InventoryPage extends BasePage {

  private static final By SORT_DROPDOWN = By.cssSelector("[data-test='product-sort-container']");
  private static final By ITEM_NAMES = By.cssSelector("[data-test='inventory-item-name']");
  private static final By ITEM_PRICES = By.cssSelector("[data-test='inventory-item-price']");

  public InventoryPage(WebDriver driver) {
    super(driver);
  }

  /**
   * La URL cambia (history API) antes de que el SPA termine de renderizar el listado. Esperamos al
   * primer producto visible para que cualquier lectura posterior no lea el DOM antes de tiempo.
   */
  public InventoryPage waitUntilLoaded() {
    waitForUrlContaining("inventory.html");
    visible(ITEM_NAMES);
    return this;
  }

  public void addToCart(String productName) {
    click(By.cssSelector("[data-test='add-to-cart-" + slug(productName) + "']"));
  }

  public void removeFromCart(String productName) {
    click(By.cssSelector("[data-test='remove-" + slug(productName) + "']"));
  }

  public void sortBy(String optionValue) {
    new Select(visible(SORT_DROPDOWN)).selectByValue(optionValue);
  }

  public void openProductDetail(String productName) {
    click(itemNameLocator(productName));
  }

  public String priceOf(String productName) {
    return text(By.xpath(inventoryItemXpath(productName) + "//div[@data-test='inventory-item-price']"));
  }

  public String imageSrcOf(String productName) {
    return visible(By.cssSelector("[data-test='inventory-item-" + slug(productName) + "-img']"))
        .getAttribute("src");
  }

  public List<String> productNames() {
    return texts(ITEM_NAMES);
  }

  public List<BigDecimal> productPrices() {
    return texts(ITEM_PRICES).stream().map(price -> new BigDecimal(price.replace("$", ""))).toList();
  }

  /** Sube desde el nombre al contenedor del producto, para leer otros datos del mismo item. */
  private static String inventoryItemXpath(String productName) {
    return "//div[@data-test='inventory-item'][.//div[@data-test='inventory-item-name' and"
        + " normalize-space()='"
        + productName
        + "']]";
  }

  private static By itemNameLocator(String productName) {
    return By.xpath(
        "//div[@data-test='inventory-item-name' and normalize-space()='" + productName + "']");
  }
}
