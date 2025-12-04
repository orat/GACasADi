package de.orat.math.gacasadi.specific.cga;

import de.orat.math.gacalc.spi.IConstantsExpression;
import de.orat.math.sparsematrix.SparseDoubleMatrix;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class CgaConstantsExpr implements IConstantsExpression<CgaMvExpr> {

    public static final CgaConstantsExpr instance = new CgaConstantsExpr();

    private CgaConstantsExpr() {

    }

    @Override
    public CgaFactory fac() {
        return CgaFactory.instance;
    }

    @Override
    public CgaMvExpr getSparseEmptyInstance() {
        final String name = "SparseEmptyInstance";
        return cached2(name, () -> CgaMvVariable.createSparse(name));
    }

    @Override
    public CgaMvExpr getDenseEmptyInstance() {
        final String name = "DenseEmptyInstance";
        return cached2(name, () -> CgaMvVariable.createDense(name));
    }

    // ConcurrentHashMap to avoid ConcurrentModificationException while testing.
    private final ConcurrentHashMap<String, CgaMvExpr> cache
        = new ConcurrentHashMap<>(128, 0.5f);

    @Override
    public CgaMvExpr cached(String name, Supplier<SparseDoubleMatrix> creator) {
        // Avoid Recursive Update exception happening with computeIfAbsent.
        var value = this.cache.get(name);
        if (value == null) {
            var sparseDoubleMatrix = creator.get();
            value = CgaMvExpr.create(sparseDoubleMatrix);
            this.cache.putIfAbsent(name, value);
        }
        return value;
    }

    protected CgaMvExpr cached2(String name, Supplier<CgaMvExpr> creator) {
        // Avoid Recursive Update exception happening with computeIfAbsent.
        var value = this.cache.get(name);
        if (value == null) {
            value = creator.get();
            this.cache.putIfAbsent(name, value);
        }
        return value;
    }
}
