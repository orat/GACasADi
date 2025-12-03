package de.orat.math.gacasadi.caching;

import java.util.Map;
import java.util.SortedMap;

public interface IFunctionCache {

    void clearCache();

    Map<String, Integer> getUnmodifiableCachedFunctionsUsage();

    SortedMap<String, Integer> getSortedUnmodifiableCachedFunctionsUsage();

    int getCacheSize();

    String cachedFunctionUsageToString();
}
