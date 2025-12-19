package de.orat.math.gacasadi.generic;

import de.orat.math.gacasadi.algebraGeneric.api.IAlgebra;
import de.orat.math.sparsematrix.MatrixSparsity;
import de.orat.math.sparsematrix.SparseDoubleMatrix;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Oliver Rettig (Oliver.Rettig@orat.de)
 */
public class GAOperatorMatrixUtils {

    private final IAlgebra algebra;

    private static SparseDoubleMatrix reversionOperatorMatrix;
    private static SparseDoubleMatrix involutionOperatorMatrix;
    private static SparseDoubleMatrix conjugationOperatorMatrix;

    
    //----
    
    // negate14
    public static SparseDoubleMatrix createNegate14MultiplicationMatrix(IAlgebra algebra) {
        int size = algebra.getBladesCount();
        MatrixSparsity sparsity = MatrixSparsity.diagonal(size);
        double[] nonzeros = new double[size];
        for (int i = 0; i < size; i++) {
            nonzeros[i] = 1d;
            int grade = algebra.getGrade(i);
            if (grade == 1 || grade == 4) {
                nonzeros[i] *= -1;
            }
        }
        return new SparseDoubleMatrix(sparsity, nonzeros);
    }

    // scalar multiplication
    public static SparseDoubleMatrix createScalarMultiplicationMatrix(IAlgebra algebra, double s) {
        int size = algebra.getBladesCount();
        MatrixSparsity sparsity = MatrixSparsity.diagonal(size);
        double[] nonzeros = new double[size];
        for (int i = 0; i < size; i++) {
            nonzeros[i] = s;
        }
        return new SparseDoubleMatrix(sparsity, nonzeros);
    }

    // reversion
    public static SparseDoubleMatrix createReversionOperatorMatrix(IAlgebra algebra) {
        //int size = cayleyTable.getBladesCount();
        int bladesCount = algebra.getBladesCount();
        MatrixSparsity sparsity = MatrixSparsity.diagonal(bladesCount);
        double[] nonzeros = new double[bladesCount];
        for (int i = 0; i < bladesCount; i++) {
            int gradei = algebra.getGrade(i);
            double exp = gradei * (gradei - 1) * 0.5;
            nonzeros[i] = Math.pow(-1d, exp);
        }
        return new SparseDoubleMatrix(sparsity, nonzeros);
    }

    // clifford conjugation
    public static SparseDoubleMatrix createConjugationOperatorMatrix(IAlgebra algebra) {
        int size = algebra.getBladesCount();
        MatrixSparsity sparsity = MatrixSparsity.diagonal(size);
        double[] nonzeros = new double[size];
        for (int i = 0; i < size; i++) {
            int gradei = algebra.getGrade(i);
            double exp = gradei * (gradei + 1) * 0.5;
            nonzeros[i] = Math.pow(-1d, exp);
        }
        return new SparseDoubleMatrix(sparsity, nonzeros);
    }

    // grade involution
    public static SparseDoubleMatrix createInvolutionOperatorMatrix(IAlgebra algebra) {
        int size = algebra.getBladesCount();
        MatrixSparsity sparsity = MatrixSparsity.diagonal(size);
        double[] nonzeros = new double[size];
        for (int i = 0; i < size; i++) {
            int gradei = algebra.getGrade(i);
            nonzeros[i] = Math.pow(-1d, gradei);
        }
        return new SparseDoubleMatrix(sparsity, nonzeros);
    }

    // grade selection
    public static SparseDoubleMatrix createGradeSelectionOperatorMatrix(IAlgebra algebra, int grade) {
        //int size = cayleyTable.getBladesCount();
        int bladesCount = algebra.getBladesCount();
        double[] values = new double[bladesCount];
        for (int i = 0; i < bladesCount; i++) {
            int gradei = algebra.getGrade(i);
            if (gradei == grade) {
                values[i] = 1;
            }
        }
        MatrixSparsity sparsity = MatrixSparsity.diagonal(values);
        return new SparseDoubleMatrix(sparsity, nonzeros(values));
    }

    private static double[] nonzeros(double[] values) {
        List<Double> nonzeros = new ArrayList<>();
        for (int i = 0; i < values.length; i++) {
            if (values[i] != 0) {
                nonzeros.add(values[i]);
            }
        }
        return nonzeros.stream().mapToDouble(d -> d).toArray();
    }

    /**
     * Versuch einer Portierung aquivalenten ganja.js codes. TODO - eine matrix erzeugen - tests
     *
     * @param values
     * @param cayleyTable
     * @return
     */
   public static double[] reverse(double[] values, IAlgebra algebra/*CayleyTable cayleyTable*/) {
        double[] result = new double[values.length];
        double[] pattern = new double[]{1, 1, -1, -1};
        for (int i = 0; i < values.length; i++) {
            result[i] = values[i] * pattern[algebra.getGrade(i) % 4];
        }
        return result;
    }

    // cga dual
    /*public static SparseDoubleMatrix createDualOperatorMatrix(IAlgebra algebra) {
        //int size = cayleyTable.getBladesCount();
        int bladesCount = algebra.getBladesCount();
        MatrixSparsity sparsity = MatrixSparsity.diagonal(size);
        double[] nonzeros = new double[]{
            -1d, -1d, 1d, -1d, 1d, 1d, 1d, -1d, 1d, 1d, 1d, -1d, -1d, 1d, 1d, -1d, 1d, -1d, -1d, 1d, 1d,
            -1d, -1d, -1d, 1d, -1d, -1d, -1d, 1d, -1d, 1d, 1d};
        iDoubleMatrix result = new SparseDoubleMatrix(sparsity, nonzeros).transpose();
        //TODO in eine sparse matrix verwandeln
        // wie kann ich das unabhängig von cga formulieren für beliebige algebren?
        throw new UnsupportedOperationException("not yet implemented!");
     
    }*/

    
    
    //----
    
    public GAOperatorMatrixUtils(IAlgebra algebra) {
        this.algebra = algebra;
    }

    public SparseDoubleMatrix getScalarMultiplicationOperatorMatrix(double s) {
        // Caching is wrong here!
        // The value "s" is written directly into the matrix by the create method.
        // And thus the returned matrix depends on the actual value of "s".
        return createScalarMultiplicationMatrix(this.algebra, s);
    }

    /*public SparseDoubleMatrix getReversionOperatorMatrix() {
        if (reversionOperatorMatrix == null) {
            reversionOperatorMatrix = createReversionOperatorMatrix(cayleyTable);
        }
        return reversionOperatorMatrix;
    }

    public SparseDoubleMatrix getInvoluteOperatorMatrix() {
        if (involutionOperatorMatrix == null) {
            involutionOperatorMatrix = createInvolutionOperatorMatrix(cayleyTable);
        }
        return involutionOperatorMatrix;
    }

    public SparseDoubleMatrix getConjugationOperatorMatrix() {
        if (conjugationOperatorMatrix == null) {
            conjugationOperatorMatrix = createConjugationOperatorMatrix(cayleyTable);
        }
        return conjugationOperatorMatrix;
    }*/
}
