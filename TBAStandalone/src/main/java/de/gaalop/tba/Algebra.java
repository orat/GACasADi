package de.gaalop.tba;

import de.gaalop.algebra.TCBlade;
import de.gaalop.cfg.AlgebraDefinitionFile;
import de.gaalop.dfg.Expression;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
//import java.util.Vector;

/**
 * Defines an algebra by storing the base elements and the blades
 * @author Christian Steinmetz
 */
public class Algebra {

    private String[] base;
    private /*Vector*/ArrayList<Blade> blades = new /*Vector*/ArrayList<>();
    
    private Map<Blade,Integer> indices = new HashMap<>();

    // grade, indizes
    private Map<Integer, List<Integer>> grades = new HashMap<>();
    
    private boolean dirty = true;
    

    public Algebra() {
    }

    public Algebra(String[] base, TCBlade[] blades) {
        this.base = base;
        for (TCBlade b: blades)
            this.blades.add(new Blade(b));
    }

    public Algebra(AlgebraDefinitionFile alFile) {
        this.base = alFile.base;
        for (Expression e: alFile.blades) 
            blades.add(Blade.createBladeFromExpression(e));
    }
    
    public int getBladeCount() {
        return blades.size();
    }

    public Blade getBlade(int index) {
        if (index < blades.size()) {
            return blades.get(index);
        } else
            return blades.get(0);   //hint: inputsVector.bladeIndex can be greater then algebra bladecount, if more than 2^n input variables exist. 
                                    //Return blade "1" in this case (of a vector), because the order of a vector depends not on the algebra. 
    }

    public void setBlade(int index, Blade bladeExpr) {
        /*if (index > blades.size() - 1) {
            blades.setSize(index + 1);
        }*/
        blades.set(index, bladeExpr);
        dirty = true;
    }

    /**
     * Returns the index to a given blade
     * @param bladeExpr The blade to be searched
     * @return The index of the blade
     */
    public int getIndex(Blade bladeExpr) {
        if (dirty) buildMap();
        if (bladeExpr.getBases().isEmpty())
            return 0;
        return indices.get(bladeExpr);
    }

    public String[] getBase() {
        return base;
    }

    public void setBase(String[] base) {
        this.base = base;
        dirty = true;
    }

    /**
     * Returns the number of elements in the base
     * @return The number of base elements
     */
    public int getBaseCount() {
        return base.length - 1;
    }

    public void buildMap() {
        indices.clear();
        int i=0;
        for (Blade b: blades){
            indices.put(b, i); // ehemals i++
            // new
            int grade = b.getBases().size();
            List<Integer> inds = grades.get(grade);
            if (inds == null){
                inds = new ArrayList<>();
                grades.put(grade, inds);
            }
            inds.add(i++);
        }
        dirty = false;
    }
    
    //new
    public int[] getIndizes(int grade){
        if (dirty) buildMap();
        return grades.get(grade).stream().mapToInt(i->i).toArray();
    }
    
    public int getGrade(int index){
        Blade blade = blades.get(index);
        return blade.getBases().size();
    }
}

