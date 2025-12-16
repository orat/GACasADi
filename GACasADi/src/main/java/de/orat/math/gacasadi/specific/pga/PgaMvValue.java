package de.orat.math.gacasadi.specific.pga;

import de.dhbw.rahmlab.casadi.impl.casadi.DM;
import de.dhbw.rahmlab.casadi.impl.casadi.Sparsity;
import de.orat.math.gacalc.api.MultivectorValue;
import de.orat.math.gacalc.spi.IConstants;
import de.orat.math.gacalc.spi.IMultivectorValue;
import de.orat.math.gacalc.util.GeometricObject;
import de.orat.math.gacasadi.delegating.annotation.api.GenerateDelegate;
import de.orat.math.gacasadi.generic.ComposableImmutableBinaryTree;
import de.orat.math.gacasadi.generic.IGaMvValue;
import de.orat.math.gacasadi.generic.IGetSparsityCasadi;
import de.orat.math.gacasadi.specific.pga.gen.DelegatingPgaMvValue;
import de.orat.math.sparsematrix.SparseDoubleMatrix;

@GenerateDelegate(to = PgaMvExpr.class)
public class PgaMvValue extends DelegatingPgaMvValue implements IGaMvValue<PgaMvValue, PgaMvExpr>, IMultivectorValue<PgaMvValue, PgaMvExpr>, IGetSparsityCasadi {

    private final ComposableImmutableBinaryTree<PgaMvValue> inputs;

    /**
     * Only to be used by non-static create Method for DelegatingGaMvValue.
     */
    @Deprecated
    private PgaMvValue(PgaMvExpr sym, ComposableImmutableBinaryTree<PgaMvValue> inputs) {
        super(sym);
        this.inputs = inputs;
    }

    @Override
    public IConstants<PgaMvValue> constants() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected PgaMvValue create(PgaMvExpr delegate) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    protected PgaMvValue create(PgaMvExpr delegate, PgaMvValue other) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public DM getDM() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void init(MultivectorValue.Callback callback) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public SparseDoubleMatrix elements() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public PgaMvExpr toExpr() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public GeometricObject decompose(boolean isIPNS) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public boolean isNull(double precision) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

}
