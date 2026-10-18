@ui @inventory
Feature: Catálogo de productos en SauceDemo
  Como cliente autenticado
  Quiero explorar y filtrar el catálogo
  Para elegir qué comprar

  @TC-INV-01
  Scenario Outline: Ordenar productos por <orden>
    Given estoy autenticado en el inventario
    When ordeno los productos por <orden>
    Then los productos quedan ordenados por <orden>

    @smoke
    Examples: Feedback rápido
      | orden             |
      | precio ascendente |

    @regression
    Examples: Resto de opciones
      | orden              |
      | precio descendente |
      | nombre A a Z       |
      | nombre Z a A       |

  @TC-INV-02 @regression
  Scenario: Reset App State limpia el badge del carrito
    Given estoy autenticado en el inventario
    And agrego al carrito el producto "Sauce Labs Backpack"
    When reseteo el estado de la app desde el menú
    Then el badge del carrito desaparece

  @TC-INV-03 @smoke
  Scenario: El badge del carrito refleja los productos agregados
    Given estoy autenticado en el inventario
    When agrego al carrito los productos:
      | Sauce Labs Backpack   |
      | Sauce Labs Bike Light |
    Then el badge del carrito muestra "2"

  @TC-INV-04 @regression
  Scenario: Quitar un producto actualiza el badge del carrito
    Given estoy autenticado en el inventario
    And agrego al carrito los productos:
      | Sauce Labs Backpack   |
      | Sauce Labs Bike Light |
    When quito del carrito desde el inventario el producto "Sauce Labs Backpack"
    Then el badge del carrito muestra "1"

  # Bug conocido de la app, verificado manualmente contra el sitio antes de automatizarlo.
  @TC-INV-05 @regression @known-defect
  Scenario: Defecto conocido - problem_user no puede ordenar productos
    Given estoy autenticado como "problem_user"
    When ordeno los productos por nombre Z a A
    Then el orden de los productos no cambia

  # visual_user: la tienda funciona (login, carrito y checkout pasan), pero la presentación está
  # rota. Comportamiento real verificado contra el sitio:
  #   - la imagen de "Sauce Labs Backpack" es un placeholder (sl-404.jpg)
  #   - los precios del listado son aleatorios en cada login; el detalle repite ese mismo precio,
  #     por eso TC-PROD-01 no lo detecta y hace falta comparar contra el catálogo real
  #   - el ícono del carrito y el botón del último producto aparecen desplazados
  # Las dos primeras se pueden verificar en el DOM. Las desalineaciones de layout requieren
  # visual regression testing (Applitools, Percy, etc.), que queda fuera del alcance del framework.
  @TC-INV-06 @regression @known-defect
  Scenario: Defecto conocido - visual_user ve una imagen de producto incorrecta
    Given estoy autenticado como "visual_user"
    Then el producto "Sauce Labs Backpack" muestra la imagen "sl-404"

  @TC-INV-07 @regression @known-defect
  Scenario: Defecto conocido - visual_user ve precios que no son los del catálogo
    Given estoy autenticado como "visual_user"
    Then los precios del listado no coinciden con el catálogo:
      | Sauce Labs Backpack               | $29.99 |
      | Sauce Labs Bike Light             | $9.99  |
      | Sauce Labs Bolt T-Shirt           | $15.99 |
      | Sauce Labs Fleece Jacket          | $49.99 |
      | Sauce Labs Onesie                 | $7.99  |
      | Test.allTheThings() T-Shirt (Red) | $15.99 |
