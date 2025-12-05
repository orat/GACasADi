package de.orat.math.gacasadi.impl;

import de.dhbw.rahmlab.casadi.SxStatic;
import de.dhbw.rahmlab.casadi.api.SXColVec;
import de.dhbw.rahmlab.casadi.api.SXScalar;
import static de.dhbw.rahmlab.casadi.api.SXScalar.ZERO_SXScalar;
import de.dhbw.rahmlab.casadi.impl.casadi.SX;
import de.dhbw.rahmlab.casadi.impl.casadi.SXElem;
import de.dhbw.rahmlab.casadi.impl.casadi.Sparsity;
import static de.orat.math.gacasadi.impl.GaMvExpr.create;
import static de.orat.math.gacasadi.impl.GaMvExpr.createSparse;
import java.util.Arrays;
import util.cga.CGACayleyTable;

/**
 * @author Oliver Rettig (Oliver.Rettig@orat.de)
 */
public class PgaNonLinearFunctions {
    
    private SX asScalar(GaMvExpr expr) {
        if (!expr.isScalar()) {
            throw new IllegalArgumentException("This is no scalar!");
        }
        return expr.getSX().at(0);
    }
    private static GaMvExpr createFromScalar(SX sx) {
        // 1x1
        if (!sx.sparsity().is_scalar()) {
            throw new IllegalArgumentException("This is no scalar!");
        }
        SX result = createSparse("").getSX();
        result.at(0).assign(sx);
        return create(result);
    }
    private GaMvExpr computeScalar(java.util.function.Function<SX, SX> computer, GaMvExpr expr) {
        SX inputScalar = asScalar(expr);
        SX outputScalar = computer.apply(inputScalar);
        GaMvExpr mv = createFromScalar(outputScalar);
        return mv;
    }
    
    private static final SX ZERO_SX = new SX(new Sparsity(1, 1));
    
    private SXColVec getRotor(GaMvExpr expr){
        // 0,5,6,7,8,9,10,15 --> 0,1,2,3,4,5,6,7
        int[] evenIndizes = CGACayleyTable.getEvenIndizes();
        return new SXColVec(expr.getSX(), evenIndizes);
    }
    
    private static final GaConstantsExpr CONSTANTS = GaConstantsExpr.instance;

