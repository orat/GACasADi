package de.orat.math.gacasadi.algebraGeneric.api;

public interface IProduct {

    /**
     * <pre>
     * Implementation could just return a cached cayley table entry.
     * Can be sometimes more than just one (Coefficient, Basisblade), if base change is used in Gaalop.
     * </pre>
     */
    Multivector product(int basisBladeIndex1, int basisBladeIndex2);

    // CoefficientAndBasisBladeIndex product(int basisBladeIndex1, int basisBladeIndex2);
    // Multivector product(Multivector a, Multivector b);
}
