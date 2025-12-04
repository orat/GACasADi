package de.orat.math.gacasadi.impl;

import de.dhbw.rahmlab.casadi.DmStatic;
import de.orat.math.sparsematrix.SparseDoubleMatrix;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import de.orat.math.gacalc.spi.IConstantsValue;

public class GaConstantsValue implements IConstantsValue<GaMvValue, GaMvExpr> {

    public static final GaConstantsValue instance = new GaConstantsValue();

    private GaConstantsValue() {

    }

    @Override
    public GaFactory fac() {
        return GaFactory.instance;
    }

    private static GaMvValue createSparseEmptyInstance() {
        var sparseSym = GaConstantsExpr.instance.getSparseEmptyInstance();
        var sparseNum = GaMvValue.createFrom(sparseSym);
        return sparseNum;
    }

    @Override
    public GaMvValue getSparseEmptyInstance() {
        final String name = "SparseEmptyInstance";
        return cached2(name, GaConstantsValue::createSparseEmptyInstance);
    }

    private static GaMvValue createDenseEmptyInstance() {
        var denseSym = GaConstantsExpr.instance.getDenseEmptyInstance();
        var dm = DmStatic.zeros(denseSym.getSX().sparsity());
        var denseNum = GaMvValue.create(dm);
        return denseNum;
    }

    @Override
    public GaMvValue getDenseEmptyInstance() {
        final String name = "DenseEmptyInstance";
        return cached2(name, GaConstantsValue::createDenseEmptyInstance);
    }

    // ConcurrentHashMap to avoid ConcurrentModificationException while testing.
    private final ConcurrentHashMap<String, GaMvValue> cache
        = new ConcurrentHashMap<>(128, 0.5f);

    @Override
    public GaMvValue cached(String name, Supplier<SparseDoubleMatrix> creator) {
        // Avoid Recursive Update exception happening with computeIfAbsent.
        var value = this.cache.get(name);
        if (value == null) {
            value = GaMvValue.create(creator.get());
            this.cache.putIfAbsent(name, value);
        }
        return value;
    }

    /**
     * Only to be used locally. creator must use cache of CGAConstants**Symbolic**!
     */
    private GaMvValue cached2(String name, Supplier<GaMvValue> creator) {
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
