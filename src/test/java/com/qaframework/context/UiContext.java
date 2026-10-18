package com.qaframework.context;

import com.qaframework.components.ErrorBanner;
import com.qaframework.components.HeaderComponent;
import com.qaframework.driver.DriverFactory;
import com.qaframework.pages.CartPage;
import com.qaframework.pages.CheckoutPage;
import com.qaframework.pages.InventoryPage;
import com.qaframework.pages.LoginPage;
import com.qaframework.pages.ProductDetailPage;
import org.openqa.selenium.WebDriver;

/**
 * Estado de UI de un escenario. PicoContainer crea una instancia nueva por escenario y la inyecta
 * en todas las step classes que la pidan por constructor, así comparten el mismo navegador sin
 * estáticos ni ThreadLocal (lo que además hace segura la ejecución en paralelo).
 *
 * <p>El driver se crea de forma perezosa: los escenarios de API nunca abren un navegador.
 */
public class UiContext {

  private WebDriver driver;

  public WebDriver driver() {
    if (driver == null) {
      driver = DriverFactory.create();
    }
    return driver;
  }

  public boolean hasDriver() {
    return driver != null;
  }

  public void quitDriver() {
    if (driver != null) {
      driver.quit();
      driver = null;
    }
  }

  // Los page objects no guardan estado propio (solo locators + driver), así que crearlos en cada
  // llamada es barato y evita referencias a elementos stale.
  public LoginPage loginPage() {
    return new LoginPage(driver());
  }

  public InventoryPage inventoryPage() {
    return new InventoryPage(driver());
  }

  public ProductDetailPage productDetailPage() {
    return new ProductDetailPage(driver());
  }

  public CartPage cartPage() {
    return new CartPage(driver());
  }

  public CheckoutPage checkoutPage() {
    return new CheckoutPage(driver());
  }

  public HeaderComponent header() {
    return new HeaderComponent(driver());
  }

  public ErrorBanner errorBanner() {
    return new ErrorBanner(driver());
  }
}
