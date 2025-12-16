package de.orat.math.gacasadi.specific.pga;

import de.dhbw.rahmlab.casadi.SxStatic;
import de.dhbw.rahmlab.casadi.api.SXColVec;
import de.dhbw.rahmlab.casadi.api.SXScalar;
import static de.dhbw.rahmlab.casadi.api.SXScalar.ZERO_SXScalar;
import de.dhbw.rahmlab.casadi.impl.casadi.SX;
import de.dhbw.rahmlab.casadi.impl.casadi.SXElem;
import de.dhbw.rahmlab.casadi.impl.casadi.Sparsity;
import de.orat.math.gacalc.api.MultivectorExpression;
import de.orat.math.gacalc.spi.IConstants;
import de.orat.math.gacalc.spi.IMultivectorExpression;
import de.orat.math.gacalc.util.CayleyTable;
import de.orat.math.gacasadi.algebraGeneric.api.IAlgebra;
import de.orat.math.gacasadi.caching.annotation.api.GenerateCached;
import de.orat.math.gacasadi.caching.annotation.api.Uncached;
import de.orat.math.gacasadi.generic.GaMvExpr;
import de.orat.math.gacasadi.generic.IGetSX;
import de.orat.math.gacasadi.generic.IGetSparsityCasadi;
import de.orat.math.sparsematrix.MatrixSparsity;
import de.orat.math.sparsematrix.SparseDoubleMatrix;
import java.util.Arrays;
import util.cga.CGACayleyTable;

@GenerateCached(warnFailedToCache = false, warnUncached = false)
public class PgaMvExpr extends GaMvExpr<PgaMvExpr> implements IMultivectorExpression<PgaMvExpr>, IGetSX, IGetSparsityCasadi {

    private final static PgaFactory fac = PgaFactory.instance;

    protected static PgaFactory getFactory() {
        return fac;
    }

    protected PgaMvExpr(PgaMvExpr mv) {
        super(mv);
    }

    protected PgaMvExpr(SX sx) {
        super(sx);
    }

    @Uncached
    @Override
    public PgaMvExpr create(SX sx) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private static final SX ZERO_SX = new SX(new Sparsity(1, 1));

    private static SXColVec getRotor(PgaMvExpr expr) {
        // 0,5,6,7,8,9,10,15 --> 0,1,2,3,4,5,6,7
        int[] evenIndizes = CGACayleyTable.getEvenIndizes();
        return new SXColVec(expr.getSX(), evenIndizes);
    }

    private static final PgaConstantsExpr CONSTANTS = PgaConstantsExpr.instance;

    public static PgaMvExpr create(SparseDoubleMatrix mx) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    // non linear operators/functions
    // [8] M Roelfs and S De Keninck. 2021.
    // Graded Symmetry Groups: Plane and Simple. arXiv:2107.03771 [math-ph]
    // https://arxiv.org/pdf/2107.03771
    // https://enki.ws/ganja.js/examples/coffeeshop.html#NSELGA
    // exponential of a bivector (B) or a scalar for PGA (R301)
    /**
     * Basis of PGA 1,e0,e1,e2,e3<p>
     * 1,e0,e1,e2,e3,e01,e02,e03,e12,e13,e23,e012,e013,e023,e123,e0123<p>
     *
     * Input:
     * <p>
     * B = B0e01 + B1e02 + B2e03 + B3e12 + B4e31 + B5e23<p>
     *
     * @return Rotor = R0 + R1e01 + R2e02 + R3e03 + R4e11 + R5e31 + R6e23 + R7e0123
     */
    @Override
    public PgaMvExpr exp() {
        if (this.isScalar()) {
            return computeScalar(SxStatic::exp);
        } else if (!this.isBivector()) {
            throw new IllegalArgumentException("exp() defined for bivectors and scalars only (" + this.toString() + ")!");
        }

        SXScalar[] generalRotorValues;

        // 5,6,7,8,9,10 --> 0,1,2,3,4,5
        // coefficient 9(4) hat anderes Vorzeichen
        SXColVec B = new SXColVec(this.getSX(), CGACayleyTable.getBivectorIndizes());

        // java if-else is possible because only test for structural zeros
        if (B.get(3).isZero() && B.get(4).isZero() && B.get(5).isZero()) {
            generalRotorValues = new SXScalar[]{new SXScalar(1), B.get(0), B.get(1), B.get(2),
                ZERO_SXScalar, ZERO_SXScalar, ZERO_SXScalar, ZERO_SXScalar};
        } else {
            // B3²+B4²+B5² corresponding to e12 + e31 + e23
            SXScalar l = SXScalar.sumSq(B, new int[]{3, 4, 5});
            SXScalar m = B.get(0).mul(B.get(5)).sub(B.get(1).mul(B.get(4)).
                add(B.get(2).mul(B.get(3))));
            SXScalar a = l.sqrt();
            SXScalar c = a.cos();
            SXScalar s = a.sin().div(a);
            SXScalar t = m.div(l).mul(c.sub(s));
            generalRotorValues = new SXScalar[]{
                c,
                s.mul(B.get(0)).add(t.mul(B.get(5))),
                s.mul(B.get(1)).sub(t.mul(B.get(4))),
                s.mul(B.get(2)).add(t.mul(B.get(3))),
                s.mul(B.get(3)),
                s.mul(B.get(4)).negate(),
                s.mul(B.get(5)),
                m.mul(s)};
        }

        SXElem[] generalRotorValuesSXElem = Arrays.stream(generalRotorValues)
            .map(SXScalar::sx)
            .map(SX::scalar)
            .toArray(SXElem[]::new);

        SX result = new SXColVec(this.getCayleyTable().getBladesCount(),
            generalRotorValuesSXElem, CGACayleyTable.getEvenIndizes()).sx;

        return create(result);
    }

