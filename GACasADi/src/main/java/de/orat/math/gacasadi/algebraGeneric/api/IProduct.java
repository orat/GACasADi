package de.orat.math.gacasadi.algebraGeneric.api;

public interface IProduct<MV extends IMultivector> {

    CoefficientAndBasisBladeIndex product(int basisBladeIndex1, int basisBladeIndex2);

    // IMultivector product(MV a, MV b);
}
