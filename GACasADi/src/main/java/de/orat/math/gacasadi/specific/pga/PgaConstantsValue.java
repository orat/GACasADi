package de.orat.math.gacasadi.specific.pga;

import de.dhbw.rahmlab.casadi.DmStatic;
import de.orat.math.sparsematrix.SparseDoubleMatrix;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class PgaConstantsValue {

    public static final PgaConstantsValue instance = new PgaConstantsValue();

    private PgaConstantsValue() {

    }

    public PgaFactory fac() {
        return PgaFactory.instance;
    }

    public PgaMvValue getSparseEmptyInstance() {
        final String name = "SparseEmptyInstance";
        return cached2(name, () -> fac().DMtoVAL(fac().createSparseDM()));
    }

    // ConcurrentHashMap to avoid ConcurrentModificationException while testing.
    private final ConcurrentHashMap<String, PgaMvValue> cache
        = new ConcurrentHashMap<>(128, 0.5f);

    public PgaMvValue cached(String name, Supplier<SparseDoubleMatrix> creator) {
        // Avoid Recursive Update exception happening with computeIfAbsent.
        var value = this.cache.get(name);
        if (value == null) {
            value = PgaMvValue.create(creator.get());
            this.cache.putIfAbsent(name, value);
        }
        return value;
    }

    /**
     * Only to be used locally. creator must use cache of CGAConstants**Symbolic**!
     */
    private PgaMvValue cached2(String name, Supplier<PgaMvValue> creator) {
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
