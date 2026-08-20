package de.orat.math.gacasadi.specific.pga;

import de.dhbw.rahmlab.casadi.SxStatic;
import de.dhbw.rahmlab.casadi.api.SXColVec;
import de.dhbw.rahmlab.casadi.api.SXScalar;
import static de.dhbw.rahmlab.casadi.api.SXScalar.ZERO_SXScalar;
import de.dhbw.rahmlab.casadi.impl.casadi.DM;
import de.dhbw.rahmlab.casadi.impl.casadi.SX;
import de.dhbw.rahmlab.casadi.impl.casadi.SXElem;
import de.dhbw.rahmlab.casadi.impl.casadi.Sparsity;
import de.dhbw.rahmlab.casadi.impl.std.StdVectorDouble;
import de.dhbw.rahmlab.casadi.impl.std.StdVectorVectorDouble;
import de.orat.math.gacalc.spi.IMultivectorExpression;
import de.orat.math.gacasadi.algebraGeneric.api.IAlgebra;
import de.orat.math.gacasadi.caching.annotation.api.GenerateCached;
import de.orat.math.gacasadi.caching.annotation.api.Uncached;
import de.orat.math.gacasadi.generic.CasADiUtil;
import de.orat.math.gacasadi.generic.GaFactory;
import de.orat.math.gacasadi.generic.GaMvExpr;
import de.orat.math.gacasadi.generic.IGetSX;
import de.orat.math.gacasadi.generic.IGetSparsityCasadi;
import de.orat.math.gacasadi.specific.pga.gen.CachedPgaMvExpr;
import de.orat.math.sparsematrix.ColumnVectorSparsity;
import de.orat.math.sparsematrix.SparseDoubleMatrix;
import java.util.Arrays;

@GenerateCached(warnFailedToCache = false, warnUncached = false)
public abstract class PgaMvExpr extends GaMvExpr<PgaMvExpr, PgaMvVariable, PgaMvValue> implements IMultivectorExpression<PgaMvExpr, PgaMvVariable, PgaMvValue>, IGetSX, IGetSparsityCasadi {

    private final static PgaFactory fac = PgaFactory.instance;

    protected static PgaFactory getFactory() {
        return fac;
    }

    @Override
    protected GaFactory<PgaMvExpr, PgaMvVariable, PgaMvValue> fac() {
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
         return new CachedPgaMvExpr(sx);
    }

    public static PgaMvVariable create(String name, int[] grades) {
        return new PgaMvVariable(name, grades);
    }
     
    
    private static final SX ZERO_SX = new SX(new Sparsity(1, 1));

    private static SXColVec getRotor(PgaMvExpr expr) {
        // 0,5,6,7,8,9,10,15 --> 0,1,2,3,4,5,6,7
        //int[] evenIndizes = CGACayleyTable.getEvenIndizes();
        int[] evenIndizes = PgaFactory.instance.getIAlgebra().getEvenIndizes();
        return new SXColVec(expr.getSX(), evenIndizes);
    }

    private static final PgaConstantsExpr CONSTANTS = PgaConstantsExpr.instance;

    public static PgaMvExpr create(SparseDoubleMatrix vector) {
        StdVectorDouble vecDouble = new StdVectorDouble(vector.nonzeros());
        SX sx = new SX(/*CgaCasADiUtil.*/CasADiUtil.toCasADiSparsity(vector.getSparsity()),
            new SX(new StdVectorVectorDouble(new StdVectorDouble[]{vecDouble})));
        return new CachedPgaMvExpr(sx);
    }
    
    public static PgaMvVariable create(String name, ColumnVectorSparsity sparsity) {
        return new PgaMvVariable(name, sparsity);
    }
    /**
     * <pre>
     * Creates a k-Vector.
     * </pre>
     *
     * @param name
     * @param grade
     */
    public static PgaMvVariable create(String name, int grade) {
        return new PgaMvVariable(name, grade);
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
        SXColVec B = new SXColVec(this.getSX(), this.getIAlgebra().getIndizes(2));

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

        SX result = new SXColVec(this.getIAlgebra().getBladesCount(),
            generalRotorValuesSXElem, this.getIAlgebra().getEvenIndizes()).sx;

        return create(result);
    }

