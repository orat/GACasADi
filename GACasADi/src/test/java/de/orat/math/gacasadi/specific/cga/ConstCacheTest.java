package de.orat.math.gacasadi.specific.cga;

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
