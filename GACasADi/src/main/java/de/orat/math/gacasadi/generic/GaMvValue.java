package de.orat.math.gacasadi.generic;

import de.dhbw.rahmlab.casadi.impl.casadi.DM;
import de.dhbw.rahmlab.casadi.impl.casadi.SX;
import de.dhbw.rahmlab.casadi.impl.std.StdVectorSX;
import de.orat.math.gacalc.spi.IConstants;
import de.orat.math.gacalc.spi.IMultivector;
import de.orat.math.gacalc.spi.IMultivectorValue;
import de.orat.math.gacasadi.delegating.annotation.api.GenerateDelegate;
import static de.orat.math.gacasadi.generic.GaFunction.transformImpl;
import de.orat.math.gacasadi.generic.gen.DelegatingGaMvValue;
import de.orat.math.gacasadi.specific.cga.CgaMvVariable;
import java.util.ArrayList;
import java.util.List;

@GenerateDelegate(to = GaMvExpr.class, genericType = "VAL extends GaMvValue<VAL, EXPR> & DelegatingGaMvValue<VAL, EXPR>, EXPR extends GaMvExpr<EXPR>", delegateType = "EXPR", wrapType = "VAL")
public abstract class GaMvValue<VAL extends GaMvValue<VAL, EXPR>, EXPR extends GaMvExpr<EXPR>>
    implements DelegatingGaMvValue<VAL, EXPR>, IMultivectorValue<VAL, EXPR>, IGetSparsityCasadi {

    @Override
    public abstract IConstants<VAL> constants();

    private final EXPR delegate;
    private final ComposableImmutableBinaryTree<GaMvValue<VAL, EXPR>> inputs;

    protected GaMvValue(EXPR delegate) {
        this.delegate = delegate;
        // Not "leaking this", because the passed "this" will not be used before fully constructed.
        // Because ComposableImmutableBinaryTree instance just stores the "this" and is itself not visible from outside the constructor.
        this.inputs = new ComposableImmutableBinaryTree<>(this);
    }

    protected final GaMvValue<VAL, EXPR> upcast() {
        return this;
    }

    protected final VAL downcast() {
        return (VAL) this;
    }

    protected GaMvValue(EXPR delegate, VAL other) {
        this.delegate = delegate;
        this.inputs = other.upcast().inputs;
    }

    protected GaMvValue(EXPR delegate, VAL a, VAL b) {
        this.delegate = delegate;
        this.inputs = a.upcast().inputs.append(b.upcast().inputs);
    }

    @Override
    public EXPR delegate() {
        return this.delegate;
    }

    /**
     * Creates a leaf. Only to be used by static create Method with DM input.
     */
    protected GaMvValue(DM dm) {
        this.delegate = dmToPureSym(dm);
        this.lazyDM = dm;
        this.inputs = new ComposableImmutableBinaryTree<>(this);
    }

    private static int num = 0;

    private EXPR dmToPureSym(DM dm) {
        var nameSym = String.format("x%s", String.valueOf(num));
        ++num;
        var pureSym = fac().createVariable(nameSym, dm.sparsity()).toEXPR();
        return pureSym;
    }

    /**
     * Nullable!
     */
    private DM lazyDM = null;

    /**
     * Expensive for MVnum, which are not created directly from a numerical constructor, but through method
     * chaining.
     */
    public <VAR extends IGaMvVariable<VAR, ?, EXPR>> DM getDM() {
        if (this.lazyDM == null) {
            var allInputs = this.inputs.computeUniqueLeafs().stream().toList();
            List<SX> allInputsParams = allInputs.stream().map(val -> val.delegate.sx).toList();
            var func = fac().<VAR>createFunction("getDM", allInputsParams, List.of(this.delegate));
            var evalMV = func.callValue(allInputs).get(0);
            // lazyDM is non-null for all leafs.
            this.lazyDM = evalMV.lazyDM;
        }
        return lazyDM;
    }

    protected abstract <VAR extends IGaMvVariable<VAR, ?, EXPR>> GaFactory<EXPR, ?, VAR, VAL> fac();

    /**
     * Can be expensive.
     */
    @Override
    public String toString() {
        return this.getDM().toString();
    }

    public double get(int index) {
        DM dm = getDM();
        double value = dm.at(index, 0).scalar();
        return value;
    }

    public List<Double> get(List<Integer> indices) {
        DM dm = getDM();
        List<Double> values = new ArrayList<>(indices.size());
        for (int index : indices) {
            double value = dm.at(index, 0).scalar();
            values.add(value);
        }
        return values;
    }
}
