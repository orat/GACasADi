package de.orat.math.gacasadi;

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
import de.orat.math.gacasadi.impl.CgaMvExpr;
import de.orat.math.gacasadi.impl.IGetSX;
import de.orat.math.gacasadi.impl.IGetSparsityCasadi;
import de.orat.math.gacalc.util.CayleyTable;
import de.orat.math.gacalc.util.CayleyTable.Cell;
import de.orat.math.sparsematrix.ColumnVectorSparsity;
import de.orat.math.sparsematrix.DenseStringMatrix;
import de.orat.math.sparsematrix.MatrixSparsity;
import de.orat.math.sparsematrix.SparseDoubleMatrix;
import de.orat.math.sparsematrix.SparseStringMatrix;
import java.util.List;
import util.cga.CGACayleyTable;
import util.cga.CGAMultivectorSparsity;
import util.cga.DenseCGAColumnVector;

/**
 * @author Oliver Rettig (Oliver.Rettig@orat.de)
 */
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

    public static CGAMultivectorSparsity toCGAMultivectorSparsity(
        de.dhbw.rahmlab.casadi.impl.casadi.Sparsity sxSparsity) {
        return new CGAMultivectorSparsity(toIntArr(sxSparsity.get_row()));
    }

    public static SX toSX(SparseDoubleMatrix m) {
        return new SX(toCasADiSparsity(m.getSparsity()), Util.toSX(m.nonzeros()));
    }

    public static SX toSX(DM dm) {
        var nonZeros = new StdVectorVectorDouble(1, dm.nonzeros());
        var sx = new SX(dm.sparsity(), new SX(nonZeros));
        return sx;
    }

    public static SX createScalar() {
        return new SX(toCasADiSparsity(CGAMultivectorSparsity.scalar()));
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

    /**
     * Create a corresponding matrix for geometric product calculation, considering of the sparsity of the
     * input multivector.
     *
     * @param mv multivector which is converted into a matrix representation
     * @param cgaCayleyTable Cayley-table representing the specific product
     *
     */
    public static SX toSXProductMatrix(CgaMvExpr mv, CGACayleyTable cgaCayleyTable) {

        String[][] log = new String[cgaCayleyTable.getRows()][cgaCayleyTable.getCols()];

        System.out.println(mv.getName() + ": toSXproductMatrix() input multivector sparsity = "
            + mv.getSparsity().toString());
        MatrixSparsity matrixSparsity = createSparsity(cgaCayleyTable, mv);
        System.out.println(mv.getName() + ": toSXproductMatrix() product matrix sparsity = "
            + matrixSparsity.toString());
        de.dhbw.rahmlab.casadi.impl.casadi.Sparsity sp = CasADiUtil.toCasADiSparsity(matrixSparsity);

        SX result = new SX(sp);

        MatrixSparsity sparsity = mv.getSparsity();
        for (int i = 0; i < cgaCayleyTable.getRows(); i++) {
            for (int j = 0; j < cgaCayleyTable.getCols(); j++) {
                Cell cell = cgaCayleyTable.getCell(i, j);
                // Cell enthält einen basis-blade
                if (cell.bladeIndex() >= 0) {
                    if (sparsity.isNonZero(cell.bladeIndex(), 0)) {
                        // in der Zelle der Cayleytable steht direkt ein blade
                        if (cell.Value() == 1d) {
                            //TODO
                            // statt dem Ausdruck im Column-Vector könnte ich doch auch
                            // den columvector[bladeIndex] nehmen? Aber wie formuliere ich das?
                            // möglicherweise liegt hier der Unterschied zwischen SX und SX, also
                            // bei SX wird die Referenz gesetzt und bei SX wird die Expr rausgeholt
                            // und gesetzt
                            result.at(i, j).assign(mv.getSX().at(cell.bladeIndex(), 0));
                            log[i][j] = mv.getSX().at(cell.bladeIndex(), 0).toString();
                            //FIXME
                            // gelogged wird hier derzeit fälschlicherweise immer "a" statt <[<index>]
                            // -1, oder -xxxx multipliziert mit dem basis-blade
                        } else {
                            // Das ist ja eine Skalarmultiplikation
                            // Wie kann ohne dot() hier arbeiten? ein mix mit SX geht ja vermutlich nicht
                            // aber vielleicht kann hier ja ein Function Objekt erzeugt und eingefügt werden?
                            //TODO
                            // mit Funktion ist unklar, ob ich dann nicht notwendigerweise den
                            // Multivektor als SX speichern muss
                            // dann wiederum ist unklar, ob SX alle Operatoren hat im Vergleich mit SX 
                            // die ich brauche
                            // wie kann ich eine Funktion in die Cell einer Matrix hängen?
                            //TODO

                            // vorher hatte ich hier dot
                            // cell.Value enthält den Zahlenwert der in der entsprechenden
                            // Zelle der Cayleytable steht. Dieser muss multipliziert werden
                            // mit dem Wert der Zelle des korrespondierenden Multivektors. Das
                            // Zell-Objekt enthält dazu den index im Column-Vector.
                            result.at(i, j).assign(SxStatic.mtimes(new SX(cell.Value()),
                                mv.getSX().at(cell.bladeIndex(), 0)));
                            //System.out.println("to(num)["+String.valueOf(i)+"]["+String.valueOf(j)+"]="+
                            //      SxStatic.times(new SX(cell.Value()),
                            //      new SX(mv.getSX().at(cell.bladeIndex(),0)) ).toString());
                            log[i][j] = SxStatic.mtimes(new SX(cell.Value()),
                                new SX(mv.getSX().at(cell.bladeIndex(), 0))).toString();
                        }
                        // wegen sparsity 0 muss kein Wert gesetzt werden
                    } else {
                    }
                    // cell enthält eine 0 als Koeffizient
                } else {
                    //FIXME
                    // muss ich dann überhaupt einen Wert setzen?
                    // den Fall hatte ich bisher noch nicht
                    result.at(i, j).assign(new SX(0d));
                }
            }
        }

        DenseStringMatrix logMatrix = new DenseStringMatrix(log);
        System.out.println(mv.getName() + ": toSXProductMatrix() matrix = " + logMatrix);

        return result;
    }

    /**
     * Create a sparsity object for the given cayleyTable based on the sparsity of the given sparse
     * multivector.
     *
     * @param cayleyTable
     * @param mv sparse multivector
     * @return sparsity of the matrix representation of the given multivector for the given cayley table
     */
    private static MatrixSparsity createSparsity(CayleyTable cayleyTable, CgaMvExpr mv) {
        double[][] values = new double[mv.getBladesCount()][mv.getBladesCount()];
        ColumnVectorSparsity sparsity = mv.getSparsity();
        for (int i = 0; i < cayleyTable.getRows(); i++) {
            for (int j = 0; j < cayleyTable.getCols(); j++) {
                Cell cell = cayleyTable.getCell(i, j);
                // Cell enthält einen basis-blade
                if (cell.bladeIndex() >= 0) {
                    if (sparsity.isNonZero(cell.bladeIndex(), 0)) {
                        values[i][j] = 1d;
                    }
                }
            }
        }
        return new MatrixSparsity(values, true);
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

    public static DenseCGAColumnVector toDenseDoubleMatrix(DM dm, CGAMultivectorSparsity sparsity) {
        double[] nonzeros = nonzeros(dm);
        return new DenseCGAColumnVector(nonzeros, sparsity.getrow());
    }

    public static SparseStringMatrix toStringMatrix(SX m) {
        String[][] stringArr = new String[(int) m.rows()][(int) m.columns()];
        for (int i = 0; i < m.rows(); i++) {
            for (int j = 0; j < m.columns(); j++) {
                SxSubMatrix cell = m.at(i, j);
                stringArr[i][j] = cell.toString();
            }
        }
        return new SparseStringMatrix(toCGAMultivectorSparsity(m.sparsity()), stringArr);
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
}
