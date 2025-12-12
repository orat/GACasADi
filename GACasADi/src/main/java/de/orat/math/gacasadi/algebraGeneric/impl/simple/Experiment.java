package de.orat.math.gacasadi.algebraGeneric.impl.simple;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.IntStream;

public class Experiment {

    // Contains squares of simpleBase vectors.
    // Scalar not included.
    final double[] simpleBaseMetric = {1, 1, 1, 1, -1}; //3d cga

    // Indizes of simpleBase vectors.
    public int[] simpleBaseIndizes = IntStream.range(0, simpleBaseMetric.length).toArray();

    // ei^2, if i==j
    // 0 otherwise
    // Precondition: ei is orthogonal to all.
    public double simpleBaseScalarProduct(int i, int j) {
        if (i != j) {
            return 0;
        }
        return simpleBaseMetric[i];
    }

    // Precondition: iArr and jArr are ordered.
    // sum of ei^2, if iArr is same as jArr.
    // 0 otherwise.
    // Schneller, wenn iArr und jArr gecachedte HashCodes haben würden.
    public double simpleBaseScalarProduct(int[] iArr, int[] jArr) {
        if (iArr.length != jArr.length) {
            return 0;
        }
        final int len = iArr.length;
        double sum = 0;
        for (int i = 0; i < len; ++i) {
            if (iArr[i] != jArr[i]) {
                return 0;
            }
            sum += simpleBaseMetric[i];
        }
        return sum;
    }

    // Postcondition: sorted, no duplicates
    public ICompound simpleBaseOuterProduct(int i, int j) {
        if (i == j) {
            return zeroCompound;
        }
        if (i < j) {
            return new CompoundOne(new int[]{i, j});
        } else {
            return new CompoundMinusOne(new int[]{j, i});
        }
    }

    public static interface ICompound {

        double factor();

        int[] iArr();
    }

    public record Compound(double factor, int[] iArr) implements ICompound {

    }

    public record CompoundOne(int[] iArr) implements ICompound {

        @Override
        public double factor() {
            return 1;
        }
    }

    public record CompoundMinusOne(int[] iArr) implements ICompound {

        @Override
        public double factor() {
            return -1;
        }
    }

    // Precondition: iArr and jArr are outerProducts of simple base vectors.
    // Precondition: iArr and jArr are sorted.
    // Precondition: iArr and jArr don't contain duplicates.
    // Precondition: iArr and jArr contain both at least 1 element.
    public int simpleBaseInversionCount(int[] iArr, int[] jArr) {
        final int iLen = iArr.length;
        final int jLen = jArr.length;
        int inversions = 0;
        int ii = 0;
        int jj = 0;
        int i = iArr[0];
        int j = jArr[0];
        // Idea: Try to insert consecutively each j into iArr.
        for (;;) {
            if (i > j) {
                inversions += iLen - ii;
                ++jj;
                if (jj >= jLen) {
                    break;
                }
                j = jArr[jj];
            } else {
                ++ii;
                if (ii >= iLen) {
                    break;
                }
                i = iArr[ii];
            }
        }
        return inversions;
    }

    private static final int[] zeroArr = new int[0];
    private static final Compound zeroCompound = new Compound(0, zeroArr);

    // Damit kann man beliebige Cayley-Table Einträge ausrechnen.
    // Lineare Laufzeit.
    // Precondition: iArr and jArr are geometricProducts of simple base vectors.
    // Precondition: iArr and jArr are sorted.
    // Precondition: iArr and jArr don't contain duplicates.
    // Precondition: iArr and jArr can share elements.
    // Precondition: iArr and jArr contain both at least 1 element.
    // Postcondition: sorted, no duplicates
    public Compound simpleBaseGeometricProduct(int[] iArr, int[] jArr) {
        final int iLen = iArr.length;
        final int jLen = jArr.length;
        int inversions = 0;
        double factor = 1;
        int ii = 0;
        int jj = 0;
        List<Integer> mergeList = new ArrayList<>();
        {
            int i = iArr[0];
            int j = jArr[0];
            final int minLen = Math.min(iLen, jLen);
            for (;;) {
                if (i == j) {
                    inversions += iLen - ii;
                    factor *= this.simpleBaseMetric[i];
                    ++ii;
                    ++jj;
                    if (ii >= minLen || jj >= minLen) {
                        break;
                    }
                    i = iArr[ii];
                    j = jArr[jj];
                } else if (i > j) {
                    inversions += iLen - ii;
                    mergeList.add(j);
                    ++jj;
                    if (jj >= minLen) {
                        break;
                    }
                    j = jArr[jj];
                } else { // if (i < j) {
                    mergeList.add(i);
                    ++ii;
                    if (ii >= minLen) {
                        break;
                    }
                    i = iArr[ii];
                }
            }
        }

        for (; ii < iLen; ++ii) {
            mergeList.add(iArr[ii]);
        }

        for (; jj < jLen; ++jj) {
            mergeList.add(jArr[jj]);
        }

        final int sgn = -1 + 2 * ((inversions + 1) & 0x1); // (-1)^inversions
        factor *= sgn;

        return new Compound(factor, mergeList.stream().mapToInt(Integer::intValue).toArray());
    }

