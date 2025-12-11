package de.orat.math.gacasadi.algebraGeneric;

import de.dhbw.rahmlab.casadi.SxStatic;
import de.dhbw.rahmlab.casadi.impl.casadi.SX;
import de.dhbw.rahmlab.casadi.impl.casadi.Sparsity;
import de.dhbw.rahmlab.casadi.impl.std.StdVectorSX;
import de.orat.math.gacasadi.algebraGeneric.api.IProduct;
import de.orat.math.gacasadi.algebraGeneric.impl.gaalop.GaalopAlgebra;
import de.orat.math.gacasadi.algebraGeneric.impl.gaalop.Product;

public class CasADiMvExpr {

    /**
     * Spaltenvektor.
     */
    private final SX sx;

    public CasADiMvExpr(SX sx) {
        this.sx = sx;
    }

    // ToDo: get Computer for correct product.
    // private static IAlgebra algebra = new GaalopAlgebra(algebraName);
    private static Product gp = new Product(null);

    // Precondition: a and b are of same length, column vectors, same algebra.
    private static SX product(IProduct product, SX a, SX b) {
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

    public CasADiMvExpr gp(CasADiMvExpr other) {
        return new CasADiMvExpr(product(gp, this.sx, other.sx));
    }
}
