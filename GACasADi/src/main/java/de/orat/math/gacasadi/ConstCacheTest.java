package de.orat.math.gacasadi;

import de.dhbw.rahmlab.casadi.impl.casadi.SX;
import de.dhbw.rahmlab.casadi.impl.casadi.Sparsity;
import de.orat.math.gacasadi.impl.CgaFactory;

public class ConstCacheTest {

    public static void main(String[] args) {
        //var sx = new SX(new Sparsity(1, 1));
        //System.out.println(sx);

        var con = CgaFactory.instance.constantsValue();
        con.testCache();
        var a = con.getDenseEmptyInstance();
        con.testCache();
        var b = con.getSparseEmptyInstance();
        con.testCache();
        System.out.println(a.add(b));
    }
}
