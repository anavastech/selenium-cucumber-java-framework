package com.qaframework.steps.api;

import static org.assertj.core.api.Assertions.assertThat;

import com.qaframework.context.ApiContext;
import com.qaframework.data.Booking;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class BookingSearchSteps {

  private final ApiContext api;

  public BookingSearchSteps(ApiContext api) {
    this.api = api;
  }

  @When("busco reservas por su nombre y apellido")
  public void searchByName() {
    Booking booking = api.booking();
    api.setLastResponse(
        api.client()
            .search(Map.of("firstname", booking.firstname(), "lastname", booking.lastname())));
  }

  @When("busco reservas con un nombre que no existe")
  public void searchByUnknownName() {
    api.setLastResponse(api.client().search(Map.of("firstname", "NoExiste" + UUID.randomUUID())));
  }

  @When("busco reservas por sus fechas exactas de checkin y checkout")
  public void searchByExactDates() {
    Booking.BookingDates dates = api.booking().bookingdates();
    api.setLastResponse(
        api.client().search(Map.of("checkin", dates.checkin(), "checkout", dates.checkout())));
  }

  @Then("la reserva aparece en el resultado")
  public void assertBookingInResults() {
    assertThat(resultIds()).contains(api.bookingId());
  }

  @Then("la reserva no aparece en el resultado")
  public void assertBookingNotInResults() {
    assertThat(resultIds()).doesNotContain(api.bookingId());
  }

  @Then("el resultado es una lista vacía")
  public void assertEmptyResult() {
    assertThat(api.lastResponse().jsonPath().getList("$")).isEmpty();
  }

  private List<Integer> resultIds() {
    return api.lastResponse().jsonPath().getList("bookingid", Integer.class);
  }
}
