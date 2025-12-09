package de.orat.math.gacasadi.algebraGeneric.api;

import java.util.Collections;
import java.util.List;

/**
 * A basis blade is an ordered list of base vector indices.
 */
public record BasisBlade(List<Integer> baseVectorIndices) {

    /**
     * <pre>
     * Actually, there is already one in Algebra. But there is no way to access it.
     * Do not use == with SCALAR! Use isScalar() instead!
     * <pre>
     */
    // Packge-private
    static final BasisBlade SCALAR = new BasisBlade(Collections.emptyList());

    public Integer get(int index) {
        return baseVectorIndices.get(index);
    }

    public boolean isScalar() {
        return baseVectorIndices.isEmpty();
    }

    public int grade() {
        return baseVectorIndices.size();
    }
}
