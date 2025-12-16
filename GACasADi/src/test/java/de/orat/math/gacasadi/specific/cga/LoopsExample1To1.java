package de.orat.math.gacasadi.specific.cga;

import de.orat.math.gacalc.api.GAServiceLoader;
import de.orat.math.gacalc.api.MultivectorExpressionArray;
import java.util.List;

public class LoopsExample1To1 {

    public static void mapaccum1To1() {
        var fac = GAServiceLoader.getGAFactoryThrowing("cga", "cgacasadisx");

        // Lokale Variablen vor dem Loop.
        var aSim = fac.createExpr(5);
        var aArr0 = fac.createExpr(7);
        var aArr1 = fac.createExpr(11);
        var aArr = new MultivectorExpressionArray(List.of(aArr0, aArr1));
        var arAcc = new MultivectorExpressionArray();
        var arAcc0 = fac.createExpr(3);
        arAcc.ensureSize(1, fac.constantsExpr().getSparseEmptyInstance());
        arAcc.set(0, arAcc0);
        var rArr = new MultivectorExpressionArray();

        // Loop: Abbildung der Variablen auf rein symbolische Parameter.
        // Nebenbedingung: Array Elemente müssen die gleiche Sparsity haben.
        var sym_arAcc = fac.createVariable("sym_arAcc", arAcc0);
        var sym_aSim = fac.createVariable("sym_aSim", aSim);
        var sym_aArr = fac.createVariable("sym_aArr", aArr0);

        // Loop: Definition der "inneren Funktion".
        var arAcc_i1 = sym_arAcc.addition(sym_aArr);
        var rArr_i = sym_arAcc.addition(sym_aSim).addition(fac.createExpr(2));

        // Loop: Erzeugung der Argumente für den Aufruf der Loop API.
        var paramsAccum = List.of(sym_arAcc);
        var paramsSimple = List.of(sym_aSim);
        var paramsArray = List.of(sym_aArr);
        var returnsAccum = List.of(arAcc_i1);
        var returnsArray = List.of(rArr_i);
        var argsAccumInitial = List.of(arAcc0);
        var argsSimple = List.of(aSim);
        var argsArray = List.of(aArr);
        var iterations = 2;

        // Loop: Aufruf der Loop API.
        var res = fac.getLoopService().mapaccum(paramsAccum, paramsSimple, paramsArray, returnsAccum, returnsArray, argsAccumInitial, argsSimple, argsArray, iterations);

        // Loop: Zuweisung der Rückgabe.
        arAcc.addAll(res.returnsAccum().get(0));
        rArr.addAll(res.returnsArray().get(0));

        // Print
        arAcc.forEach(System.out::println);
        System.out.println("---");
        rArr.forEach(System.out::println);
        System.out.println("------");
    }

    public static void main(String[] args) {
        mapaccum1To1();
    }
}
