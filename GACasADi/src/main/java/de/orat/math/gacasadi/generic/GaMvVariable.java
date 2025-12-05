package de.orat.math.gacasadi.generic;

import de.orat.math.gacalc.spi.IMultivectorExpression;
import de.orat.math.gacalc.spi.IMultivectorVariable;

public interface GaMvVariable<EXPR extends GaMvExpr<EXPR>>
    extends IMultivectorVariable<EXPR>, IMultivectorExpression<EXPR>, IGetSX, IGetSparsityCasadi {

}
