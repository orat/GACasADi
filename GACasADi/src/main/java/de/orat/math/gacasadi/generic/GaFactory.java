package de.orat.math.gacasadi.generic;

import de.dhbw.rahmlab.casadi.impl.casadi.DM;
import de.dhbw.rahmlab.casadi.impl.casadi.SX;
import de.dhbw.rahmlab.casadi.nativelib.NativeLibLoader;
import de.orat.math.gacalc.spi.IGAFactory;
import de.orat.math.gacasadi.algebraGeneric.api.IAlgebra;
import java.util.List;

/**
 * @author Oliver Rettig (Oliver.Rettig@orat.de)
 */
public abstract class GaFactory<EXPR extends IGaMvExpr<EXPR>, CACHED extends IGaMvExprCached<CACHED, EXPR>, VAR extends IGaMvVariable<VAR, CACHED, EXPR>, VAL extends IGaMvValue<VAL, EXPR>>
    implements IGAFactory<EXPR, VAR, VAL> {

    static {
        // Init JCasADi eagerly to improve profiling.
        NativeLibLoader.load();
    }

    protected abstract EXPR SXtoEXPR(SX sx);

    protected abstract VAL DMtoVAL(DM dm);

    public abstract CACHED cachedEXPR(EXPR expr);

    public abstract VAR EXPRtoVAR(String name, EXPR from);

    @Override
    public abstract GaFunction<EXPR, VAL> createFunction(String name, List<? extends VAR> parameters, List<? extends EXPR> returns);
}
