package de.orat.math.gacasadi.algebraGeneric.impl.simple;

import de.orat.math.gacasadi.algebraGeneric.api.BasisBlade;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class Algebra {

    // ToDo: Change to Methods.
    /**
     * <pre>
     * Contains squares of 1-vectors. Scalar not included.
     * Usually 1, -1, 0.
     * -> Maybe use enum for this.
     * </pre>
     */
    final List<Float> baseVectorMetric = List.of(1f, 1f, 1f, 1f, -1f);  //3d cga
    final List<Integer> baseVectorIndices = IntStream.range(0, baseVectorMetric.size()).boxed().toList();
    // An dem Index 0 ist ein BasisBlade mit einer leeren Liste. Dieses enspricht dem Skalar.
    // Eigentlich wäre es auch schön, eine List<Grades> zu haben.
    final List<BasisBlade> basisBlades = allCombinationsReuse(baseVectorMetric.size()).stream().map(BasisBlade::new).toList();
    // ToDo: Check, dass höchster Index in int passt.
    // 2^n ausrechnen mit long.
    // Oder mit Double und schauen, ob es einen Overflow gab.
    final Map<BasisBlade, Integer> basisBladesToIndices = calculateBasisBladesToIndices(basisBlades);
    // Ich könnte hierfür eine spezialisierte Funktion machen, wie schon detaillierter überlegt.
    // final List<Double> basisBladesMetric;

    private static Map<BasisBlade, Integer> calculateBasisBladesToIndices(List<BasisBlade> basisBlades) {
        final int size = basisBlades.size();
        Map<BasisBlade, Integer> basisBladesToIndices = HashMap.newHashMap(size);
        for (int i = 0; i < size; ++i) {
            basisBladesToIndices.put(basisBlades.get(i), i);
        }
        return basisBladesToIndices;
    }

    private static List<List<Integer>> allCombinationsReuse(int n) {
        List<List<Integer>> result = new ArrayList<>();

        List<List<Integer>> previous = new ArrayList<>();
        previous.add(new ArrayList<>());
        result.addAll(previous);

        for (int k = 1; k <= n; k++) {
            List<List<Integer>> current = new ArrayList<>();

            for (List<Integer> combo : previous) {
                int last = combo.isEmpty() ? 0 : combo.get(combo.size() - 1);

                for (int next = last + 1; next <= n; next++) {
                    List<Integer> extended = new ArrayList<>(combo);
                    extended.add(next);
                    current.add(extended);
                }
            }

            result.addAll(current);
            previous = current;
        }

        return result;
    }
}