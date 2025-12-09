package de.orat.math.gacasadi.algebraGeneric.impl.simple;

import de.orat.math.gacasadi.algebraGeneric.api.Coefficient;
import de.orat.math.gacasadi.algebraGeneric.api.IMultivector;
import java.util.List;

public record Multivector(List<Integer> basisBladeIndices, List<Coefficient> coefficents) implements IMultivector {

    /**
     * <pre>
     * Preconditions:
     * - basisBladeIndices are only ones which are available in the algebra.
     * - basisBladeIndices.size() == coefficents.size()
     * </pre>
     */
    public Multivector(List<Integer> basisBladeIndices, List<Coefficient> coefficents) {
        this.basisBladeIndices = basisBladeIndices;
        this.coefficents = coefficents;
        if (basisBladeIndices.size() != coefficents.size()) {
            throw new IllegalArgumentException();
        }
    }
}
