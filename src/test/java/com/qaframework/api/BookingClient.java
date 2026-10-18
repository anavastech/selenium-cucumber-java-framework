package com.qaframework.api;

import static io.restassured.RestAssured.given;

import com.qaframework.config.Config;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.EncoderConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import java.util.Map;

/**
 * Cliente de restful-booker. Encapsula URLs, headers y autenticación para que los steps solo
 * expresen la intención ("crear", "actualizar sin token") y no el detalle HTTP.
 *
 * <p>Devuelve el {@link Response} crudo: los asserts de status/contrato se hacen en los steps,
 * porque muchos escenarios negativos esperan justamente respuestas de error.
 */
public class BookingClient {

  private final RequestSpecification spec =
      new RequestSpecBuilder()
          .setBaseUri(Config.apiBaseUrl())
          // restful-booker es estricto con los headers (verificado con curl):
          // - Content-Type con "; charset=ISO-8859-1" (default de REST Assured) -> 415
          // - Accept con la lista que manda ContentType.JSON -> 418 (I'm a Teapot)
          .setConfig(
              RestAssuredConfig.config()
                  .encoderConfig(
                      EncoderConfig.encoderConfig()
                          .appendDefaultContentCharsetToContentTypeIfUndefined(false)))
          .setContentType(ContentType.JSON)
          .setAccept("application/json")
          // Cada request/response queda adjunto en el reporte de Allure.
          .addFilter(new AllureRestAssured())
          .build();

  public Response ping() {
    return given(spec).get("/ping");
  }

  public Response auth(String username, String password) {
    return given(spec).body(Map.of("username", username, "password", password)).post("/auth");
  }

  /** Token de admin listo para usar. Falla con un mensaje claro si la API no lo devuelve. */
  public String createToken() {
    Response response = auth(Config.bookerUsername(), Config.bookerPassword());
    String token = response.path("token");
    if (token == null || token.isBlank()) {
      throw new IllegalStateException(
          "restful-booker no devolvió un token. Respuesta: " + response.asString());
    }
    return token;
  }

  public Response create(Object body) {
    return given(spec).body(body).post("/booking");
  }

  public Response get(int bookingId) {
    return given(spec).get("/booking/{id}", bookingId);
  }

  public Response search(Map<String, ?> queryParams) {
    return given(spec).queryParams(queryParams).get("/booking");
  }

  /** {@code token} puede ser null para probar el rechazo sin autenticación. */
  public Response update(int bookingId, Object body, String token) {
    return withToken(token).body(body).put("/booking/{id}", bookingId);
  }

  public Response patch(int bookingId, Map<String, ?> fields, String token) {
    return withToken(token).body(fields).patch("/booking/{id}", bookingId);
  }

  public Response delete(int bookingId, String token) {
    return withToken(token).delete("/booking/{id}", bookingId);
  }

  private RequestSpecification withToken(String token) {
    RequestSpecification request = given(spec);
    return token == null ? request : request.cookie("token", token);
  }
}
