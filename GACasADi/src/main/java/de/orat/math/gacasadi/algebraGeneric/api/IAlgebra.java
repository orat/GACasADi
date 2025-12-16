package de.orat.math.gacasadi.algebraGeneric.api;

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
}
