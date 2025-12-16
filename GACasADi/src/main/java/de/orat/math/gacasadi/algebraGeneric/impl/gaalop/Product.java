package de.orat.math.gacasadi.algebraGeneric.impl.gaalop;

import de.dhbw.rahmlab.casadi.SxStatic;
import de.dhbw.rahmlab.casadi.api.Util;
import de.dhbw.rahmlab.casadi.impl.casadi.SX;
import de.dhbw.rahmlab.casadi.impl.std.StdVectorCasadiInt;
import de.gaalop.tba.MultTableAbsDirectComputer;
import de.orat.math.gacasadi.algebraGeneric.api.CoefficientAndBasisBladeIndex;
import de.orat.math.gacasadi.algebraGeneric.api.IProduct;
import de.orat.math.gacasadi.algebraGeneric.api.Multivector;
import de.orat.math.gacasadi.specific.cga.CgaCasADiUtil;
import de.orat.math.gacasadi.specific.cga.CgaMvExpr;
import static de.orat.math.gacasadi.specific.cga.CgaMvExpr.create;
import java.util.ArrayList;
import java.util.List;
import util.cga.CGACayleyTable;
import util.cga.CGACayleyTableGeometricProduct;

public class Product implements IProduct {

    private final MultTableAbsDirectComputer comp;

    public Product(MultTableAbsDirectComputer comp) {
        this.comp = comp;
    }

    /**
     * <pre>
     * Implementation could just return a cached cayley table entry.
     * Can be sometimes more than just one (Coefficient, Basisblade), if base change is used in Gaalop.
     * </pre>
     */
    @Override
    public Multivector product(int basisBladeIndex1, int basisBladeIndex2) {
        var mvGaalop = this.comp.getProduct(basisBladeIndex1, basisBladeIndex2);
        var blades = mvGaalop.getBlades();
        List<CoefficientAndBasisBladeIndex> entries = new ArrayList<>(1);
        for (var blade : blades) {
            byte prefactor = blade.getPrefactor();
            // Test unneccesay. Will never occur.
            /*
            if (prefactor == 0) {
                continue;
            }
             */
            int index = blade.getIndex();
            var cbbi = new CoefficientAndBasisBladeIndex(prefactor, index);
            entries.add(cbbi);
        }
        /*
        if (entries.isEmpty()) {
            return Multivector.ZERO;
        }
         */
        var mv = new Multivector(entries);
        return mv;
    }
}
