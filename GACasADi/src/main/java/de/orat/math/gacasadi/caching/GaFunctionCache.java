package de.orat.math.gacasadi.caching;

import de.orat.math.gacasadi.impl.GaFunction;
import de.orat.math.gacasadi.impl.GaMvVariable;
import de.orat.math.gacasadi.impl.GaMvExpr;
import de.orat.math.gacasadi.impl.gen.CachedGaMvExpr;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import de.orat.math.gacalc.spi.IMultivectorExpression;

public class GaFunctionCache implements IFunctionCache {

    private final Map<String, GaFunction> functionCache
        = new HashMap<>(1024, 0.5f);
    private final Map<String, Integer> cachedFunctionsUsage
        = new HashMap<>(1024, 0.5f);

    private static final boolean NOCACHE = false;

    /**
     *
     * @param name Used directly as key in the cache. Needs to be unique for combination of actual function
     * name, symbolic arguments and their sparsities, and need to take identity of the arguments into account.
     * Use createFuncName() for this. Example usage is in CachedGaMvExpr.
     */
    public CachedGaMvExpr getOrCreateSymbolicFunction(String name, List<GaMvExpr> args, Function<List<? extends CachedGaMvExpr>, GaMvExpr> res) {
        if (NOCACHE) {
            List<CachedGaMvExpr> params = args.stream().map(CachedGaMvExpr::new).toList();
            return new CachedGaMvExpr(res.apply(params));
        } else {
            GaFunction func = functionCache.get(name);
            if (func == null) {
                func = createSymbolicFunction(String.format("cache_func_%s", functionCache.size()), args, res);
                functionCache.put(name, func);
                cachedFunctionsUsage.put(name, 0);
            }
            // Specific type: CachedGaMvExpr.
            GaMvExpr retVal = func.callExpr(args).get(0).simplifySparsify();
            cachedFunctionsUsage.compute(name, (k, v) -> ++v);
            return new CachedGaMvExpr(retVal);
        }
    }

    private static GaFunction createSymbolicFunction(String name, List<GaMvExpr> args, Function<List<? extends CachedGaMvExpr>, GaMvExpr> res) {
        final int size = args.size();
        List<GaMvVariable> casadiFuncParams = new ArrayList<>(size);
        List<GaMvVariable> symbolicMultivectorParams = new ArrayList<>(size);
        List<Integer> argsFirstOccurrences = computeFirstOccurrences(args);
        // Convert to purely symbolic multivector.
        for (int i = 0; i < size; ++i) {
            GaMvExpr arg = args.get(i);
            // sparsity
            var param = new GaMvVariable(getParamName(i), arg);
            casadiFuncParams.add(param);

            // Preserve identity for symbolicMultivectorParams.
            Integer firstOccurrence = argsFirstOccurrences.get(i);
            // assert firstOccurrence <= i;
            // assert casadiFuncParams.size() - 1 == i;
            var symbolicMultivectorParam = casadiFuncParams.get(firstOccurrence);
            symbolicMultivectorParams.add(symbolicMultivectorParam);
        }
        // Specific type: CachedGaMvExpr.
        GaMvExpr symbolicReturn = res.apply(symbolicMultivectorParams).simplifySparsify();
        GaFunction func = new GaFunction(name, casadiFuncParams, List.of(symbolicReturn));
        return func;
    }

    @Override
    public void clearCache() {
        this.functionCache.clear();
        this.cachedFunctionsUsage.clear();
    }

    @Override
    public Map<String, Integer> getUnmodifiableCachedFunctionsUsage() {
        return Collections.unmodifiableMap(this.cachedFunctionsUsage);
    }

    @Override
    public SortedMap<String, Integer> getSortedUnmodifiableCachedFunctionsUsage() {
        return new TreeMap<>(getUnmodifiableCachedFunctionsUsage());
    }

    @Override
    public int getCacheSize() {
        return this.functionCache.size();
    }

    @Override
    public String cachedFunctionUsageToString() {
        SortedMap<String, Integer> cachedFunctionUsage = getSortedUnmodifiableCachedFunctionsUsage();
        int maxKeyLength = cachedFunctionUsage.entrySet().stream()
            .mapToInt(entry -> entry.getKey().length())
            .max().orElse(0);
        String format = String.format("%%-%ss : %%s", maxKeyLength);
        return cachedFunctionUsage.entrySet().stream()
            .map(entry -> String.format(format, entry.getKey(), entry.getValue()))
            .collect(Collectors.joining("\n"));
    }

    private static String getParamName(int i) {
        return String.format("a%s", i);
    }

    /**
     * <pre>
     * Computes index of first occurrence of the same object.
     * Postcondition: for all i=0..objects.size(): <code>firstOcccurence.get(i) <= i</code>.
     * Example: [0:a, 1:b, 2:a, 3:c] -> [0:0, 1:1, 2:0, 3:3]
     * </pre>
     */
    private static List<Integer> computeFirstOccurrences(List<? extends Object> objects) {
        final int size = objects.size();
        Map<Object, Integer> objectToFirstOccurrence = new IdentityHashMap<>(size);
        List<Integer> indexToFirstOccurrence = new ArrayList<>(size);
        for (int i = 0; i < size; ++i) {
            Object object = objects.get(i);
            Integer firstOccurrence = objectToFirstOccurrence.get(object);
            if (firstOccurrence == null) {
                // Object not seen before.
                objectToFirstOccurrence.put(object, i);
                indexToFirstOccurrence.add(i);
            } else {
                // Object seen before.
                indexToFirstOccurrence.add(firstOccurrence);
            }
        }
        return indexToFirstOccurrence;
    }

    /**
     * @param params Either iMultivectorSymbolic or int
     */
    public String createFuncName(String name, Object... params) {
        List<Integer> paramsFirstOccurrences = computeFirstOccurrences(Arrays.asList(params));
        StringBuilder sb = new StringBuilder();
        sb.append(name);
        sb.append("_");
        for (int paramIndex = 0; paramIndex < params.length; ++paramIndex) {
            // Consecutive "_" would not be compatible with casadi function names.
            sb.append("_");
            // firstOccurrences used to take identity of params into account.
            sb.append(getParamName(paramsFirstOccurrences.get(paramIndex)));
            Object param = params[paramIndex];
            if (param instanceof IMultivectorExpression mv) {
                // complete sparsity of the parameter
                String colind = Arrays.stream(mv.getSparsity().getcolind())
                    .mapToObj(String::valueOf)
                    .collect(Collectors.joining("_"));
                String row = Arrays.stream(mv.getSparsity().getrow())
                    .mapToObj(String::valueOf)
                    .collect(Collectors.joining("_"));
                if (!colind.isEmpty()) {
                    sb.append("_colind_").append(colind);
                }
                if (!row.isEmpty()) {
                    sb.append("_row_").append(row);
                }
            } else if (param instanceof Integer intParam) {
                sb.append("_");
                sb.append(intParam);
            } else {
                throw new RuntimeException("Param of unexpected type.");
            }
            sb.append("_");
        }
        sb.setLength(sb.length() - 1);
        return sb.toString();
    }
}
