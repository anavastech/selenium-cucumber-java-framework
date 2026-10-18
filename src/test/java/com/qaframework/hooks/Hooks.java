package com.qaframework.hooks;

import com.qaframework.api.BookingClient;
import com.qaframework.config.Config;
import com.qaframework.context.ApiContext;
import com.qaframework.context.UiContext;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.Scenario;
import io.restassured.response.Response;
import java.time.Duration;
import org.awaitility.Awaitility;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

public class Hooks {

  // Resultado del pre-flight compartido por todos los escenarios (y threads) de la corrida.
  private static volatile String apiUnavailableReason;
  private static volatile boolean apiChecked;

  private final UiContext ui;
  private final ApiContext api;

  public Hooks(UiContext ui, ApiContext api) {
    this.ui = ui;
    this.api = api;
  }

  /**
   * Los asserts sobre la UI se envuelven en {@code await().untilAsserted(...)}: se reintentan hasta
   * este timeout en vez de fallar al primer intento mientras la página todavía se está actualizando.
   */
  @BeforeAll
  public static void configureAwaitility() {
    Awaitility.setDefaultTimeout(Config.explicitWait());
    Awaitility.setDefaultPollInterval(Duration.ofMillis(200));
    Awaitility.ignoreExceptionsByDefault();
  }

  /**
   * Pre-flight check: si restful-booker está caído, cada escenario de API falla al instante con un
   * mensaje claro en vez de con timeouts de red confusos. Se hace una sola vez por corrida.
   */
  @Before(value = "@api", order = 0)
  public void ensureApiIsAvailable() {
    if (!apiChecked) {
      synchronized (Hooks.class) {
        if (!apiChecked) {
          apiUnavailableReason = pingApi();
          apiChecked = true;
        }
      }
    }
    if (apiUnavailableReason != null) {
      throw new IllegalStateException(
          "La API de restful-booker no está disponible, se omite el escenario. Detalle: "
              + apiUnavailableReason);
    }
  }

  @After(order = 100)
  public void captureScreenshotOnFailure(Scenario scenario) {
    if (scenario.isFailed() && ui.hasDriver()) {
      byte[] png = ((TakesScreenshot) ui.driver()).getScreenshotAs(OutputType.BYTES);
      scenario.attach(png, "image/png", scenario.getName());
    }
  }

  @After(order = 50)
  public void quitBrowser() {
    ui.quitDriver();
  }

  /** Limpieza: no dejamos reservas huérfanas en el entorno compartido de restful-booker. */
  @After(value = "@api", order = 50)
  public void deleteCreatedBookings() {
    if (api.createdBookingIds().isEmpty()) {
      return;
    }
    String token = api.token();
    for (int id : api.createdBookingIds()) {
      api.client().delete(id, token);
    }
  }

  private static String pingApi() {
    try {
      Response response = new BookingClient().ping();
      return response.statusCode() == 201
          ? null
          : "\"" + Config.apiBaseUrl() + "/ping\" respondió " + response.statusCode();
    } catch (RuntimeException e) {
      return e.getMessage();
    }
  }
}
