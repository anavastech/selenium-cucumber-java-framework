@ui @cart
Feature: Carrito en SauceDemo
  Como cliente autenticado
  Quiero gestionar el contenido de mi carrito
  Para llegar a checkout con los productos correctos

  Background:
    Given estoy autenticado en el inventario

  @TC-CART-01 @smoke
  Scenario: Continuar comprando vuelve al inventario sin perder el carrito
    Given agrego al carrito el producto "Sauce Labs Backpack"
    And voy al carrito
    When presiono "Continue Shopping"
    Then estoy en la página de inventario
    And el badge del carrito muestra "1"

  @TC-CART-02 @regression
  Scenario: Quitar un producto directamente desde el carrito
    Given agrego al carrito los productos:
      | Sauce Labs Backpack   |
      | Sauce Labs Bike Light |
    And voy al carrito
    When quito desde el carrito el producto "Sauce Labs Backpack"
    Then el carrito lista 1 producto
    And el badge del carrito muestra "1"

  # Comportamiento real verificado contra la app: "Reset App State" limpia el badge de inmediato,
  # pero la lista ya renderizada en cart.html no se actualiza hasta navegar o recargar.
  # Documentamos el comportamiento actual, no el ideal.
  @TC-CART-03 @regression @known-defect
  Scenario: Defecto conocido - Reset App State no actualiza la lista ya renderizada
    Given agrego al carrito el producto "Sauce Labs Backpack"
    And voy al carrito
    When reseteo el estado de la app desde el menú
    Then el badge del carrito desaparece
    But el carrito lista 1 producto
