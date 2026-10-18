@ui @login
Feature: Login en SauceDemo
  Como usuario de la tienda
  Quiero poder autenticarme
  Para acceder al catálogo de productos

  @TC-LOGIN-01 @smoke
  Scenario: Login exitoso con usuario estándar
    Given estoy en la página de login
    When ingreso las credenciales de "standard_user"
    Then estoy en la página de inventario

  @TC-LOGIN-02 @negative
  Scenario: Usuario bloqueado no puede ingresar
    Given estoy en la página de login
    When ingreso las credenciales de "locked_out_user"
    Then veo el mensaje de error "Sorry, this user has been locked out"

  @TC-LOGIN-03 @negative
  Scenario: Campos vacíos no permiten enviar el formulario
    Given estoy en la página de login
    When intento ingresar sin usuario ni contraseña
    Then veo el mensaje de error "Username is required"

  # performance_glitch_user es funcionalmente válido pero con latencia inducida:
  # se le da un margen explícito mayor en vez de subir el timeout global.
  @TC-LOGIN-04 @regression
  Scenario: Usuario con latencia eventualmente ingresa
    Given estoy en la página de login
    When ingreso las credenciales de "performance_glitch_user"
    Then estoy en la página de inventario en menos de 15 segundos

  @TC-LOGIN-05 @smoke
  Scenario: Cerrar sesión vuelve a la página de login
    Given estoy autenticado en el inventario
    When cierro sesión desde el menú
    Then vuelvo a la página de login

  @TC-LOGIN-06 @negative
  Scenario: No se puede acceder al inventario tras cerrar sesión
    Given estoy autenticado en el inventario
    When cierro sesión desde el menú
    And intento navegar directamente a "inventory.html"
    Then no estoy en la página de inventario
    And veo el mensaje de error "You can only access '/inventory.html' when you are logged in"

  # Data-driven: un mismo flujo con distintas combinaciones inválidas. Cada fila es un caso
  # independiente en el reporte, sin duplicar steps.
  @TC-LOGIN-07 @negative
  Scenario Outline: Credenciales inválidas muestran un error específico (<caso>)
    Given estoy en la página de login
    When ingreso el usuario "<usuario>" y la contraseña "<contraseña>"
    Then veo el mensaje de error "<error>"
    And no estoy en la página de inventario

    Examples:
      | caso                           | usuario       | contraseña     | error                                                       |
      | contraseña incorrecta          | standard_user | wrong_password | Username and password do not match any user in this service |
      | usuario inexistente            | not_a_user    | secret_sauce   | Username and password do not match any user in this service |
      | usuario distingue mayúsculas   | Standard_User | secret_sauce   | Username and password do not match any user in this service |
      | sin contraseña                 | standard_user |                | Password is required                                        |
      | sin usuario                    |               | secret_sauce   | Username is required                                        |
