package de.orat.math.gacalcdata.util.tbastandalone;

import de.gaalop.tba.Multivector;
import de.gaalop.tba.Products;
import java.awt.BorderLayout;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * @author Oliver Rettig (Oliver.Rettig@orat.de)
 */
public class Main extends JFrame {
      
    private static File determineAlgebraFolder(String name) throws URISyntaxException {
        URI gaalopSRCFolder = Main.class.getProtectionDomain().getCodeSource().
            getLocation().toURI();
        URI algebraFolder = gaalopSRCFolder.normalize().resolve("de/gaalop/algebra/algebra/" + name);
        File file = new File(algebraFolder);
        return file;
    }

    
    // Show JTable of a given algebra definition
    
    // TODO
    // https://support.hcl-software.com/csm?id=kb_article&sysparm_article=KB0024706
    // Speicherverbrauch bestimmen
    public static void main(String[] args) throws URISyntaxException {
        try {
            String algebraName = "3dpga"; //cga";//d41";//cga";
            Products product = Products.GEO;
            
            JFrame frame = new JFrame("Cayley table for "+product.toString()+"-product for algebra \""+ algebraName+"\""); // Fenster mit Titel erstellen
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Beenden der Anwendung beim Schließen des Fensters
            frame.setSize(400, 300); // Fenstergröße festlegen

            JPanel panel = new JPanel(); // Ein neues JPanel erstellen
            panel.setLayout(new BorderLayout()); // Beispiel: Layout-Manager für das Panel setzen

            GAProductTblJTable table = new GAProductTblJTable(determineAlgebraFolder(algebraName), product);
            //TODO in den Konstruktor von GAProductTblJTable verschieben
            // scheint trotz setzen nicht aufgerufen zu werden
            //FIXME
            table.setDefaultRenderer(MultivectorCell.class, new MultivectorCellRenderer());
            table.setDefaultRenderer(Multivector.class, new MultivectorCellRenderer());
            
            
            panel.add(table, BorderLayout.CENTER); // Label zum Panel hinzufügen
            frame.getContentPane().add(new JScrollPane(panel)); // Das Panel zum Inhaltsbereich des Frames hinzufügen

            //frame.pack();
            frame.setVisible(true); // Das Fenster anzeigen
        
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        //  System.in.read();
        //  System.in.read();
    }
}
