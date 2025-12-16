package de.orat.math.gacasadi.algebraGeneric.api;

import java.util.List;

public record CoefficientAndBasisBlade(Coefficient coefficient, BasisBlade basisBlade) {

    public static final CoefficientAndBasisBlade ZERO = new CoefficientAndBasisBlade(Coefficient.ZERO, BasisBlade.SCALAR);

    public CoefficientAndBasisBlade(float coefficient, List<Integer> baseVectorIndices) {
        this(new Coefficient(coefficient), new BasisBlade(baseVectorIndices));
    }
}
