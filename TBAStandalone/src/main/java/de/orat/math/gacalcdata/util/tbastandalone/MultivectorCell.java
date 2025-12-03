package de.orat.math.gacalcdata.util.tbastandalone;

import de.gaalop.tba.Algebra;
import de.gaalop.tba.BladeRef;
import de.gaalop.tba.Multivector;
import java.util.List;

/**
 * @author Oliver Rettig (Oliver.Rettig@orat.de)
 */
public class MultivectorCell {
     
    private final Multivector m;
    private final Algebra algebra;
    
    public MultivectorCell(Algebra algebra, Multivector m){
        this.m = m;
        this.algebra = algebra;
    }
    
    public int getGrade(){
        // the assumption is that all blades has the same grade
        List<BladeRef> blades = m.getBlades();
        if (!blades.isEmpty()){
            return getGrade(m.getBlades().getFirst().getIndex());
           
            // das ginge nur wenn der Index der binary-representation entsprechen würde
            //return Integer.bitCount(blades.getFirst().getIndex());
        } else {
            //System.out.println("Empty multivector found!");
            return -1;
        }
    }
    private int getGrade(int bladeIndex) {
        return algebra.getBlade(bladeIndex).getBases().size();
    }
    
    public boolean isNegative(){
        boolean result = false;
        if (m.getBlades().size() == 1){
            if (m.getBlades().getFirst().getPrefactor() == -1) result = true;
        }
        return result;
    }
    
    public String toString(String[] basisBladeNames){
        // representation corresponding to the human readable file type for the tables (E0, E1, ...)
        //return m.print(); 
        
        StringBuilder sb = new StringBuilder();
        boolean firstBlade = true;
        List<BladeRef> blades = m.getBlades();
        if (blades.isEmpty()){
            return "0";
        } else {
            // Scalars are no blade
            
            for (BladeRef blade: blades){
                if (isNegative()){
                    sb.append("-");
                } else if (!firstBlade){
                    sb.append("+");
                }
                sb.append(algebra.getBlade(blade.getIndex()).toString());
                firstBlade = false;
            }
            return sb.toString();
        }
        // use bitset based output
        //return BladesPrinter.toString(m, basisBladeNames);
    }
}