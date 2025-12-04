package de.orat.math.gacasadi;

import de.dhbw.rahmlab.casadi.SxStatic;
import de.dhbw.rahmlab.casadi.impl.casadi.DM;
import de.dhbw.rahmlab.casadi.impl.casadi.Function;
import de.dhbw.rahmlab.casadi.impl.casadi.SX;
import de.dhbw.rahmlab.casadi.impl.casadi.Sparsity;
import de.dhbw.rahmlab.casadi.impl.std.StdVectorDM;
import de.dhbw.rahmlab.casadi.impl.std.StdVectorSX;
import de.orat.math.gacasadi.impl.GaFactory;
import de.orat.math.gacasadi.impl.GaFunction;
import de.orat.math.gacasadi.impl.GaMvExpr;
import de.orat.math.gacasadi.impl.GaMvValue;
import de.orat.math.gacasadi.impl.GaMvVariable;
import de.orat.math.gacasadi.impl.IGetSX;
import de.orat.math.sparsematrix.SparseDoubleColumnVector;
import java.util.Arrays;
import java.util.List;
import util.cga.CGACayleyTableGeometricProduct;
import util.cga.CGAMultivectorSparsity;
import util.cga.SparseCGAColumnVector;

public class ExpNanTest {

    /**
     * Works only if expr are constructed from getDelegate() of inputs.
     */
    public static GaMvValue evalPoint(List<GaMvValue> inputs, GaMvExpr expr) {
        List<GaMvVariable> variables = inputs.stream().map(v -> (GaMvVariable) v.getDelegate()).toList();
        return (new GaFunction("evalPoint", variables, List.of(expr))).callValue(inputs).get(0);
    }

    private static StdVectorSX transformImpl(List<? extends IGetSX> mvs) {
        List<SX> sxs = mvs.stream().map(IGetSX::getSX).toList();
        return new StdVectorSX(sxs);
    }

    public static void main(String[] args) {
        var fac = GaFactory.instance;

        /*
        // Numerisch Expr tut immer noch:
        var pVec = SparseCGAColumnVector.createEuclid(new double[]{0d, 0.0996, 0d});
        var p = fac.createValue(pVec).toExpr();
        var translator = fac.createExpr(-0.5).gp(p).gp(fac.constantsExpr().getBaseVectorInfinity());
        var exp = translator.exp();
        System.out.println(p);
        System.out.println(translator);
        System.out.println(exp);

        // Symbolisch bei MVValue tut jetzt (davor nicht):
        var pVec2 = SparseCGAColumnVector.createEuclid(new double[]{0d, 0.0996, 0d});
        var p2 = fac.createValue(pVec2);
        var translator2 = fac.createValue(-0.5).gp(p2).gp(fac.constantsValue().getBaseVectorInfinity());
        var exp2 = translator2.exp();
        System.out.println(p2);
        System.out.println(translator2);
        System.out.println(exp2);

        // Test tut jetzt (davor nicht)
        var indizes = CGACayleyTableGeometricProduct.getIndizes(2);
        var sparsity = new CGAMultivectorSparsity(indizes);
        double[] values = {00, 00, -0.5, -0.5, 00, -1.5, -1.5, -2.5, -2.5, 00};
        var sdm = new SparseDoubleColumnVector(sparsity, values);
        var rand = fac.createValue(sdm); // Mit toEpr tut es. So aber nicht.
        var exp3 = rand.exp().toString();
        System.out.println(exp3);
         */
        //  Noch näher an der DSL.
        GaMvValue ae = fac.createValue(SparseCGAColumnVector.createEuclid(new double[]{0d, 1d, 0d}));
        GaMvExpr d6 = fac.createExpr(0.0996);
        GaMvExpr vec = d6.gp(ae.getDelegate());
        GaMvExpr expInput = fac.createExpr(-0.5).gp(vec).gp(fac.constantsExpr().getBaseVectorInfinity());
        GaMvExpr exp = expInput.exp();
        GaMvValue expNum = evalPoint(List.of(ae), exp);
        System.out.println(exp);
        System.out.println(expNum); // Richtig

        StdVectorSX def_sym_in = transformImpl(List.of(ae.getDelegate()));
        StdVectorSX def_sym_out = transformImpl(List.of(exp));
        var func = new Function("casadiExpNanTest", def_sym_in, def_sym_out);
        StdVectorDM call_num_in = new StdVectorDM(List.of(ae.getDM()));
        StdVectorDM call_num_out = new StdVectorDM();
        func.call(call_num_in, call_num_out);
        DM result = call_num_out.get(0);
        System.out.println(result); // Mit NaN

        // Wie verhält sich CasADi# hinsichtlich 0/0?
        GaMvValue t1 = fac.createValue(0);
        GaMvValue t2 = fac.createValue(0);
        SX div = SxStatic.rdivide(t1.getDelegate().getSX(), t2.getDelegate().getSX());
        GaMvExpr expr = GaMvExpr.create(div).simplifySparsify();
        GaMvValue nanEval = evalPoint(List.of(t1, t2), expr);
        // Das gibt NaN. Also liegt es wo anders.
        // Wenn ich nur mit t1 mache, gibt es 1.
        System.out.println(nanEval);
    }