    /**
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
        return create(new SXColVec(this.getIAlgebra().getBladesCount(), valuesSXElem, this.getIAlgebra().getEvenIndizes()).sx);
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
        SXScalar[] bivectorValues = R.get(0).eq(1d, new SXScalar[]{
            R.get(1), R.get(2), R.get(3),
            ZERO_SXScalar, ZERO_SXScalar, ZERO_SXScalar}, logTemp(R));

        SXElem[] valuesSXElem = Arrays.stream(bivectorValues)
            .map(SXScalar::sx)
            .map(SX::scalar)
            .toArray(SXElem[]::new);

        return create(new SXColVec(this.getIAlgebra().getBladesCount(),
            valuesSXElem, this.getIAlgebra().getIndizes(2)).sx);
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

    @Uncached
    @Override
    public PgaMvExpr createSparse() {
        return PgaMvVariable.createSparse("");
    }

    @Override
    public PgaMvExpr undual() {
        return dual();
        //TODO see Gunns publications, copy auch in die dsl-helper-functions
        //muss ich da das Vorzeichen noch ändern? oder ist das nur bei CGA nötig?
    }

    protected static PgaMvExpr createFromSX(SX sx) {
        return new CachedPgaMvExpr(sx);
    }
    
    public static PgaMvExpr create(DM dm) {
        //var sx = CgaCasADiUtil.toSX(dm);
        var sx = CasADiUtil.toSX(dm);
        return new CachedPgaMvExpr(sx);
    }

    /**
     * euclidean point --> projected point
     * 
     * @return 
     */
    @Override
    public PgaMvExpr up() {
        if (!isOnlyEuclidBasevector()){
            throw new IllegalArgumentException("Up projection with an argument which is no euclidian vector is not allowed: "+toString());
        }
        PgaMvExpr e0 = this.createSparse();
        e0.sx.at(this.getIAlgebra().indexOfBlade("e0")).assign(new SX(1));
        // vec + ε₀
        return add(e0);
    }

    private PgaMvExpr createBaseVector(String name){
        PgaMvExpr baseVector = this.createSparse();
        switch (name){
            case "e0":
                baseVector.sx.at(this.getIAlgebra().indexOfBlade("e0")).assign(new SX(1));
                return baseVector;
            case "e1":
                 baseVector.sx.at(this.getIAlgebra().indexOfBlade("e1")).assign(new SX(1));
                return baseVector;
            case "e2":
                 baseVector.sx.at(this.getIAlgebra().indexOfBlade("e2")).assign(new SX(1));
                return baseVector;
            case "e3":
                 baseVector.sx.at(this.getIAlgebra().indexOfBlade("e3")).assign(new SX(1));
                return baseVector;
            default:
                System.out.println("Try to create a basevector with unknown name \""+name+"\"!");
                return null;
        }
    }
    /**
     * projected (extrinsic) point (PGA) --> euclidean point
     * 
     * @return Euclidean point from down projection
     */
    @Override
    public PgaMvExpr down() {
        //PgaMvExpr e0 = this.createSparse();
        //e0.sx.at(this.getIAlgebra().indexOfBlade("e0")).assign(new SX(1));
        //return sub(e0);
        PgaMvExpr E3 = createBaseVector("e0").op(createBaseVector("e1")).op(createBaseVector("e2")).op(createBaseVector("e3"));
        return negate().divs(ip(E3).gradeSelection(0)).sub(E3).dual();
    }

    @Override
    public PgaMvExpr negate14() {
        throw new UnsupportedOperationException("Not available (cga only) and not needed."); 
    }

    // non linear function, iplementation via matrix calculations of casadi
    @Override
    public PgaMvExpr meet(PgaMvExpr b) {
        throw new UnsupportedOperationException("Not yet implemented."); 
    }
    @Override
    public PgaMvExpr join(PgaMvExpr b) {
        throw new UnsupportedOperationException("Not yet implemented."); 
    }

    @Override
    public PgaMvExpr inorm() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    /*@Override
    public PgaMvExpr normalizeBySquaredNorm() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }*/

    /**
     * TODO
     * das ist eine copy aus CgaMvExpr
     * könnte das also nicht in einer für alle gemeinsame impl class ...
     * 
     * Elementwise division with a scalar.
     *
     * @param s scalar
     * @throws IllegalArgumentException if the argument is no structural scalar
     * @return a multivector for which each component of the given multivector is divided by the given scalar
     */
    public PgaMvExpr divs(PgaMvExpr s) {
        // test allowed because it is a test against structural beeing a scalar
        // test against structural 0 not useful
        // runtime can fail if scalar == 0
        if (!s.isScalar()) {
            throw new IllegalArgumentException("The argument of divs() must be a scalar!");
        }
        SX svec = SxStatic.repmat(s.asScalarSXCell(), sx.sparsity().rows(), 1);
        return create(SxStatic.rdivide(sx, svec));
    }
    
    private static boolean checkEpsilon(double actualValue, double target) {
        final double epsilon = 1e-5d;
        return Math.abs(actualValue - target) <= epsilon;
    }
    
    private boolean isMotor(){
        // 1. belongs to even sub-algebra without including the pseudoscalar
        if (!isEven() || !gradeSelection(4).isSparseEmpty()) return false;
        // 2. satisfies the unit magnitude (versor) normalization constraint
        // this is equivalent with reversibility
        PgaMvExpr temp = gp(reverse());
        if (!temp.isScalar()
            || checkEpsilon(temp.asScalarDouble(), 1))
            return false;
        return true;
    }
    
