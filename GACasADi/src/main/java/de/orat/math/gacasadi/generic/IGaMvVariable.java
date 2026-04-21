package de.orat.math.gacasadi.generic;

import de.orat.math.gacalc.spi.IMultivectorVariable;
import de.orat.math.gacasadi.caching.annotation.api.Uncached;

public interface IGaMvVariable<EXPR extends IGaMvExpr<EXPR, VAR, VAL>, VAR extends IGaMvVariable<EXPR, VAR, VAL>, VAL extends IGaMvValue<EXPR, VAR, VAL>>
    extends IMultivectorVariable<EXPR, VAR, VAL>, IGaMvExpr<EXPR, VAR, VAL>, IGetSX, IGetSparsityCasadi {

    @Override
    @Uncached
    default VAR asVAR() {
        return (VAR) this;
    }
}
