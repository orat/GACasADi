package de.gaalop.tba;

/**
 * Represents a blade (ordered list of basis blades) by its index.
 * Stores also a prefactor
 * 
 * @author Christian Steinmetz
 */
public class BladeRef {

    private byte prefactor;
    private int index;

    public BladeRef(byte prefactor, int index) {
        this.prefactor = prefactor;
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public byte getPrefactor() {
        return prefactor;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setPrefactor(byte prefactor) {
        this.prefactor = prefactor;
    }

    @Override
    public String toString() {
        return prefactor+"["+index+"]";
    }

    private int getBit(int value, int k) {
        // int bit 32 ist das Vorzeichen
        if (k>31) throw new IllegalArgumentException("getBit("+String.valueOf(value)+",k>31)!");
        int result = (value >> k) & 1;
        System.out.println("getBit("+String.valueOf(value)+","+String.valueOf(k)+")="+String.valueOf(result));
        return result;
    }
    
    /**
     * funktioniert so nicht!
     * FIXME
     * 
     * @param isShort e123 instead of e1^e2^e3
     * @return 
     */
    public String printGenericBladeName(boolean isShort){
        if (index == 0) return "1";
        StringBuilder sb = new StringBuilder();
        if (isShort){
            sb.append("e");
            // bit 31 excludes, it is the sign of the int
            for (int k=0;k<31;k++){
                if (getBit(index, k+1) == 1) sb.append(String.valueOf(k));
            }
        } else {
            boolean deleteOperator = false;
            // bit 31 excludes, it is the sign of the int
            for (int k=0;k<31;k++){
                if (getBit(index, k+1) == 1) {
                    sb.append("e");
                    sb.append(String.valueOf(k));
                    sb.append("^");
                    deleteOperator = true;
                }
            }
            //sb.deleteCharAt(0);
            if (deleteOperator) sb.deleteCharAt(sb.length()-1);
        }
        return sb.toString();
    }
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + this.prefactor;
        hash = 59 * hash + this.index;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final BladeRef other = (BladeRef) obj;
        if (this.prefactor != other.prefactor) {
            return false;
        }
        return this.index == other.index;
    }
}