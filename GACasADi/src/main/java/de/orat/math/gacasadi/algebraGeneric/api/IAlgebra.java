package de.orat.math.gacasadi.algebraGeneric.api;

import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 * Generic Algebra definition.
 * Different implementations of this are still generic for all algebras. Specific algebras are in another package.
 * The difference between implementations is how they approach to generically calculate algebras.
 * </pre>
 */
public interface IAlgebra {

    IProduct gp();

    IProduct inner();

    IProduct outer();

    //n
    int getBaseSize();

    /**
     * Inclusive scalar grade.
     */
    default int getGradesCount() {
        return 1 + getBaseSize();
    }

    int[] getIndizes(int grade);
    int[] getIndizes(int[] grades);

    List<Integer> getGrades(List<Integer> indices);
    
    // 2^n
    // n can be at most 31.
    default int getBladesCount() {
        return 1 << getBaseSize();
    }

    //einf
    int indexOfBlade(String baseVector);

    //[e1, e3, einf]
    /**
     *
     * @param bladeOfBasevectors Strings of base vectors representing a basis blade.
     * @return
     */
    int indexOfBlade(String... bladeOfBasevectors);

    public int[] getEvenIndizes();
    
    public int getGrade(int index);

    /**
     * <pre>
     * (-1)^exponent
     * Precondition: exponent >= 0
     * </pre>
     */
    public static int minusOneToThePowerOf(int exponent) {
        return -1 + 2 * ((exponent + 1) & 0x1);
    }

    public static List<Integer> computeGradeToReverseSign(int gradesCount) {
        List<Integer> gradeToConjugateSign = new ArrayList<>(gradesCount);
        for (int grade = 0; grade < gradesCount; ++grade) {
            int exp = (grade * (grade - 1)) / 2;
            int sign = minusOneToThePowerOf(exp);
            gradeToConjugateSign.add(sign);
        }
        return gradeToConjugateSign;
    }

    public static List<Integer> computeGradeToConjugateSign(int gradesCount) {
        List<Integer> gradeToConjugateSign = new ArrayList<>(gradesCount);
        for (int grade = 0; grade < gradesCount; ++grade) {
            int exp = (grade * (grade + 1)) / 2;
            int sign = minusOneToThePowerOf(exp);
            gradeToConjugateSign.add(sign);
        }
        return gradeToConjugateSign;
    }

    public static List<Integer> computeGradeToGradeInversionSign(int gradesCount) {
        List<Integer> gradeToConjugateSign = new ArrayList<>(gradesCount);
        for (int grade = 0; grade < gradesCount; ++grade) {
            int sign = minusOneToThePowerOf(grade);
            gradeToConjugateSign.add(sign);
        }
        return gradeToConjugateSign;
    }

    int gradeToReverseSign(int grade);

    int gradeToConjugateSign(int grade);

    int gradeToGradeInversionSign(int grade);
}
