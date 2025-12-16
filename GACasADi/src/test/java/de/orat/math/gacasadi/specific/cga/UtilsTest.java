package de.orat.math.gacasadi.specific.cga;

import de.dhbw.rahmlab.casadi.MxStatic;
import de.dhbw.rahmlab.casadi.impl.casadi.MX;
import de.orat.math.sparsematrix.SparseStringMatrix;
import org.junit.jupiter.api.Test;
import util.cga.CGACayleyTableGeometricProduct;
import util.cga.CGAKVectorSparsity;

/**
 *
 * @author Oliver Rettig (Oliver.Rettig@orat.de)
 */
public class UtilsTest {

    public UtilsTest() {
    }

    @Test
    public void testMatrixExpr() {
        MX x = MxStatic.sym("x", 2);
        MX A = new MX(2, 2);
        A.at(0, 0).assign(x.at(0));
        A.at(1, 1).assign(MxStatic.mtimes(x.at(0), new MX(3)));

        // A:(project((zeros(2x2,1nz)[0] = x[0]))[1] = (x[0]+x[1]))
        System.out.println("A:" + A);
        //FIXME
        // A[0][0]=(project((zeros(2x2,1nz)[0] = x[0]))[1] = (3*x[0]))[0]
        // statt den Ausdruck für die Zelle bekomme ich den Inhalt der gesamten Matrix
        System.out.println("A[0][0]=" + A.at(0, 0));
        /**
         * bei MX wird nach Zuweisung eines Elementes und anschließendem Anschauen des Elementes alles
         * geprinted. TODO Test unter Python/Matlab In der Dokumentation ist die Ausgabe des Beispiels mit MX,
         * wo dies gemacht wird, auch so: https://web.casadi.org/docs/#the-mx-symbolics (siehe letzter Code
         * Block).
         *
         * Dass beim Printen eines Elementes einer Matrix nur dieses angezeigt wird, funktioniert hingegen mit
         * SX ohne Weiteres.
         *
         * Trick gefunden... Und zwar, wenn ich die MxSubMatrix, welche von dem at() von MX zurück gegeben
         * wird, einer neuen Funktion als Output-Parameter spezifiziere und diese MX-Funktion dann symbolisch
         * mit SX Output-Argument calle und den Output printe, dann sehe ich auch genau nur das Element. Das
         * funktioniert für normale MX gut, für MX aus MX.sym gibt es nachvollziehbarerweise eine Exception,
         * weil die als Inputs gedacht sind, so wie ich das verstehe.
         *
         * Ich könnte eine Methode schreiben und in MxSubMatrix injizieren, die den Trick umsetzt. Oder soll
         * ich lieber die toString() Methode von MxSubMatrix dafür entsprechend anpassen? Gerade ist es so,
         * dass MxSubMatrix die toString Methode von MX erbt. Man könnte also zur Not immernoch explizit die
         * toString Methode der Basisklasse aufrufen. Sollte ich in der toString() Methode vielleicht
         * überprüfen, ob die MxSubMatrix 1x1 ist und nur dann den Trick durchführen? Oder sollte ich gleich
         * ganz MX generell die toString Methode den Trick benutzen lassen? Womöglich kann man auch dem MX
         * irgendwo ansehen, ob es mit MX.sym gebaut wurde und dann die normale toString() Methode nehmen
         * automatisch...
         */
    }

    public void testCGAKSparsity() {
        CGACayleyTableGeometricProduct table = CGACayleyTableGeometricProduct.instance();
        String[] basisBladeNames = table.getBasisBladeNames();
        int size = basisBladeNames.length;
        CGAKVectorSparsity sparsity = CGAKVectorSparsity.instance(1);

        String[][] m = new String[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                m[i][j] = "*";
            }
        }
        SparseStringMatrix sm = new SparseStringMatrix(sparsity, m);
        System.out.println(sm.toString(true));
        System.out.println(sm.toString(false));
    }
}
