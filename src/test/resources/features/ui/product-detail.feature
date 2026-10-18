@ui @product-detail
Feature: Detalle de producto en SauceDemo
  Como cliente autenticado
  Quiero ver el detalle de un producto
  Para decidir si lo agrego al carrito antes de comprarlo

  Background:
    Given estoy autenticado en el inventario

  @TC-PROD-01 @smoke
  Scenario: El detalle muestra el mismo nombre y precio que el listado
    When abro el detalle del producto "Sauce Labs Bike Light"
    Then el detalle muestra el mismo nombre y precio que el listado

  @TC-PROD-02 @regression
  Scenario: Agregar y quitar un producto desde su detalle
    Given abro el detalle del producto "Sauce Labs Backpack"
    When lo agrego al carrito desde el detalle
    Then el badge del carrito muestra "1"
    And el detalle ofrece quitarlo del carrito
    When lo quito del carrito desde el detalle
    Then el badge del carrito desaparece
    And el detalle ofrece agregarlo al carrito

  @TC-PROD-03 @regression
  Scenario: Volver al listado no pierde el carrito
    Given abro el detalle del producto "Sauce Labs Backpack"
    And lo agrego al carrito desde el detalle
    When vuelvo al listado con "Back to products"
    Then estoy en la página de inventario
    And el badge del carrito muestra "1"