    private static DM callDM(List<SX> params, List<DM> args, SX expr) {
        StdVectorSX def_sym_in = new StdVectorSX(params.toArray(SX[]::new));
        StdVectorSX def_sym_out = new StdVectorSX(new SX[]{expr});
        var func = new Function("callDM", def_sym_in, def_sym_out);
        StdVectorDM call_num_in = new StdVectorDM(args.toArray(DM[]::new));
        StdVectorDM call_num_out = new StdVectorDM();
        func.call(call_num_in, call_num_out);
        DM result = call_num_out.get(0);
        return result;
    }

    private static SX callSX(List<SX> params, List<SX> args, SX expr) {
        StdVectorSX def_sym_in = new StdVectorSX(params.toArray(SX[]::new));
        StdVectorSX def_sym_out = new StdVectorSX(new SX[]{expr});
        var func = new Function("callSX", def_sym_in, def_sym_out);
        StdVectorSX call_num_in = new StdVectorSX(args.toArray(SX[]::new));
        StdVectorSX call_num_out = new StdVectorSX();
        func.call(call_num_in, call_num_out);
        SX result = call_num_out.get(0);
        return result;
    }

    public static void main2(String[] args) {
        SX a = SxStatic.sym("a", 1, 1);
        SX b = SxStatic.sym("b", 1, 1);
        SX div = SxStatic.rdivide(a, b);
        System.out.println(div);
        DM cDM = new DM(0);
        DM outDM = callDM(List.of(a, b), List.of(cDM, cDM), div);
        System.out.println(outDM);

        //SxStatic.sym("c", 1, 1); // Variable: 1
        //new SX(new Sparsity(1, 1)); //Symbolische null gleich wie numerische: NaN
        SX cSX = SxStatic.sym("c", 1, 1);
        SX outSX = callSX(List.of(a, b), List.of(cSX, cSX), div);
        System.out.println(outSX);
    }
}
/*
[11.11.2025]
Es ist durchaus so, dass in CasADi# gilt: a/a=1 und 0/0=NaN.
Sodass es eine Rolle spielt, ob man erst symbolisch minimiert oder erst numerisch evaluiert.
Allerdings ist das Ergebnis bei DM und SX gleich, wenn da eine 0 (oder 00) drin steckt.
Das erklärt also noch nicht, warum es für die Evaluierung von exp für bestimmte Eingaben notwendig ist, in CgaCasADi::CgaFunction::callNumeric den internen Call mit numerischem SX anstatt numerischem DM zu machen, um NaN zu vermeiden.
→ GaCasADi::ExpNanTest
 */
