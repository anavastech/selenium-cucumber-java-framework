@ui @a11y
Feature: Accesibilidad en SauceDemo
  Como usuario que depende de tecnología de asistencia
  Quiero que las pantallas clave cumplan WCAG 2.0 A/AA
  Para poder usar el sitio sin barreras

  # No reemplaza una auditoría manual, pero atrapa regresiones de accesibilidad introducidas
  # sin darse cuenta (contraste, labels, roles ARIA, etc.).

  @TC-A11Y-01 @regression
  Scenario: Login sin violaciones de accesibilidad
    Given estoy en la página de login
    Then la página no tiene violaciones WCAG 2.0 A/AA

  @TC-A11Y-02 @regression
  Scenario: Inventario sin violaciones de accesibilidad
    Given estoy autenticado en el inventario
    Then la página no tiene violaciones WCAG 2.0 A/AA

  @TC-A11Y-03 @regression
  Scenario: Paso de información de checkout sin violaciones de accesibilidad
    Given estoy autenticado en el inventario
    And agrego al carrito el producto "Sauce Labs Backpack"
    When inicio el checkout desde el carrito
    Then la página no tiene violaciones WCAG 2.0 A/AA

  # Los textos de error son un punto típico de regresiones de accesibilidad.
  @TC-A11Y-04 @regression
  Scenario: Mensaje de error de login sin violaciones de accesibilidad
    Given estoy en la página de login
    When ingreso las credenciales de "locked_out_user"
    Then veo el mensaje de error "Sorry, this user has been locked out"
    And la página no tiene violaciones WCAG 2.0 A/AA
