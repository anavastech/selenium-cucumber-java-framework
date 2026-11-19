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
   * Abre el menú lateral y hace click en uno de sus links dentro de un mismo loop de espera: en
   * cada intento se vuelve a abrir el menú si no está realmente abierto (ver {@link #isMenuOpen}).
   * Un click al botón mientras la página aún termina de cargar puede registrarse sin que React abra
   * el menú (visto en Firefox en CI). Si aun así se agota el tiempo, el mensaje incluye el estado
   * del menú para diagnosticar.
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

  /**
   * Abierto = aria-hidden="false" y sin el atributo {@code hidden}. aria-hidden solo no basta: al
   * montarse la página, react-burger-menu programa un timer de 500ms que pone {@code hidden} al
   * wrapper. Si el click al botón cae justo antes de que ese timer se dispare y el navegador va
   * lento (CI con 4 navegadores en paralelo), el timer gana la carrera: React queda con el menú
   * abierto (aria-hidden="false") pero el wrapper oculto para siempre. En ese estado un nuevo click
   * lo cierra y el siguiente lo abre bien, así que el loop de {@link #clickInMenu} lo recupera.
   */
  private boolean isMenuOpen() {
    List<WebElement> wrap = driver.findElements(MENU_WRAP);
    return !wrap.isEmpty()
        && "false".equals(wrap.getFirst().getDomAttribute("aria-hidden"))
        && wrap.getFirst().getDomAttribute("hidden") == null;
  }

  private String menuState(By link) {
    try {
      List<WebElement> links = driver.findElements(link);
      WebElement wrap = driver.findElement(MENU_WRAP);
      return "Estado: menú aria-hidden="
          + wrap.getDomAttribute("aria-hidden")
          + " hidden="
          + (wrap.getDomAttribute("hidden") != null)
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
