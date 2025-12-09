package de.orat.math.gacasadi.algebraGeneric.impl.gaalop;

import de.gaalop.tba.MultTableAbsDirectComputer;
import de.orat.math.gacasadi.algebraGeneric.api.CoefficientAndBasisBladeIndex;
import de.orat.math.gacasadi.algebraGeneric.api.IProduct;

public class GeometricProduct implements IProduct<Multivector> {

    private final GaalopAlgebra algebra;
    private final MultTableAbsDirectComputer comp;

    public GeometricProduct(GaalopAlgebra algebra, MultTableAbsDirectComputer comp) {
        this.algebra = algebra;
        this.comp = comp;
    }

    @Override
    public CoefficientAndBasisBladeIndex product(int basisBladeIndex1, int basisBladeIndex2) {
        var mv = this.comp.getProduct(basisBladeIndex1, basisBladeIndex2);
        var blades = mv.getBlades();
        if (blades.size() != 1) {
            throw new AssertionError();
        }
        var blade = blades.get(0);
        int index = blade.getIndex();
        byte prefactor = blade.getPrefactor();
        if (prefactor == 0) {
            return CoefficientAndBasisBladeIndex.ZERO;
        }
        var cbbi = new CoefficientAndBasisBladeIndex(prefactor, index);
        return cbbi;
    }
}
