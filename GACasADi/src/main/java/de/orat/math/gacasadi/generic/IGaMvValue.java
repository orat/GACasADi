package de.orat.math.gacasadi.generic;

import de.dhbw.rahmlab.casadi.impl.casadi.DM;
import de.orat.math.gacalc.spi.IMultivectorValue;

public interface IGaMvValue<VAL extends IGaMvValue<VAL, EXPR>, EXPR extends IGaMvExpr<EXPR>>
    extends IMultivectorValue<VAL, EXPR>, IGetSparsityCasadi {

    DM getDM();
}
