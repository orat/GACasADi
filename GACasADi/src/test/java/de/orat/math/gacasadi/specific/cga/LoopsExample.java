package de.orat.math.gacasadi.specific.cga;

import de.orat.math.gacalc.api.GAServiceLoader;
import de.orat.math.gacalc.api.MultivectorExpression;
import de.orat.math.gacalc.api.MultivectorExpressionArray;
import java.util.List;

public class LoopsExample {

    public static void mapaccum() {
        var fac = GAServiceLoader.getGAFactoryThrowing("cga", "cgacasadisx");

        var xi = fac.createVariableDense("xi");
        var ai = fac.createVariableDense("ai");
        var bi = fac.createVariableDense("bi");
        var h = fac.createVariableDense("h");
        var xi1 = xi.addition(xi);
        var ai1 = ai.addition(bi);
        var c = h;

        var paramsAccum = List.of(xi, ai);
        var paramsSimple = List.of(h);
        var paramsArray = List.of(bi);
        var returnsAccum = List.of(xi1, ai1);
        var returnsArray = List.<MultivectorExpression>of(c);

        var x0 = fac.createExpr(3.0);
        var a0 = fac.createExpr(5.0);
        var argb1 = fac.createExpr(7.0);
        var argb2 = fac.createExpr(11.0);
        var arga = new MultivectorExpressionArray(List.of(argb1, argb2));

        var harg = fac.createExpr(2.7);

        var argsAccumInitial = List.of(x0, a0);
        var argsSimple = List.of(harg);
        var argsArray = List.of(arga);
        int iteration = 2;

        var res = fac.getLoopService().mapaccum(paramsAccum, paramsSimple, paramsArray, returnsAccum, returnsArray, argsAccumInitial, argsSimple, argsArray, iteration);
        res.returnsAccum().forEach(o -> {
            System.out.println("..");
            o.forEach(System.out::println);
        });
        System.out.println("....");
        res.returnsArray().forEach(o -> {
            System.out.println("..");
            o.forEach(System.out::println);
        });
        System.out.println("------");
    }

    public static void fold() {
        var fac = GAServiceLoader.getGAFactoryThrowing("cga", "cgacasadisx");

        var xi = fac.createVariableDense("xi");
        var ai = fac.createVariableDense("ai");
        var bi = fac.createVariableDense("bi");
        var h = fac.createVariableDense("h");
        var xi1 = xi.addition(xi);
        var ai1 = ai.addition(bi);
        var c = h;

        var paramsAccum = List.of(xi, ai);
        var paramsSimple = List.of(h);
        var paramsArray = List.of(bi);
        var returnsAccum = List.of(xi1, ai1);
        var returnsArray = List.<MultivectorExpression>of(c);

        var x0 = fac.createExpr(3.0);
        var a0 = fac.createExpr(5.0);
        var argb1 = fac.createExpr(7.0);
        var argb2 = fac.createExpr(11.0);
        var arga = new MultivectorExpressionArray(List.of(argb1, argb2));

        var harg = fac.createExpr(2.7);

        var argsAccumInitial = List.of(x0, a0);
        var argsSimple = List.of(harg);
        var argsArray = List.of(arga);
        int iteration = 2;

        var res = fac.getLoopService().fold(paramsAccum, paramsSimple, paramsArray, returnsAccum, returnsArray, argsAccumInitial, argsSimple, argsArray, iteration);
        res.returnsAccum().forEach(System.out::println);
        res.returnsArray().forEach(o -> {
            System.out.println("..");
            o.forEach(System.out::println);
        });
        System.out.println("------");
    }

    public static void map() {
        var fac = GAServiceLoader.getGAFactoryThrowing("cga", "cgacasadisx");

        var bi = fac.createVariableDense("bi");
        var h = fac.createVariableDense("h");
        var xi = bi.addition(bi);
        var yi = bi.addition(h);

        var paramsSimple = List.of(h);
        var paramsArray = List.of(bi);
        var returnsArray = List.of(xi, yi);

        var argb1 = fac.createExpr(7.0);
        var argb2 = fac.createExpr(11.0);
        var argb = new MultivectorExpressionArray(List.of(argb1, argb2));

        var argh = fac.createExpr(2.7);

        var argsSimple = List.of(argh);
        var argsArray = List.of(argb);
        int iteration = 2;

        var res = fac.getLoopService().map(paramsSimple, paramsArray, returnsArray, argsSimple, argsArray, iteration);
        res.forEach(o -> {
            System.out.println("..");
            o.forEach(System.out::println);
        });
        System.out.println("------");
    }

    public static void main(String[] args) {
        map();
        fold();
        mapaccum();
    }
}
