package de.orat.math.gacasadi.specific.cga;

import static de.dhbw.rahmlab.casadi.api.Util.toIntArr;
import de.dhbw.rahmlab.casadi.impl.casadi.DM;
import de.dhbw.rahmlab.casadi.impl.casadi.SX;
import de.orat.math.gacasadi.generic.CasADiUtil;
import static de.orat.math.gacasadi.generic.CasADiUtil.nonzeros;
import static de.orat.math.gacasadi.generic.CasADiUtil.toCasADiSparsity;
import util.cga.CGAMultivectorSparsity;
import util.cga.DenseCGAColumnVector;

/**
 * @author Oliver Rettig (Oliver.Rettig@orat.de)
 */
public class CgaCasADiUtil extends CasADiUtil {

    public static CGAMultivectorSparsity toCGAMultivectorSparsity(
        de.dhbw.rahmlab.casadi.impl.casadi.Sparsity sxSparsity) {
        return new CGAMultivectorSparsity(toIntArr(sxSparsity.get_row()));
    }

    public static SX createScalar() {
        return new SX(toCasADiSparsity(CGAMultivectorSparsity.scalar()));
    }

    /**
     * Create a corresponding matrix for geometric product calculation, considering of the sparsity of the
     * input multivector.
     *
     * @param mv multivector which is converted into a matrix representation
     * @param cgaCayleyTable Cayley-table representing the specific product
     *
     */
    /*
    public static SX toSXProductMatrix(CgaMvExpr mv, CGACayleyTable cgaCayleyTable) {

        String[][] log = new String[cgaCayleyTable.getRows()][cgaCayleyTable.getCols()];

        System.out.println(mv.getName() + ": toSXproductMatrix() input multivector sparsity = "
            + mv.getSparsity().toString());
        MatrixSparsity matrixSparsity = createSparsity(cgaCayleyTable, mv);
        System.out.println(mv.getName() + ": toSXproductMatrix() product matrix sparsity = "
            + matrixSparsity.toString());
        de.dhbw.rahmlab.casadi.impl.casadi.Sparsity sp = toCasADiSparsity(matrixSparsity);

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
     */

    /**
     * Create a sparsity object for the given cayleyTable based on the sparsity of the given sparse
     * multivector.
     *
     * @param cayleyTable
     * @param mv sparse multivector
     * @return sparsity of the matrix representation of the given multivector for the given cayley table
     */
    /*
    private static MatrixSparsity createSparsity(CayleyTable cayleyTable, IGaMvExpr mv) {
        double[][] values = new double[mv.getBladesCount()][mv.getBladesCount()];
        MatrixSparsity sparsity = mv.getSparsity();
        for (int i = 0; i < cayleyTable.getRows(); i++) {
            for (int j = 0; j < cayleyTable.getCols(); j++) {
                CayleyTable.Cell cell = cayleyTable.getCell(i, j);
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
     */

    public static DenseCGAColumnVector toDenseDoubleMatrix(DM dm, CGAMultivectorSparsity sparsity) {
        double[] nonzeros = nonzeros(dm);
        return new DenseCGAColumnVector(nonzeros, sparsity.getrow());
    }

}
