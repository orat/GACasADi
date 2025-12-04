package de.orat.math.gacasadi.generic;

import de.orat.math.gacalc.spi.IMultivectorExpression;
import de.orat.math.gacalc.spi.IMultivectorVariable;
import de.orat.math.gacasadi.generic.IGetSX;
import de.orat.math.gacasadi.generic.IGetSparsityCasadi;

public interface GaMvVariable<EXPR extends GaMvExpr<EXPR>>
    extends IMultivectorVariable<EXPR>, IMultivectorExpression<EXPR>, IGetSX, IGetSparsityCasadi {

}
