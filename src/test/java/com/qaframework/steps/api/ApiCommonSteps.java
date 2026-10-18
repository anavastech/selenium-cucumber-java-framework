package com.qaframework.steps.api;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.assertj.core.api.Assertions.assertThat;

import com.qaframework.context.ApiContext;
import io.cucumber.java.en.Then;
import io.restassured.response.Response;

public class ApiCommonSteps {

  private final ApiContext api;

  public ApiCommonSteps(ApiContext api) {
    this.api = api;
  }

  @Then("recibo un status {int}")
  public void assertStatus(int expected) {
    Response response = api.lastResponse();
    assertThat(response.statusCode())
        .as("status HTTP. Body: %s", response.asString())
        .isEqualTo(expected);
  }

  /**
   * Contrato documentado una sola vez en {@code src/test/resources/schemas}: si restful-booker cambia
   * la forma de la respuesta, falla acá diciendo qué campo cambió.
   */
  @Then("la respuesta cumple el esquema {string}")
  public void assertMatchesSchema(String schemaName) {
    api.lastResponse()
        .then()
        .assertThat()
        .body(matchesJsonSchemaInClasspath("schemas/" + schemaName + ".json"));
  }
}
