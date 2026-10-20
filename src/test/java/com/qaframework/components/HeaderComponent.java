package com.qaframework.components;

import com.qaframework.pages.BasePage;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

/**
 * Header + menú lateral, presentes en todas las páginas autenticadas. Se modela como componente
 * aparte para no duplicar el badge del carrito y el menú en cada page object.
 */
public class HeaderComponent extends BasePage {

  private static final By CART_BADGE = By.cssSelector("[data-test='shopping-cart-badge']");
  private static final By CART_LINK = By.cssSelector("[data-test='shopping-cart-link']");
  private static final By MENU_BUTTON = By.id("react-burger-menu-btn");
  private static final By MENU_WRAP = By.cssSelector(".bm-menu-wrap");
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
    openMenu();
    click(LOGOUT_LINK);
  }

  public void resetAppState() {
    openMenu();
    click(RESET_APP_STATE_LINK);
  }

  /**
   * Abre el menú lateral y confirma que quedó abierto. Un click sobre el botón mientras la página
   * aún termina de cargar puede registrarse sin que React abra el menú (visto en Firefox en CI);
   * en ese caso se vuelve a hacer click en vez de esperar a un link que nunca aparece.
   */
  private void openMenu() {
    wait.ignoring(ElementClickInterceptedException.class)
        .ignoring(StaleElementReferenceException.class)
        .until(
            d -> {
              if (isMenuOpen()) {
                return true;
              }
              WebElement button = ExpectedConditions.elementToBeClickable(MENU_BUTTON).apply(d);
              if (button != null) {
                button.click();
              }
              return false;
            });
  }

  private boolean isMenuOpen() {
    List<WebElement> wrap = driver.findElements(MENU_WRAP);
    return !wrap.isEmpty() && "false".equals(wrap.getFirst().getDomAttribute("aria-hidden"));
  }
}
