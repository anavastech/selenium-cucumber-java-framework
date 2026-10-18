package com.qaframework.pages;

import java.util.Arrays;
import java.util.Comparator;

/** Opciones del combo de ordenamiento, con la etiqueta en lenguaje de negocio que usan los .feature. */
public enum SortOption {
  PRICE_LOW_TO_HIGH("precio ascendente", "lohi", true, false),
  PRICE_HIGH_TO_LOW("precio descendente", "hilo", true, true),
  NAME_A_TO_Z("nombre A a Z", "az", false, false),
  NAME_Z_TO_A("nombre Z a A", "za", false, true);

  private final String label;
  private final String value;
  private final boolean byPrice;
  private final boolean descending;

  SortOption(String label, String value, boolean byPrice, boolean descending) {
    this.label = label;
    this.value = value;
    this.byPrice = byPrice;
    this.descending = descending;
  }

  /** Valor del {@code <option>} en el DOM. */
  public String value() {
    return value;
  }

  public boolean byPrice() {
    return byPrice;
  }

  public <T extends Comparable<? super T>> Comparator<T> comparator() {
    return descending ? Comparator.reverseOrder() : Comparator.naturalOrder();
  }

  public static SortOption fromLabel(String label) {
    return Arrays.stream(values())
        .filter(option -> option.label.equals(label))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Orden desconocido: " + label));
  }
}
