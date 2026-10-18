package com.qaframework.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.time.Duration;
import java.util.Locale;
import java.util.Properties;

/**
 * Configuración centralizada. Orden de precedencia para cada clave (ej. "ui.base.url"):
 *
 * <ol>
 *   <li>System property: {@code mvn test -Dui.base.url=...}
 *   <li>Variable de entorno equivalente: {@code UI_BASE_URL=...} (útil en CI)
 *   <li>Valor por defecto en {@code src/test/resources/config.properties}
 * </ol>
 */
public final class Config {

  private static final Properties DEFAULTS = loadDefaults();

  private Config() {}

  public static String uiBaseUrl() {
    return get("ui.base.url");
  }

  public static String apiBaseUrl() {
    return get("api.base.url");
  }

  public static String bookerUsername() {
    return get("booker.username");
  }

  public static String bookerPassword() {
    return get("booker.password");
  }

  /** chrome | firefox | edge | chrome-mobile */
  public static String browser() {
    return get("browser").toLowerCase(Locale.ROOT);
  }

  public static boolean headless() {
    return Boolean.parseBoolean(get("headless"));
  }

  /** Timeout por defecto de las esperas explícitas (WebDriverWait y Awaitility). */
  public static Duration explicitWait() {
    return Duration.ofSeconds(Long.parseLong(get("explicit.wait.seconds")));
  }

  public static Duration pageLoadTimeout() {
    return Duration.ofSeconds(Long.parseLong(get("page.load.timeout.seconds")));
  }

  private static String get(String key) {
    String value = System.getProperty(key);
    if (isBlank(value)) {
      value = System.getenv(key.toUpperCase(Locale.ROOT).replace('.', '_'));
    }
    if (isBlank(value)) {
      value = DEFAULTS.getProperty(key);
    }
    if (isBlank(value)) {
      throw new IllegalStateException("Falta la configuración obligatoria \"" + key + "\"");
    }
    return value.trim();
  }

  private static boolean isBlank(String value) {
    return value == null || value.isBlank();
  }

  private static Properties loadDefaults() {
    Properties properties = new Properties();
    try (InputStream in = Config.class.getClassLoader().getResourceAsStream("config.properties")) {
      if (in != null) {
        properties.load(in);
      }
    } catch (IOException e) {
      throw new UncheckedIOException("No se pudo leer config.properties", e);
    }
    return properties;
  }
}
