package com.qaframework.steps.ui;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import com.qaframework.config.Config;
import com.qaframework.context.UiContext;
import com.qaframework.data.SauceUser;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class AuthSteps {

  private final UiContext ui;

  public AuthSteps(UiContext ui) {
    this.ui = ui;
  }

  @Given("estoy en la página de login")
  public void openLoginPage() {
    ui.loginPage().open();
  }

  /** Precondición de la mayoría de los escenarios de UI: deja el inventario ya renderizado. */
  @Given("estoy autenticado en el inventario")
  public void loginAsStandardUser() {
    loginAs(SauceUser.STANDARD);
  }

  @Given("estoy autenticado como {string}")
  public void loginAsUser(String username) {
    loginAs(SauceUser.fromUsername(username));
  }

  @When("ingreso las credenciales de {string}")
  public void enterCredentialsOf(String username) {
    SauceUser user = SauceUser.fromUsername(username);
    ui.loginPage().login(user.username(), user.password());
  }

  @When("ingreso el usuario {string} y la contraseña {string}")
  public void enterCredentials(String username, String password) {
    ui.loginPage().login(username, password);
  }

  @When("intento ingresar sin usuario ni contraseña")
  public void submitEmptyCredentials() {
    ui.loginPage().login("", "");
  }

  @When("cierro sesión desde el menú")
  public void logout() {
    ui.header().logout();
  }

  @When("intento navegar directamente a {string}")
  public void navigateDirectlyTo(String path) {
    ui.loginPage().openPath(path);
  }

  @Then("vuelvo a la página de login")
  public void assertOnLoginPage() {
    await()
        .untilAsserted(
            () -> {
              assertThat(ui.driver().getCurrentUrl()).isEqualTo(Config.uiBaseUrl() + "/");
              assertThat(ui.loginPage().isDisplayed()).as("formulario de login visible").isTrue();
            });
  }

  @Then("veo el mensaje de error {string}")
  public void assertErrorMessage(String expected) {
    await().untilAsserted(() -> assertThat(ui.errorBanner().text()).contains(expected));
  }

  private void loginAs(SauceUser user) {
    ui.loginPage().open().login(user.username(), user.password());
    ui.inventoryPage().waitUntilLoaded();
  }
}
