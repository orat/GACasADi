package de.orat.math.gacalcdata.util.tbastandalone;

//import de.gaalop.algebra.AlStrategy;
import de.gaalop.cfg.AlgebraDefinitionFile;
import de.gaalop.productComputer.AlgebraPC;
import de.gaalop.productComputer.GeoProductCalculator;
//import de.gaalop.productComputer.InnerProductCalculator;
//import de.gaalop.productComputer.OuterProductCalculator;
//import de.gaalop.productComputer.ProductComputer;
import de.gaalop.tba.IMultTable;
import de.gaalop.tba.MultTableAbsDirectComputer;
//import de.gaalop.tba.Multivector;
import de.orat.math.sparsematrix.SparseStringColumnVector;
import de.orat.math.sparsematrix.SparseDoubleMatrix;
import de.orat.math.sparsematrix.SparseStringMatrix;
import de.orat.math.sparsematrix.DenseStringMatrix;
import de.orat.math.sparsematrix.SparseDoubleColumnVector;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
//import java.io.InputStreamReader;
//import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 * class testweise in dieses Modul TBA Standalone kopiert um zu schauen ob sich das auf
 * Basis des Gaalop Codes implementieren läßt.
 * 
 * Die gleichnamige class in GACalcdata sollte vermutlich in ein Interface verwandelt 
 * werden und hier die Implementierung soll dann in z.B. von GAGenericCasADi eingesetzt werden
 * 
 * Vielleicht aber sollte die class auch nach GACalcData verschoben werden?
 * ein neues Modul GAGenericCasADi soll das dann verwenden für GACalcData Implementierung
 * 
 * https://discourse.bivector.net/t/matrix-representation/232/4
 *
 * For those interested, the Cayley table of a Clifford algebra is a latin square (a defect latin square in
 * the degenerate case). This means it can be re-ordered. For the expression c=a∗bc=a∗b the Cayley table has
 * aa as row headers, bb as column headers and cc in the body. Because it is a latin square, each element
 * occurs only once in each row and each column. This means we can reorganize it so that it has cc as row
 * headers, bb as column headers and aa in the body. That is exactly the matrix form.<p>
 *
 * https://www.euclideanspace.com/maths/algebra/clifford/theory/cayleyTable/index.htm
 *
 * To multiply two multivectors (a * b) then we multiply each part of a by each part of b, so that we have
 * multiplied every combination of terms. When multiplying terms the result will be of type given by the
 * following table:
 * <p>
 *
 * The entries in the table only shows the type and sign change of the product, it does not show its absolute
 * value. We therefore need to prefix the product by its numerical value which is the real number which is the
 * product of the numbers at the top and left headings.
 *
 * https://rigidgeometricalgebra.org/wiki/index.php?title=Geometric_products
 *
 * @author Oliver Rettig (Oliver.Rettig@orat.de)
 */
public class CayleyTable extends DenseStringMatrix {

    private final List<String> names = new ArrayList<>();

