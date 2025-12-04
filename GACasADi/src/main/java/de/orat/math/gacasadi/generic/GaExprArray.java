package de.orat.math.gacasadi.generic;

import de.dhbw.rahmlab.casadi.impl.casadi.Sparsity;
import de.orat.math.gacalc.spi.IMultivectorExpressionArray;
import java.util.ArrayList;
import java.util.Collection;

public class GaExprArray<EXPR extends GaMvExpr<EXPR>> extends ArrayList<EXPR> implements IMultivectorExpressionArray<EXPR> {

    public GaExprArray() {
        super();
    }

    /**
     *
     * @param mvs all elements must have equal sparsity.
     */
    public GaExprArray(Collection<? extends EXPR> mvs) {
        super(mvs);
    }

    public final boolean areSparsitiesSubsetsOf(Sparsity sparsity) {
        for (var e : this) {
            if (!e.getSX().sparsity().is_subset(sparsity)) {
                return false;
            }
        }
        return true;
    }
}
