package de.orat.math.gacasadi.generic;

import de.dhbw.rahmlab.casadi.impl.casadi.DM;
import de.orat.math.gacalc.spi.IMultivectorValue;

public interface GaMvValue<VAL extends GaMvValue<VAL, EXPR>, EXPR extends GaMvExpr<EXPR>>
    extends IMultivectorValue<VAL, EXPR>, IGetSparsityCasadi {

    DM getDM();
}
