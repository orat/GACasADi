package de.orat.math.gacasadi.caching.annotation.processor.generation;

import com.squareup.javapoet.ClassName;
import java.util.List;

public final class Classes {

    private Classes() {

    }

    public static final ClassName T_GaFunctionCache = ClassName.get("de.orat.math.gacasadi.caching", "GaFunctionCache");
    public static final ClassName T_IFunctionCache = ClassName.get("de.orat.math.gacasadi.caching", "IFunctionCache");
    public static final ClassName T_IGaMvExprCached = ClassName.get("de.orat.math.gacasadi.generic", "IGaMvExprCached");
    public static final ClassName T_SX = ClassName.get("de.dhbw.rahmlab.casadi.impl.casadi", "SX");
    public static final ClassName T_IMultivectorExpression = ClassName.get("de.orat.math.gacalc.spi", "IMultivectorExpression");

    public static final ClassName T_String = ClassName.get(String.class);
    public static final ClassName T_Override = ClassName.get(Override.class);
    public static final ClassName T_List = ClassName.get(List.class);
}
