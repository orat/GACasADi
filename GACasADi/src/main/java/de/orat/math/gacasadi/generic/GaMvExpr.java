package de.orat.math.gacasadi.generic;

import de.dhbw.rahmlab.casadi.SxStatic;
import de.dhbw.rahmlab.casadi.impl.casadi.SX;
import de.dhbw.rahmlab.casadi.impl.casadi.Sparsity;
import de.dhbw.rahmlab.casadi.impl.std.StdVectorSX;
import de.orat.math.gacasadi.algebraGeneric.api.IAlgebra;
import de.orat.math.gacasadi.algebraGeneric.api.IProduct;
import de.orat.math.gacasadi.caching.annotation.api.Uncached;
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

    @Uncached
    public abstract EXPR create(SX sx);

    @Uncached
    public EXPR createSparse() {
        return create(createSparseSX());
    }

    public SX createSparseSX() {
        int basisBladeCount = getIAlgebra().getBladesCount();
        SX sparse = new SX(new Sparsity(basisBladeCount, 1)); // fullSparse
        return sparse;
    }

    @Uncached
    public EXPR create(int index, double value) {
        SX mv = createSparseSX();
        mv.at(index, 0).assign(new SX(value));
        return create(mv);
    }

    /**
     * Precondition: same size
     */
    @Uncached
    public EXPR create(List<Integer> indices, List<Double> values) {
        final int size = indices.size();
        if (values.size() != size) {
            throw new IllegalArgumentException("indices and values are not of same size.");
        }
        SX mv = createSparseSX();
        for (int i = 0; i < size; ++i) {
            int index = indices.get(i);
            double value = values.get(i);
            mv.at(index, 0).assign(new SX(value));
        }
        return create(mv);
    }

    @Override
    public SX getSX() {
        return this.sx;
    }

    @Uncached
    public EXPR simplifySparsify() {
        SX simple = SxStatic.simplify(this.sx);
        SX sparse = SxStatic.sparsify(simple);
        return create(sparse);
    }

    @Override
    public Sparsity getSparsityCasadi() {
        return this.sx.sparsity();
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
        SX result = createSparse().getSX();
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
}
