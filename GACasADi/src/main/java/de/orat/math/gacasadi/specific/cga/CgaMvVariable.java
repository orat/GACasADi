package de.orat.math.gacasadi.specific.cga;

import de.dhbw.rahmlab.casadi.SxStatic;
import de.dhbw.rahmlab.casadi.impl.casadi.Sparsity;
import de.orat.math.gacalc.spi.IMultivectorVariable;
import de.orat.math.gacasadi.generic.CasADiUtil;
import de.orat.math.gacasadi.generic.IGaMvVariable;
import de.orat.math.gacasadi.specific.cga.gen.CachedCgaMvExpr;
import de.orat.math.sparsematrix.ColumnVectorSparsity;
import util.cga.CGACayleyTableGeometricProduct;
import util.cga.CGAKVectorSparsity;

public class CgaMvVariable extends CachedCgaMvExpr implements IGaMvVariable<CgaMvVariable, CachedCgaMvExpr, CgaMvExpr>, IMultivectorVariable<CgaMvExpr> {

    private final String name;

    private static final ColumnVectorSparsity SPARSE = ColumnVectorSparsity.empty(CGACayleyTableGeometricProduct.instance().getBladesCount());

    public static CgaMvVariable createSparse(String name) {
        return new CgaMvVariable(name, SPARSE);
    }

    private static final ColumnVectorSparsity DENSE = ColumnVectorSparsity.dense(CGACayleyTableGeometricProduct.instance().getBladesCount());

    public static CgaMvVariable createDense(String name) {
        return new CgaMvVariable(name, DENSE);
    }

    public CgaMvVariable(String name, ColumnVectorSparsity sparsity) {
        super(SxStatic.sym(name, CasADiUtil.toCasADiSparsity(sparsity)));
        assert super.getSX().is_valid_input();
        this.name = name;
    }

    public CgaMvVariable(String name, Sparsity sparsity) {
        super(SxStatic.sym(name, sparsity));
        assert super.getSX().is_valid_input();
        this.name = name;
    }

    public CgaMvVariable(String name, CgaMvExpr from) {
        this(name, from.getSX().sparsity());
    }

    public CgaMvVariable(String name, int grade) {
        this(name, CGAKVectorSparsity.instance(grade));
    }

    public CgaMvVariable(String name, int[] grades) {
        this(name, CasADiUtil.determineSparsity(grades, CgaFactory.instance.getIAlgebra()));
    }

    @Override
    public String getName() {
        return this.name;
    }
}
