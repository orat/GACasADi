package de.orat.math.gacasadi.generic;

import de.dhbw.rahmlab.casadi.impl.casadi.DM;
import de.orat.math.gacalc.spi.IMultivectorValue;
import de.orat.math.sparsematrix.SparseDoubleMatrix;
import java.util.ArrayList;
import java.util.List;

public interface IGaMvValue<VAL extends IGaMvValue<VAL, EXPR>, EXPR extends IGaMvExpr<EXPR>>
    extends IMultivectorValue<VAL, EXPR>, IGetSparsityCasadi {

    DM getDM();

    default double get(int index) {
        DM dm = getDM();
        double value = dm.at(index, 0).scalar();
        return value;
    }

    default List<Double> get(List<Integer> indices) {
        DM dm = getDM();
        List<Double> values = new ArrayList<>(indices.size());
        for (int index : indices) {
            double value = dm.at(index, 0).scalar();
            values.add(value);
        }
        return values;
    }

    @Override
    default SparseDoubleMatrix elements() {
        return CasADiUtil.elements(this.getDM());
    }
}
