package de.orat.math.gacasadi.specific.pga;

import de.dhbw.rahmlab.casadi.SxStatic;
import de.dhbw.rahmlab.casadi.impl.casadi.Sparsity;
import de.orat.math.gacalc.spi.IMultivectorVariable;
import de.orat.math.gacasadi.algebraGeneric.api.IAlgebra;
import de.orat.math.gacasadi.generic.CasADiUtil;
import de.orat.math.gacasadi.specific.pga.gen.CachedPgaMvExpr;
import de.orat.math.gacasadi.generic.IGaMvVariable;
import de.orat.math.sparsematrix.ColumnVectorSparsity;
import de.orat.math.sparsematrix.SparseDoubleMatrix;

public class PgaMvVariable extends CachedPgaMvExpr implements IGaMvVariable<PgaMvVariable, CachedPgaMvExpr, PgaMvExpr>, IMultivectorVariable<PgaMvExpr> {

    private final String name;

    
    private static final ColumnVectorSparsity SPARSE = ColumnVectorSparsity.empty(
        PgaFactory.instance.getIAlgebra().getBladesCount()
        /*CGACayleyTableGeometricProduct.instance().getBladesCount()*/);

    public static PgaMvVariable createSparse(String name) {
        return new PgaMvVariable(name, SPARSE);
    }

    private static final ColumnVectorSparsity DENSE = ColumnVectorSparsity.dense(
        PgaFactory.instance.getIAlgebra().getBladesCount());

    public static PgaMvVariable createDense(String name) {
        return new PgaMvVariable(name, DENSE);
    }

    public PgaMvVariable(String name, ColumnVectorSparsity sparsity) {
        super(SxStatic.sym(name, CasADiUtil.toCasADiSparsity(sparsity)));
        assert super.getSX().is_valid_input();
        this.name = name;
    }

    public PgaMvVariable(String name, Sparsity sparsity) {
        super(SxStatic.sym(name, sparsity));
        assert super.getSX().is_valid_input();
        this.name = name;
    }

    public PgaMvVariable(String name, PgaMvExpr from) {
        this(name, from.getSX().sparsity());
    }

    public PgaMvVariable(String name, int grade) {
        this(name, /*CGAKVectorSparsity.instance(grade)*/CasADiUtil.determineSparsity(grade, PgaFactory.instance.getIAlgebra()));
    }

   
    
    public PgaMvVariable(String name, int[] grades) {
        this(name, CasADiUtil.determineSparsity(grades, PgaFactory.instance.getIAlgebra()));
    }
    

    @Override
    public String getName() {
        return this.name;
    }
}
