package de.orat.math.gacasadi.generic;

public interface IGaMvExprCached<CACHED extends IGaMvExprCached<CACHED, EXPR>, EXPR extends IGaMvExpr<EXPR>> extends IGaMvExpr<EXPR> {

    default CACHED toCACHED() {
        // Downcast.
        // Possible, if CACHED currently used subtype of IGaMvExprCached.
        // this without cast is possible in subclass.
        return (CACHED) this;
    }
}