    private GaConstantsExpr constants() {
        return CONSTANTS;
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
     * Input:<p>
     * B = B0e01 + B1e02 + B2e03 + B3e12 + B4e31 + B5e23<p>
     * 
     * @return Rotor = R0 + R1e01 + R2e02 + R3e03 + R4e11 + R5e31 + R6e23 + R7e0123
     */
    public GaMvExpr exp(GaMvExpr expr) {
        if (expr.isScalar()) {
            return computeScalar(SxStatic::exp, expr);
        } else if (!expr.isBivector()) {
            throw new IllegalArgumentException("exp() defined for bivectors and scalars only ("+this.toString()+")!");
        }
        
        SXScalar[] generalRotorValues;
        
        // 5,6,7,8,9,10 --> 0,1,2,3,4,5
        // coefficient 9(4) hat anderes Vorzeichen
        SXColVec B = new SXColVec(expr.getSX(), CGACayleyTable.getBivectorIndizes());
        
        // java if-else is possible because only test for structural zeros
        if (B.get(3).isZero() && B.get(4).isZero() && B.get(5).isZero())
            generalRotorValues = new SXScalar[]{new SXScalar(1),B.get(0),B.get(1),B.get(2),
            ZERO_SXScalar,ZERO_SXScalar,ZERO_SXScalar,ZERO_SXScalar};
        else {
            // B3²+B4²+B5² corresponding to e12 + e31 + e23
            SXScalar l = SXScalar.sumSq(B, new int[]{3,4,5});
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
        
        SX result = new SXColVec(expr.getCayleyTable().getBladesCount(), 
            generalRotorValuesSXElem, CGACayleyTable.getEvenIndizes()).sx;
        
        return create(result);
    }
   
    /**
     * CGA R4,1. e1*e1 = e2*e2 = e3*e3 = e4*4 = 1, e5*e5 = -1<p>
     * 
     * Normalize an even element (a general rotor R with 16 coefficients)
     * X = [1,e12,e13,e14,e15,e23,e24,e25,e34,e35,e45,e1234,e1235,e1245,e1345,e2345]<p>
     *
     * Normalization, Square Roots, and the Exponential and Logarithmic Maps in<br>
     * Geometric Algebras of Less than 6D<br>
     * S. de. Keninck, M. Roelfs, 2022
     */
    public GaMvExpr normalizeRotor(GaMvExpr expr) {
        if (!expr.isEven()) {
            throw new IllegalArgumentException("Multivector must be an even element/general rotor!");
        }
        
        SXColVec R = getRotor(expr);

        SXScalar s = (new SXScalar(1)).div(
                (R.get(0).sq().add(R.get(4).sq()).
             add(R.get(5).sq()).add(R.get(6).sq())).sqrt());
        SXScalar d = (R.get(7).mul(R.get(0)).sub(
                R.get(1).mul(R.get(6)).add(R.get(2).mul(R.get(5)))
               .sub(R.get(3).mul(R.get(4)))  )).mul(s.sq());
        
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
        return create(new SXColVec(expr.getCayleyTable().getBladesCount(), 
                valuesSXElem, CGACayleyTable.getEvenIndizes()).sx);
    }
    
    //TODO sieht generisch aus
    // https://enki.ws/ganja.js/examples/coffeeshop.html#NSELGA
    public GaMvExpr sqrtRotorOrScalar(GaMvExpr expr) {
        if (expr.isEven()) {
            if (expr.isScalar()){
                return expr.scalarSqrt();
            } else {
                return normalizeRotor(expr.add(CONSTANTS.one()));
            }
        }
        throw new RuntimeException("sqrt() not yet implemented for non even elements. Should be implemented in the default method of the interface with a generic version.");
    }

    // https://enki.ws/ganja.js/examples/coffeeshop.html#NSELGA
    // log of a normalized rotor, result is a bivector
    public GaMvExpr log(GaMvExpr expr) {
      
        if (!expr.isEven()) {
            throw new IllegalArgumentException("Multivector must be an even element/general rotor!");
        }
        
        // 0,5,6,7,8,9,10,15 --> 0,1,2,3,4,5,6,7
        SXColVec R = getRotor(expr); 
        
        // numerical test against 1, because we have no structural fix numbers (e.g. 1)
        SXScalar[] bivectorValues = R.get(0).eq(1d, new SXScalar[]{R.get(1),R.get(2), R.get(3),
                        ZERO_SXScalar,ZERO_SXScalar,ZERO_SXScalar}, logTemp(R));
        
        SXElem[] valuesSXElem = Arrays.stream(bivectorValues)
            .map(SXScalar::sx)
            .map(SX::scalar)
            .toArray(SXElem[]::new);
       
        return create(new SXColVec(expr.getCayleyTable().getBladesCount(), 
            valuesSXElem, CGACayleyTable.getBivectorIndizes()).sx);
    }
    
    private SXScalar[] logTemp(SXColVec R){
        SXScalar a = (new SXScalar(1d)).
                div((new SXScalar(1d)).sub(R.get(0).sq())); // inv squared length
        SXScalar b = R.get(0).acos().mul(a.sqrt()); // rotation scale
        SXScalar c = a.mul(R.get(7)).mul((new SXScalar(1d)).sub(R.get(0).mul(b)));
        return new SXScalar[]{c.mul(R.get(6)).add(b.mul(R.get(1))),
                              c.mul(R.get(5)).add(b.mul(R.get(2))),
                              c.mul(R.get(4)).add(b.mul(R.get(3))),
                              b.mul(R.get(4)), b.mul(R.get(5), b.mul(R.get(6)))};
    }
    
}
