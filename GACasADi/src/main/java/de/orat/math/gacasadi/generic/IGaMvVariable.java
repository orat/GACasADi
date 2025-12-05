package de.orat.math.gacasadi.generic;

import de.orat.math.gacalc.spi.IMultivectorExpression;
import de.orat.math.gacalc.spi.IMultivectorVariable;

public interface IGaMvVariable<VAR extends IGaMvVariable<VAR, CACHED, EXPR>, CACHED extends IGaMvExprCached<CACHED, EXPR>, EXPR extends IGaMvExpr<EXPR>>
    extends IGaMvExprCached<CACHED, EXPR>, IMultivectorVariable<EXPR>, IMultivectorExpression<EXPR>, IGetSX, IGetSparsityCasadi {

}
