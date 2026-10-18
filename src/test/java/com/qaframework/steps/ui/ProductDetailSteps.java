package com.qaframework.steps.ui;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import com.qaframework.context.UiContext;
import com.qaframework.pages.InventoryPage;
import com.qaframework.pages.ProductDetailPage;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class ProductDetailSteps {

  private final UiContext ui;
  private String listedName;
  private String listedPrice;

  public ProductDetailSteps(UiContext ui) {
    this.ui = ui;
  }

  @When("abro el detalle del producto {string}")
  public void openProductDetail(String productName) {
    InventoryPage inventory = ui.inventoryPage();
    listedName = productName;
    listedPrice = inventory.priceOf(productName);
    inventory.openProductDetail(productName);
    ui.productDetailPage().waitUntilLoaded();
  }

  @Then("el detalle muestra el mismo nombre y precio que el listado")
  public void assertDetailMatchesListing() {
    ProductDetailPage detail = ui.productDetailPage();
    assertThat(detail.name()).isEqualTo(listedName);
    assertThat(detail.price()).isEqualTo(listedPrice);
  }

  @When("lo agrego al carrito desde el detalle")
  public void addFromDetail() {
    ui.productDetailPage().addToCart();
  }

  @When("lo quito del carrito desde el detalle")
  public void removeFromDetail() {
    ui.productDetailPage().removeFromCart();
  }

  @Then("el detalle ofrece quitarlo del carrito")
  public void assertRemoveOffered() {
    await()
        .untilAsserted(
            () -> assertThat(ui.productDetailPage().isRemoveDisplayed()).as("botón Remove visible").isTrue());
  }

  @Then("el detalle ofrece agregarlo al carrito")
  public void assertAddOffered() {
    await()
        .untilAsserted(
            () ->
                assertThat(ui.productDetailPage().isAddToCartDisplayed())
                    .as("botón Add to cart visible")
                    .isTrue());
  }

  @When("vuelvo al listado con \"Back to products\"")
  public void backToProducts() {
    ui.productDetailPage().backToProducts();
  }
}
