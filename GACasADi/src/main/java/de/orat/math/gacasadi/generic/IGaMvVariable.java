package de.orat.math.gacasadi.generic;

import de.orat.math.gacalc.spi.IMultivectorVariable;

public interface IGaMvVariable<EXPR extends IGaMvExpr<EXPR, VAR, VAL>, VAR extends IGaMvVariable<EXPR, VAR, VAL>, VAL extends IGaMvValue<EXPR, VAR, VAL>>
    extends IMultivectorVariable<EXPR, VAR, VAL>, IGaMvExpr<EXPR, VAR, VAL>, IGetSX, IGetSparsityCasadi {

}