    /**
     * CGA R4,1. e1*e1 = e2*e2 = e3*e3 = e4*4 = 1, e5*e5 = -1
     * <p>
     *
     * Normalize an even element (a general rotor R with 16 coefficients) X =
     * [1,e12,e13,e14,e15,e23,e24,e25,e34,e35,e45,e1234,e1235,e1245,e1345,e2345]
     * <p>
     *
     * Normalization, Square Roots, and the Exponential and Logarithmic Maps in<br>
     * Geometric Algebras of Less than 6D<br>
     * S. de. Keninck, M. Roelfs, 2022
     */
    @Override
    public PgaMvExpr normalizeRotor() {
        if (!this.isEven()) {
            throw new IllegalArgumentException("Multivector must be an even element/general rotor!");
        }

        SXColVec R = getRotor(this);

        SXScalar s = (new SXScalar(1)).div(
            (R.get(0).sq().add(R.get(4).sq()).
                add(R.get(5).sq()).add(R.get(6).sq())).sqrt());
        SXScalar d = (R.get(7).mul(R.get(0)).sub(
            R.get(1).mul(R.get(6)).add(R.get(2).mul(R.get(5)))
                .sub(R.get(3).mul(R.get(4))))).mul(s.sq());

        SXScalar[] generalRotorValues = new SXScalar[]{
            R.get(0).mul(s),
            R.get(1).mul(s).add(R.get(6).mul(d)),
            R.get(2).mul(s).add(R.get(5).mul(d)),
            R.get(3).mul(s).sub(R.get(4).mul(d)),
            R.get(4).mul(s),
            R.get(5).mul(s),
            R.get(6).mul(s),
            R.get(7).mul(s).sub(R.get(0).mul(d))
        };

        SXElem[] valuesSXElem = Arrays.stream(generalRotorValues)
            .map(SXScalar::sx)
            .map(SX::scalar)
            .toArray(SXElem[]::new);

        // create SX with sparsity corresponding to a rotor (even element)
        return create(new SXColVec(this.getCayleyTable().getBladesCount(), valuesSXElem, CGACayleyTable.getEvenIndizes()).sx);
    }

    @Override
    public PgaMvExpr sqrt() {
        return sqrtRotorOrScalar();
    }

    //TODO sieht generisch aus
    // https://enki.ws/ganja.js/examples/coffeeshop.html#NSELGA
    public PgaMvExpr sqrtRotorOrScalar() {
        if (this.isEven()) {
            if (this.isScalar()) {
                return this.scalarSqrt();
            } else {
                return (this.add(CONSTANTS.one())).normalizeRotor();
            }
        }
        throw new RuntimeException("sqrt() not yet implemented for non even elements. Should be implemented in the default method of the interface with a generic version.");
    }

    // https://enki.ws/ganja.js/examples/coffeeshop.html#NSELGA
    // log of a normalized rotor, result is a bivector
    @Override
    public PgaMvExpr log() {

        if (!this.isEven()) {
            throw new IllegalArgumentException("Multivector must be an even element/general rotor!");
        }

        // 0,5,6,7,8,9,10,15 --> 0,1,2,3,4,5,6,7
        SXColVec R = getRotor(this);

        // numerical test against 1, because we have no structural fix numbers (e.g. 1)
        SXScalar[] bivectorValues = R.get(0).eq(1d, new SXScalar[]{R.get(1), R.get(2), R.get(3),
            ZERO_SXScalar, ZERO_SXScalar, ZERO_SXScalar}, logTemp(R));

        SXElem[] valuesSXElem = Arrays.stream(bivectorValues)
            .map(SXScalar::sx)
            .map(SX::scalar)
            .toArray(SXElem[]::new);

        return create(new SXColVec(this.getCayleyTable().getBladesCount(),
            valuesSXElem, CGACayleyTable.getBivectorIndizes()).sx);
    }

    private static SXScalar[] logTemp(SXColVec R) {
        SXScalar a = (new SXScalar(1d)).
            div((new SXScalar(1d)).sub(R.get(0).sq())); // inv squared length
        SXScalar b = R.get(0).acos().mul(a.sqrt()); // rotation scale
        SXScalar c = a.mul(R.get(7)).mul((new SXScalar(1d)).sub(R.get(0).mul(b)));
        return new SXScalar[]{c.mul(R.get(6)).add(b.mul(R.get(1))),
            c.mul(R.get(5)).add(b.mul(R.get(2))),
            c.mul(R.get(4)).add(b.mul(R.get(3))),
            b.mul(R.get(4)), b.mul(R.get(5), b.mul(R.get(6)))};
    }

    @Override
    public IAlgebra getIAlgebra() {
        return PgaFactory.instance.alDef;
    }

    @Override
    public PgaMvExpr createSparse() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public int getBladesCount() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void init(MultivectorExpression.Callback callback) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public MatrixSparsity getSparsity() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public CayleyTable getCayleyTable() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public int grade() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public int[] grades() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public IConstants<PgaMvExpr> constants() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public PgaMvExpr gradeSelection(int grade) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public PgaMvExpr reverse() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Uncached
    @Override
    public PgaMvExpr gpWithScalar(double s) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public PgaMvExpr undual() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public PgaMvExpr conjugate() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public PgaMvExpr up() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public PgaMvExpr down() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public PgaMvExpr negate14() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public PgaMvExpr meet(PgaMvExpr b) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public PgaMvExpr join(PgaMvExpr b) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public PgaMvExpr inorm() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public PgaMvExpr normalizeBySquaredNorm() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public PgaMvExpr generalInverse() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public PgaMvExpr scalarInverse() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public boolean isGeneralEven() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public boolean isEven() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public boolean isBivector() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public PgaMvExpr dual() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
