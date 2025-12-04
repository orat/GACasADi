package de.orat.math.gacasadi;



import de.dhbw.rahmlab.casadi.impl.casadi.SX;
import de.dhbw.rahmlab.casadi.impl.casadi.Sparsity;
import de.orat.math.gacasadi.impl.GaFactory;
import de.orat.math.gacasadi.impl.GaMvExpr;

public class LittleTest {

    public static void main(String[] args) {
        GaFactory fac = GaFactory.instance;
        var a = fac.createVariable("a", 0);
        // var b = a.scalarInverse();
        // var b = a.scp(a);
        // var b = a.scalarCos();
        // var b = a.scalarAtan2(a);
        // System.out.println(a);
        // System.out.println(b);

        var x = fac.createValue(17);
        System.out.println(x);
    }
}
