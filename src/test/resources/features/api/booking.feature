@api @booking
Feature: API de reservas (restful-booker) - CRUD
  Como sistema cliente
  Quiero gestionar reservas vía API
  Para integrar el flujo de booking sin pasar por la UI

  @TC-BOOK-01 @smoke
  Scenario: Autenticación devuelve un token válido
    When solicito un token con credenciales de administrador
    Then recibo un status 200
    And recibo un token no vacío

  @TC-BOOK-02 @smoke @regression
  Scenario: Ciclo de vida completo de una reserva
    When creo una reserva
    Then recibo un status 200
    And la respuesta cumple el esquema "create-booking-response"
    When consulto la reserva creada
    Then recibo un status 200
    And la respuesta cumple el esquema "booking"
    And la reserva consultada tiene los mismos datos que envié
    When actualizo el precio de la reserva con PUT
    Then recibo un status 200
    And el precio queda actualizado
    When elimino la reserva
    Then recibo un status 201
    When consulto la reserva creada
    Then recibo un status 404

  @TC-BOOK-03 @negative
  Scenario: Actualizar sin token es rechazado
    When intento actualizar la reserva 1 sin token
    Then recibo un status 403

  @TC-BOOK-04 @regression
  Scenario: Actualización parcial con PATCH
    Given existe una reserva
    When actualizo solo el precio con PATCH
    Then recibo un status 200
    And el precio queda actualizado
    And el resto de los datos no se ve afectado
