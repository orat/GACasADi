package de.orat.math.gacasadi.generic;

import de.dhbw.rahmlab.casadi.SxStatic;
import de.dhbw.rahmlab.casadi.impl.casadi.SX;
import de.dhbw.rahmlab.casadi.impl.casadi.Sparsity;
import de.dhbw.rahmlab.casadi.impl.std.StdVectorSX;
import de.orat.math.gacasadi.algebraGeneric.api.IProduct;

public abstract class GaMvExpr<EXPR extends GaMvExpr<EXPR>> implements IGaMvExpr<EXPR> {

    /**
     * Sparse column vector.
     */
    protected final SX sx;

    protected GaMvExpr(SX sx) {
        this.sx = sx;
    }

    protected GaMvExpr(EXPR other) {
        this.sx = other.sx;
    }

    // protected abstract EXPR create(SX sx);
    // Precondition: a and b are of same length, column vectors, same algebra.
    public static SX product(IProduct product, SX a, SX b) {
        final long n_rows = a.rows(); //==b.rows()
        int[] aIndices = a.get_row().stream().mapToInt(Long::intValue).toArray();
        int[] bIndices = b.get_row().stream().mapToInt(Long::intValue).toArray();
        SX result = new SX(new Sparsity(n_rows, 1));
        for (int ai : aIndices) {
            var aCell = a.at(ai, 0);
            for (int bk : bIndices) {
                var bCell = b.at(bk, 0);
                var mv = product.product(ai, bk);
                /*
                if (mv == Multivector.ZERO) {
                    continue;
                }
                 */
                for (var cbbi : mv.entries()) {
                    int bbi = cbbi.basisBladeIndex();
                    float coeff = cbbi.coefficient();
                    var resCell = result.at(bbi, 0);
                    SX factor = SxStatic.mtimes_(new StdVectorSX(new SX[]{new SX(coeff), aCell, bCell}));
                    SX newSum = SxStatic.plus(resCell, factor);
                    resCell.assign(newSum);
                }
            }
        }
        return result;
    }
}
