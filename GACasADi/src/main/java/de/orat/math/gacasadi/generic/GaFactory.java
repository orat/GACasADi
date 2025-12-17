package de.orat.math.gacasadi.generic;

import de.dhbw.rahmlab.casadi.impl.casadi.DM;
import de.dhbw.rahmlab.casadi.impl.casadi.SX;
import de.dhbw.rahmlab.casadi.impl.casadi.Sparsity;
import de.dhbw.rahmlab.casadi.nativelib.NativeLibLoader;
import de.orat.math.gacalc.spi.IGAFactory;
import de.orat.math.gacasadi.algebraGeneric.api.IAlgebra;
import java.util.List;

/**
 * @author Oliver Rettig (Oliver.Rettig@orat.de)
 */
public abstract class GaFactory<EXPR extends GaMvExpr<EXPR>, CACHED extends IGaMvExprCached<CACHED, EXPR>, VAR extends IGaMvVariable<VAR, CACHED, EXPR>, VAL extends GaMvValue<VAL, EXPR>>
    implements IGAFactory<EXPR, VAR, VAL> {

    static {
        // Init JCasADi eagerly to improve profiling.
        NativeLibLoader.load();
    }

    protected abstract EXPR SXtoEXPR(SX sx);

    protected abstract VAL DMtoVAL(DM dm);

    public abstract CACHED cachedEXPR(EXPR expr);

    public abstract VAR EXPRtoVAR(String name, EXPR from);

    public abstract VAR createVariable(String name, Sparsity sparsity);

    @Override
    public abstract GaFunction<EXPR, VAL> createFunction(String name, List<? extends VAR> parameters, List<? extends EXPR> returns);

    public abstract IAlgebra getIAlgebra();

    public EXPR createSparse() {
        return SXtoEXPR(createSparseSX());
    }

    public SX createSparseSX() {
        int basisBladeCount = getIAlgebra().getBladesCount();
        SX sparse = new SX(new Sparsity(basisBladeCount, 1)); // fullSparse
        return sparse;
    }

    public DM createSparseDM() {
        int basisBladeCount = getIAlgebra().getBladesCount();
        DM sparse = new DM(new Sparsity(basisBladeCount, 1)); // fullSparse
        return sparse;
    }

    public VAL create(int index, double value) {
        DM mv = createSparseDM();
        mv.at(index, 0).assign(new DM(value));
        return DMtoVAL(mv);
    }

    /**
     * Precondition: same size
     */
    public VAL create(List<Integer> indices, List<Double> values) {
        final int size = indices.size();
        if (values.size() != size) {
            throw new IllegalArgumentException("indices and values are not of same size.");
        }
        DM mv = createSparseDM();
        for (int i = 0; i < size; ++i) {
            int index = indices.get(i);
            double value = values.get(i);
            mv.at(index, 0).assign(new DM(value));
        }
        return DMtoVAL(mv);
    }
}
