package de.gaalop.productComputer;

import de.gaalop.algebra.AlStrategy;
import de.gaalop.algebra.DefinedAlgebra;
//import de.gaalop.algebra.Plugin;
import de.gaalop.cfg.AlgebraDefinitionFile;
import de.gaalop.tba.IMultTable;
import de.gaalop.tba.MultTableAbsDirectComputer;
import de.gaalop.tba.MultTableImpl;
import de.gaalop.tba.table.TableFormat;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.IOUtils;

/**
 * Creates the multiplication tables.
 * 
 * @author Christian Steinmetz
 */
public class Main {

     public static LinkedList<DefinedAlgebra> getDefinedAlgebras() {
        try {
            LinkedList<DefinedAlgebra> result = new LinkedList<>();
            for (String line: IOUtils.toString(DefinedAlgebra.class.getResourceAsStream("algebra/definedAlgebras.txt"), "UTF-8").split("\r\n")) {
                String[] parts = line.split(";");
                if (parts.length == 2) {
                    result.add(new DefinedAlgebra(parts[0], parts[1]));
                }
            }
            return result;
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
     
    // create and save all tables for all defined algebras
    public static void main(String[] args) throws IOException {
        for (DefinedAlgebra definedAlgebra: getDefinedAlgebras()) {
            System.out.println(definedAlgebra.name+"...");
            ProductComputer productComputer = new ProductComputer();
            AlgebraDefinitionFile alFile = new AlgebraDefinitionFile();
            alFile.loadFromFile(new InputStreamReader(AlStrategy.class.getResourceAsStream("algebra/"+definedAlgebra.id+"/definition.csv")));
            AlgebraPC algebraPC = new AlgebraPC(alFile);
            productComputer.initialize(algebraPC);

            
            //precalculate tables because of double use in TableCompressed
            
            int bladeCount = (int) Math.pow(2, algebraPC.base.length);
            MultTableImpl inner = new MultTableImpl();
            inner.createTable(bladeCount);
            MultTableImpl outer = new MultTableImpl();
            outer.createTable(bladeCount);
            MultTableImpl geo = new MultTableImpl();
            geo.createTable(bladeCount);

            IMultTable innerLive = new MultTableAbsDirectComputer(alFile, new InnerProductCalculator());
            IMultTable outerLive = new MultTableAbsDirectComputer(alFile, new OuterProductCalculator());
            IMultTable geoLive = new MultTableAbsDirectComputer(alFile, new GeoProductCalculator());
            
            for (int i=0;i<bladeCount;i++)
                for (int j=0;j<bladeCount;j++) {
                    inner.setProduct(i, j, innerLive.getProduct(i, j));
                    outer.setProduct(i, j, outerLive.getProduct(i, j));
                    geo.setProduct(i, j, geoLive.getProduct(i, j));
                }
            
            TableFormat.writeToFile(bladeCount,algebraPC.base.length,inner,outer,geo,new FileOutputStream("products_"+definedAlgebra.id+".csv"),TableFormat.TABLE_HUMAN_READABLE/*.TABLE_COMPRESSED_MAX*/);
        }
    }
}
