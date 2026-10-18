@api @booking
Feature: API de reservas (restful-booker) - búsqueda
  GET /booking?firstname=&lastname=&checkin=&checkout=

  @TC-BOOK-13 @regression
  Scenario: Buscar reservas por nombre y apellido
    Given existe una reserva con un nombre único
    When busco reservas por su nombre y apellido
    Then recibo un status 200
    And la respuesta cumple el esquema "booking-ids"
    And la reserva aparece en el resultado

  @TC-BOOK-14 @regression
  Scenario: Buscar con un nombre que no existe
    When busco reservas con un nombre que no existe
    Then recibo un status 200
    And el resultado es una lista vacía

  # Comportamiento real verificado contra la API: filtrar por checkin/checkout no acota los
  # resultados a reservas con esas fechas exactas. Documentamos el comportamiento actual.
  @TC-BOOK-15 @regression @known-defect
  Scenario: Defecto conocido - filtrar por fechas exactas no devuelve la reserva
    Given existe una reserva
    When busco reservas por sus fechas exactas de checkin y checkout
    Then recibo un status 200
    And la respuesta cumple el esquema "booking-ids"
    But la reserva no aparece en el resultado
