package de.orat.math.gacasadi.specific.cga;

import de.dhbw.rahmlab.casadi.SxStatic;
import de.dhbw.rahmlab.casadi.impl.casadi.SX;
import de.dhbw.rahmlab.casadi.impl.casadi.Sparsity;

public class ShowSparsitySubset {

    public static void main(String[] args) {
        // -----------------
        System.out.println("// -----------------");
        var diagSparsity = Sparsity.diag(2, 2);
        SX a = SxStatic.sym("a", diagSparsity);
        SX b = SxStatic.sym("b", diagSparsity);
        System.out.println(a);
        System.out.println(b);
        System.out.println("// -----------------");
        // -----------------
        System.out.println("Addition of submatrices preserves structural zero:");
        var structuralZeroSum = SxStatic.plus(a.at(0, 1), b.at(0, 1));
        System.out.println(structuralZeroSum);
        System.out.println("// -----------------");
        // -----------------
        var c = SxStatic.sym("c", 2, 2);
        c.at(0, 1).assign(structuralZeroSum);
        System.out.println("Assignment of structural zero to submatrix works:");
        System.out.println(c);
        System.out.println("// -----------------");
        // -----------------
        var isSubsetac = a.sparsity().is_subset(c.sparsity());
        System.out.println("isSubsetac:");
        System.out.println(isSubsetac);
        System.out.println("// -----------------");
        var isSubsetca = c.sparsity().is_subset(a.sparsity());
        System.out.println("isSubsetca:");
        System.out.println(isSubsetca);
        System.out.println("// -----------------");
    }
}
