package de.orat.math.gacasadi.algebraGeneric.api;

public record CoefficientAndBasisBladeIndex(float coefficient, int basisBladeIndex) {

    public static final CoefficientAndBasisBladeIndex ZERO = new CoefficientAndBasisBladeIndex(0f, 0);
}
