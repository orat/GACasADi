package de.orat.math.gacasadi.caching.annotation.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Doc: https://github.com/orat/CGACasADi/blob/master/CGACasADi_SymbolicMultivectorCachingProcessor/README.md
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface GenerateCached {

    boolean warnFailedToCache() default true;

    boolean warnUncached() default false;
}
