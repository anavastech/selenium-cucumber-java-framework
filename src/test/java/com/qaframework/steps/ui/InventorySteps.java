package com.qaframework.steps.ui;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import com.qaframework.context.UiContext;
import com.qaframework.pages.InventoryPage;
import com.qaframework.pages.SortOption;
import io.cucumber.java.ParameterType;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class InventorySteps {

  private final UiContext ui;
  private List<String> namesBeforeSort;

  public InventorySteps(UiContext ui) {
    this.ui = ui;
  }

  /** Parameter type propio: el .feature dice "precio ascendente" y el step recibe un SortOption. */
  @ParameterType("precio ascendente|precio descendente|nombre A a Z|nombre Z a A")
  public SortOption orden(String label) {
    return SortOption.fromLabel(label);
  }

  @When("ordeno los productos por {orden}")
  public void sortProducts(SortOption option) {
    InventoryPage inventory = ui.inventoryPage();
    namesBeforeSort = inventory.productNames();
    inventory.sortBy(option.value());
  }

  @Then("los productos quedan ordenados por {orden}")
  public void assertSorted(SortOption option) {
    InventoryPage inventory = ui.inventoryPage();
    await()
        .untilAsserted(
            () -> {
              if (option.byPrice()) {
                assertThat(inventory.productPrices()).isSortedAccordingTo(option.comparator());
              } else {
                assertThat(inventory.productNames()).isSortedAccordingTo(option.comparator());
              }
            });
  }

  @Then("el orden de los productos no cambia")
  public void assertOrderUnchanged() {
    assertThat(ui.inventoryPage().productNames()).containsExactlyElementsOf(namesBeforeSort);
  }

  @Then("el producto {string} muestra la imagen {string}")
  public void assertProductImage(String productName, String imageName) {
    // SauceDemo agrega un hash al nombre del archivo (sl-404-Cq1a9k9X.jpg): comparamos por el prefijo.
    assertThat(ui.inventoryPage().imageSrcOf(productName)).contains("/" + imageName);
  }

  @Then("los precios del listado no coinciden con el catálogo:")
  public void assertPricesDifferFromCatalog(Map<String, String> catalog) {
    InventoryPage inventory = ui.inventoryPage();
    Map<String, String> shown =
        catalog.keySet().stream().collect(Collectors.toMap(Function.identity(), inventory::priceOf));
    assertThat(shown).isNotEqualTo(catalog);
  }

  @When("agrego al carrito el producto {string}")
  public void addProductToCart(String productName) {
    ui.inventoryPage().addToCart(productName);
  }

  @When("agrego al carrito los productos:")
  public void addProductsToCart(List<String> productNames) {
    InventoryPage inventory = ui.inventoryPage();
    productNames.forEach(inventory::addToCart);
  }

  @When("quito del carrito desde el inventario el producto {string}")
  public void removeProductFromInventory(String productName) {
    ui.inventoryPage().removeFromCart(productName);
  }

  @When("reseteo el estado de la app desde el menú")
  public void resetAppState() {
    ui.header().resetAppState();
  }

  @Then("el badge del carrito muestra {string}")
  public void assertCartBadge(String expected) {
    await().untilAsserted(() -> assertThat(ui.header().cartBadgeText()).isEqualTo(expected));
  }

  @Then("el badge del carrito desaparece")
  public void assertCartBadgeGone() {
    await()
        .untilAsserted(
            () -> assertThat(ui.header().isCartBadgeDisplayed()).as("badge del carrito visible").isFalse());
  }

  @Then("estoy en la página de inventario")
  public void assertOnInventoryPage() {
    await().untilAsserted(() -> assertThat(ui.driver().getCurrentUrl()).contains("inventory.html"));
  }

  @Then("estoy en la página de inventario en menos de {int} segundos")
  public void assertOnInventoryPageWithin(int seconds) {
    await()
        .atMost(Duration.ofSeconds(seconds))
        .untilAsserted(() -> assertThat(ui.driver().getCurrentUrl()).contains("inventory.html"));
  }

  @Then("no estoy en la página de inventario")
  public void assertNotOnInventoryPage() {
    await()
        .untilAsserted(() -> assertThat(ui.driver().getCurrentUrl()).doesNotContain("inventory.html"));
  }
}
