package de.orat.math.gacasadi.specific.cga;

import de.orat.math.sparsematrix.SparseDoubleMatrix;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class CgaConstantsValue extends CgaConstants<CgaMvValue> {

    public static final CgaConstantsValue instance = new CgaConstantsValue();

    private CgaConstantsValue() {

    }

    @Override
    public CgaMvValue getSparseEmptyInstance() {
        final String name = "SparseEmptyInstance";
        return cached2(name, () -> fac().DMtoVAL(fac().createSparseDM()));
    }

    // ConcurrentHashMap to avoid ConcurrentModificationException while testing.
    private final ConcurrentHashMap<String, CgaMvValue> cache
        = new ConcurrentHashMap<>(128, 0.5f);

    @Override
    public CgaMvValue cached(String name, Supplier<SparseDoubleMatrix> creator) {
        // Avoid Recursive Update exception happening with computeIfAbsent.
        var value = this.cache.get(name);
        if (value == null) {
            var sparseDoubleMatrix = creator.get();
            value = CgaMvValue.create(sparseDoubleMatrix);
            this.cache.putIfAbsent(name, value);
        }
        return value;
    }

    protected CgaMvValue cached2(String name, Supplier<CgaMvValue> creator) {
        // Avoid Recursive Update exception happening with computeIfAbsent.
        var value = this.cache.get(name);
        if (value == null) {
            value = creator.get();
            this.cache.putIfAbsent(name, value);
        }
        return value;
    }
}
