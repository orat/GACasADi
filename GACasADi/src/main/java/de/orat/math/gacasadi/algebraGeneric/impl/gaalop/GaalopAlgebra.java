package de.orat.math.gacasadi.algebraGeneric.impl.gaalop;

import de.gaalop.tba.Algebra;
import de.orat.math.gacasadi.algebraGeneric.api.IAlgebra;

public class GaalopAlgebra implements IAlgebra {

    public final Algebra algebra;

    public GaalopAlgebra(Algebra algebra) {
        this.algebra = algebra;
    }
}
