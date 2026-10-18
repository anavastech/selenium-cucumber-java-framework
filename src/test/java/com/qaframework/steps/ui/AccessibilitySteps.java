package com.qaframework.steps.ui;

import static org.assertj.core.api.Assertions.assertThat;

import com.deque.html.axecore.results.Results;
import com.deque.html.axecore.results.Rule;
import com.deque.html.axecore.selenium.AxeBuilder;
import com.qaframework.context.UiContext;
import io.cucumber.java.en.Then;
import java.util.List;
import java.util.stream.Collectors;

public class AccessibilitySteps {

  private static final List<String> WCAG_2_A_AA = List.of("wcag2a", "wcag2aa");

  private final UiContext ui;

  public AccessibilitySteps(UiContext ui) {
    this.ui = ui;
  }

  @Then("la página no tiene violaciones WCAG 2.0 A\\/AA")
  public void assertNoAccessibilityViolations() {
    Results results = new AxeBuilder().withTags(WCAG_2_A_AA).analyze(ui.driver());

    assertThat(results.getViolations()).as(describe(results.getViolations())).isEmpty();
  }

  /** Resumen legible en el reporte: regla, impacto y cantidad de nodos afectados. */
  private static String describe(List<Rule> violations) {
    return violations.stream()
        .map(
            rule ->
                "%s (%s): %s - %d nodo(s)"
                    .formatted(rule.getId(), rule.getImpact(), rule.getHelp(), rule.getNodes().size()))
        .collect(Collectors.joining("\n", "Violaciones de accesibilidad:\n", ""));
  }
}
