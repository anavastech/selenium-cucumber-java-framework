@ui @checkout
Feature: Checkout en SauceDemo
  Como cliente autenticado
  Quiero completar la compra de un producto
  Para finalizar mi pedido

  @TC-CHK-01 @smoke
  Scenario: Compra exitosa de punta a punta
    Given estoy autenticado en el inventario
    And agrego al carrito el producto "Sauce Labs Backpack"
    When inicio el checkout desde el carrito
    And completo mis datos de envío
    And finalizo la compra
    Then veo la confirmación "Thank you for your order!"

  @TC-CHK-02 @negative
  Scenario: No se puede continuar sin código postal
    Given estoy autenticado en el inventario
    And agrego al carrito el producto "Sauce Labs Backpack"
    When inicio el checkout desde el carrito
    And completo mis datos de envío sin código postal
    Then veo el mensaje de error "Postal Code is required"

  @TC-CHK-03 @regression
  Scenario: El total coincide con subtotal más impuestos
    Given estoy autenticado en el inventario
    And agrego al carrito los productos:
      | Sauce Labs Backpack   |
      | Sauce Labs Bike Light |
    When inicio el checkout desde el carrito
    And completo mis datos de envío
    Then el total es igual al subtotal más el impuesto

  # Bug conocido de la app: error_user llega al resumen, pero "Finish" no confirma la compra.
  @TC-CHK-04 @regression @known-defect
  Scenario: Defecto conocido - error_user no puede finalizar la compra
    Given estoy autenticado como "error_user"
    And agrego al carrito el producto "Sauce Labs Backpack"
    When inicio el checkout desde el carrito
    And completo mis datos de envío
    And finalizo la compra
    Then permanezco en el resumen sin confirmación de compra

  @TC-CHK-05 @regression
  Scenario: Cancelar en el paso de información vuelve al carrito
    Given estoy autenticado en el inventario
    And agrego al carrito el producto "Sauce Labs Backpack"
    When inicio el checkout desde el carrito
    And cancelo el checkout
    Then estoy en la página del carrito
    And el carrito lista 1 producto

  @TC-CHK-06 @regression
  Scenario: Cancelar en el paso de resumen vuelve al inventario
    Given estoy autenticado en el inventario
    And agrego al carrito el producto "Sauce Labs Backpack"
    When inicio el checkout desde el carrito
    And completo mis datos de envío
    And cancelo el checkout
    Then estoy en la página de inventario
    And el badge del carrito muestra "1"
