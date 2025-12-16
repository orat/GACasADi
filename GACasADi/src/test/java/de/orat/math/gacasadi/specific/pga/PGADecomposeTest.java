package de.orat.math.gacasadi.specific.pga;

import de.orat.math.gacasadi.specific.cga.*;
import de.orat.math.gacalc.util.GeometricObject;
import de.orat.math.gacalc.util.GeometricObject.Sign;
import de.orat.math.gacalc.util.Tuple;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

/**
 * @author Oliver Rettig (Oliver.Rettig@orat.de)
 */
public class PGADecomposeTest {

    public PGADecomposeTest() {
    }

    //@Test
    public void testPoint(){
        double[] position = new double[]{1d, 2d, 3d};
        boolean isIPNS = true;
        double squaredWeight = 1d;
        GeometricObject obj = GeometricObject.createRoundPoint(position, isIPNS,
                                               squaredWeight, Sign.POSITIVE, 1);
        System.out.println(obj.toString());
        PgaMvValue mv = PgaMvValue.compose(obj);
        test(obj, mv);
    } 
    
    //@Test
    public void testLine(){
        double[] position = new double[]{1d, 2d, 3d};
        double[] direction = new double[]{4,-1,1};// new double[]{0,0,1};
        double squaredWeight = 1d;
        boolean isIPNS = true;
        GeometricObject obj = GeometricObject.createLine(position,  direction, isIPNS,
                                               squaredWeight, Sign.POSITIVE, 2);
        PgaMvValue mv = PgaMvValue.compose(obj);
        test(obj, mv);
    }
    
    //@Test
    public void testPlane(){
        double[] position = new double[]{2d, 3d, 4d};
        double[] direction = new double[]{0,0,1};
        double squaredWeight = 1d;
        boolean isIPNS = true;                   
        GeometricObject obj = GeometricObject.createPlane(isIPNS,  direction, position, 
                                               squaredWeight, Sign.POSITIVE, 1);
        System.out.println(obj.toString());
        PgaMvValue mv = PgaMvValue.compose(obj);
        //System.out.println("testIPNSPlane: mv="+mv.toString());
        test(obj, mv);
    } 
    
    private void test(GeometricObject obj, PgaMvValue mv){
        GeometricObject obj2 = mv.decompose(obj.isIPNS());
        System.out.println(obj2.toString());
        assertEquals(obj, obj2);
    }
}
