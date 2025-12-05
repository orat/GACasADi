package de.orat.math.gacasadi.impl;

import de.dhbw.rahmlab.casadi.SxStatic;
import de.dhbw.rahmlab.casadi.api.SXColVec;
import de.dhbw.rahmlab.casadi.api.SXScalar;
import de.dhbw.rahmlab.casadi.impl.casadi.SX;
import de.dhbw.rahmlab.casadi.impl.casadi.SXElem;
import de.dhbw.rahmlab.casadi.impl.casadi.Sparsity;
import de.orat.math.gacasadi.specific.cga.CgaConstantsExpr;
import de.orat.math.gacasadi.specific.cga.CgaMvExpr;
import java.util.Arrays;
import util.cga.CGACayleyTable;

/**
 * @author Oliver Rettig (Oliver.Rettig@orat.de)
 */
public class CgaNonLinearFunctions {
    
    private SX asScalar(CgaMvExpr expr) {
        if (!expr.isScalar()) {
            throw new IllegalArgumentException("This is no scalar!");
        }
        return expr.getSX().at(0);
    }
    private static CgaMvExpr createFromScalar(SX sx) {
        // 1x1
        if (!sx.sparsity().is_scalar()) {
            throw new IllegalArgumentException("This is no scalar!");
        }
        SX result = CgaMvExpr.createSparse("").getSX();
        result.at(0).assign(sx);
        return CgaMvExpr.create(result);
    }
    private CgaMvExpr computeScalar(java.util.function.Function<SX, SX> computer, CgaMvExpr expr) {
        SX inputScalar = asScalar(expr);
        SX outputScalar = computer.apply(inputScalar);
        CgaMvExpr mv = createFromScalar(outputScalar);
        return mv;
    }
    
    private static final SX ZERO_SX = new SX(new Sparsity(1, 1));
    
    private SXColVec getRotor(CgaMvExpr expr) {
        // 0,5,6,7,8,9,10,15 --> 0,1,2,3,4,5,6,7
        int[] evenIndizes = CGACayleyTable.getEvenIndizes();
        return new SXColVec(expr.getSX(), evenIndizes);
    }
    
