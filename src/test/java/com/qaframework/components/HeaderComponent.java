package com.qaframework.components;

import com.qaframework.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Header + menú lateral, presentes en todas las páginas autenticadas. Se modela como componente
 * aparte para no duplicar el badge del carrito y el menú en cada page object.
 */
public class HeaderComponent extends BasePage {

  private static final By CART_BADGE = By.cssSelector("[data-test='shopping-cart-badge']");
  private static final By CART_LINK = By.cssSelector("[data-test='shopping-cart-link']");
  private static final By MENU_BUTTON = By.id("react-burger-menu-btn");
  private static final By LOGOUT_LINK = By.cssSelector("[data-test='logout-sidebar-link']");
  private static final By RESET_APP_STATE_LINK = By.cssSelector("[data-test='reset-sidebar-link']");

  public HeaderComponent(WebDriver driver) {
    super(driver);
  }

  /** Texto del badge, o cadena vacía si no hay badge (carrito vacío). */
  public String cartBadgeText() {
    return isPresent(CART_BADGE) ? driver.findElement(CART_BADGE).getText().trim() : "";
  }

  public boolean isCartBadgeDisplayed() {
    return isDisplayed(CART_BADGE);
  }

  public void goToCart() {
    click(CART_LINK);
  }

  public void logout() {
    click(MENU_BUTTON);
    click(LOGOUT_LINK);
  }

  public void resetAppState() {
    click(MENU_BUTTON);
    click(RESET_APP_STATE_LINK);
  }
}
