package com.qaframework.steps.api;

import static org.assertj.core.api.Assertions.assertThat;

import com.qaframework.config.Config;
import com.qaframework.context.ApiContext;
import com.qaframework.data.Booking;
import com.qaframework.data.TestDataFactory;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import java.util.LinkedHashMap;
import java.util.Map;

public class BookingSteps {

  private final ApiContext api;
  private int expectedPrice;

  public BookingSteps(ApiContext api) {
    this.api = api;
  }

  // --- Autenticación ---

  @When("solicito un token con credenciales de administrador")
  public void requestAdminToken() {
    api.setLastResponse(api.client().auth(Config.bookerUsername(), Config.bookerPassword()));
  }

  @When("solicito un token con usuario {string} y contraseña {string}")
  public void requestToken(String username, String password) {
    api.setLastResponse(api.client().auth(username, password));
  }

  @Then("recibo un token no vacío")
  public void assertTokenNotBlank() {
    String token = api.lastResponse().path("token");
    assertThat(token).isNotBlank();
  }

  @Then("la respuesta indica la razón {string}")
  public void assertReason(String reason) {
    assertThat(api.lastResponse().jsonPath().getMap("")).isEqualTo(Map.of("reason", reason));
  }

  // --- CRUD ---

  @When("creo una reserva")
  public void createBooking() {
    create(TestDataFactory.booking());
  }

  @Given("existe una reserva")
  public void existingBooking() {
    create(TestDataFactory.booking());
    assertThat(api.lastResponse().statusCode()).as("precondición: crear la reserva").isEqualTo(200);
  }

  @Given("existe una reserva con un nombre único")
  public void existingBookingWithUniqueName() {
    create(TestDataFactory.bookingWithUniqueName());
    assertThat(api.lastResponse().statusCode()).as("precondición: crear la reserva").isEqualTo(200);
  }

  @When("consulto la reserva creada")
  public void getCreatedBooking() {
    api.setLastResponse(api.client().get(api.bookingId()));
  }

  @When("consulto la reserva con id {int}")
  public void getBookingById(int bookingId) {
    api.setLastResponse(api.client().get(bookingId));
  }

  @Then("la reserva consultada tiene los mismos datos que envié")
  public void assertFetchedMatchesSent() {
    // Comparar records completos: un solo assert cubre todos los campos.
    assertThat(api.lastResponse().as(Booking.class)).isEqualTo(api.booking());
  }

  @When("actualizo el precio de la reserva con PUT")
  public void updatePriceWithPut() {
    expectedPrice = TestDataFactory.updatedPrice();
    Booking updated = api.booking().withTotalprice(expectedPrice);
    api.setLastResponse(api.client().update(api.bookingId(), updated, api.token()));
  }

  @When("actualizo solo el precio con PATCH")
  public void updatePriceWithPatch() {
    expectedPrice = TestDataFactory.updatedPrice();
    api.setLastResponse(
        api.client().patch(api.bookingId(), Map.of("totalprice", expectedPrice), api.token()));
  }

  @Then("el precio queda actualizado")
  public void assertPriceUpdated() {
    Integer totalprice = api.lastResponse().path("totalprice");
    assertThat(totalprice).isEqualTo(expectedPrice);
  }

  @Then("el resto de los datos no se ve afectado")
  public void assertOtherFieldsUnchanged() {
    assertThat(api.lastResponse().as(Booking.class))
        .isEqualTo(api.booking().withTotalprice(expectedPrice));
  }

  @When("elimino la reserva")
  public void deleteBooking() {
    int bookingId = api.bookingId();
    Response response = api.client().delete(bookingId, api.token());
    api.setLastResponse(response);
    if (response.statusCode() == 201) {
      api.forgetBooking(bookingId);
    }
  }

  // --- Negativos ---

  @When("intento actualizar la reserva {int} sin token")
  public void updateWithoutToken(int bookingId) {
    api.setLastResponse(api.client().update(bookingId, TestDataFactory.booking(), null));
  }

  @When("intento actualizar la reserva {int} con el token {string}")
  public void updateWithToken(int bookingId, String token) {
    api.setLastResponse(api.client().update(bookingId, TestDataFactory.booking(), token));
  }

  @When("intento borrar la reserva {int} con el token {string}")
  public void deleteWithToken(int bookingId, String token) {
    api.setLastResponse(api.client().delete(bookingId, token));
  }

  @When("intento crear una reserva con body vacío")
  public void createWithEmptyBody() {
    api.setLastResponse(api.client().create(Map.of()));
  }

  @When("intento crear una reserva solo con el nombre {string}")
  public void createWithOnlyFirstname(String firstname) {
    api.setLastResponse(api.client().create(Map.of("firstname", firstname)));
  }

  @When("creo una reserva con el precio {string}")
  public void createWithInvalidPrice(String invalidPrice) {
    Booking valid = TestDataFactory.booking();
    Map<String, Object> body = new LinkedHashMap<>();
    body.put("firstname", valid.firstname());
    body.put("lastname", valid.lastname());
    body.put("totalprice", invalidPrice);
    body.put("depositpaid", valid.depositpaid());
    body.put("bookingdates", valid.bookingdates());
    body.put("additionalneeds", valid.additionalneeds());

    Response response = api.client().create(body);
    api.setLastResponse(response);
    rememberIfCreated(response, valid);
  }

  @Then("la reserva creada tiene el precio en null")
  public void assertCreatedPriceIsNull() {
    Object totalprice = api.lastResponse().path("booking.totalprice");
    assertThat(totalprice).isNull();
  }

  @When("consulto el endpoint de salud")
  public void ping() {
    api.setLastResponse(api.client().ping());
  }

  private void create(Booking booking) {
    Response response = api.client().create(booking);
    api.setLastResponse(response);
    rememberIfCreated(response, booking);
  }

  /** Toda reserva creada queda registrada para que el hook @After la borre. */
  private void rememberIfCreated(Response response, Booking sent) {
    if (response.statusCode() == 200) {
      api.rememberCreatedBooking(response.path("bookingid"), sent);
    }
  }
}
