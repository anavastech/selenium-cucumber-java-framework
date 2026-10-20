# Selenium QA Framework

[![Selenium Tests](https://github.com/anavastech/selenium-cucumber-java-framework/actions/workflows/selenium.yml/badge.svg)](https://github.com/anavastech/selenium-cucumber-java-framework/actions/workflows/selenium.yml)

Framework de automatización E2E y de API construido con **Java 21 + Selenium 4 + Cucumber (BDD)**, sobre dos sitios de práctica públicos:

- **UI**: [SauceDemo](https://www.saucedemo.com): login, catálogo, detalle de producto, carrito, checkout y accesibilidad.
- **API**: [restful-booker](https://restful-booker.herokuapp.com): CRUD, búsqueda y casos negativos, con autenticación por token.

La suite tiene 52 escenarios, cada uno con un ID trazable (`@TC-LOGIN-01`, `@TC-CHK-03`, `@TC-BOOK-15`, …). Algunos casos destacados:

- `@TC-LOGIN-07`: login negativo *data-driven* (`Scenario Outline` con 5 combinaciones de credenciales inválidas).
- `@TC-INV-06` y `@TC-INV-07`: defectos de `visual_user`, el usuario de SauceDemo cuya tienda funciona pero se ve mal (imagen de placeholder y precios aleatorios). Lo que se puede detectar en el DOM se automatiza. Las desalineaciones de layout quedan documentadas como fuera de alcance, porque requieren visual regression testing.

## Stack

| Necesidad | Herramienta |
| --- | --- |
| Lenguaje / build | Java 21, Maven (incluye Maven Wrapper `./mvnw`) |
| UI | Selenium 4 (drivers resueltos por Selenium Manager) |
| BDD | Cucumber 7 + Gherkin |
| Runner | JUnit 5 Platform Suite (`cucumber-junit-platform-engine`) |
| Inyección de dependencias | PicoContainer (`cucumber-picocontainer`) |
| API | REST Assured + JSON Schema Validator |
| Asserts / esperas | AssertJ + Awaitility |
| Datos de prueba | Datafaker |
| Accesibilidad | axe-core (`com.deque.html.axe-core:selenium`) |
| Reportes | Cucumber HTML/JSON + Allure |

## Por qué esta arquitectura

- **Gherkin ejecutable**: los `.feature` no son documentación aparte, **son** los tests. El criterio de aceptación y la automatización no pueden desincronizarse.
- **Page Object Model** (`pages/`, `components/`): cada página expone locators y acciones, no asserts. El header (badge del carrito y menú) y el banner de error son *componentes* compartidos en vez de estar duplicados en cada página.
- **Esperas explícitas, nunca `Thread.sleep`**: `BasePage` centraliza `WebDriverWait`, y el click reintenta si el elemento está tapado o fue re-renderizado (el menú lateral de SauceDemo tiene animación). Los asserts de UI se envuelven en `await().untilAsserted(...)` de Awaitility, que reintenta el assert hasta el timeout en vez de fallar al primer intento. El implicit wait queda en cero: mezclar implícitas con explícitas da timeouts impredecibles.
- **Contexto por escenario con PicoContainer** (`context/`): `UiContext` y `ApiContext` se inyectan por constructor en las step classes, que así comparten el mismo navegador y estado sin estáticos ni `ThreadLocal`. Cada escenario tiene su propio contenedor, lo que hace segura la ejecución en paralelo. El driver se crea de forma perezosa: los escenarios de API nunca abren un navegador.
- **Selectores**: se priorizan los `data-test` estables de SauceDemo sobre CSS/XPath estructural.
- **Contrato de API**: la forma de las respuestas se valida con JSON Schema (`src/test/resources/schemas/`) en vez de asserts campo por campo. Las reservas se modelan como `record`, así que comparar lo enviado con lo devuelto es un solo `isEqualTo`.
- **Limpieza automática**: toda reserva creada se registra en `ApiContext` y un hook `@After` la borra, aunque el escenario falle a mitad de camino. No hace falta `try/finally` en cada test.
- **Pre-flight**: si restful-booker no responde a `/ping`, los escenarios de API fallan de inmediato con un mensaje claro en vez de timeouts de red confusos.
- **Defectos conocidos documentados**: los escenarios `@known-defect` fijan el comportamiento *real* (verificado manualmente) de la app o la API, no el ideal. Si alguien lo corrige, el escenario falla y avisa del cambio.
- **Evidencia en fallos**: screenshot adjunto al reporte en cada escenario de UI fallido; request/response de cada llamada de API adjunto en Allure.
- **Resolución de drivers una sola vez**: Selenium Manager corre por defecto en cada `new ChromeDriver()`. `DriverFactory` memoriza la ruta del driver y del navegador por corrida. En WSL con el chromium de snap esto bajó la suite completa de ~215s a ~48s.

## Estructura

```
src/test/java/com/qaframework/
  config/      Config: system property > variable de entorno > config.properties
  driver/      DriverFactory (chrome, firefox, edge, chrome-mobile)
  context/     UiContext y ApiContext (estado por escenario, inyectado por PicoContainer)
  pages/       Page Objects + BasePage (esperas explícitas) + SortOption
  components/  HeaderComponent, ErrorBanner
  api/         BookingClient (REST Assured)
  data/        SauceUser, Booking (record), TestDataFactory (Datafaker)
  hooks/       Screenshot en fallo, cierre del navegador, limpieza de API, pre-flight
  steps/ui/    Step definitions de UI
  steps/api/   Step definitions de API
  runners/     RunCucumberTest (JUnit Platform Suite)
src/test/resources/
  features/ui/   login, inventory, product-detail, cart, checkout, accessibility
  features/api/  booking, booking-edge-cases, booking-search
  schemas/       JSON Schemas del contrato de restful-booker
  config.properties, junit-platform.properties
```

## Requisitos

- JDK 21+
- Chrome, Firefox o Edge instalados. Si no hay ninguno, Selenium Manager descarga Chrome for Testing automáticamente.

No hace falta instalar Maven: `./mvnw` lo descarga.

## Ejecución

```bash
./mvnw test                                                  # toda la suite (UI en Chrome + API)
./mvnw test -Dcucumber.filter.tags="@ui"                     # solo UI
./mvnw test -Dcucumber.filter.tags="@api"                    # solo API
./mvnw test -Dcucumber.filter.tags="@smoke"                  # solo @smoke
./mvnw test -Dcucumber.filter.tags="@regression and not @known-defect"
./mvnw test -Dcucumber.filter.tags="@TC-CHK-01"              # un caso puntual por su ID

./mvnw test -Dbrowser=firefox                                # chrome | firefox | edge | chrome-mobile
./mvnw test -Dbrowser=chrome-mobile -Dcucumber.filter.tags="@ui and @smoke"   # viewport Pixel 5
./mvnw test -Dheadless=false -Dcucumber.execution.parallel.config.fixed.parallelism=1  # ver el navegador
```

Tags disponibles: `@ui`, `@api`, `@smoke`, `@regression`, `@negative`, `@known-defect`, `@a11y`, uno por feature (`@login`, `@cart`, …) y un ID por caso (`@TC-XXX-NN`).

## Reportes

- **Cucumber HTML**: `target/cucumber-report/index.html` (autocontenido, se abre directo en el navegador).
- **Allure**: `./mvnw allure:serve` (requiere haber corrido los tests antes).

## Configuración

Valores por defecto en `src/test/resources/config.properties`. Cualquier clave se puede sobrescribir con `-Dclave=valor` o con la variable de entorno equivalente en mayúsculas:

| Clave | Variable de entorno | Default |
| --- | --- | --- |
| `ui.base.url` | `UI_BASE_URL` | `https://www.saucedemo.com` |
| `api.base.url` | `API_BASE_URL` | `https://restful-booker.herokuapp.com` |
| `booker.username` / `booker.password` | `BOOKER_USERNAME` / `BOOKER_PASSWORD` | `admin` / `password123` |
| `browser` | `BROWSER` | `chrome` |
| `headless` | `HEADLESS` | `true` |
| `explicit.wait.seconds` | `EXPLICIT_WAIT_SECONDS` | `10` |

## CI

`.github/workflows/selenium.yml`:

- En cada **push/PR a main**, todas las noches (**cron**) y en **workflow_dispatch**: la suite de UI completa en una matriz Chrome / Firefox / Edge / chrome-mobile (`@smoke`), y la API una sola vez en un job aparte, porque no depende del navegador.
- El cron detecta regresiones o cambios en los sitios públicos aunque no haya push ese día.

Los reportes de Cucumber y los resultados de Allure quedan como artefactos de cada run.

## Navegadores

| `-Dbrowser=` | Navegador |
| --- | --- |
| `chrome` (default) | Google Chrome / Chromium |
| `firefox` | Mozilla Firefox |
| `edge` | Microsoft Edge |
| `chrome-mobile` | Chrome con mobile emulation (viewport y user agent de un Pixel 5) |

Safari no está incluido: safaridriver solo corre en macOS y los runners de CI son Linux.

## Nota sobre el contenido

Este repositorio usa exclusivamente sitios públicos de práctica. No contiene código, selectores ni datos de ningún proyecto propietario.
