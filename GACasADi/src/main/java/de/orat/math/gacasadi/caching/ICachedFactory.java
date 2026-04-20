package de.orat.math.gacasadi.caching;

import de.orat.math.gacasadi.generic.GaMvExpr;
import de.orat.math.gacasadi.generic.IGaMvValue;
import de.orat.math.gacasadi.generic.IGaMvVariable;

public interface ICachedFactory<CACHED extends ICached<CACHED, EXPR, VAR, VAL>, EXPR extends GaMvExpr<EXPR, VAR, VAL>, VAR extends IGaMvVariable<EXPR, VAR, VAL>, VAL extends IGaMvValue<EXPR, VAR, VAL>> {

    CACHED cachedEXPR(EXPR expr);
}
