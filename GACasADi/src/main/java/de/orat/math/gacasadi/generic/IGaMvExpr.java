package de.orat.math.gacasadi.generic;

import de.orat.math.gacalc.spi.IMultivectorExpression;

public interface IGaMvExpr<EXPR extends IGaMvExpr<EXPR>>
    extends IMultivectorExpression<EXPR>, IGetSX, IGetSparsityCasadi {

    default EXPR toEXPR() {
        // Downcast.
        // Possible, if EXPR currently used subtype of IGaMvExpr.
        // this without cast is possible in subclass.
        return (EXPR) this;
    }

    int getBladesCount();

    EXPR simplifySparsify();

    boolean isGeneralEven();

    boolean isEven();

    boolean isBivector();
}
