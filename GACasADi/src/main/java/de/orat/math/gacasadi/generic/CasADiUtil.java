package de.orat.math.gacasadi.generic;

import de.dhbw.rahmlab.casadi.SxStatic;
import de.dhbw.rahmlab.casadi.api.Util;
import static de.dhbw.rahmlab.casadi.api.Util.toIntArr;
import static de.dhbw.rahmlab.casadi.api.Util.toLongArr;
import static de.dhbw.rahmlab.casadi.api.Util.toStdVectorDouble;
import de.dhbw.rahmlab.casadi.impl.casadi.DM;
import de.dhbw.rahmlab.casadi.impl.casadi.SX;
import de.dhbw.rahmlab.casadi.impl.casadi.Sparsity;
import de.dhbw.rahmlab.casadi.impl.casadi.SxSubMatrix;
import de.dhbw.rahmlab.casadi.impl.std.StdVectorCasadiInt;
import de.dhbw.rahmlab.casadi.impl.std.StdVectorDouble;
import de.dhbw.rahmlab.casadi.impl.std.StdVectorVectorDouble;
import de.orat.math.gacasadi.algebraGeneric.api.IAlgebra;
import de.orat.math.sparsematrix.ColumnVectorSparsity;
import de.orat.math.sparsematrix.MatrixSparsity;
import de.orat.math.sparsematrix.SparseDoubleMatrix;
import de.orat.math.sparsematrix.SparseStringMatrix;
import java.util.List;

public class CasADiUtil {

    public static List<Sparsity> toSparsities(List<? extends IGetSparsityCasadi> mvs) {
        return mvs.stream().map(IGetSparsityCasadi::getSparsityCasadi).toList();
    }

    public static boolean areMVSparsitiesSupersetsOfSubsets(List<? extends IGetSparsityCasadi> supersets, List<? extends IGetSparsityCasadi> subsets) {
        var supersetsSparsities = toSparsities(supersets);
        var subsetSparsities = toSparsities(subsets);
        return areSparsitiesSupersetsOfSubsets(supersetsSparsities, subsetSparsities);
    }

    public static boolean areSparsitiesSupersetsOfSubsets(List<Sparsity> supersets, List<Sparsity> subsets) {
        final int size = supersets.size();
        if (size != subsets.size()) {
            return false;
        }
        for (int i = 0; i < size; ++i) {
            var superset = supersets.get(i);
            var subset = subsets.get(i);
            if (!subset.is_subset(superset)) {
                return false;
            }
        }
        return true;
    }

    public static SX toSX(SparseDoubleMatrix m) {
        return new SX(toCasADiSparsity(m.getSparsity()), Util.toSX(m.nonzeros()));
    }

    public static SX toSX(DM dm) {
        var nonZeros = new StdVectorVectorDouble(1, dm.nonzeros());
        var sx = new SX(dm.sparsity(), new SX(nonZeros));
        return sx;
    }

    public static MatrixSparsity toMatrixSparsity(de.dhbw.rahmlab.casadi.impl.casadi.Sparsity sxSparsity) {
        //TODO
        // kann ich identifizieren ob es ein Row- oder Column -Vektor ist und wenn ja
        // mit welchem grade?
        return new MatrixSparsity((int) sxSparsity.rows(), (int) sxSparsity.columns(),
            toIntArr(sxSparsity.get_colind()),
            toIntArr(sxSparsity.get_row()));
    }

    public static ColumnVectorSparsity toColumnVectorSparsity(de.dhbw.rahmlab.casadi.impl.casadi.Sparsity sxSparsity) {
        return new ColumnVectorSparsity((int) sxSparsity.rows(),
            toIntArr(sxSparsity.get_row()));
    }

    public static double[] nonzeros(DM dm) {
        StdVectorDouble res = dm.nonzeros();
        double[] result = new double[res.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = res.get(i);
        }
        return result;
    }

    public static SparseDoubleMatrix elements(DM dm) {
        //StdVectorDouble res = dm.get_elements();
        StdVectorDouble nonzeros = dm.nonzeros();
        double[] nonzerosArray = new double[nonzeros.size()];
        for (int i = 0; i < nonzerosArray.length; i++) {
            nonzerosArray[i] = nonzeros.get(i);
        }
        ColumnVectorSparsity sparsity = toColumnVectorSparsity(dm.sparsity());
        return new SparseDoubleMatrix(sparsity, nonzerosArray);
    }

    public static Sparsity toCasADiSparsity(de.orat.math.sparsematrix.MatrixSparsity sparsity) {
        StdVectorCasadiInt row = new StdVectorCasadiInt(toLongArr(sparsity.getrow()));
        StdVectorCasadiInt colind = new StdVectorCasadiInt(toLongArr(sparsity.getcolind()));
        Sparsity result = new Sparsity(sparsity.getn_row(), sparsity.getn_col(), colind, row);
        //result.spy();
        return result;
    }

    public static DM toDM(int n_row, double[] nonzeros, int[] rows) {
        ColumnVectorSparsity sparsity = new ColumnVectorSparsity(n_row, rows);
        return new DM(toCasADiSparsity(sparsity), toStdVectorDouble(nonzeros), false);
    }

    /**
     * @param sparsity
     * @param nonzeros only nonzeros
     * @return
     */
    public static DM toDM(ColumnVectorSparsity sparsity, double[] nonzeros) {
        StdVectorDouble nonzeroVec = new StdVectorDouble(nonzeros);
        return new DM(toCasADiSparsity(sparsity), nonzeroVec, false);
    }

    /**
     * https://github.com/casadi/casadi/wiki/L_rf Evaluates the expression numerically. An error is raised
     * when the expression contains symbols.
     */
    public static DM toDM(SX sx) {
        return SxStatic.evalf(sx);
    }
    
    // eigentlich nicht CasADi sonder SparseMatrix util
    
    public static ColumnVectorSparsity determineSparsity(int grade, IAlgebra algebra){
        int[] indizes = algebra.getIndizes(grade);
        int bladesCount = algebra.getBladesCount();
        return new ColumnVectorSparsity(bladesCount, indizes);
    }
    public static ColumnVectorSparsity determineSparsity(int[] grades, IAlgebra algebra){
        int[] indizes = algebra.getIndizes(grades);
        int bladesCount = algebra.getBladesCount();
        return new ColumnVectorSparsity(bladesCount, indizes);
    }
    
    public static SparseStringMatrix toStringMatrix(SX m) {
        String[][] stringArr = new String[(int) m.rows()][(int) m.columns()];
        for (int i = 0; i < m.rows(); i++) {
            for (int j = 0; j < m.columns(); j++) {
                SxSubMatrix cell = m.at(i, j);
                stringArr[i][j] = cell.toString();
            }
        }
        return new SparseStringMatrix(toColumnVectorSparsity(m.sparsity()), stringArr);
    }
}
