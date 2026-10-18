package com.qaframework.data;

import java.time.LocalDate;
import java.util.UUID;
import net.datafaker.Faker;

/** Datos aleatorios pero consistentes para cada escenario (equivalente a faker en el proyecto TS). */
public final class TestDataFactory {

  private static final Faker FAKER = new Faker();

  private TestDataFactory() {}

  /** Reserva con checkout siempre posterior al checkin. */
  public static Booking booking() {
    LocalDate checkin = LocalDate.now().plusDays(FAKER.number().numberBetween(1, 10));
    LocalDate checkout = checkin.plusDays(FAKER.number().numberBetween(1, 10));
    return new Booking(
        FAKER.name().firstName(),
        FAKER.name().lastName(),
        FAKER.number().numberBetween(50, 500),
        FAKER.bool().bool(),
        new Booking.BookingDates(checkin.toString(), checkout.toString()),
        FAKER.options().option("Breakfast", "Lunch", "Dinner", "None"));
  }

  /** Reserva con un nombre único, para poder encontrarla por búsqueda sin falsos positivos. */
  public static Booking bookingWithUniqueName() {
    Booking base = booking();
    String uniqueName = "Qa" + UUID.randomUUID().toString().replace("-", "").substring(0, 8);
    return new Booking(
        uniqueName,
        base.lastname(),
        base.totalprice(),
        base.depositpaid(),
        base.bookingdates(),
        base.additionalneeds());
  }

  /** Precio fuera del rango de {@link #booking()}, para que un update siempre cambie el valor. */
  public static int updatedPrice() {
    return FAKER.number().numberBetween(501, 1000);
  }

  public static String firstName() {
    return FAKER.name().firstName();
  }

  public static String lastName() {
    return FAKER.name().lastName();
  }

  public static String postalCode() {
    return FAKER.address().zipCode();
  }
}
