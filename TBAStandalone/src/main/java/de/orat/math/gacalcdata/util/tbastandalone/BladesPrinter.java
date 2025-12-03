package de.orat.math.gacalcdata.util.tbastandalone;

import de.gaalop.tba.BladeRef;
import de.gaalop.tba.Multivector;
import java.awt.Color;

/**
 * @author Oliver Rettig (Oliver.Rettig@orat.de)
 */
public class BladesPrinter {
    
    
    /**
     * @param basisBladeNames The names of the basis vector (e1, e2, e3) are used when
     * not available (null)
     * ok null und e1, e2, e3 liefern identische Ergebnisse
     * 
     * ok 
     * TODO short version without ^-operators
     * 
     * @return Human readable String representation of the blade.
     */
    public static String toString(BladeRef bladeRef, String[] basisBladeNames) {
        StringBuilder result = new StringBuilder();
        int i = 1;
        int b = bladeRef.getIndex();
        while (b != 0) {
            if ((b & 1) != 0) {
                if (result.length() > 0) result.append("^");
                if ((basisBladeNames == null) || (i > basisBladeNames.length) || (basisBladeNames[i-1] == null))
                    result.append("e").append(i);
                else result.append(basisBladeNames[i-1]);
            }
            b >>= 1;
            i++;
        }
        //return (result.length() == 0) ? Double.toString(prefactor) : prefactor + "*" + result.toString();
        if (result.length() == 0){
            return Double.toString(bladeRef.getPrefactor());
        } else {
            String res;
            switch (bladeRef.getPrefactor()){
                case 1:
                    res = result.toString();
                    break;
                case -1:
                    result.insert(0, "-");
                    res = result.toString();
                    break;
                default:
                    result.insert(0, "*");
                    result.insert(0,bladeRef.getPrefactor());
                    res = result.toString();
            }
            return res;
        }
    }
    
    public static String toString(Multivector m, String[] basisBladeNames){
        if (m.getBlades().isEmpty()) return "0";
        StringBuilder sb = new StringBuilder();
        for (BladeRef ref: m.getBlades()) {
            if (ref.getIndex() == 0){
                switch (ref.getPrefactor()) {
                    case -1:
                        sb.append("-1");
                        break;
                    case 0:
                        sb.append("0");
                        break;
                    case 1:
                        sb.append("1");
                        break;
                    default:
                        System.err.println("Only -1,0,1 allowed as prefactors in multivectors");
                        break;
                }
            } else {
                switch (ref.getPrefactor()) {
                    case -1:
                        /*sb.append("-").*/sb.append(toString(ref, basisBladeNames)); //append(ref.getIndex());
                        break;
                    case 0:
                        //FIXME darf das bei den Tabellen überhaupt vorkommen? In der Diagonalen tritt das auf
                        break;
                    case 1:
                        sb.append("+").append(toString(ref, basisBladeNames)); //.append(ref.getIndex());
                        break;
                    default:
                        System.err.println("Only -1,0,1 allowed as prefactors in multivectors - prefactor is a byte!");
                        if (ref.getPrefactor() > 0){
                            sb.append("+");
                        } 
                        sb.append(String.valueOf(ref.getPrefactor())).
                                    append(toString(ref, basisBladeNames));
                        break;
                }
            }
        }
        if (sb.length()==0) return "";
        if (sb.charAt(0) == '+')
            return sb.substring(1);
        else
            return sb.toString();
    }
    
    
    /**
     * To create the ASCII represenation of Caylay-Tables used in Gaazelle.
     * 
     * funktioniert noch nicht
     * 
     * @param isShort useful only if basis blade count is single-digit
     * @return 
     */
    public String print2(Multivector m, boolean isShort){
        StringBuilder sb = new StringBuilder();
        for (BladeRef ref: m.getBlades()) {
            if (ref.getIndex() == 0){
                switch (ref.getPrefactor()) {
                    case -1:
                        sb.append("-1");
                        break;
                    case 0:
                        sb.append("0");
                        break;
                    case 1:
                        sb.append("1");
                        break;
                    default:
                        System.err.println("Only -1,0,1 allowed as prefactors in multivectors");
                        break;
                }
            } else {
                switch (ref.getPrefactor()) {
                    case -1:
                        sb.append("-").append(ref.printGenericBladeName(isShort)); //append(ref.getIndex());
                        break;
                    case 0:
                        break;
                    case 1:
                        sb.append("+").append(ref.printGenericBladeName(isShort)); //.append(ref.getIndex());
                        break;
                    default:
                        System.err.println("Only -1,0,1 allowed as prefactors in multivectors?");
                        if (ref.getPrefactor() > 0){
                            sb.append("+");
                        } else {
                            sb.append(String.valueOf(ref.getPrefactor())).
                                    append(ref.printGenericBladeName(isShort));
                        } 
                        break;
                }
            }
        }
        if (sb.length()==0) return "";
        if (sb.charAt(0) == '+')
            return sb.substring(1);
        else
            return sb.toString();
    }
}