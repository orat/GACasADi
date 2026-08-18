package de.orat.math.gacasadi.specific.cga;

import de.orat.math.gacalc.spi.IMultivector;
import java.util.function.Supplier;

public abstract class CgaConstants<MV extends IMultivector<MV>> {

    public CgaFactory fac() {
        return CgaFactory.instance;
    }

    abstract MV cached(String name, Supplier<CgaMvValue> creator);

    public abstract MV getSparseEmptyInstance();

    public MV getBaseVectorOrigin() {
        return cached("ε₀", () -> fac().createBaseVectorOrigin());
    }

    public MV getBaseVectorInfinity() {
        return cached("εᵢ", () -> fac().createBaseVectorInfinity());
    }

    public MV getBaseVectorX() {
        return cached("ε₁", () -> fac().createBaseVectorX());
    }

    public MV getBaseVectorY() {
        return cached("ε₂", () -> fac().createBaseVectorY());
    }

    public MV getBaseVectorZ() {
        return cached("ε₃", () -> fac().createBaseVectorZ());
    }

    public MV getEpsilonPlus() {
        return cached("ε₊", () -> fac().createEpsilonPlus());
    }

    public MV getEpsilonMinus() {
        return cached("ε₋", () -> fac().createEpsilonMinus());
    }

    public MV getPi() {
        return cached("π", () -> fac().createScalar(Math.PI));
    }

    public MV getBaseVectorInfinityDorst() {
        return cached("∞", () -> fac().createBaseVectorInfinityDorst());
    }

    public MV getBaseVectorOriginDorst() {
        return cached("o", () -> fac().createBaseVectorOriginDorst());
    }

    public MV getBaseVectorInfinityDoran() {
        return cached("n", () -> fac().createBaseVectorInfinityDoran());
    }

    public MV getBaseVectorOriginDoran() {
        return cached("ñ", () -> fac().createBaseVectorOriginDoran());
    }

    public MV getMinkovskiBiVector() {
        return cached("E₀", () -> fac().createMinkovskiBiVector());
    }

    public MV getEuclideanPseudoscalar() {
        return cached("E₃", () -> fac().createEuclideanPseudoscalar());
    }

    public MV getPseudoscalar() {
        return cached("I", () -> fac().createPseudoscalar());
    }

    public MV getInversePseudoscalar() {
        return cached("I˜", () -> fac().createInversePseudoscalar());
    }

    public MV one() {
        return cached("1", () -> fac().createScalar(1d));
    }

    public MV half() {
        return cached("0.5", () -> fac().createScalar(0.5d));
    }
}
