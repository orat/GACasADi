package de.orat.math.gacasadi.specific.cga;

import de.orat.math.gacalc.util.GeometricObject;
import de.orat.math.gacalc.util.GeometricObject.Sign;
import de.orat.math.gacalc.util.Tuple;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

/**
 * @author Oliver Rettig (Oliver.Rettig@orat.de)
 */
public class CGADecomposeTest {

    public CGADecomposeTest() {
    }

    @Test
    public void testIPNSSphere(){
        double[] position = new double[]{1d, 2d, 3d};
        Tuple probePoint = new Tuple(new double[]{0,0,0});
        double radius = 2d;
        double squaredWeight = 1d;
        boolean isIPNS = true;
        GeometricObject obj = GeometricObject.createSphere(position,  isIPNS, radius, 
                                               squaredWeight, Sign.POSITIVE, 1);
        System.out.println(obj.toString());
        // SPHERE_IPNS{REAL, location=(1.0, 2.0, 3.0), squaredSize=4.0, squaredWeight=1.0,  grade=1}
        CgaMvValue mv = CgaMvValue.compose(obj);
        
        test(obj, mv);
        // SPHERE_IPNS{REAL, location=(1.0, 2.0, 3.0), squaredSize=4.0, squaredWeight=-1.0,  grade=1}
    }
    @Test
    public void testIPNSRealCircle(){
        double[] position = new double[]{1d, 2d, 3d};
        double[] direction = new double[]{0,0,1};
        double squaredRadius = 4d;
        double squaredWeight = 1d;
        boolean isIPNS = true;
                              
        GeometricObject obj = GeometricObject.createCircle(isIPNS, true, position,  direction, squaredRadius, 
                                               squaredWeight, Sign.POSITIVE, 2);
        System.out.println(obj.toString());
        // CIRCLE{REAL, attitude=(0.0, 0.0, 1.0), location=(1.0, 2.0, 3.0), squaredSize=4.0, squaredWeight=1.0,  grade=2}
        CgaMvValue mv = CgaMvValue.compose(obj);
        test(obj, mv);
        //<CIRCLE_IPNS{REAL,      attitude=(0.0, 0.0, 1.0), location=(1.0, 2.0, 3.0), squaredSize=4.0, squaredWeight=1.0,  grade=2}>
        //<CIRCLE_IPNS{IMAGINARY, attitude=(0.0, 0.0, 1.0), location=(1.0, 2.0, 0.0), squaredSize=-5.0, squaredWeight=1.0,  grade=2}>
    }
    
    // funktioniert nicht mehr, attitude hat falsches Vorzeichen, 
    //@Test
    public void testIPNSOrientedPoint(){
        double[] position = new double[]{1d, 2d, 3d};
        double[] direction = new double[]{0,0,1};
        double squaredWeight = 1d;
        boolean isIPNS = true;
                              
        GeometricObject obj = GeometricObject.createOrientedPoint(isIPNS, true, position,  
                                 direction, squaredWeight, Sign.POSITIVE, 2);
        System.out.println(obj.toString());
        CgaMvValue mv = CgaMvValue.compose(obj);
        test(obj, mv);
    }
    
    @Test
    public void testIPNSLine(){
        double[] position = new double[]{1d, 2d, 3d};
        double[] direction = new double[]{4,-1,1};// new double[]{0,0,1};
        double squaredWeight = 1d;
        boolean isIPNS = true;
        GeometricObject obj = GeometricObject.createLine(position,  direction, isIPNS,
                                               squaredWeight, Sign.POSITIVE, 2);
        CgaMvValue mv = CgaMvValue.compose(obj);
        test(obj, mv);
    }
    
    @Test
    public void testIPNSPlane(){
        double[] position = new double[]{2d, 3d, 4d};
        double[] direction = new double[]{0,0,1};
        double squaredWeight = 1d;
        boolean isIPNS = true;                   
        GeometricObject obj = GeometricObject.createPlane(isIPNS,  direction, position, 
                                               squaredWeight, Sign.POSITIVE, 1);
        System.out.println(obj.toString());
        CgaMvValue mv = CgaMvValue.compose(obj);
        //System.out.println("testIPNSPlane: mv="+mv.toString());
        test(obj, mv);
    }
    
    @Test
    public void testIPNSRoundPoint(){
        double[] position = new double[]{1d, 2d, 3d};
        boolean isIPNS = true;
        double squaredWeight = 1d;
        GeometricObject obj = GeometricObject.createRoundPoint(position, isIPNS,
                                               squaredWeight, Sign.POSITIVE, 1);
        System.out.println(obj.toString());
        CgaMvValue mv = CgaMvValue.compose(obj);
        test(obj, mv);
    } 
    
    // attitude war schon vorher falsch
    //@Test
    public void testIPNSFlatPoint(){
        double[] position = new double[]{1d, 2d, 3d};
        double[] direction = new double[]{0,0,1};
        double squaredWeight = 1d;
        boolean isIPNS = true;
       
        GeometricObject obj = GeometricObject.createFlatPoint(isIPNS, position,  direction, 
                                               squaredWeight, Sign.POSITIVE, 3);
        System.out.println(obj.toString());
        CgaMvValue mv = CgaMvValue.compose(obj);
        // <FLAT_POINT_IPNS{REAL, attitude=(0.0, 0.0, 1.0), location=(1.0, 2.0, 3.0), squaredSize=0.0, squaredWeight=1.0,  grade=3}> 
        // but was: 
        // <FLAT_POINT_IPNS{REAL, attitude=(0.0, 0.0, 0.0), location=(1.0, 2.0, 3.0), squaredSize=0.0, squaredWeight=1.0,  grade=3}>
	
        test(obj, mv);
    }
        
    //@Test
    public void testIPNSDipole(){
        double[] position1 = new double[]{1d, 2d, 3d};
        double[] position2 = new double[]{2d, 3d, 4d};
        double squaredWeight = 1d;
        boolean isIPNS = true;
        boolean isReal = true;
                                            
        GeometricObject obj = GeometricObject.createDipole(isIPNS, isReal, position1,  position2, 
                                               squaredWeight,  Sign.POSITIVE, 3);
        
        System.out.println(obj.toString());
        CgaMvValue mv = CgaMvValue.compose(obj);
        System.out.println("mv="+mv.toString());
        // mv=[00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, nan, nan, nan, nan, -0.57735, nan, nan, 1.1547, -0.57735, 00, 00, 00, 00, 00, 00]
        //FIXME Warum stehen hier NaNs drin
        test(obj, mv);
        // <DIPOLE_IPNS{REAL, attitude=(0.5773502691896258, 0.5773502691896258, 0.5773502691896258), location=(1.0, 2.0, 3.0), location2=(2.0, 3.0, 4.0), squaredSize=NaN, squaredWeight=1.0,  grade=3}> 
        // but was: 
        // <DIPOLE_IPNS{REAL, attitude=(NaN, NaN, NaN), location=(NaN, NaN, 0.0), location2=(NaN, NaN, 0.0), squaredSize=NaN, squaredWeight=0.0,  grade=3}>
    }
       
    
    private void test(GeometricObject obj, CgaMvValue mv){
        GeometricObject obj2 = mv.decompose(obj.isIPNS());
        System.out.println(obj2.toString());
        assertEquals(obj, obj2);
    }
}