    public static List<List<Integer>> combineTimes(final int n, final int k) {
        List<List<Integer>> result = new ArrayList<>();

        if (k > n || k <= 0) {
            return result;
        }

        int[] indices = new int[k];
        for (int i = 0; i < k; i++) {
            indices[i] = i + 1;
        }

        while (true) {

            List<Integer> combo = new ArrayList<>();
            for (int x : indices) {
                combo.add(x);
            }
            result.add(combo);

            int i;
            for (i = k - 1; i >= 0; i--) {
                if (indices[i] < n - (k - 1 - i)) {
                    indices[i]++;
                    for (int j = i + 1; j < k; j++) {
                        indices[j] = indices[j - 1] + 1;
                    }
                    break;
                }
            }

            if (i < 0) {
                break;
            }
        }

        return result;
    }

    public static List<List<Integer>> allCombinationsReuse(int n) {
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

    public static class CombinationIterator implements Iterator<List<Integer>> {

        private final int n;

        private List<List<Integer>> currentLevel = new ArrayList<>();

        private Iterator<List<Integer>> levelIter;

        private List<Integer> nextValue = null;

        private int k = 0;

        public CombinationIterator(int n) {
            this.n = n;

            currentLevel.add(new ArrayList<>());
            levelIter = currentLevel.iterator();
            prepareNext();
        }

        @Override
        public boolean hasNext() {
            return nextValue != null;
        }

        @Override
        public List<Integer> next() {
            if (nextValue == null) {
                throw new NoSuchElementException();
            }

            List<Integer> result = nextValue;
            prepareNext();
            return result;
        }

        private void prepareNext() {
            if (levelIter.hasNext()) {

                nextValue = levelIter.next();
                return;
            }

            if (k == n) {

                nextValue = null;
                return;
            }

            k++;

            List<List<Integer>> nextLevel = new ArrayList<>();

            for (List<Integer> combo : currentLevel) {
                int last = combo.isEmpty() ? 0 : combo.get(combo.size() - 1);

                for (int x = last + 1; x <= n; x++) {
                    List<Integer> newCombo = new ArrayList<>(combo);
                    newCombo.add(x);
                    nextLevel.add(newCombo);
                }
            }

            currentLevel = nextLevel;
            levelIter = currentLevel.iterator();

            prepareNext();
        }
    }

    public static class CombinationIteratorIntArray implements Iterator<int[]> {

        private final int n;

        private int[][] currentLevel;
        private int currentIndex = 0;

        private int levelSize = 0;

        private int k = 0;
        private int[] nextValue;

        public CombinationIteratorIntArray(int n) {
            this.n = n;

            currentLevel = new int[][]{new int[0]};
            levelSize = 1;
            prepareNext();
        }

        @Override
        public boolean hasNext() {
            return nextValue != null;
        }

        @Override
        public int[] next() {
            if (nextValue == null) {
                throw new NoSuchElementException();
            }

            int[] result = nextValue;
            prepareNext();
            return result;
        }

        private void prepareNext() {

            if (currentIndex < levelSize) {
                nextValue = currentLevel[currentIndex++];
                return;
            }

            if (k == n) {
                nextValue = null;
                return;
            }

            k++;

            int newCount = 0;
            for (int[] combo : currentLevel) {
                int last = combo.length == 0 ? 0 : combo[combo.length - 1];
                newCount += (n - last);
            }

            int[][] nextLevel = new int[newCount][];
            int pos = 0;

            for (int[] combo : currentLevel) {
                int last = combo.length == 0 ? 0 : combo[combo.length - 1];

                for (int x = last + 1; x <= n; x++) {
                    int[] newCombo = new int[combo.length + 1];
                    System.arraycopy(combo, 0, newCombo, 0, combo.length);
                    newCombo[combo.length] = x;
                    nextLevel[pos++] = newCombo;
                }
            }

            currentLevel = nextLevel;
            levelSize = nextLevel.length;
            currentIndex = 0;

            prepareNext();
        }
    }

    public static void main(String[] args) {
        CombinationIteratorIntArray it = new CombinationIteratorIntArray(2);

        while (it.hasNext()) {
            int[] c = it.next();
            System.out.println(Arrays.toString(c));
        }
    }

    public static void main4(String[] args) {
        CombinationIterator it = new CombinationIterator(20);

        while (it.hasNext()) {
            System.out.println(it.next());
        }
    }

    public static void main3(String[] args) {
        int n = 4;
        int k = 2;

        List<List<Integer>> result = allCombinationsReuse(100);

        for (List<Integer> combo : result) {
            System.out.println(combo);
        }
    }

    public static void main2(String[] args) {
        Experiment e = new Experiment();

        int[] a = new int[]{0, 1, 2}; //e123
        int[] b = new int[]{0, 1}; //e12

        // -e3
        // Korrekt. e3 hat Index 2.
        var ret = e.simpleBaseGeometricProduct(a, b);
        System.out.println(ret.factor);
        System.out.println(Arrays.toString(ret.iArr()));
    }
}
