package de.orat.math.gacasadi.specific.cga;

import de.orat.math.gacalc.spi.IMultivector;
import de.orat.math.sparsematrix.SparseDoubleMatrix;
import java.util.function.Supplier;

public abstract class CgaConstants<MV extends IMultivector<MV>> {

    public CgaFactory fac() {
        return CgaFactory.instance;
    }

    abstract MV cached(String name, Supplier<SparseDoubleMatrix> creator);

    public abstract MV getSparseEmptyInstance();

    public MV getBaseVectorOrigin() {
        return cached("ε₀", () -> fac().createBaseVectorOrigin(1d));
    }

    public MV getBaseVectorInfinity() {
        return cached("εᵢ", () -> fac().createBaseVectorInfinity(1d));
    }

    public MV getBaseVectorX() {
        return cached("ε₁", () -> fac().createBaseVectorX(1d));
    }

    public MV getBaseVectorY() {
        return cached("ε₂", () -> fac().createBaseVectorY(1d));
    }

    public MV getBaseVectorZ() {
        return cached("ε₃", () -> fac().createBaseVectorZ(1d));
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
        return cached("E", () -> fac().createPseudoscalar());
    }

    public MV getInversePseudoscalar() {
        return cached("E˜", () -> fac().createInversePseudoscalar());
    }

    public MV one() {
        return cached("1", () -> fac().createScalar(1d));
    }

    public MV half() {
        return cached("0.5", () -> fac().createScalar(0.5d));
    }
}
