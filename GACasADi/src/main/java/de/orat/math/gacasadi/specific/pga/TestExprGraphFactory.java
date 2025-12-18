package de.orat.math.gacasadi.specific.pga;

import de.orat.math.gacasadi.specific.cga.*;
import de.orat.math.gacalc.api.GAFactory;
import de.orat.math.gacalc.spi.IGAFactory;

/**
 * @author Oliver Rettig (Oliver.Rettig@orat.de)
 */
public class TestExprGraphFactory extends GAFactory {

    public static PgaFactory impl_ = PgaFactory.instance;

    public static GAFactory instance() {
        return get(impl_);
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
