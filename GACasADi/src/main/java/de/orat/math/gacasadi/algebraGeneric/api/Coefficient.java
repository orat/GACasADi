package de.orat.math.gacasadi.algebraGeneric.api;

/**
 * A coefficent is a double.
 */
public record Coefficient(float coefficient) {

    public static final Coefficient ZERO = new Coefficient(0f);
    public static final Coefficient ONE = new Coefficient(1f);
}
