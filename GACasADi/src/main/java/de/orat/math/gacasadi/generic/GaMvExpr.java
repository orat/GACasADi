package de.orat.math.gacasadi.generic;

import de.orat.math.gacalc.spi.IMultivectorExpression;
import de.orat.math.gacasadi.generic.IGetSX;
import de.orat.math.gacasadi.generic.IGetSparsityCasadi;

public abstract class GaMvExpr<EXPR extends GaMvExpr<EXPR>>
    implements IMultivectorExpression<EXPR>, IGetSX, IGetSparsityCasadi {

    /**
     * Retrofit
     */
    @Deprecated
    public String getName() {
        // Value will be different if this instanceof CgaMvVariable.
        return "(CgaMvExpr)";
    }

    public abstract int getBladesCount();

    public abstract EXPR simplifySparsify();
}
