package de.orat.math.gacasadi.specific.cga;

import de.orat.math.gacalc.api.GAFactory;
import de.orat.math.gacalc.spi.IGAFactory;
import de.orat.math.gacasadi.algebraGeneric.api.IAlgebra;

/**
 * @author Oliver Rettig (Oliver.Rettig@orat.de)
 */
public class TestExprGraphFactory extends GAFactory {

    public static final CgaFactory impl_ = CgaFactory.instance;

    public static final IAlgebra al = impl_.getIAlgebra();

    public static final TestExprGraphFactory instance = new TestExprGraphFactory(impl_);

    public static TestExprGraphFactory instance() {
        return instance;
    }

    protected TestExprGraphFactory(IGAFactory impl) {
        super(impl);
    }

    /*public static double[] createRandomKVector(int basisBladesCount) {
        return impl_.createRandomKVector(basisBladesCount);
    }

    public double[] createRandomCGAMultivector() {
        return impl_.createRandomCGAMultivector();
    }
    
    public static double[] createRandomCGAKVector(int basisBladesCount, int grade){
        return impl_.createRandomCGAKVector(basisBladesCount, grade);
    }

    public static double[] createRandomCGAKVector(int grade){
        return impl_.createRandomCGAKVector(grade);
    }*/
}
