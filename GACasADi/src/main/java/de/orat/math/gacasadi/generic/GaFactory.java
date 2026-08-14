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
public abstract class GaFactory<EXPR extends IGaMvExpr<EXPR, VAR, VAL>, VAR extends IGaMvVariable<EXPR, VAR, VAL>, VAL extends IGaMvValue<EXPR, VAR, VAL>>
    implements IGAFactory<EXPR, VAR, VAL> {

    static {
        // Init JCasADi eagerly to improve profiling.
        NativeLibLoader.load();
    }

    protected abstract EXPR SXtoEXPR(SX sx);

    protected abstract VAL DMtoVAL(DM dm);

    public abstract VAR EXPRtoVAR(String name, EXPR from);

    @Override
    public abstract GaFunction<EXPR, VAR, VAL> createFunction(String name, List<? extends VAR> parameters, List<? extends EXPR> returns);

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

    @Override
    public VAL createValue(List<String> bladeOfBasevectors, double value) {
        DM res = this.createSparseDM();
        int index = this.getIAlgebra().indexOfBlade(bladeOfBasevectors.toArray(String[]::new));
        res.at(index, 0).assign(new DM(value));
        return DMtoVAL(res);
    }
}
