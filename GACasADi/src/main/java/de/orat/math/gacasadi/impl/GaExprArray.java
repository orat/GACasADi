package de.orat.math.gacasadi.impl;

import de.dhbw.rahmlab.casadi.SxStatic;
import de.dhbw.rahmlab.casadi.impl.casadi.SX;
import de.dhbw.rahmlab.casadi.impl.casadi.Sparsity;
import de.dhbw.rahmlab.casadi.impl.std.StdVectorSX;
import static de.orat.math.gacasadi.impl.GaFunction.transformImpl;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import de.orat.math.gacalc.spi.IMultivectorExpressionArray;

public class GaExprArray extends ArrayList<GaMvExpr> implements IMultivectorExpressionArray<GaMvExpr> {

    public GaExprArray() {
        super();
    }

    /**
     *
     * @param mvs all elements must have equal sparsity.
     */
    public GaExprArray(Collection<? extends GaMvExpr> mvs) {
        super(mvs);
    }

    public final boolean areSparsitiesSubsetsOf(Sparsity sparsity) {
        for (var e : this) {
            if (!e.getSX().sparsity().is_subset(sparsity)) {
                return false;
            }
        }
        return true;
    }

    public static SX horzcat(List<? extends IGetSX> mvs) {
        StdVectorSX stdVec = transformImpl(mvs);
        SX sxHorzcat = SxStatic.horzcat(stdVec);
        return sxHorzcat;
    }

    public static List<? extends GaMvExpr> horzsplit(SX sxHorzcat) {
        StdVectorSX stdVec = SxStatic.horzsplit_n(sxHorzcat, sxHorzcat.columns());
        var mvs = stdVec.stream().map(GaMvExpr::create).toList();
        return mvs;
    }
}
