package com.qaframework.context;

import com.qaframework.api.BookingClient;
import com.qaframework.data.Booking;
import io.restassured.response.Response;
import java.util.LinkedHashSet;
import java.util.Set;

/** Estado de API compartido entre las step classes de un mismo escenario (inyectado por PicoContainer). */
public class ApiContext {

  private final BookingClient client = new BookingClient();
  private final Set<Integer> createdBookingIds = new LinkedHashSet<>();

  private String token;
  private Response lastResponse;
  private Booking booking;
  private Integer bookingId;

  public BookingClient client() {
    return client;
  }

  /** Token de admin, pedido una sola vez por escenario. */
  public String token() {
    if (token == null) {
      token = client.createToken();
    }
    return token;
  }

  public Response lastResponse() {
    if (lastResponse == null) {
      throw new IllegalStateException("Ningún step hizo todavía un request en este escenario");
    }
    return lastResponse;
  }

  public void setLastResponse(Response response) {
    this.lastResponse = response;
  }

  public Booking booking() {
    return booking;
  }

  public int bookingId() {
    if (bookingId == null) {
      throw new IllegalStateException("Ningún step creó todavía una reserva en este escenario");
    }
    return bookingId;
  }

  /** Registra la reserva creada para poder borrarla en el hook @After y no ensuciar el entorno. */
  public void rememberCreatedBooking(int id, Booking sent) {
    this.bookingId = id;
    this.booking = sent;
    createdBookingIds.add(id);
  }

  public void forgetBooking(int id) {
    createdBookingIds.remove(id);
  }

  public Set<Integer> createdBookingIds() {
    return Set.copyOf(createdBookingIds);
  }
}
