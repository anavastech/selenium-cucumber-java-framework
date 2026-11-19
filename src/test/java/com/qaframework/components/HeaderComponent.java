package com.qaframework.components;

import com.qaframework.config.Config;
import com.qaframework.pages.BasePage;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

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
    clickInMenu(LOGOUT_LINK);
  }

  public void resetAppState() {
    clickInMenu(RESET_APP_STATE_LINK);
  }

  /**
   * Abre el menú lateral y hace click en uno de sus links dentro de un mismo loop de espera. En CI
   * el menú a veces aparece abierto (aria-hidden=false) y luego el link nunca llega a ser
   * clickeable: un click al botón mientras la página aún termina de cargar puede registrarse sin
   * que React abra el menú, o el menú puede volver a cerrarse. Abrir y clickear en pasos separados
   * dejaba esperando 10s a un link oculto; así, en cada intento se vuelve a abrir el menú si hace
   * falta. Si aun así se agota el tiempo, el mensaje incluye el estado del menú para diagnosticar.
   */
  private void clickInMenu(By link) {
    new WebDriverWait(driver, Config.explicitWait())
        .ignoring(ElementClickInterceptedException.class)
        .ignoring(StaleElementReferenceException.class)
        .withMessage(() -> "click en " + link + " del menú lateral. " + menuState(link))
        .until(
            d -> {
              if (!isMenuOpen()) {
                WebElement button = ExpectedConditions.elementToBeClickable(MENU_BUTTON).apply(d);
                if (button != null) {
                  button.click();
                }
                return false;
              }
              WebElement element = ExpectedConditions.elementToBeClickable(link).apply(d);
              if (element == null) {
                return false;
              }
              element.click();
              return true;
            });
  }

  private boolean isMenuOpen() {
    List<WebElement> wrap = driver.findElements(MENU_WRAP);
    return !wrap.isEmpty() && "false".equals(wrap.getFirst().getDomAttribute("aria-hidden"));
  }

  private String menuState(By link) {
    try {
      List<WebElement> links = driver.findElements(link);
      return "Estado: menú "
          + (isMenuOpen() ? "abierto" : "cerrado")
          + ", link "
          + (links.isEmpty()
              ? "no existe"
              : (links.getFirst().isDisplayed() ? "visible" : "oculto")
                  + " en "
                  + links.getFirst().getRect().getPoint());
    } catch (RuntimeException e) {
      return "Estado del menú no disponible: " + e.getMessage();
    }
  }
}
