package de.orat.math.gacalcdata.util.tbastandalone;

import de.gaalop.algebra.AlStrategy;
import de.gaalop.cfg.AlgebraDefinitionFile;
import de.gaalop.productComputer.AlgebraPC;
import de.gaalop.productComputer.GeoProductCalculator;
import de.gaalop.productComputer.InnerProductCalculator;
import de.gaalop.productComputer.OuterProductCalculator;
import de.gaalop.tba.Algebra;
import de.gaalop.tba.IMultTable;
import de.gaalop.tba.MultTableAbsDirectComputer;
import de.gaalop.tba.Multivector;
import de.gaalop.tba.Products;
import static de.orat.math.gacalcdata.util.tbastandalone.CayleyTable.getAlgebraDefinitionFile;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.AbstractTableModel;

/**
 * @author Oliver Rettig (Oliver.Rettig@orat.de)
 */
public class MultivectorJTableModel extends AbstractTableModel {

    private final IMultTable tbl;
    
    private AlgebraPC algebraPC;
    private Algebra algebra;
    
    private final int n;   // count of basis vectors
    private final int dim; // count of basis blades +1 (scalar)
    
    public MultivectorJTableModel(File algebraDir, Products product){
    
        System.out.println("load algebra file: "+algebraDir.getAbsolutePath());
        
        AlgebraDefinitionFile alFile = loadAlgebraDefinitionFile(algebraDir);
        if (null == product){
            throw new IllegalArgumentException("product==null not allowed!");
        } else switch (product) {
            case GEO:
                tbl = new MultTableAbsDirectComputer(alFile, new GeoProductCalculator());
                break;
            case INNER:
                tbl = new MultTableAbsDirectComputer(alFile, new InnerProductCalculator());
                break;
            default:
                tbl = new MultTableAbsDirectComputer(alFile, new OuterProductCalculator());
                break;
        }
        
        algebraPC = new AlgebraPC(alFile); //loadAlgebra(algebraDir);
       
        AlStrategy.createBlades(alFile);
        algebra = new Algebra(alFile);
        

        // Create grade based order similar to ganja.js
        
        n = alFile.getSignature().getDimension(); // alFile.base2.length-1;;
        //System.out.println("n="+String.valueOf(n));
        
        //gradeOrderList = createGradeOrderList(n);
        
        //TODO falls vorhanden die gespeicherte Tabelle verwenden
        //

        //TODO 
        // only with single-digit count of basis blades short names are possible
        // e123 instead of e1^e2^e3
        // boolean isShort = true;
        // if (algebraPC.base.length > 9) isShort = false;
        
        dim = (int) Math.pow(2, n);
    }
    
    private static AlgebraDefinitionFile loadAlgebraDefinitionFile(File algebraDir){
        AlgebraDefinitionFile alFile = getAlgebraDefinition(algebraDir);
        //AlgebraPC algebraPC = null;
        try (FileReader reader = new FileReader(getAlgebraDefinitionFile(algebraDir))) {
            alFile.loadFromFile(reader);
            //algebraPC = new AlgebraPC(alFile);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(MultivectorJTableModel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MultivectorJTableModel.class.getName()).log(Level.SEVERE, null, ex);
        } 
        return alFile;
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
    
    private static int fk(int n){
        if (n == 0){
            return 1;
        } else {
            return n * fk(n - 1);
        }
    }
    private static int bk(int n, int k){
        return fk(n) / (fk(k) * fk(n - k));
    }
                      
    @Override
    public int getRowCount() {
        return dim; 
    }

    @Override
    public int getColumnCount() {
        return dim; 
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return new MultivectorCell(algebra, tbl.getProduct(getBladeIndex(rowIndex),
                                                    getBladeIndex(columnIndex)));
    }
    
    
    //TODO
    // wie komme ich von den Zeilen/Spaltenpositionen auf die passenden blade indizes?
    // dazu muss ich die Reihenfolge der blades kennen
    // hier kann ich damit die Reihenfolge der Blades in der Tabelle ändern, vielleicht
    // sollte diese auch in der algebra def formuliert werden
    // TODO unklar wie die Reihenfolge der basisblades in den Blades konfigurierbar 
    // gemacht werden könnte.
    
    //private int[] gradeOrderList;
    private static int[] createGradeOrderList(int n){
        int[] result = new int[(int) Math.pow(2, n)];
        int[] j_k = new int[n+1]; // current j index for a given k
        int[] bks = new int[n+1]; // binominla coefficients == number of blades of a given k
        for (int k=0;k<=n;k++){
            bks[k] = bk(n,k);
        }
        for (int i=0;i<result.length;i++){
            int k = (int) Long.bitCount(i);
            System.out.println("k(i="+String.valueOf(i)+")="+String.valueOf(k));
            int j = j_k[k];
            j_k[k] = j_k[k] + 1;
            int index = getIndex(k,j,bks);
            System.out.println("getIndex(k="+String.valueOf(k)+", j="+String.valueOf(j)
                    +", bks.length="+String.valueOf(bks.length)+")="+String.valueOf(index));
            result[index] = i;        
        }
        
        // WORKAROUND
        if (n==4){
            int tmp = result[7];
            result[7] = result[8];
            result[8] = tmp;
        }
        return result;
    }
    /**
     * Get index in the grade baded orderd array.
     * 
     * @param k grade
     * @param j index in the subarray corresponding to the given grade
     * @param n dimensionality of the used geometric algebra
     * @return position index in the grade based ordered blade array
     */
    private static int getIndex(int k, int j, int[] bks){
        int result = 0;
        for (int i=0;i<k;i++){
            result +=bks[i];
        }
        result +=j;
        return result;
    }
    
    // TODO
    // https://oeis.org/A294648 --> gleiches Problem, siehe WORKAROUND oben
    // aber https://oeis.org/A359941 könnte funktionieren (siehe Discord discussion)
    // recursive algo, scheint schneller zu sein, aber startet auch immer mit dem
    // ersten Wert der Liste
    /*
    For n=1, 2, 3, ..., L(n) is defined by the recurrence:
    if n=1, L(1)= 0, 1;
    else L(n)= l(n, 0), l(n, 1), ..., l(n, k), ..., l(n, n), where the subsequences are defined as follows:
    l(n, k)= 0, if k=0, else
    l(n, k)= 2^n - 1, if k=n, else
    l(n, k)= l(n-1, k), l(n-1, k-1) + 2^{n-1}, for 0 < k < n.
    */
    /*public static int[] createGradeOrderList(int n){
        int[] result = new int[(int) Math.pow(2, n)];
        int i=0;
        for (int k=0;k<n;k++){
        
        }
    }*/
    /*private static int l(int n, int k, int i, int[] list){
        if (n==1) {
            //L(1)= 0, 1;
            list[0] = 0;
            list[1] = 1;
            i = 2;
        } else {
            L(n)= l(n, 0), l(n, 1), ..., l(n, k), ..., l(n, n) //, where the subsequences are defined as follows:
        }
        l(n, k)= 0, if k=0, else
        l(n, k)= 2^n - 1, if k=n, else
        l(n, k)= l(n-1, k), l(n-1, k-1) + 2^{n-1}, for 0 < k < n.
    }*/
    
    public int getBladeIndex(int index){
        return index;
        //return gradeOrderList[index];
    }
    
    public Class<?> getColumnClass(int columnIndex) {
        return MultivectorCell.class;
    }
    
    /*@Override
    public Class<?> getColumnClass(int columnIndex){
        return columnClass[columnIndex];
    }*/
    
    public String[] getBasisBladeNames(){
        return algebraPC.base;
    }
}