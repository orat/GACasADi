package de.orat.math.gacasadi.specific.pga;

import de.dhbw.rahmlab.casadi.SxStatic;
import de.dhbw.rahmlab.casadi.impl.casadi.Sparsity;
import de.orat.math.gacalc.spi.IMultivectorVariable;
import de.orat.math.gacasadi.specific.pga.gen.CachedPgaMvExpr;
import de.orat.math.sparsematrix.ColumnVectorSparsity;
import util.cga.CGACayleyTableGeometricProduct;
import de.orat.math.gacasadi.generic.IGaMvVariable;

public class PgaMvVariable extends CachedPgaMvExpr implements IGaMvVariable<PgaMvVariable, CachedPgaMvExpr, PgaMvExpr>, IMultivectorVariable<PgaMvExpr> {

    private final String name;

    public static PgaMvVariable createSparse(String name) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public static PgaMvVariable createDense(String name) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public PgaMvVariable(String name, Sparsity sparsity) {
        super(SxStatic.sym(name, sparsity));
        assert super.getSX().is_valid_input();
        this.name = name;
    }

    public PgaMvVariable(String name, PgaMvExpr from) {
        this(name, from.getSX().sparsity());
    }

    @Override
    public String getName() {
        return this.name;
    }
}
