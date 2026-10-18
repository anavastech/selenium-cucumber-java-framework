@api @booking
Feature: API de reservas (restful-booker) - casos negativos y de contrato
  Varios de estos escenarios documentan el comportamiento REAL de la API (verificado manualmente),
  no el ideal: por ejemplo, un body inválido devuelve 500 en vez de 400. Si la API se corrige,
  estos escenarios fallan y avisan del cambio.

  @TC-BOOK-05 @negative
  Scenario: Consultar una reserva inexistente
    When consulto la reserva con id 999999999
    Then recibo un status 404

  @TC-BOOK-06 @negative @known-defect
  Scenario: Crear una reserva con body vacío
    When intento crear una reserva con body vacío
    Then recibo un status 500

  @TC-BOOK-07 @smoke
  Scenario: Endpoint de salud
    When consulto el endpoint de salud
    Then recibo un status 201

  @TC-BOOK-08 @negative @known-defect
  Scenario: Autenticación con credenciales inválidas
    When solicito un token con usuario "not-a-real-user" y contraseña "wrong-password"
    Then recibo un status 200
    And la respuesta indica la razón "Bad credentials"

  @TC-BOOK-09 @negative @known-defect
  Scenario: Crear una reserva sin campos requeridos
    When intento crear una reserva solo con el nombre "OnlyFirstName"
    Then recibo un status 500

  @TC-BOOK-10 @negative @known-defect
  Scenario: Defecto conocido - tipo de dato inválido no se rechaza
    When creo una reserva con el precio "not-a-number"
    Then recibo un status 200
    And la reserva creada tiene el precio en null

  @TC-BOOK-11 @negative
  Scenario: Token malformado es rechazado al actualizar
    When intento actualizar la reserva 1 con el token "this-is-not-a-real-token"
    Then recibo un status 403

  @TC-BOOK-12 @negative
  Scenario: Token malformado es rechazado al borrar
    When intento borrar la reserva 1 con el token "this-is-not-a-real-token"
    Then recibo un status 403
