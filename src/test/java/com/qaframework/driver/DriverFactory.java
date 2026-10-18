package com.qaframework.driver;

import com.qaframework.config.Config;
import java.io.File;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeDriverService;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.GeckoDriverService;
import org.openqa.selenium.remote.service.DriverFinder;
import org.openqa.selenium.remote.service.DriverService;

/**
 * Crea el WebDriver según {@code -Dbrowser}. Los drivers (chromedriver, geckodriver, msedgedriver)
 * los resuelve Selenium Manager automáticamente: no hace falta WebDriverManager ni binarios en el
 * repo.
 *
 * <p>Selenium Manager se ejecuta por defecto en cada {@code new ChromeDriver()}. Como la resolución no
 * cambia durante una corrida, se hace una sola vez por browser y se reutiliza: en máquinas donde
 * detectar el navegador es lento (ej. el wrapper de snap de chromium en WSL tarda ~25s) esto evita
 * pagar ese costo en cada escenario.
 */
public final class DriverFactory {

  private static final String DESKTOP_WINDOW_SIZE = "1920,1080";

  // Form factor de un Pixel 5, vía mobile emulation de Chrome.
  private static final Map<String, Object> PIXEL_5 =
      Map.of(
          "deviceMetrics", Map.of("width", 393, "height", 851, "pixelRatio", 2.75, "touch", true),
          "userAgent",
          "Mozilla/5.0 (Linux; Android 11; Pixel 5) AppleWebKit/537.36 (KHTML, like Gecko)"
              + " Chrome/120.0.0.0 Mobile Safari/537.36");

  private static final Map<String, ResolvedBinaries> RESOLVED = new ConcurrentHashMap<>();

  private record ResolvedBinaries(String driverPath, String browserPath) {}

  private DriverFactory() {}

  public static WebDriver create() {
    boolean headless = Config.headless();
    WebDriver driver =
        switch (Config.browser()) {
          case "chrome" -> chrome(chromeOptions(headless));
          case "chrome-mobile" -> {
            ChromeOptions options = chromeOptions(headless);
            options.setExperimentalOption("mobileEmulation", PIXEL_5);
            yield chrome(options);
          }
          case "firefox" -> firefox(firefoxOptions(headless));
          case "edge" -> edge(edgeOptions(headless));
          default ->
              throw new IllegalArgumentException(
                  "Browser no soportado: \""
                      + Config.browser()
                      + "\". Usa chrome, firefox, edge o chrome-mobile.");
        };

    // Solo esperas explícitas: mezclar implícitas con explícitas produce timeouts impredecibles.
    driver.manage().timeouts().implicitlyWait(Duration.ZERO);
    driver.manage().timeouts().pageLoadTimeout(Config.pageLoadTimeout());
    return driver;
  }

  private static WebDriver chrome(ChromeOptions options) {
    ResolvedBinaries binaries = resolve("chrome", new ChromeDriverService.Builder().build(), options);
    if (binaries.browserPath() != null) {
      options.setBinary(binaries.browserPath());
    }
    return new ChromeDriver(
        new ChromeDriverService.Builder()
            .usingDriverExecutable(new File(binaries.driverPath()))
            .build(),
        options);
  }

  private static WebDriver firefox(FirefoxOptions options) {
    ResolvedBinaries binaries = resolve("firefox", new GeckoDriverService.Builder().build(), options);
    if (binaries.browserPath() != null) {
      options.setBinary(binaries.browserPath());
    }
    return new FirefoxDriver(
        new GeckoDriverService.Builder()
            .usingDriverExecutable(new File(binaries.driverPath()))
            .build(),
        options);
  }

  private static WebDriver edge(EdgeOptions options) {
    ResolvedBinaries binaries = resolve("edge", new EdgeDriverService.Builder().build(), options);
    if (binaries.browserPath() != null) {
      options.setBinary(binaries.browserPath());
    }
    return new EdgeDriver(
        new EdgeDriverService.Builder()
            .usingDriverExecutable(new File(binaries.driverPath()))
            .build(),
        options);
  }

  /** Corre Selenium Manager la primera vez y memoriza las rutas para el resto de la corrida. */
  private static ResolvedBinaries resolve(
      String browser, DriverService service, Capabilities options) {
    return RESOLVED.computeIfAbsent(
        browser,
        key -> {
          DriverFinder finder = new DriverFinder(service, options);
          return new ResolvedBinaries(
              finder.getDriverPath(), finder.hasBrowserPath() ? finder.getBrowserPath() : null);
        });
  }

  private static ChromeOptions chromeOptions(boolean headless) {
    ChromeOptions options = new ChromeOptions();
    if (headless) {
      options.addArguments("--headless=new");
    }
    options.addArguments(
        "--window-size=" + DESKTOP_WINDOW_SIZE, "--disable-dev-shm-usage", "--disable-gpu");
    // SauceDemo usa una contraseña "filtrada": sin esto, Chrome muestra un popup de
    // "cambia tu contraseña" que tapa la página en modo headed.
    options.setExperimentalOption(
        "prefs",
        Map.of(
            "credentials_enable_service", false,
            "profile.password_manager_enabled", false,
            "profile.password_manager_leak_detection", false));
    options.setExperimentalOption("excludeSwitches", List.of("enable-automation"));
    return options;
  }

  private static FirefoxOptions firefoxOptions(boolean headless) {
    FirefoxOptions options = new FirefoxOptions();
    if (headless) {
      options.addArguments("-headless");
    }
    options.addArguments("--width=1920", "--height=1080");
    return options;
  }

  private static EdgeOptions edgeOptions(boolean headless) {
    EdgeOptions options = new EdgeOptions();
    if (headless) {
      options.addArguments("--headless=new");
    }
    options.addArguments("--window-size=" + DESKTOP_WINDOW_SIZE, "--disable-dev-shm-usage");
    return options;
  }
}
