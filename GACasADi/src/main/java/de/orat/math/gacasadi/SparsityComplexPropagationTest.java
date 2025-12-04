package de.orat.math.gacasadi;



import de.dhbw.rahmlab.casadi.SxStatic;
import de.dhbw.rahmlab.casadi.impl.casadi.SX;
import de.dhbw.rahmlab.casadi.impl.casadi.Sparsity;
import de.orat.math.gacasadi.impl.GaFactory;
import de.orat.math.gacasadi.impl.GaFunction;
import de.orat.math.gacasadi.impl.gen.CachedGaMvExpr;
import java.util.List;
import util.cga.SparseCGAColumnVector;

public class SparsityComplexPropagationTest {

    public static void main(String[] args) {
        var fac = GaFactory.instance;
        var a = fac.createVariable("a", 0);
        var b = fac.createVariable("b", 0);

        SX withScalar = a.add(a.gpWithScalar(-2)).add(a).getSX();
        System.out.println(withScalar); // Strukturelle Null.

        var yy = fac.createValue(3);
        var zz = fac.createValue(3);

        var yyOut = yy.add(yy.gpWithScalar(-2)).add(yy);
        System.out.println(yyOut); // Strukturelle Null

        var yyzzOut = yy.add(zz.gpWithScalar(-2)).add(yy);
        System.out.println(yyzzOut); // Numerische Null

        ////////////

        var zero = new SX(1, 1);
        var value = SxStatic.sym("value", 1, 1);
        var minusFunc = SxStatic.minus(zero, value);
        System.out.println(minusFunc);

        var xNum = new SX(12);
        var xSym = SxStatic.sym("xSym", 1, 1);
        System.out.println(xNum);
        System.out.println(xSym);
        var resNum = SxStatic.minus(xNum, xNum);
        var resSym = SxStatic.minus(xSym, xSym);
        System.out.println(resNum); // Numerische Null.
        System.out.println(resSym); // Numerische Null.

        ////////////

        var withoutNum = a.sub(a);
        System.out.println(withoutNum.toString()); // SymbolicMV printet strukturelle Null
        System.out.println(withoutNum.getSX()); // SX printet strukturelle Null.

        ////////////

        String caching = CachedGaMvExpr.getCache().cachedFunctionUsageToString();
        System.out.println(caching);

        ////////////

        var aa1 = fac.createVariable("a", 0);
        var aa2 = fac.createVariable("a", 0);
        var bb = aa1.add(aa2).simplifySparsify();
        System.out.println(bb);
        //new CGASymbolicFunction("ffff", List.of(aa1), List.of(bb));
    }
}