    public CayleyTable(File algebraDir) throws IOException {
        this(createTbl(algebraDir), createNames(algebraDir));
    }
    public static File getAlgebraDefinitionFile(File algebraDir){
        return new File(algebraDir, "definition.csv");
    }
    private static AlgebraDefinitionFile getAlgebraDefinition(File algebraDir){
        System.out.println("algebra def path = "+algebraDir.getAbsolutePath());
        System.out.print(algebraDir.getName()+":");
        if (!algebraDir.exists()) {
            System.out.println("The given first parameter, is not the path of an existing directory!");
            return null;
        }
        File definitionFile = getAlgebraDefinitionFile(algebraDir);
        if (!definitionFile.exists()) {
            System.out.println("There is no file named 'definition.csv' in the directory!");
            return null;
        }
        return new AlgebraDefinitionFile();
    }
    private static AlgebraDefinitionFile loadAlgebraDefinitionFile(File algebraDir){
        AlgebraDefinitionFile alFile = getAlgebraDefinition(algebraDir);
        //AlgebraPC algebraPC = null;
        try (FileReader reader = new FileReader(getAlgebraDefinitionFile(algebraDir))) {
            alFile.loadFromFile(reader);
            //algebraPC = new AlgebraPC(alFile);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(CayleyTable.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CayleyTable.class.getName()).log(Level.SEVERE, null, ex);
        } 
        return alFile;
    }
    private static String[][] createTbl(File algebraDir){
        System.out.println("load algebra file: "+algebraDir.getAbsolutePath());
        
        AlgebraDefinitionFile alFile = loadAlgebraDefinitionFile(algebraDir);
        //IMultTable inner = new MultTableAbsDirectComputer(alFile, new InnerProductCalculator());
        //IMultTable outer = new MultTableAbsDirectComputer(alFile, new OuterProductCalculator());
        IMultTable geo = new MultTableAbsDirectComputer(alFile, new GeoProductCalculator());

        AlgebraPC algebraPC = new AlgebraPC(alFile); //loadAlgebra(algebraDir);
        
        //TODO falls vorhanden die gespeicherte Tabelle verwenden
        //

        //TODO geht das nicht eleganter, also auf die String-Darstellung verzichten
        // und kompakter die Informationen speichern bzw. gar nicht speichern sondern
        // on-the-fly erzeugen

        // only with single-digit count of basis blades short names are possible
        // e123 instead of e1^e2^e3
        //boolean isShort = true;
        //if (algebraPC.base.length > 9) isShort = false;
        
        // jetzt müssen noch die ganzen String-Elemente für das StringMatrix-Array 
        // erzeugt und damit der super-Constructor aufgerufen werden
        int dim = (int) Math.pow(2, algebraPC.base.length/*-1*/);
        System.out.println("matrix dim = "+String.valueOf(dim));
        String[][] result = new String[dim][dim];
        for (int i=0;i<dim;i++){
            for (int j=0;j<dim;j++){
                result[i][j] = BladesPrinter.toString(geo.getProduct(i, j), algebraPC.base);
            }
        }
        return result;
    }
    private static String[] createNames(File algebraDir){
        AlgebraDefinitionFile alFile = loadAlgebraDefinitionFile(algebraDir);
        AlgebraPC algebraPC = new AlgebraPC(alFile); //loadAlgebra(algebraDir);
        return algebraPC.base;
        
    }
    protected CayleyTable(String[][] m, String[] names) {
        super(m);
        this.names.addAll(Arrays.asList(names));
    }

    public final String[] getBasisBladeNames() {
        return getBasisBladeNamesList().toArray(String[]::new);
    }
    public List<String> getBasisBladeNamesList(){
        return names;
    }
    public int getBladesCount() {
        return getBasisBladeNamesList().size();
    }

    public int getGrade(int index) {
        return getBasisBladeName(index).length() - 1;
    }

    public String getBasisBladeName(int index) {
        return getBasisBladeNamesList().get(index);
    }

    // The pseudoscalar is always the last element
    public String getPseudoscalarName() {
        return getBasisBladeNamesList().get(getBasisBladeNamesList().size() - 1);
    }

    public int getPseudoscalarGrade() {
        return getGrade(getBasisBladeNamesList().size() - 1);
    }

    /**
     * Get the row in the column vector of basis blades for the given basis blade name.
     *
     * @param basisBladeName
     * @return -1 if the base names list does not contain the given blade
     */
    public int getBasisBladeIndex(String basisBladeName) {
        return getBasisBladeNamesList().indexOf(basisBladeName);
    }

    /**
     * Cell.
     *
     * A cell of a cayley table can include "0", "1" or other numerical values with or without a prefix sign
     * or the name of a blade also eventually with a sign a prefix.<p>
     *
     * In a typical bases blade names list, the "1" is used for the scalor part and it has the index=0 in the
     * list.
     * <p>
     *
     * @param bladeIndex >=0, if cell includes a blade, the bladeIndex corresponds with the basis blade index
     * in the basisBladeNames[].
     *
     * @param value == 1 or 0, -1 or eventually further numbers e.g. 0.5 wenn die Zelle nur ein Blade enthält
     * ist value=1, wenn die Zelle ein Blade mit Vorzeichen enthält, dann ist value = -1
     */
    public record Cell(int bladeIndex, double Value) {}

