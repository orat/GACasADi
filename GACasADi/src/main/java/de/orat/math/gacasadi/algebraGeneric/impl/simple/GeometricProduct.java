package de.orat.math.gacasadi.algebraGeneric.impl.simple;

import de.orat.math.gacasadi.algebraGeneric.api.CoefficientAndBasisBlade;
import de.orat.math.gacasadi.algebraGeneric.api.BasisBlade;
import de.orat.math.gacasadi.algebraGeneric.api.Coefficient;
import de.orat.math.gacasadi.algebraGeneric.api.CoefficientAndBasisBladeIndex;
import de.orat.math.gacasadi.algebraGeneric.api.IProduct;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GeometricProduct implements IProduct<Multivector> {

    private final Algebra algebra;

    public GeometricProduct(Algebra algebra) {
        this.algebra = algebra;
    }

    /**
     * Das hier könnte auch gecached sein durch eine Cayley-Table.
     */
    @Override
    public CoefficientAndBasisBladeIndex product(int basisBladeIndex1, int basisBladeIndex2) {
        List<BasisBlade> basisBlades = this.algebra.basisBlades;
        BasisBlade aBlade = basisBlades.get(basisBladeIndex1);
        BasisBlade bBlade = basisBlades.get(basisBladeIndex2);
        CoefficientAndBasisBlade cbb = product(aBlade, bBlade);
        if (cbb == CoefficientAndBasisBlade.ZERO) {
            return CoefficientAndBasisBladeIndex.ZERO;
        }
        int basisBladeIndex = this.algebra.basisBladesToIndices.get(cbb.basisBlade());
        var ret = new CoefficientAndBasisBladeIndex(cbb.coefficient().coefficient(), basisBladeIndex);
        return ret;
    }

    // Damit kann man beliebige Cayley-Table Einträge ausrechnen.
    // Lineare Laufzeit.
    // Precondition: iBlade and jBlade are geometricProducts of simple base vectors.
    // Precondition: iBlade and jBlade are sorted.
    // Precondition: iBlade and jBlade don't contain duplicates.
    // Precondition: iBlade and jBlade can share elements.
    // Postcondition: sorted, no duplicates
    public CoefficientAndBasisBlade product(BasisBlade iBlade, BasisBlade jBlade) {
        final int iLen = iBlade.grade();
        final int jLen = jBlade.grade();

        // Scalar
        if (iBlade.isScalar()) {
            return new CoefficientAndBasisBlade(Coefficient.ONE, jBlade);
        } else if (jBlade.isScalar()) {
            return new CoefficientAndBasisBlade(Coefficient.ONE, iBlade);
        }

        int inversions = 0;
        float factor = 1;
        int ii = 0;
        int jj = 0;
        List<Integer> mergeList = new ArrayList<>(iLen + jLen); // Max possible size.
        {
            int i = iBlade.get(0);
            int j = jBlade.get(0);
            final int minLen = Math.min(iLen, jLen);
            for (;;) {
                if (i == j) {
                    inversions += iLen - ii;
                    // If metric entry is zero: could return here.
                    // But test float to zero is difficult.
                    // Maybe use enum in Metric?
                    // But can happen only in certain algebras.
                    // Maybe use differently implemented method in these algebras.
                    // So will not be tested if not necessary.
                    factor *= this.algebra.baseVectorMetric.get(i);
                    ++ii;
                    ++jj;
                    if (ii >= minLen || jj >= minLen) {
                        break;
                    }
                    i = iBlade.get(ii);
                    j = jBlade.get(jj);
                } else if (i > j) {
                    inversions += iLen - ii;
                    mergeList.add(j);
                    ++jj;
                    if (jj >= minLen) {
                        break;
                    }
                    j = jBlade.get(jj);
                } else { // if (i < j) {
                    mergeList.add(i);
                    ++ii;
                    if (ii >= minLen) {
                        break;
                    }
                    i = iBlade.get(ii);
                }
            }
        }

        for (; ii < iLen; ++ii) {
            mergeList.add(iBlade.get(ii));
        }

        for (; jj < jLen; ++jj) {
            mergeList.add(jBlade.get(jj));
        }

        final int sgn = -1 + 2 * ((inversions + 1) & 0x1); // (-1)^inversions
        factor *= sgn;

        if (mergeList.isEmpty()) {
            return CoefficientAndBasisBlade.ZERO;
        }

        return new CoefficientAndBasisBlade(factor, mergeList);
    }

    /**
     * Precondition: a and b are of the same specific algebra.
     */
    // @Override
    public Multivector product(Multivector a, Multivector b) {
        final int aLen = a.basisBladeIndices().size();
        final int bLen = b.basisBladeIndices().size();
        final List<BasisBlade> basisBlades = algebra.basisBlades;

        // calculate product
        List<CoefficientAndBasisBlade> sum = new ArrayList<>(bLen);
        for (int i = 0; i < aLen; ++i) {
            BasisBlade aBlade = basisBlades.get(i);
            float aCoeff = a.coefficents().get(i).coefficient();
            for (int k = 0; k < bLen; ++k) {
                BasisBlade bBlade = basisBlades.get(k);
                CoefficientAndBasisBlade cbb = product(aBlade, bBlade);
                float retCoeff = aCoeff * b.coefficents().get(i).coefficient() * cbb.coefficient().coefficient();
                var retCbb = new CoefficientAndBasisBlade(new Coefficient(retCoeff), cbb.basisBlade());
                sum.add(retCbb);
            }
        }

        // group together
        Map<BasisBlade, Coefficient> sumGrouped = HashMap.newHashMap(aLen * bLen);
        for (CoefficientAndBasisBlade part : sum) {
            sumGrouped.merge(part.basisBlade(), part.coefficient(), GeometricProduct::mergeCoefficients);
        }

        // sort
        List<BasisBlade> sortedBasisBlades = sumGrouped.keySet().stream().sorted(new BasisBladeComparator()).toList();
        List<Integer> retBasisBladeIndices = sortedBasisBlades.stream().map(algebra.basisBladesToIndices::get).toList();
        List<Coefficient> retCoefficients = sortedBasisBlades.stream().map(bl -> sumGrouped.get(bl)).toList();

        // return
        Multivector ret = new Multivector(retBasisBladeIndices, retCoefficients);
        return ret;
    }

    public class BasisBladeComparator implements Comparator<BasisBlade> {

        @Override
        public int compare(BasisBlade a, BasisBlade b) {
            {
                final int aGrade = a.grade();
                final int bGrade = b.grade();
                if (aGrade != bGrade) {
                    return aGrade - bGrade;
                }
            }

            int aIndex = algebra.basisBladesToIndices.get(a);
            int bIndex = algebra.basisBladesToIndices.get(b);
            return aIndex - bIndex;
        }

    }

    private static Coefficient mergeCoefficients(Coefficient a, Coefficient b) {
        return new Coefficient(a.coefficient() * b.coefficient());
    }
}
