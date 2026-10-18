package com.qaframework.steps.ui;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import com.qaframework.context.UiContext;
import com.qaframework.data.TestDataFactory;
import com.qaframework.pages.CheckoutPage;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.math.BigDecimal;
import java.time.Duration;

public class CheckoutSteps {

  private final UiContext ui;

  public CheckoutSteps(UiContext ui) {
    this.ui = ui;
  }

  @When("inicio el checkout desde el carrito")
  public void startCheckout() {
    ui.header().goToCart();
    ui.cartPage().waitUntilLoaded().checkout();
    ui.checkoutPage().waitForInformationStep();
  }

  @When("completo mis datos de envío")
  public void fillShippingInfo() {
    CheckoutPage checkout = ui.checkoutPage();
    checkout.fillInfo(
        TestDataFactory.firstName(), TestDataFactory.lastName(), TestDataFactory.postalCode());
    checkout.continueToOverview();
    checkout.waitForOverview();
  }

  @When("completo mis datos de envío sin código postal")
  public void fillShippingInfoWithoutPostalCode() {
    CheckoutPage checkout = ui.checkoutPage();
    checkout.fillInfo(TestDataFactory.firstName(), TestDataFactory.lastName(), "");
    checkout.continueToOverview();
  }

  @When("finalizo la compra")
  public void finishPurchase() {
    ui.checkoutPage().finish();
  }

  @When("cancelo el checkout")
  public void cancelCheckout() {
    ui.checkoutPage().cancel();
  }

  @Then("veo la confirmación {string}")
  public void assertConfirmation(String expected) {
    await().untilAsserted(() -> assertThat(ui.checkoutPage().completeHeader()).isEqualTo(expected));
  }

  @Then("el total es igual al subtotal más el impuesto")
  public void assertTotalMatchesSubtotalPlusTax() {
    CheckoutPage checkout = ui.checkoutPage();
    BigDecimal subtotal = checkout.subtotal();
    BigDecimal tax = checkout.tax();

    // BigDecimal en vez de double: comparación exacta de montos, sin tolerancias de punto flotante.
    assertThat(checkout.total())
        .as("total = subtotal (%s) + impuesto (%s)", subtotal, tax)
        .isEqualByComparingTo(subtotal.add(tax));
  }

  /**
   * Un assert instantáneo pasaría aunque la confirmación llegara medio segundo después. Con
   * {@code during} exigimos que la condición se mantenga durante 2 segundos.
   */
  @Then("permanezco en el resumen sin confirmación de compra")
  public void assertStuckOnOverview() {
    CheckoutPage checkout = ui.checkoutPage();
    await()
        .during(Duration.ofSeconds(2))
        .atMost(Duration.ofSeconds(5))
        .untilAsserted(
            () -> {
              assertThat(ui.driver().getCurrentUrl()).contains("checkout-step-two.html");
              assertThat(checkout.isOrderCompleteDisplayed()).as("confirmación de compra visible").isFalse();
            });
  }
}
