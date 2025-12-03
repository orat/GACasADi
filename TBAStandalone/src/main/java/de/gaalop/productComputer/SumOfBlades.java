package de.gaalop.productComputer;

import de.gaalop.tba.BladeRef;
import de.gaalop.tba.Multivector;
import java.util.LinkedList;
import java.util.Map;

/**
 * Represents a sum of weighted blades (coefficients only -1,0,1?)
 * @author Christian Steinmetz
 */
public class SumOfBlades extends LinkedList<SignedBlade> {

    /**
     * Converts this sum of blades to a multivector
     * @param map The map, which represents the zeroInfBlade->index map
     * @param bitCount The maximum number of bits
     * @return The resulting multivector
     * 
     * Signed blades of this must contain only blades with coefficients -1,0,1.
     * Why?
     * This produces problems...
     * Who uses this method?
     */
    public Multivector toMultivector(Map<Blade, Integer> map, int bitCount) {
        Multivector result = new Multivector();
        for (SignedBlade sb: this) {
            Blade b = new Blade(bitCount, sb);
            if (1-Math.abs(sb.coefficient) > 10E-4) // WORKAROUND
                // with 2dcga:
                // Error: MvCoeff is not -1,0,1 but -0.25
                // Error: MvCoeff is not -1,0,1 but -0.5
                System.err.println("Error: MvCoeff is not -1,0,1 but "+sb.coefficient);

            Integer m = map.get(b);
            result.addBlade(new BladeRef((sb.coefficient > 0) ? (byte) 1 : (byte) -1, m));
        }
        return result;
    }
}
