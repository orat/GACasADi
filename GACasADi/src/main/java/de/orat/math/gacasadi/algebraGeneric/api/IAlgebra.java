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
    
    //TODO erweitern und dann auch die impl in GaalopAlgebra in impl.gaalop
    
}
