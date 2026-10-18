package com.qaframework.steps.ui;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import com.qaframework.context.UiContext;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class CartSteps {

  private final UiContext ui;

  public CartSteps(UiContext ui) {
    this.ui = ui;
  }

  @When("voy al carrito")
  public void goToCart() {
    ui.header().goToCart();
    ui.cartPage().waitUntilLoaded();
  }

  @When("presiono \"Continue Shopping\"")
  public void continueShopping() {
    ui.cartPage().continueShopping();
  }

  @When("quito desde el carrito el producto {string}")
  public void removeFromCart(String productName) {
    ui.cartPage().removeFromCart(productName);
  }

  @Then("el carrito lista {int} producto(s)")
  public void assertCartItemCount(int expected) {
    await().untilAsserted(() -> assertThat(ui.cartPage().productNames()).hasSize(expected));
  }

  @Then("estoy en la página del carrito")
  public void assertOnCartPage() {
    await().untilAsserted(() -> assertThat(ui.driver().getCurrentUrl()).contains("cart.html"));
  }
}
