package de.orat.math.gacasadi.impl;

import de.dhbw.rahmlab.casadi.DmStatic;
import de.orat.math.sparsematrix.SparseDoubleMatrix;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import de.orat.math.gacalc.spi.IConstantsValue;

public class CgaConstantsValue implements IConstantsValue<CgaMvValue, CgaMvExpr> {

    public static final CgaConstantsValue instance = new CgaConstantsValue();

    private CgaConstantsValue() {

    }

    @Override
    public CgaFactory fac() {
        return CgaFactory.instance;
    }

    private static CgaMvValue createSparseEmptyInstance() {
        var sparseSym = CgaConstantsExpr.instance.getSparseEmptyInstance();
        var sparseNum = CgaMvValue.createFrom(sparseSym);
        return sparseNum;
    }

    @Override
    public CgaMvValue getSparseEmptyInstance() {
        final String name = "SparseEmptyInstance";
        return cached2(name, CgaConstantsValue::createSparseEmptyInstance);
    }

    private static CgaMvValue createDenseEmptyInstance() {
        var denseSym = CgaConstantsExpr.instance.getDenseEmptyInstance();
        var dm = DmStatic.zeros(denseSym.getSX().sparsity());
        var denseNum = CgaMvValue.create(dm);
        return denseNum;
    }

    @Override
    public CgaMvValue getDenseEmptyInstance() {
        final String name = "DenseEmptyInstance";
        return cached2(name, CgaConstantsValue::createDenseEmptyInstance);
    }

    // ConcurrentHashMap to avoid ConcurrentModificationException while testing.
    private final ConcurrentHashMap<String, CgaMvValue> cache
        = new ConcurrentHashMap<>(128, 0.5f);

    @Override
    public CgaMvValue cached(String name, Supplier<SparseDoubleMatrix> creator) {
        // Avoid Recursive Update exception happening with computeIfAbsent.
        var value = this.cache.get(name);
        if (value == null) {
            value = CgaMvValue.create(creator.get());
            this.cache.putIfAbsent(name, value);
        }
        return value;
    }

    /**
     * Only to be used locally. creator must use cache of CGAConstants**Symbolic**!
     */
    private CgaMvValue cached2(String name, Supplier<CgaMvValue> creator) {
        // Avoid Recursive Update exception happening with computeIfAbsent.
        var value = this.cache.get(name);
        if (value == null) {
            value = creator.get();
            this.cache.putIfAbsent(name, value);
        }
        return value;
    }

    public void testCache() {
        cache.values().forEach(mv -> System.out.println(mv));
        System.out.println("------------------------");
    }
}