    private boolean isLine(){
        // is bivector?
        if (!isBivector()) return false;
        // test plucker condition
        return op(this).isSparseEmpty();
    }
    
    /**
     * Matrices for 3D/4D PGA, ganja.js uses closed-form recursive adjunct/denominator expansions based on grade
     * involutions and antiautomorphisms.
     * 
     * utilizing specialized non-metric Poincare duality and custom adjugate formulas
     * 
     * this.Conjugate.Mul(this.Mul(this.Conjugate).Map(3,4)).Mul( this.constructor.Scalar(this.Mul(this.
     * Conjugate).Mul(this.Mul(this.Conjugate).Map(3,4))[0].Inverse))
     * 
     * Object.defineProperty(res.prototype, 'Conjugate', {configurable:true,get(){var res = new this.
     * constructor(); for (var i=0; i<this.length; i++) res[i]= this[i].slice().Scale([1,-1,-1,1][grades[i]%4]);
     * return res; }});
     * ['~','Conjugate',1]
     * 
     * https://enkimute.github.io/LookMaNoMatrices/
     * 
     * for normalized objects only:
     * plane --> x
     * line --> -x
     * point --> -x
     * motor --> reverse(x) (changes the sign of the bivector and trivector coefficients only.)
     * 
     * general bivector BB: Recall that a bivector BB only represents a single line if B∧B=0B∧B=0, 
     * the so called Plücker condition. If a bivector BB does not satisfy that requirement, it is no blade, 
     * i.e. not the result of meeting two planes or joining two points. For such an element the inverse is 
     * slightly more complicated.
     * 
     * @return 
     */
    @Override
    public PgaMvExpr generalInverse() {
        // The precondition "normalization" is not tested here. It should be part of the typesystem. Because
        // we have not yet implemented one, it is a precondition that the user has to fulfil. So If multiple
        // motors are sandwhiched before invoking generalInverse() the motor must be normalized first.
        // Numerical test if it is normalized instead to runtime is possible but time consuming.
        // is motor?
        if (isMotor()){
            return this.reverse();
        }
        switch (grade()) {
            // is plane?
            case 1:
                return this;
            // is bivector?
            case 2:
                // is line? 
                if (isLine()){
                    return negate();
                // is general bivector
                } else {
                    // bivectors only
                    PgaMvExpr Brev = this.reverse();
                    PgaMvExpr B2 = this.gp(Brev);
                    PgaMvExpr a = B2.filterScalar();
                    PgaMvExpr b = B2.idle(); // b e0123
                    return CONSTANTS.one().gp(a.scalarInverse()).sub((b.gp(a.square().scalarInverse()).gp(b))).gp(Brev);
                }
            // is point?
            case 3:
                return negate();
            default:
                throw new UnsupportedOperationException("Multivector of this type can not be inverted!"); 
        }
    }

    @Override
    public PgaMvExpr dual() {
        //return lc(CONSTANTS.getInversePseudoscalar());
        
        /**
         * Dual = {
            coefficient(_P(1),1)*(e0^e1^e2^e3)
            + coefficient(_P(1),e0)*(e1^e2^e3)
            + coefficient(_P(1),e1)*(e0^e3^e2)
            + coefficient(_P(1),e2)*(e0^e1^e3)
            + coefficient(_P(1),e3)*(e0^e2^e1)
            + coefficient(_P(1),e0^e1)*(e2^e3)
            + coefficient(_P(1),e0^e2)*(e3^e1)
            + coefficient(_P(1),e0^e3)*(e1^e2)
            + coefficient(_P(1),e1^e2)*(e0^e3)
            + coefficient(_P(1),e3^e1)*(e0^e2) -
            + coefficient(_P(1),e2^e3)*(e0^e1)
            + coefficient(_P(1),e0^e2^e1)*(e3) -
            + coefficient(_P(1),e0^e1^e3)*(e2)
            + coefficient(_P(1),e0^e3^e2)*(e1) -
            + coefficient(_P(1),e1^e2^e3)*(e0)
            + coefficient(_P(1),e0^e1^e2^e3)*(1)
         */
        int[] map = new int[]{15,14,13,12,11,10,9,8,7,6,5,4,3,2,1,0};
        SX result = super.createSparseSX();
        for (int i : this.nzIndices()) {
            SX resCell = sx.at(i, 0);
            if (i == 9 || i == 11 || i == 13){
                resCell = SxStatic.mtimes(new SX(-1), resCell);
            }
            result.at(map[i], 0).assign(resCell);
        }
        return create(result);
    }

    @Override
    public PgaMvVariable toVar(String name) {
        return new PgaMvVariable(name, this.getSparsityCasadi());
    }
}
