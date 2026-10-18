package com.qaframework.data;

import java.util.Arrays;

/** Usuarios públicos de SauceDemo. Todos comparten la misma contraseña. */
public enum SauceUser {
  STANDARD("standard_user"),
  LOCKED_OUT("locked_out_user"),
  PROBLEM("problem_user"),
  PERFORMANCE_GLITCH("performance_glitch_user"),
  ERROR("error_user"),
  VISUAL("visual_user");

  private static final String PASSWORD = "secret_sauce";

  private final String username;

  SauceUser(String username) {
    this.username = username;
  }

  public String username() {
    return username;
  }

  public String password() {
    return PASSWORD;
  }

  /** Permite que los .feature usen el username real ("locked_out_user") en vez del nombre del enum. */
  public static SauceUser fromUsername(String username) {
    return Arrays.stream(values())
        .filter(user -> user.username.equals(username))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Usuario de SauceDemo desconocido: " + username));
  }
}
