package de.orat.math.gacasadi.algebraGeneric.api;

import java.util.Collections;
import java.util.List;

public record Multivector(List<CoefficientAndBasisBladeIndex> entries) implements IMultivector {

    public static final Multivector ZERO = new Multivector(Collections.emptyList());

}
