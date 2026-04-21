package de.orat.math.gacasadi.generic;

import de.orat.math.gacalc.spi.IMultivectorExpression;
import de.orat.math.gacasadi.caching.annotation.api.Uncached;

public interface IGaMvExpr<EXPR extends IGaMvExpr<EXPR, VAR, VAL>, VAR extends IGaMvVariable<EXPR, VAR, VAL>, VAL extends IGaMvValue<EXPR, VAR, VAL>>
    extends IMultivectorExpression<EXPR, VAR, VAL>, IGetSX, IGetSparsityCasadi {

    @Override
    @Uncached
    default EXPR asEXPR() {
        return (EXPR) this;
    }

    int getBladesCount();

    EXPR simplifySparsify();

    boolean isEven();

    boolean isBivector();
}
