package de.orat.math.gacasadi.caching.annotation.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <pre>
 * This annotation is contagious to overridden methods.
 * Doc: https://github.com/orat/GACasADi/blob/master/GACasADi_MvExprCachingProcessor/README.md
 * </pre>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
public @interface Uncached {
}
