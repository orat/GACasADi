package de.orat.math.gacasadi.generic;

import de.dhbw.rahmlab.casadi.SxStatic;
import de.dhbw.rahmlab.casadi.impl.casadi.SX;
import de.dhbw.rahmlab.casadi.impl.casadi.Sparsity;
import de.dhbw.rahmlab.casadi.impl.casadi.SxSubMatrix;
import de.dhbw.rahmlab.casadi.impl.std.StdVectorSX;
import de.dhbw.rahmlab.casadi.spi.ExternalServiceLoader;
import de.dhbw.rahmlab.casadi.spi.ICasADiExternalProcessor;
import de.orat.math.gacasadi.algebraGeneric.api.IAlgebra;
import de.orat.math.gacasadi.algebraGeneric.api.IProduct;
import de.orat.math.gacasadi.caching.annotation.api.Uncached;
import de.orat.math.sparsematrix.ColumnVectorSparsity;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class GaMvExpr<EXPR extends GaMvExpr<EXPR, VAR, VAL>, VAR extends IGaMvVariable<EXPR, VAR, VAL>, VAL extends IGaMvValue<EXPR, VAR, VAL>>
    implements IGaMvExpr<EXPR, VAR, VAL> {

    /**
     * Sparse column vector.
     */
    protected final SX sx;

    protected GaMvExpr(SX sx) {
        this.sx = sx;
    }

    protected GaMvExpr(EXPR other) {
        this.sx = other.sx;
    }

    public abstract IAlgebra getIAlgebra();

    public List<Integer> nzIndices() {
        return sx.get_row().stream().map(Long::intValue).toList();
    }

    @Override
    public int getBladesCount() {
        return getIAlgebra().getBladesCount();
    }

    @Uncached
    public abstract EXPR create(SX sx);

    public static SX createSparseSX(IAlgebra algebra) {
        int basisBladeCount = algebra.getBladesCount();
        SX sparse = new SX(new Sparsity(basisBladeCount, 1)); // fullSparse
        return sparse;
    }

    public SX createSparseSX() {
        return GaMvExpr.createSparseSX(this.getIAlgebra());
    }

    @Uncached
    public EXPR createSparse() {
        return create(createSparseSX());
    }

    protected abstract GaFactory<EXPR, VAR, VAL> fac();

    @Uncached
    @Override
    public EXPR getSparseEmptyInstance() {
        return create(GaMvExpr.createSparseSX(this.getIAlgebra()));
    }

    @Uncached
    @Override
    public EXPR createScalar(double scalar) {
        return this.create(0, scalar);
    }

    @Override
    public SX getSX() {
        return this.sx;
    }

    private static SX simplifySparsifySX(SX input) {
        SX simple = SxStatic.simplify(input);
        SX sparse = SxStatic.sparsify(simple);
        return sparse;
    }

    /**
     * Simplifies only with CasADi.
     */
    @Uncached
    public EXPR simplifyFast() {
        return create(simplifySparsifySX(this.sx));
    }

    @Override
    @Uncached
    public EXPR simplify(List<? extends VAR> variables) {
        Optional<ICasADiExternalProcessor> processorOpt = ExternalServiceLoader.getProcessor();
        SX result;
        if (processorOpt.isPresent()) {
            List<SX> variablesSX = variables.stream().map(VAR::getSX).toList();
            result = processorOpt.get().simplifySparsify(this.sx, variablesSX);
        } else {
            result = GaMvExpr.simplifySparsifySX(this.sx);
        }
        return create(result);
    }

    @Override
    public Sparsity getSparsityCasadi() {
        return this.sx.sparsity();
    }

    @Override
    public ColumnVectorSparsity getSparsity() {
        return CasADiUtil.toColumnVectorSparsity(sx.sparsity());
    }

    @Override
    public EXPR gp(EXPR b) {
        SX gp = GaMvExpr.product(getIAlgebra().gp(), this.sx, b.sx);
        EXPR mv = create(gp);
        System.out.println("---gp()---");
        System.out.println(": input multivector a = " + this.toString());
        System.out.println(": input multivector b = " + b.toString());
        System.out.println(": input identical? = " + (this == b));
        System.out.println(": output multivector" + mv.toString());
        System.out.println(": output multivector sparsity = " + mv.getSparsity().toString());
        return mv;
    }

    @Override
    public EXPR ip(EXPR b) {
        SX gp = GaMvExpr.product(getIAlgebra().inner(), this.sx, b.sx);
        EXPR mv = create(gp);
        System.out.println("---ip()---");
        System.out.println(": input multivector a = " + this.toString());
        System.out.println(": input multivector b = " + b.toString());
        System.out.println(": input identical? = " + (this == b));
        System.out.println(": output multivector" + mv.toString());
        System.out.println(": output multivector sparsity = " + mv.getSparsity().toString());
        return mv;
    }

    @Override
    public EXPR op(EXPR b) {
        SX gp = GaMvExpr.product(getIAlgebra().outer(), this.sx, b.sx);
        EXPR mv = create(gp);
        System.out.println("---op()---");
        System.out.println(": input multivector a = " + this.toString());
        System.out.println(": input multivector b = " + b.toString());
        System.out.println(": input identical? = " + (this == b));
        System.out.println(": output multivector" + mv.toString());
        System.out.println(": output multivector sparsity = " + mv.getSparsity().toString());
        return mv;
    }

    // Precondition: a and b are of same length, column vectors, same algebra.
    protected static SX product(IProduct product, SX a, SX b) {
        final long n_rows = a.rows(); //==b.rows()
        int[] aIndices = a.get_row().stream().mapToInt(Long::intValue).toArray();
        int[] bIndices = b.get_row().stream().mapToInt(Long::intValue).toArray();
        SX result = new SX(new Sparsity(n_rows, 1));
        for (int ai : aIndices) {
            var aCell = a.at(ai, 0);
            for (int bk : bIndices) {
                var bCell = b.at(bk, 0);
                var mv = product.product(ai, bk);
                /*
                if (mv == Multivector.ZERO) {
                    continue;
                }
                 */
                for (var cbbi : mv.entries()) {
                    int bbi = cbbi.basisBladeIndex();
                    float coeff = cbbi.coefficient();
                    var resCell = result.at(bbi, 0);
                    SX factor = SxStatic.mtimes_(new StdVectorSX(new SX[]{new SX(coeff), aCell, bCell}));
                    SX newSum = SxStatic.plus(resCell, factor);
                    resCell.assign(newSum);
                }
            }
        }
        return result;
    }

    /**
     * Add.
     *
     * Multivector addition
     *
     * @param a
     * @param b
     * @return a + b
     */
    @Override
    public EXPR add(EXPR b) {
        //System.out.println("sparsity(a)="+sx.sparsity().toString(true));
        //System.out.println("sparsity(b)="+( b).getSX().sparsity().toString(true));
        SX result = SxStatic.plus(sx, b.getSX());
        //System.out.println("sparsity(add)="+result.sparsity().toString(true));
        return create(result);
    }

    @Override
    public EXPR hadamard(EXPR b) {
        // element-wise mulitplication (linear mapping)
        SX result = SxStatic.times(sx, b.getSX());
        return create(result);
    }

    /**
     * Multivector subtraction.
     *
     * @param a
     * @param b
     * @return a - b
     */
    @Override
    public EXPR sub(EXPR b) {
        SX result = SxStatic.minus(sx, b.getSX());
        return create(result);
    }

    @Uncached
    public EXPR createFromScalar(SX scalarSX) {
        // checks dimension is 1x1
        if (!scalarSX.sparsity().is_scalar()) {
            throw new IllegalArgumentException("This is no scalar!");
        }
        SX result = createSparseSX();
        result.at(0, 0).assign(scalarSX);
        return create(result);
    }

    /**
     * Use only if you know, what you do! Returns a single SX cell.
     */
    @Deprecated
    public SxSubMatrix asScalarSXCell() {
        if (!this.isScalar()) {
            throw new IllegalArgumentException("This is no scalar!");
        }
        return this.getSX().at(0, 0);
    }

    /**
     * Be cautious! Works only, if scalar and numeric.
     */
    public double asScalarDouble() {
        return SxStatic.evalf(this.asScalarSXCell()).scalar(); // asScalarSXCell used correctly.
    }

    @Uncached
    public EXPR computeScalar(java.util.function.Function<SX, SX> computer) {
        SX inputScalar = this.asScalarSXCell(); // asScalarSXCell used correctly.
        SX outputScalar = computer.apply(inputScalar);
        EXPR mv = createFromScalar(outputScalar);
        return mv;
    }

    @Override
    public EXPR scalarAbs() {
        return computeScalar(SxStatic::abs);
    }

    @Override
    public EXPR scalarAtan2(EXPR y) {
        if (!isScalar()) {
            throw new IllegalArgumentException("The argument x of atan2(y,x) is no scalar!");
        }
        if (!y.isScalar()) {
            throw new IllegalArgumentException("The argument y of atan2(y,x) is no scalar!");
        }
        SX result = SxStatic.atan2(y.asScalarSXCell(), this.asScalarSXCell()); // asScalarSXCell used correctly.
        return createFromScalar(result);
    }

    @Override
    public EXPR scalarSqrt() {
        return computeScalar(SxStatic::sqrt);
    }

    @Override
    public EXPR scalarSign() {
        return computeScalar(SxStatic::sign);
    }

    @Override
    public EXPR scalarSin() {
        return computeScalar(SxStatic::sin);
    }

    @Override
    public EXPR scalarCos() {
        return computeScalar(SxStatic::cos);
    }

    @Override
    public EXPR scalarTan() {
        return computeScalar(SxStatic::tan);
    }

    @Override
    public EXPR scalarAtan() {
        return computeScalar(SxStatic::atan);
    }

    @Override
    public EXPR scalarAsin() {
        return computeScalar(SxStatic::asin);
    }

    @Override
    public EXPR scalarAcos() {
        return computeScalar(SxStatic::acos);
    }

    @Override
    public EXPR scalarInverse() {
        return computeScalar(SxStatic::inv);
    }

    @Override
    public int grade() {
        List<Integer> grades = this.getIAlgebra().getGrades(nzIndices());
        if (grades.size() != 1) {
            throw new IllegalArgumentException(String.format("grades count not equal to 1: %s", grades.toString()));
        }
        return grades.get(0);
    }

    @Override
    public int[] grades() {
        return this.getIAlgebra().getGrades(nzIndices()).stream().mapToInt(Integer::intValue).toArray();
    }

    @Override
    public boolean isScalar() {
        final long nnz = this.sx.nnz_();
        if (nnz == 1) {
            // Regular scalar?
            return this.sx.at(0, 0).nnz_() == 1;
        }
        // Structural zero?
        return nnz == 0;
    }

    @Override
    public boolean isSparseEmpty() {
        return this.sx.nnz_() == 0;
    }

    @Override
    public int pseudoscalarGrade() {
        return this.getIAlgebra().getGradesCount() - 1;
    }

    @Override
    public EXPR gradeSelection(int grade) {
        int[] indicesOfGrade = this.getIAlgebra().getIndizes(grade);
        SX res = createSparseSX();
        for (int i : indicesOfGrade) {
            // Structural zero will be propagated.
            SX thisCell = this.sx.at(i, 0);
            res.at(i, 0).assign(thisCell);
        }

        return create(res);
    }

    /**
     * <pre>
     * Beim direkten Aufruf so schneller.
     * Wenn of verwendet, wäre es sinnvoller, das double s in ein EXPR zu verpacken und dann die normale gp Funktion aufzurufen.
     * </pre>
     */
    @Uncached
    @Override
    public EXPR gpWithScalar(double s) {
        SX res = createSparseSX();
        SX scalar = new SX(s);
        for (int i : nzIndices()) {
            SX thisCell = this.sx.at(i, 0);
            SX resCell = SxStatic.times(thisCell, scalar);
            res.at(i, 0).assign(resCell);
        }
        return create(res);
    }

    // Could be implemented with Hadamard Product.
    @Override
    public EXPR reverse() {
        IAlgebra algebra = this.getIAlgebra();
        SX res = createSparseSX();
        for (int i : nzIndices()) {
            int grade = algebra.getGrade(i);
            int sign = algebra.gradeToReverseSign(grade);
            SX resCell = this.sx.at(i, 0);
            if (sign != 1) {
                resCell = SxStatic.times(new SX(sign), resCell);
            }
            res.at(i, 0).assign(resCell);
        }
        return create(res);
    }

    // Could be implemented with Hadamard Product.
    @Override
    public EXPR gradeInversion() {
        IAlgebra algebra = this.getIAlgebra();
        SX res = createSparseSX();
        for (int i : nzIndices()) {
            int grade = algebra.getGrade(i);
            int sign = algebra.gradeToGradeInversionSign(grade);
            SX resCell = this.sx.at(i, 0);
            if (sign != 1) {
                resCell = SxStatic.times(new SX(sign), resCell);
            }
            res.at(i, 0).assign(resCell);
        }
        return create(res);
    }

    // Could be implemented with Hadamard Product.
    @Override
    public EXPR conjugate() {
        IAlgebra algebra = this.getIAlgebra();
        SX res = createSparseSX();
        for (int i : nzIndices()) {
            int grade = algebra.getGrade(i);
            int sign = algebra.gradeToConjugateSign(grade);
            SX resCell = this.sx.at(i, 0);
            if (sign != 1) {
                resCell = SxStatic.times(new SX(sign), resCell);
            }
            res.at(i, 0).assign(resCell);
        }
        return create(res);
    }

    @Override
    public EXPR scp(EXPR rhs) {
        return this.lc(rhs).gradeSelection(0);
    }

    @Override
    public boolean isBivector() {
        int[] grades = this.grades();
        if (grades.length != 1) {
            return false;
        }
        return grades[0] == 2;
    }

    @Override
    public boolean isEven() {
        // Could be implemented with getEvenIndices and check, if there is one of nzIndices which is not in getEvenIndices.
        List<Integer> grades = getIAlgebra().getGrades(nzIndices());
        for (int grade : grades) {
            if (grade % 2 != 0) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return this.sx.toString();
    }

    @Override
    public String LaTeXify() {
        return ExternalServiceLoader.getProcessor().orElseThrow(UnsupportedOperationException::new).LaTeXify(this.sx);
    }

    // Could be implemented with Hadamard Product.
    @Override
    public EXPR euclid() {
        List<Integer> euclidBladeIndices = this.getIAlgebra().getEuclidBladeIndices();
        List<Integer> nzIndices = this.nzIndices();
        List<Integer> indicesIntersection = GaMvExpr.intersection(euclidBladeIndices, nzIndices);
        return this.filter(indicesIntersection);
    }

    // Could be implemented with Hadamard Product.
    @Override
    public EXPR idle() {
        List<Integer> idleBladeIndices = this.getIAlgebra().getIdleBladeIndices();
        List<Integer> nzIndices = this.nzIndices();
        List<Integer> indicesIntersection = GaMvExpr.intersection(idleBladeIndices, nzIndices);
        return this.filter(indicesIntersection);
    }

    /**
     * <pre>
     * Precondition:
     * - the highest index in retainedIndices is less than or equal to the size of the MV in the current algebra.
     * </pre>
     */
    @Uncached
    public EXPR filter(List<Integer> retainedIndices) {
        SX res = createSparseSX();
        for (int index : retainedIndices) {
            SX resCell = this.sx.at(index, 0);
            res.at(index, 0).assign(resCell);

        }
        return create(res);
    }

    /**
     * <pre>
     * Precondition:
     * - retainedIndex is less than or equal to the size of the MV in the current algebra.
     * </pre>
     */
    public EXPR filter(int retainedIndex) {
        SX res = createSparseSX();
        SX resCell = this.sx.at(retainedIndex, 0);
        res.at(retainedIndex, 0).assign(resCell);
        return create(res);
    }

    // Would be even easier, if their size would be the same. BitSet for Blade Indices.
    /**
     * <pre>
     * Preconditions:
     * - Sorted
     * - No duplicates
     * </pre>
     */
    private static List<Integer> intersection(List<Integer> a, List<Integer> b) {
        final int m = a.size();
        final int n = b.size();
        int i = 0;
        int k = 0;
        List<Integer> intersection = new ArrayList<>(Math.min(m, n)); // Upper bound size.
        while (i < m && k < n) {
            int a_i = a.get(i);
            int b_k = b.get(k);
            if (a_i == b_k) {
                intersection.add(a_i);
                ++i;
                ++k;
            } else if (a_i < b_k) {
                ++i;
            } else { // (a_i > b_k)
                ++k;
            }
        }
        return intersection;
    }

    /**
     * <pre>
     * Precondition:
     * - index is less than or equal to the size of the MV in the current algebra.
     * </pre>
     */
    @Uncached
    public EXPR create(int index, double value) {
        SX res = this.createSparseSX();
        if (index >= res.rows()) {
            throw new IllegalArgumentException(String.format("Index %s exceeds algebra dimension.", index));
        }
        res.at(index, 0).assign(new SX(value));
        return create(res);
    }

    /**
     * <pre>
     * Precondition:
     * - same size
     * - all indices are less than or equal to the size of the MV in the current algebra.
     * </pre>
     */
    @Uncached
    public EXPR create(List<Integer> indices, List<Double> values) {
        final int size = indices.size();
        if (values.size() != size) {
            throw new IllegalArgumentException("indices and values are not of same size.");
        }
        SX res = this.createSparseSX();
        for (int i = 0; i < size; ++i) {
            int index = indices.get(i);
            double value = values.get(i);
            res.at(index, 0).assign(new SX(value));
        }
        return create(res);
    }

    @Uncached
    public EXPR create(List<String> bladeOfBasevectors, double value) {
        SX res = this.createSparseSX();
        int index = this.getIAlgebra().indexOfBlade(bladeOfBasevectors.toArray(String[]::new));
        res.at(index, 0).assign(new SX(value));
        return create(res);
    }

    public static SX filterShift(IAlgebra algebra, SX sx, int selectIndex, int shiftToIndex) {
        SX res = GaMvExpr.createSparseSX(algebra);
        SX resCell = sx.at(selectIndex, 0);
        res.at(shiftToIndex, 0).assign(resCell);
        return res;
    }

    public EXPR filterShift(int selectIndex, int shiftToIndex) {
        return create(GaMvExpr.filterShift(this.getIAlgebra(), this.sx, selectIndex, shiftToIndex));
    }

    @Uncached
    public EXPR filterShift(List<String> selectBladeOfBasevectors, List<String> shiftToBladeOfBasevectors) {
        IAlgebra algebra = this.getIAlgebra();
        return create(GaMvExpr.filterShift(algebra, this.sx,
            algebra.indexOfBlade(selectBladeOfBasevectors.toArray(String[]::new)),
            algebra.indexOfBlade(shiftToBladeOfBasevectors.toArray(String[]::new))));
    }

    public EXPR filterScalar() {
        return this.filterShift(0, 0);
    }

    @Override
    public EXPR coef1(EXPR coefBladeMV) {
        List<Integer> coefBladeMVIndices = coefBladeMV.nzIndices();
        if (coefBladeMVIndices.size() != 1) {
            throw new IllegalArgumentException(String.format("coef() allows only 1 blade but got %s", coefBladeMVIndices.size()));
        }
        int coefBladeIndex = coefBladeMVIndices.get(0);
        return filterShift(coefBladeIndex, 0);
    }

    @Uncached
    @Override
    public EXPR coef2(String... coefBladeOfBasevectors) {
        int coefBladeIndex = this.getIAlgebra().indexOfBlade(coefBladeOfBasevectors);
        return filterShift(coefBladeIndex, 0);
    }
}
