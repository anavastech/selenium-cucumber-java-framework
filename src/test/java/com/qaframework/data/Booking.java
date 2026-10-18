package com.qaframework.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** Cuerpo de una reserva de restful-booker. Jackson lo serializa/deserializa directamente. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record Booking(
    String firstname,
    String lastname,
    Integer totalprice,
    Boolean depositpaid,
    BookingDates bookingdates,
    String additionalneeds) {

  @JsonIgnoreProperties(ignoreUnknown = true)
  public record BookingDates(String checkin, String checkout) {}

  public Booking withTotalprice(int newPrice) {
    return new Booking(firstname, lastname, newPrice, depositpaid, bookingdates, additionalneeds);
  }
}