    private static final CgaConstantsExpr CONSTANTS = CgaConstantsExpr.instance;
    
     
    // non linear operators/functions
    // [8] M Roelfs and S De Keninck. 2021.
    // Graded Symmetry Groups: Plane and Simple. arXiv:2107.03771 [math-ph]
    // https://arxiv.org/pdf/2107.03771
    // https://enki.ws/ganja.js/examples/coffeeshop.html#NSELGA
    // exponential of a bivector or a scalar for CGA (R41)
    public CgaMvExpr exp(CgaMvExpr expr) {
        if (expr.isScalar()) {
            return computeScalar(SxStatic::exp, expr);
        } else if (!expr.isBivector()) {
            throw new IllegalArgumentException("exp() defined for bivectors and scalars only ("+this.toString()+")!");
        }
        
        SXColVec B = new SXColVec(expr.getSX(), CGACayleyTable.getBivectorIndizes());

        // var S = -B[0]*B[0]-B[1]*B[1]-B[2]*B[2]+B[3]*B[3]-B[4]*B[4]-B[5]*B[5]+B[6]*B[6]-B[7]*B[7]+B[8]*B[8]+B[9]*B[9];
        SXScalar S = SXScalar.sumSq(B, new int[]{3,6,8,9}).sub(SXScalar.sumSq(B, new int[]{0,1,2,4,5,7}));

        // 2*(B[4]*B[9]-B[5]*B[8]+B[6]*B[7]), //e2345
        SXScalar T1 = SXScalar.sumProd(B, new int[]{4,6}, new int[]{9,7}).
            sub(B.get(5).mul(B.get(8))).muls(2d);
        // 2*(B[1]*B[9]-B[2]*B[8]+B[3]*B[7]), //e1345
        //SXScalar T2 = SXScalar.sumProd(B, new int[]{1,3}, new int[]{9,7}).
        SXScalar T2 = B.get(1).mul(B.get(9)).add(B.get(3).mul(B.get(7))).
            sub(B.get(2).mul(B.get(8))).muls(2d);
        // 2*(B[0]*B[9]-B[2]*B[6]+B[3]*B[5]), //e1245
        SXScalar T3 = SXScalar.sumProd(B, new int[]{0,3}, new int[]{9,5}).
            sub(B.get(2).mul(B.get(6))).muls(2d);
        // 2*(B[0]*B[8]-B[1]*B[6]+B[3]*B[4]), //e1235
        SXScalar T4 = SXScalar.sumProd(B, new int[]{0,3}, new int[]{8,4}).
            sub(B.get(1).mul(B.get(6))).muls(2d);
        // 2*(B[0]*B[7]-B[1]*B[5]+B[2]*B[4])  //e1234
        SXScalar T5 = SXScalar.sumProd(B, new int[]{0,2}, new int[]{7,4}). 
            sub(B.get(1).mul(B.get(5))).muls(2d);
        
        // Calculate the norms of the invariants
        // var Tsq = -T1*T1-T2*T2-T3*T3-T4*T4+T5*T5;
        SXScalar Tsq = T1.sq().negate().sub(T2.sq()).sub(T3.sq()).sub(T4.sq()).add(T5.sq());
        // var norm = sqrt(S*S - Tsq), sc = -0.5/norm, lambdap = 0.5*S+0.5*norm;
        SXScalar norm = S.sq().sub(Tsq).sqrt();
        SXScalar sc = (new SXScalar(-0.5)).div(norm);
        SXScalar lambdap = (new SXScalar(0.5)).mul(S).add(new SXScalar(0.5).mul(norm));
        // var [lp, lm] = [sqrt(abs(lambdap)), sqrt(-0.5*S+0.5*norm)]
        SXScalar lp = lambdap.abs().sqrt();
        SXScalar lm = (new SXScalar(-0.5)).mul(S).add((new SXScalar(0.5).mul(norm))).sqrt();
        // The associated trig (depending on sign lambdap)
        // var [cp, sp] = lambdap>0?[cosh(lp), sinh(lp)/lp]:lambdap<0?[cos(lp), sin(lp)/lp]:[1,1]
        SXScalar[] temp = lambdap.lt(0d,
            new SXScalar[]{lp.cos(), lp.sin().div(lp)}, 
            new SXScalar[]{new SXScalar(1d), new SXScalar(1d)});
        SXScalar[] cp_sp = lambdap.gt(0d, new SXScalar[]{lp.cosh(), lp.sinh().div(lp)}, temp);
        SXScalar cp = cp_sp[0];
        SXScalar sp = cp_sp[1];
        // var [cm, sm] = [cos(lm), lm==0?1:sin(lm)/lm]
        SXScalar cm = lm.cos();
        SXScalar sm = lm.eq(0d, new SXScalar(1d), lm.sin().div(lm));
        // Calculate the mixing factors alpha and beta_i.
        //var [cmsp, cpsm, spsm] = [cm*sp,cp*sm,sp*sm/2], D = cmsp-cpsm, E = sc*D;
        SXScalar cmsp = cm.mul(sp);
        SXScalar cpsm = cp.mul(sm);
        SXScalar spsm = sp.mul(sm).div(2d);
        SXScalar D = cmsp.sub(cpsm);
        SXScalar E = sc.mul(D);
            
        // var [alpha,beta1,beta2,beta3,beta4,beta5] = [ D*(0.5-sc*S) + cpsm, E*T1, -E*T2, E*T3, -E*T4, -E*T5 ]
        SXScalar alpha = D.mul(new SXScalar(0.5).sub(sc.mul(S))).add(cpsm);
        SXScalar beta1 = E.mul(T1);
        SXScalar beta2 = E.mul(T2).negate();
        SXScalar beta3 = E.mul(T3);
        SXScalar beta4 = E.mul(T4).negate();
        SXScalar beta5 = E.mul(T5).negate();
        
        // create SX with sparsity corresponding to a rotor (even element)
        SXScalar[] generalRotorValues = new SXScalar[]{
            //cp*cm,
            cp.mul(cm),            
            //(B[0]*alpha+B[7]*beta5-B[8]*beta4+B[9]*beta3),
            B.get(0).mul(alpha).add(B.get(7).mul(beta5)).sub(B.get(8).mul(beta4)).
            add(B.get(9).mul(beta3)),
            //(B[1]*alpha-B[5]*beta5+B[6]*beta4-B[9]*beta2),
            B.get(1).mul(alpha).sub(B.get(5).mul(beta5)).add(B.get(6).mul(beta4)).
            sub(B.get(9).mul(beta2)),
            //(B[2]*alpha+B[4]*beta5-B[6]*beta3+B[8]*beta2),
            B.get(2).mul(alpha).add(B.get(4).mul(beta5)).sub(B.get(6).mul(beta3)).
            add(B.get(8).mul(beta2)),
            //(B[3]*alpha+B[4]*beta4-B[5]*beta3+B[7]*beta2),
            B.get(3).mul(alpha).add(B.get(4).mul(beta4)).sub(B.get(5).mul(beta3)).
            add(B.get(7).mul(beta2)),
            //(B[4]*alpha+B[2]*beta5-B[3]*beta4+B[9]*beta1),
            B.get(4).mul(alpha).add(B.get(2).mul(beta5)).sub(B.get(3).mul(beta4)).
            add(B.get(9).mul(beta1)),
            //(B[5]*alpha-B[1]*beta5+B[3]*beta3-B[8]*beta1),
            B.get(5).mul(alpha).sub(B.get(1).mul(beta5)).add(B.get(3).mul(beta3)). 
            sub(B.get(8).mul(beta1)),
            //(B[6]*alpha-B[1]*beta4+B[2]*beta3-B[7]*beta1),
            B.get(6).mul(alpha).sub(B.get(1).mul(beta4)).add(B.get(2).mul(beta3)). 
            sub(B.get(7).mul(beta1)),
            //(B[7]*alpha+B[0]*beta5-B[3]*beta2+B[6]*beta1),
            B.get(7).mul(alpha).add(B.get(0).mul(beta5)).sub(B.get(3).mul(beta2)). 
            add(B.get(6).mul(beta1)),
            //(B[8]*alpha+B[0]*beta4-B[2]*beta2+B[5]*beta1),
            B.get(8).mul(alpha).add(B.get(0).mul(beta4)).sub(B.get(2).mul(beta2)).  
            add(B.get(5).mul(beta1)),
            //(B[9]*alpha-B[0]*beta3+B[1]*beta2-B[4]*beta1),
            B.get(9).mul(alpha).sub(B.get(0).mul(beta3)).add(B.get(1).mul(beta2)).  
            sub(B.get(4).mul(beta1)),
            //spsm*T5, spsm*T4, spsm*T3, spsm*T2, spsm*T1
            spsm.mul(T5), spsm.mul(T4), spsm.mul(T3),
            spsm.mul(T2), spsm.mul(T1)
        };

        SXElem[] generalRotorValuesSXElem = Arrays.stream(generalRotorValues)
            .map(SXScalar::sx)
            .map(SX::scalar)
            .toArray(SXElem[]::new);
        
        //TODO
        // abhängig von den Argumenten kann result mehr spasity haben. Das muss noch bestimmt werden
        // um result entsprechend gesetzt werden.
        // Warum das CasADI allein nicht korrekt macht ist unklar, eventuell immer noch Fehler in der Forml, oder zum komplex um strukturelle Nullen zu finden
        // wenn nur euclidean input, dann darf der output keine grade-4-Element enthalten.
        //TODO
        // alle in der exp()-Impl verwendeten casdi-Funktionen überprüfen, ob diese auch wirklich
        // die richtige sparsity zurückliefern. Hier könnte die Ursache des Problems liegen, z.B. 
        // insbesondere die trigometrischen Funktionen
        SX result = new SXColVec(expr.getCayleyTable().getBladesCount(), 
            generalRotorValuesSXElem, CGACayleyTable.getEvenIndizes()).sx;
        
        //WORKAROUND
        // bei euclidian only input arguements I got 0-vales in grade-4 elements instead of 
        // structurell 00-elements
        /*if (){
            result.erase(new StdVectorCasadiInt(Util.toLongArr(CGACayleyTable.get4VectorIndizes())));
        }*/
        return CgaMvExpr.create(result);
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
    public CgaMvExpr normalizeRotor(CgaMvExpr expr) {
        if (!expr.isEven()) {
            throw new IllegalArgumentException("Multivector must be an even element/general rotor!");
        }
        
        int[] evenIndizes = CGACayleyTable.getEvenIndizes();
        SXColVec R = new SXColVec(expr.getSX(), evenIndizes);

        // var S = R[0]*R[0]-R[10]*R[10]+R[11]*R[11]-R[12]*R[12]-R[13]*R[13]-R[14]*R[14]-R[15]*R[15]+R[1]*R[1]
        // +R[2]*R[2]+R[3]*R[3]-R[4]*R[4]+R[5]*R[5]+R[6]*R[6]-R[7]*R[7]+R[8]*R[8]-R[9]*R[9];
        SXScalar S = SXScalar.sumSq(R, new int[]{0,11,1,2,3,5,6,8}).
            sub(SXScalar.sumSq(R, new int[]{10,12,13,14,15,4,7,9}));

        // var T1 = 2*(R[0]*R[11]-R[10]*R[12]+R[13]*R[9]-R[14]*R[7]+R[15]*R[4]-R[1]*R[8]+R[2]*R[6]-R[3]*R[5]);
        SXScalar T1 = SXScalar.sumProd(R, new int[]{0,13,15,2}, new int[]{11,9,4,6}).
            sub(SXScalar.sumProd(R, new int[]{10,14,1,3}, new int[]{12,7,8,5})).muls(2d);

        // var T2 = 2*(R[0]*R[12]-R[10]*R[11]+R[13]*R[8]-R[14]*R[6]+R[15]*R[3]-R[1]*R[9]+R[2]*R[7]-R[4]*R[5]);
        SXScalar T2 = SXScalar.sumProd(R, new int[]{0,13,15,2}, new int[]{12,8,3,7}).
            sub(SXScalar.sumProd(R, new int[]{10,14,1,4}, new int[]{11,6,9,5})).muls(2d);

        //var T3 = 2*(R[0]*R[13]-R[10]*R[1]+R[11]*R[9]-R[12]*R[8]+R[14]*R[5]-R[15]*R[2]+R[3]*R[7]-R[4]*R[6]);
        SXScalar T3 = SXScalar.sumProd(R, new int[]{0,11,14,3}, new int[]{13,9,5,7}).
            sub(SXScalar.sumProd(R, new int[]{10,12,15,4}, new int[]{1,8,2,6})).muls(2d);

        //var T4 = 2*(R[0]*R[14]-R[10]*R[2]-R[11]*R[7]+R[12]*R[6]-R[13]*R[5]+R[15]*R[1]+R[3]*R[9]-R[4]*R[8]);
        SXScalar T4 = SXScalar.sumProd(R, new int[]{0,12,15,3}, new int[]{14,6,1,9}).
            sub(SXScalar.sumProd(R, new int[]{10,11,13,4}, new int[]{2,7,5,8})).muls(2d);

        //var T5 = 2*(R[0]*R[15]-R[10]*R[5]+R[11]*R[4]-R[12]*R[3]+R[13]*R[2]-R[14]*R[1]+R[6]*R[9]-R[7]*R[8]);
        SXScalar T5 = SXScalar.sumProd(R, new int[]{0,11,13,6}, new int[]{15,4,2,9}).
            sub(SXScalar.sumProd(R, new int[]{10,12,14,7}, new int[]{5,3,1,8})).muls(2d);

        //var TT = -T1*T1+T2*T2+T3*T3+T4*T4+T5*T5;
        SXScalar TT = T1.sq().negate().add(T2.sq()).add(T3.sq()).add(T4.sq()).add(T5.sq());

        //var N = ((S*S+TT)**0.5+S)**0.5, N2 = N*N;
        SXScalar N = S.sq().add(TT).pow(0.5).add(S).pow(0.5);
        SXScalar N2 = N.sq();

        //var M = 2**0.5*N/(N2*N2+TT);
        SXScalar M = new SXScalar(Math.pow(2d, 0.5)).mul(N).div(N2.sq().add(TT));
        //var A = N2*M, [B1,B2,B3,B4,B5] = [-T1*M,-T2*M,-T3*M,-T4*M,-T5*M];
        SXScalar A = N2.mul(M);
        //TODO
        // neue Methode mit function als argument um negate().mul(M) übergeben zu können
        // damit die nachfolgenden Zeilen in eine zusammengezogen werden können
        SXScalar B1 = T1.negate().mul(M);
        SXScalar B2 = T2.negate().mul(M);
        SXScalar B3 = T3.negate().mul(M);
        SXScalar B4 = T4.negate().mul(M);
        SXScalar B5 = T5.negate().mul(M);
         
        
        SXScalar[] values = new SXScalar[]{
            // A*R[0] + B1*R[11] - B2*R[12] - B3*R[13] - B4*R[14] - B5*R[15],
            A.mul(R.get(0)).add(B1.mul(R.get(11))).sub(B2.mul(R.get(12))).
            sub(B3.mul(R.get(13))).sub(B4.mul(R.get(14))).sub(B5.mul(R.get(15))),
            // A*R[1] - B1*R[8] + B2*R[9] + B3*R[10] - B4*R[15] + B5*R[14],
            A.mul(R.get(1)).sub(B1.mul(R.get(8))).add(B2.mul(R.get(9))).
            add(B3.mul(R.get(10))).sub(B4.mul(R.get(15))).add(B5.mul(R.get(14))),
            // A*R[2] + B1*R[6] - B2*R[7] + B3*R[15] + B4*R[10] - B5*R[13],
            A.mul(R.get(2)).add(B1.mul(R.get(6))).sub(B2.mul(R.get(7))).
            add(B3.mul(R.get(15))).add(B4.mul(R.get(10))).sub(B5.mul(R.get(13))),
            //A*R[3] - B1*R[5] - B2*R[15] - B3*R[7] - B4*R[9] + B5*R[12],
            A.mul(R.get(3)).sub(B1.mul(R.get(5))).sub(B2.mul(R.get(15))).
            sub(B3.mul(R.get(7))).sub(B4.mul(R.get(9))).add(B5.mul(R.get(12))),
            //A*R[4] - B1*R[15] - B2*R[5] - B3*R[6] - B4*R[8] + B5*R[11],
SXScalar.sumProd(new SXScalar[]{A,B5}, R, new int[]{4,11}).
            sub(SXScalar.sumProd(new SXScalar[]{B1, B2, B3, B4}, R, new int[]{15, 5, 6, 8})),
            //A*R[5] - B1*R[3] + B2*R[4] - B3*R[14] + B4*R[13] + B5*R[10],
SXScalar.sumProd(new SXScalar[]{A,B2,B4,B5}, R, new int[]{5,4,13,10}).
            sub(SXScalar.sumProd(new SXScalar[]{B1, B3}, R, new int[]{3, 14})),
            //A*R[6] + B1*R[2] + B2*R[14] + B3*R[4] - B4*R[12] - B5*R[9],
SXScalar.sumProd(new SXScalar[]{A,B1,B2,B3}, R, new int[]{6,2,14,4}).
            sub(SXScalar.sumProd(new SXScalar[]{B4, B5}, R, new int[]{12, 9})),
            //A*R[7] + B1*R[14] + B2*R[2] + B3*R[3] - B4*R[11] - B5*R[8],
SXScalar.sumProd(new SXScalar[]{A,B1,B2,B3}, R, new int[]{7,14,2,3}).
            sub(SXScalar.sumProd(new SXScalar[]{B4, B5}, R, new int[]{11, 8})),
            //A*R[8] - B1*R[1] - B2*R[13] + B3*R[12] + B4*R[4] + B5*R[7],
SXScalar.sumProd(new SXScalar[]{A,B3,B4,B5}, R, new int[]{8,12,4,7}).
            sub(SXScalar.sumProd(new SXScalar[]{B1, B2}, R, new int[]{1, 13})),
            //A*R[9] - B1*R[13] - B2*R[1] + B3*R[11] + B4*R[3] + B5*R[6],
SXScalar.sumProd(new SXScalar[]{A,B3,B4,B5}, R, new int[]{9,11,3,6}).
            sub(SXScalar.sumProd(new SXScalar[]{B1, B2}, R, new int[]{13, 1})),
            //A*R[10] + B1*R[12] - B2*R[11] - B3*R[1] - B4*R[2] - B5*R[5],
SXScalar.sumProd(new SXScalar[]{A,B1}, R, new int[]{10,12}).
            sub(SXScalar.sumProd(new SXScalar[]{B2, B3, B4, B5}, R, new int[]{11, 1, 2, 5})),
            //A*R[11] + B1*R[0] + B2*R[10] - B3*R[9] + B4*R[7] - B5*R[4],
SXScalar.sumProd(new SXScalar[]{A,B1,B2,B4}, R, new int[]{11,0,10,7}).
            sub(SXScalar.sumProd(new SXScalar[]{B3, B5}, R, new int[]{9, 4})),
            //A*R[12] + B1*R[10] + B2*R[0] - B3*R[8] + B4*R[6] - B5*R[3],
SXScalar.sumProd(new SXScalar[]{A,B1,B2,B4}, R, new int[]{12,10,0,6}).
            sub(SXScalar.sumProd(new SXScalar[]{B3, B5}, R, new int[]{8, 3})),
            //A*R[13] - B1*R[9] + B2*R[8] + B3*R[0] - B4*R[5] + B5*R[2],
SXScalar.sumProd(new SXScalar[]{A,B2,B3,B5}, R, new int[]{13,8,0,2}).
            sub(SXScalar.sumProd(new SXScalar[]{B1, B4}, R, new int[]{9, 5})),
            //A*R[14] + B1*R[7] - B2*R[6] + B3*R[5] + B4*R[0] - B5*R[1],
SXScalar.sumProd(new SXScalar[]{A,B1,B3,B4}, R, new int[]{14,7,5,0}).
            sub(SXScalar.sumProd(new SXScalar[]{B2, B5}, R, new int[]{6, 1})),
            //A*R[15] - B1*R[4] + B2*R[3] - B3*R[2] + B4*R[1] + B5*R[0]
SXScalar.sumProd(new SXScalar[]{A,B2,B4,B5}, R, new int[]{15,3,1,0}).
            sub(SXScalar.sumProd(new SXScalar[]{B1, B3}, R, new int[]{4, 2}))
        };

        SXElem[] valuesSXElem = Arrays.stream(values)
            .map(SXScalar::sx)
            .map(SX::scalar)
            .toArray(SXElem[]::new);
        
        // create SX with sparsity corresponding to a rotor (even element)
        return CgaMvExpr.create(new SXColVec(expr.getCayleyTable().getBladesCount(), valuesSXElem, evenIndizes).sx);
    }

    //TODO sieht generisch aus
    // https://enki.ws/ganja.js/examples/coffeeshop.html#NSELGA
    public CgaMvExpr sqrt(CgaMvExpr expr) {
        if (expr.isEven()) {
            if (expr.isScalar()){
                return expr.scalarSqrt();
            } else {
                return normalizeRotor(expr.add(CONSTANTS.one()));
                //return add(CONSTANTS.one()).normalizeRotor();
            }
        }
        throw new RuntimeException("sqrt() not yet implemented for non even elements. Should be implemented in the default method of the interface with a generic version.");
    }

    // https://enki.ws/ganja.js/examples/coffeeshop.html#NSELGA
    // log of a normalized rotor, result is a bivector
    public CgaMvExpr log(CgaMvExpr expr) {
      
        if (!expr.isEven()) {
            throw new IllegalArgumentException("Multivector must be an even element/general rotor!");
        }
        
        SXColVec R = new SXColVec(expr.getSX(), CGACayleyTable.getEvenIndizes());
        
        SXScalar S = R.get(0).sq().add(R.get(11).sq()).sub(R.get(12).sq()). 
                     sub(R.get(13).sq()).sub(R.get(14).sq()).sub(R.get(15).sq()).sub(new SXScalar(1d));
  
        SXScalar T1 = (new SXScalar(2d)).mul(R.get(0)).mul(R.get(15));   //e2345
        SXScalar T2 = R.get(0).mul(R.get(14)).muls(2d);   //e1345
        SXScalar T3 = R.get(0).mul(R.get(13)).muls(2d);   //e1245
        SXScalar T4 = R.get(0).mul(R.get(12)).muls(2d);   //e1235
        SXScalar T5 = R.get(0).mul(R.get(11)).muls(2d);    //e1234
  
        //-T1*T1-T2*T2-T3*T3-T4*T4+T5*T5;
        SXScalar Tsq      = T1.sq().negate().sub(T2.sq()).sub(T3.sq()).sub(T4.sq()).add(T5.sq()); 
        SXScalar norm     = S.sq().sub(Tsq).sqrt(); //Math.sqrt(S*S - Tsq);
        //if (norm==0 && S==0)   // at most a single translation
        //    return bivector(new double[]{R[1], R[2], R[3], R[4], R[5], R[6], R[7], R[8], R[9], R[10]});
        SXScalar[] Bif = new SXScalar[]{R.get(1), R.get(2), R.get(3), R.get(4), 
            R.get(5), R.get(6), R.get(7), R.get(8), R.get(9), R.get(10)};
       
        SXScalar lambdap  = new SXScalar(0.5d).mul(S).add(new SXScalar(0.5).mul(norm));
        // lm is always a rotation, lp can be boost, translation, rotation
        //double lp = Math.sqrt(Math.abs(lambdap));
        SXScalar lp = lambdap.abs().sqrt();
        
        //double lm = Math.sqrt(-0.5*S+0.5*norm);
        SXScalar lm = (new SXScalar(-0.5d)).mul(S).add((new SXScalar(0.5)).mul(norm)).sqrt();
        
        //double theta2   = lm==0?0:atan2(lm, R[0]); 
        //double theta2 = 0d;
        //if (lm != 0d) theta2 = Math.atan2(lm, R[0]);
        //SXScalar theta2 = lm.eq(0d, new SXScalar(0d),SXScalar.atan2(lm, R.get(0)));
        SXScalar theta2 = lm.ne(0d, SXScalar.atan2(lm, R.get(0)));
        
        // var theta1   = lambdap<0?asin(lp/cos(theta2)):lambdap>0?atanh(lp/R[0]):lp/R[0];
        //double theta1;
        //if (lambdap < 0){
        //    theta1 = Math.asin(lp/Math.cos(theta2));
        //} else if (lambdap > 0){
        //    theta1 = Trigometry.atanh(lp/R[0]);
        //} else {
        //    theta1 = lp/R[0];
        //}
        SXScalar temp = lambdap.gt(0d, lp.div(R.get(0)).atanh(), lp.div(R.get(0)));
        SXScalar theta1 = lambdap.lt(0d, lp.div(theta2.cos()).asin(), temp);
        // var [l1, l2] = [lp==0?0:theta1/lp, lm==0?0:theta2/lm]
        //double l1=0d;
        //if (lp != 0){
        //    l1 = theta1/lp;
        //}
        //double l2=0d;
        //if (lm != 0){
        //    l2 = theta2/lm;
        //}
        //SXScalar l1 = lp.eq(0d, new SXScalar(0d), theta1.div(lp));
        SXScalar l1 = lp.ne(0d, theta1.div(lp));
        //SXScalar l2 = lm.eq(0d, new SXScalar(0d), theta2.div(lm));
        SXScalar l2 = lm.ne(0d, theta2.div(lm));
        
        //var [A, B1, B2, B3, B4, B5]   = [
        //  (l1-l2)*0.5*(1+S/norm) + l2,  -0.5*T1*(l1-l2)/norm, -0.5*T2*(l1-l2)/norm, 
        //  -0.5*T3*(l1-l2)/norm,         -0.5*T4*(l1-l2)/norm, -0.5*T5*(l1-l2)/norm, 
        //];

        // (l1-l2)*0.5*(1+S/norm) + l2;
        SXScalar A = l1.sub(l2).muls(0.5d).mul((new SXScalar(1d)).add(S.div(norm))).add(l2);
        // -0.5*T1*(l1-l2)/norm;
        SXScalar B1 = new SXScalar(-0.5).mul(T1).mul(l1.sub(l2)).div(norm);
        // -0.5*T2*(l1-l2)/norm;
        SXScalar B2 = new SXScalar(-0.5).mul(T2).mul(l1.sub(l2)).div(norm);
        // -0.5*T3*(l1-l2)/norm;
        SXScalar B3 = new SXScalar(-0.5).mul(T3).mul(l1.sub(l2)).div(norm);
        // -0.5*T4*(l1-l2)/norm;
        SXScalar B4 = new SXScalar(-0.5).mul(T4).mul(l1.sub(l2)).div(norm);
        // -0.5*T5*(l1-l2)/norm;
        SXScalar B5 = new SXScalar(-0.5).mul(T5).mul(l1.sub(l2)).div(norm);
        
        SXScalar[] Belse = new SXScalar[]{
            //(A*R[1]+B3*R[10]+B4*R[9]-B5*R[8]),
            A.mul(R.get(1)).add(B3.mul(R.get(10))).add(B4.mul(R.get(9))).sub(B5.mul(R.get(8))),
            
            //(A*R[2]+B2*R[10]-B4*R[7]+B5*R[6]),
            A.mul(R.get(2)).add(B2.mul(R.get(10))).sub(B4.mul(R.get(7))).add(B5.mul(R.get(6))),
            //(A*R[3]-B2*R[9]-B3*R[7]-B5*R[5]),
            A.mul(R.get(3)).sub(B2.mul(R.get(9))).sub(B3.mul(R.get(7))).sub(B5.mul(R.get(5))),
            
            //(A*R[4]-B2*R[8]-B3*R[6]-B4*R[5]),
            A.mul(R.get(4)).sub(B2.mul(R.get(8))).sub(B3.mul(R.get(6))).sub(B4.mul(R.get(5))),
            //(A*R[5]+B1*R[10]+B4*R[4]-B5*R[3]),
            A.mul(R.get(5)).add(B1.mul(R.get(10))).add(B4.mul(R.get(4))).sub(B5.mul(R.get(3))),
            //(A*R[6]-B1*R[9]+B3*R[4]+B5*R[2]),
            A.mul(R.get(6)).sub(B1.mul(R.get(9))).add(B3.mul(R.get(4))).add(B5.mul(R.get(2))),
            //(A*R[7]-B1*R[8]+B3*R[3]+B4*R[2]),
            A.mul(R.get(7)).sub(B1.mul(R.get(8))).add(B3.mul(R.get(3))).add(B4.mul(R.get(2))),
            //(A*R[8]+B1*R[7]+B2*R[4]-B5*R[1]),
            A.mul(R.get(8)).add(B1.mul(R.get(7))).add(B2.mul(R.get(4))).sub(B5.mul(R.get(1))),
            //(A*R[9]+B1*R[6]+B2*R[3]-B4*R[1]),
            A.mul(R.get(9)).add(B1.mul(R.get(6))).add(B2.mul(R.get(3))).sub(B4.mul(R.get(1))),
            //(A*R[10]-B1*R[5]-B2*R[2]-B3*R[1])
            A.mul(R.get(10)).sub(B1.mul(R.get(5))).sub(B2.mul(R.get(2))).sub(B3.mul(R.get(1)))
        };
        SXScalar[] B = SXScalar.eq(norm, new SXScalar(0d), S, new SXScalar(0d), Bif, Belse);
         
        // neu
        SXElem[] valuesSXElem = Arrays.stream(B)
            .map(SXScalar::sx)
            .map(SX::scalar)
            .toArray(SXElem[]::new);
        //SXElem[] values = conv(B);
        
        return CgaMvExpr.create(new SXColVec(expr.getCayleyTable().getBladesCount(),
            valuesSXElem, CGACayleyTable.getBivectorIndizes()).sx);
    }
    
    // geht auch ohne
    /*private SXElem[] conv(SXScalar[] values){
        SXElem[] result = new SXElem[values.length];
        for (int i=0;i<values.length;i++){
            result[i] = values[i].sx().scalar();
        }
        return result;
    }*/
    
}
