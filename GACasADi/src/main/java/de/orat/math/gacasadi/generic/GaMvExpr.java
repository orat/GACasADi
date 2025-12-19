package de.orat.math.gacasadi.generic;

import de.dhbw.rahmlab.casadi.SxStatic;
import de.dhbw.rahmlab.casadi.impl.casadi.SX;
import de.dhbw.rahmlab.casadi.impl.casadi.Sparsity;
import de.dhbw.rahmlab.casadi.impl.std.StdVectorSX;
import de.orat.math.gacasadi.algebraGeneric.api.IAlgebra;
import de.orat.math.gacasadi.algebraGeneric.api.IProduct;
import de.orat.math.gacasadi.caching.annotation.api.Uncached;
import de.orat.math.sparsematrix.ColumnVectorSparsity;
import de.orat.math.sparsematrix.SparseStringMatrix;
import java.util.List;

public abstract class GaMvExpr<EXPR extends GaMvExpr<EXPR>> implements IGaMvExpr<EXPR> {

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

    public SX createSparseSX() {
        int basisBladeCount = getIAlgebra().getBladesCount();
        SX sparse = new SX(new Sparsity(basisBladeCount, 1)); // fullSparse
        return sparse;
    }

    @Uncached
    public EXPR createSparse() {
        return create(createSparseSX());
    }

    protected abstract GaFactory<EXPR, ?, ?, ?> fac();

    @Uncached
    @Override
    public EXPR getSparseEmptyInstance() {
        return this.fac().createSparse();
    }

    @Uncached
    @Override
    public EXPR createScalar(double scalar) {
        return this.fac().createExpr(scalar);
    }

    @Override
    public SX getSX() {
        return this.sx;
    }

    public static SX simplifySparsifySX(SX input) {
        SX simple = SxStatic.simplify(input);
        SX sparse = SxStatic.sparsify(simple);
        return sparse;
    }

    @Uncached
    public EXPR simplifySparsify() {
        return create(simplifySparsifySX(this.sx));
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
    public static SX product(IProduct product, SX a, SX b) {
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
    public EXPR createFromScalar(SX sx) {
        // 1x1
        if (!sx.sparsity().is_scalar()) {
            throw new IllegalArgumentException("This is no scalar!");
        }
        SX result = createSparseSX();
        result.at(0).assign(sx);
        return create(result);
    }

    public SX asScalar() {
        if (!this.isScalar()) {
            throw new IllegalArgumentException("This is no scalar!");
        }
        return this.getSX().at(0);
    }

    @Uncached
    public EXPR computeScalar(java.util.function.Function<SX, SX> computer) {
        SX inputScalar = this.asScalar();
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
        SX result = SxStatic.atan2(y.asScalar(), this.asScalar());
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

    /*
    @Override
    public EXPR reverse() {
        var revm = GAOperatorMatrixUtils.createReversionOperatorMatrix(getIAlgebra());
        SX result = SxStatic.mtimes(CasADiUtil.toSX(revm), sx);
        return create(result);
    }
     */
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
        SparseStringMatrix stringMatrix = CasADiUtil.toStringMatrix(sx);
        return stringMatrix.toString(true);
    }
}
