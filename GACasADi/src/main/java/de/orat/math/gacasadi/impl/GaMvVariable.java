package de.orat.math.gacasadi.impl;

import de.dhbw.rahmlab.casadi.SxStatic;
import de.dhbw.rahmlab.casadi.impl.casadi.Sparsity;
import de.orat.math.gacasadi.CasADiUtil;
import de.orat.math.gacasadi.impl.gen.CachedGaMvExpr;
import de.orat.math.gacalc.spi.IMultivectorVariable;
import de.orat.math.sparsematrix.ColumnVectorSparsity;
import util.cga.CGACayleyTableGeometricProduct;
import util.cga.CGAKVectorSparsity;
import util.cga.CGAMultivectorSparsity;

public class GaMvVariable extends CachedGaMvExpr implements IMultivectorVariable<GaMvExpr> {

    private final String name;

    private static final ColumnVectorSparsity SPARSE = ColumnVectorSparsity.empty(CGACayleyTableGeometricProduct.instance().getBladesCount());

    public static GaMvVariable createSparse(String name) {
        return new GaMvVariable(name, SPARSE);
    }

    private static final ColumnVectorSparsity DENSE = ColumnVectorSparsity.dense(CGACayleyTableGeometricProduct.instance().getBladesCount());

    public static GaMvVariable createDense(String name) {
        return new GaMvVariable(name, DENSE);
    }

    public GaMvVariable(String name, ColumnVectorSparsity sparsity) {
        super(SxStatic.sym(name, CasADiUtil.toCasADiSparsity(sparsity)));
        assert super.getSX().is_valid_input();
        this.name = name;
    }

    public GaMvVariable(String name, Sparsity sparsity) {
        super(SxStatic.sym(name, sparsity));
        assert super.getSX().is_valid_input();
        this.name = name;
    }

    public GaMvVariable(String name, GaMvExpr from) {
        this(name, from.getSX().sparsity());
    }

    public GaMvVariable(String name, int grade) {
        this(name, CGAKVectorSparsity.instance(grade));
    }

    public GaMvVariable(String name, int[] grades) {
        this(name, CGAMultivectorSparsity.fromGrades(grades));
    }

    @Override
    public String getName() {
        return this.name;
    }
}
