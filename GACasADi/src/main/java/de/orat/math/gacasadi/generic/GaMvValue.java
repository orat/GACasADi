package de.orat.math.gacasadi.generic;

import de.dhbw.rahmlab.casadi.impl.casadi.DM;
import de.orat.math.gacalc.spi.IMultivectorValue;
import de.orat.math.gacasadi.specific.cga.CgaMvValue;
import java.util.ArrayList;
import java.util.List;

public abstract class GaMvValue<VAL extends GaMvValue<VAL, EXPR>, EXPR extends IGaMvExpr<EXPR>>
    implements IMultivectorValue<VAL, EXPR>, IGetSparsityCasadi {

    private final ComposableImmutableBinaryTree<CgaMvValue> inputs;

    protected GaMvValue(ComposableImmutableBinaryTree<CgaMvValue> inputs) {
        this.inputs = inputs;
    }

    public abstract DM getDM();

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
