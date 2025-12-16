package de.orat.math.gacasadi.specific.pga;

import de.orat.math.sparsematrix.SparseDoubleMatrix;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class PgaConstantsExpr {

    public static final PgaConstantsExpr instance = new PgaConstantsExpr();

    private PgaConstantsExpr() {

    }

    public PgaMvExpr one() {
        return cached2("one", () -> fac().createExpr(1d));
    }

    public PgaFactory fac() {
        return PgaFactory.instance;
    }

    public PgaMvExpr getSparseEmptyInstance() {
        final String name = "SparseEmptyInstance";
        return cached2(name, () -> PgaMvVariable.createSparse(name));
    }

    // ConcurrentHashMap to avoid ConcurrentModificationException while testing.
    private final ConcurrentHashMap<String, PgaMvExpr> cache
        = new ConcurrentHashMap<>(128, 0.5f);

    public PgaMvExpr cached(String name, Supplier<SparseDoubleMatrix> creator) {
        // Avoid Recursive Update exception happening with computeIfAbsent.
        var value = this.cache.get(name);
        if (value == null) {
            var sparseDoubleMatrix = creator.get();
            value = PgaMvExpr.create(sparseDoubleMatrix);
            this.cache.putIfAbsent(name, value);
        }
        return value;
    }

    protected PgaMvExpr cached2(String name, Supplier<PgaMvExpr> creator) {
        // Avoid Recursive Update exception happening with computeIfAbsent.
        var value = this.cache.get(name);
        if (value == null) {
            value = creator.get();
            this.cache.putIfAbsent(name, value);
        }
        return value;
    }
}