    /**
     * Get cell.
     *
     * @param row
     * @param col
     * @return cell
     */
    public Cell getCell(int row, int col) {
        String cellVal = get(row, col);
        int positionIndex = -1; // position in der Matrix-Zelle
        // TODO
        // kann ich nicht auf baseBladeIndex verzichten und einfach mit i weiterarbeiten nach dem break?
        // muss ich das i dann ausserhalb von for deklarieren?
        int baseBladeIndex = -1; // index in der basis-blade Liste
        // start bei i=1, da basisBlade="1" damit nicht verarbeitet werden soll
        // eine Zahl bedeutet implizit ein Faktor aus dieser Zahl und dem Scalarwert also Wert bei index=0
        for (int i = 1; i < getBladesCount(); i++) {
            String basisBladeName = getBasisBladeNamesList().get(i);
            positionIndex = cellVal.indexOf(basisBladeName);
            if (positionIndex >= 0 && positionIndex + basisBladeName.length() == cellVal.length()) {
                baseBladeIndex = i;
                break;
            }
        }
        // Wenn der cell-name mit dem base-blade-name direkt startet, dann ist der 
        // zusätzlich Faktor = 1
        if (positionIndex == 0) {
            return new Cell(baseBladeIndex, 1d);
            // wenn der cell-name erst bei index 2 startet, dann sollte da ein "-" als
            // prefix stehen
        } else if (positionIndex == 1) {
            if (cellVal.charAt(0) == '-') {
                return new Cell(baseBladeIndex, -1d);
            } else {
                throw new RuntimeException("Illegal cell content at row="
                    + String.valueOf(row) + ", col=" + String.valueOf(col) + ", cell=\"" + cellVal + "\", basisBladeIndex=" + String.valueOf(baseBladeIndex));
            }
            // wenn der cell-name erst später anfängt, dann solle eine Zahl als prefix
            // davorstehen
        } else if (positionIndex > 1) {
            return new Cell(baseBladeIndex,
                Double.parseDouble(cellVal.substring(0, positionIndex)));
            // positionIndex < 0
            // dann darf da nur eine Zahl drinstehen
        } else {
            try {
                double value = Double.parseDouble(cellVal);
                if (value == 0d) {
                    return new Cell(-1, value);
                } else {
                    return new Cell(0, value);
                }
            } catch (NumberFormatException e) {
                throw new RuntimeException("Illegal cell content at row="
                    + String.valueOf(row) + ", col=" + String.valueOf(col) + "\"" + cellVal + "\"!");
            }
        }
    }

    // not yet tested
    public SparseDoubleMatrix[] determineBasisBlades() {
        SparseDoubleMatrix[] result = new SparseDoubleMatrix[getBasisBladeNamesList().size()];
        // loop over basis blade names
        for (int i = 0; i < getBladesCount(); i++) {
            String name = getBasisBladeNamesList().get(i);
            double[][] blade = new double[data.length][data[0].length];
            // loop over rows
            for (int row = 0; row < data.length; row++) {
                // loop over columns
                for (int col = 0; col < data[0].length; col++) {
                    if ((data[row][col]).contains(name)) {
                        blade[row][col] = 1;
                    } else if ((data[row][col]).contains("-" + name)) {
                        blade[row][col] = -1;
                    }
                }
            }
            result[i] = new SparseDoubleMatrix(blade, true);
        }
        return result;
    }

    /**
     * Reorder the cayley-matrix in the matrix-form for easy calculation of a product from two multivectors.
     *
     * The Cayley table of a Clifford algebra is a latin square (a defect latin square in the degenerate
     * case). This means it can be re-ordered. For the expression c=a∗b the Cayley table has a as row headers,
     * b as column headers and c in the body. Because it is a latin square, each element occurs only once in
     * each row and each column. This means we can reorganize it so that it has c as row headers, b as column
     * headers and a in the body. That is exactly the matrix form.<p>
     *
     * Eine solche Matrix repräsentiert einen Spaltenvektor a, mit einem Row-Vector multiplizieren ergibt dann
     * einen Spaltenvektor des Produkts von a und b.<p>
     *
     * @param vector
     * @return
     */
    public static DenseStringMatrix reorder2MatrixForm(DenseStringMatrix cayleyTable) {
        //TODO
        return null;
    }

    // not yet tested
    public SparseStringMatrix sparseStringMatrix(SparseStringColumnVector vector) {
        // cayley table (matrix form) kopieren
        String[][] result = toArr();
        // alle nicht benötigten Elemente entsprechend der sparsity of the 
        // given vec auf null setzen
        // loop over rows
        for (int row = 0; row < data.length; row++) {
            // loop over columns
            for (int col = 0; col < data[0].length; col++) {
                //TODO
                // in der cayley-matrix stehen auch noch Vorzeichen. 
                // Muss ich nicht daher nach solchen speziellen
                // Zeichenketten suchen?
                if (!data[row][col].equals("0")) {
                    String value = data[row][col];
                    // führendes Minus-Zeichen entfernen
                    if (value.startsWith("-")) {
                        value = value.substring(1);
                    }
                    if (!vector.contains(value)) {
                        result[row][col] = "0";
                    }
                }
            }
        }
        return new SparseStringMatrix(result, true);
    }

    public SparseDoubleMatrix sparseDoubleMatrix(SparseDoubleColumnVector vector) {
        //TODO
        return null;
    }
}
