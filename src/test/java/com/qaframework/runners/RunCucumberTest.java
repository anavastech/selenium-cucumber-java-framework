package com.qaframework.runners;

import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;

/**
 * Punto de entrada de la suite. El resto de la configuración (plugins, paralelismo) vive en
 * {@code junit-platform.properties}; el filtro de tags se pasa por línea de comando:
 *
 * <pre>mvn test -Dcucumber.filter.tags="@smoke and @ui"</pre>
 */
@Suite
@IncludeEngines("cucumber")
@SelectPackages("features")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "com.qaframework")
public class RunCucumberTest {}
