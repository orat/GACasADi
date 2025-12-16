package de.orat.math.gacasadi.specific.pga;

import de.dhbw.rahmlab.casadi.impl.casadi.DM;
import de.dhbw.rahmlab.casadi.impl.casadi.Sparsity;
import de.orat.math.gacalc.api.MultivectorValue;
import de.orat.math.gacalc.spi.IConstants;
import de.orat.math.gacalc.spi.IMultivectorValue;
import de.orat.math.gacalc.util.GeometricObject;
import de.orat.math.gacalc.util.Tuple;
import de.orat.math.gacasadi.delegating.annotation.api.GenerateDelegate;
import de.orat.math.gacasadi.generic.ComposableImmutableBinaryTree;
import de.orat.math.gacasadi.generic.IGaMvValue;
import de.orat.math.gacasadi.generic.IGetSparsityCasadi;
import de.orat.math.gacasadi.specific.cga.CgaMvValue;
import static de.orat.math.gacasadi.specific.cga.CgaMvValue.constants2;
import de.orat.math.gacasadi.specific.pga.gen.DelegatingPgaMvValue;
import de.orat.math.sparsematrix.SparseDoubleMatrix;
import java.util.ArrayList;
import java.util.List;

@GenerateDelegate(to = PgaMvExpr.class)
public class PgaMvValue extends DelegatingPgaMvValue implements IGaMvValue<PgaMvValue, PgaMvExpr>, IMultivectorValue<PgaMvValue, PgaMvExpr>, IGetSparsityCasadi {

    private final ComposableImmutableBinaryTree<PgaMvValue> inputs;

    /**
     * Only to be used by non-static create Method for DelegatingGaMvValue.
     */
    @Deprecated
    private PgaMvValue(PgaMvExpr sym, ComposableImmutableBinaryTree<PgaMvValue> inputs) {
        super(sym);
        this.inputs = inputs;
    }

    @Override
    public IConstants<PgaMvValue> constants() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected PgaMvValue create(PgaMvExpr delegate) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    protected PgaMvValue create(PgaMvExpr delegate, PgaMvValue other) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public DM getDM() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void init(MultivectorValue.Callback callback) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public SparseDoubleMatrix elements() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public PgaMvExpr toExpr() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public static PgaMvValue compose(GeometricObject obj) {
        PgaMvValue result = null;
        switch (obj.geometricType){
            case GeometricObject.GeometricType.PLANE:
                result = createPlane(obj);
                break;
            case GeometricObject.GeometricType.LINE:
                result = createLine(obj);
                break;
            case GeometricObject.GeometricType.SCREW:
                throw new RuntimeException("not yet implemented!");
            case GeometricObject.GeometricType.ROUND_POINT:
                result = createPoint(obj);
                break;
            default:
                throw new RuntimeException("Composition of the given type is not yet supported!");
        }
        if (!obj.isIPNS()){ 
            result = result.dual();
        }    
        return result;
    }
    
    /**
     * Create a point as a grade-3 trivector.
     * 
     * @param location
     * @param signedWeight
     * @return 
     */
    private static PgaMvValue createPoint(Tuple location, double signedWeight) {
        // dual(1e0 + xe1 + ye2 + ze3)
        List<Double> values = new ArrayList<>();
        values.add(1d);
        values.add(location.values[0]);
        values.add(location.values[1]);
        values.add(location.values[2]);
        PgaMvValue mv = PgaFactory.instance.create(getBaseVectorIndizes(), values);
        //TODO signedWeight hineinbekommen
        //mv.gpWithScalar(signedWeight);
        return mv.dual();
    }
    private static List<Integer> getBaseVectorIndizes(){
        List<Integer> result = new ArrayList();
        result.add(PgaFactory.instance.getIAlgebra().indexOfBlade("e0"));
        result.add(PgaFactory.instance.getIAlgebra().indexOfBlade("e1"));
        result.add(PgaFactory.instance.getIAlgebra().indexOfBlade("e2"));
        result.add(PgaFactory.instance.getIAlgebra().indexOfBlade("e3"));
        return result;
    }
    private static int getE0Index(){
        return PgaFactory.instance.getIAlgebra().indexOfBlade("e0");
    }
    private static PgaMvValue createPoint(GeometricObject obj) {
        return createPoint(obj.location[0], obj.getSignedWeight());
    }
    
    public static PgaMvValue createPlane(Tuple abcd){
        // a*1e1 + b*1e2 + c*1e3 + d*1e0
        List<Double> values = new ArrayList<>();
        values.add(abcd.values[3]);
        values.add(abcd.values[0]);
        values.add(abcd.values[1]);
        values.add(abcd.values[2]);
        return  PgaFactory.instance.create(getBaseVectorIndizes(), values);
    }
    private static PgaMvValue createPlane(GeometricObject obj) {
        //TODO 
        // aus location und attitude die 3 Parameter a,b,c,d beschaffen
        throw new RuntimeException("not yet implemented!");
    }
    private static List<Integer> pluckerIndices(){
        List<Integer> indices = new ArrayList();
        indices.add(PgaFactory.instance.getIAlgebra().indexOfBlade("e0","e1"));
        indices.add(PgaFactory.instance.getIAlgebra().indexOfBlade("e0","e2"));
        indices.add(PgaFactory.instance.getIAlgebra().indexOfBlade("e0","e3"));
        indices.add(PgaFactory.instance.getIAlgebra().indexOfBlade("e1","e2"));
        indices.add(PgaFactory.instance.getIAlgebra().indexOfBlade("e1","e3"));
        indices.add(PgaFactory.instance.getIAlgebra().indexOfBlade("e2","e3"));
        return indices;
    }
    private static PgaMvValue createLine(Tuple plucker){
        // TODO 6 plucker coordinates an als coefficienten der entsprechenden Blades setzen
        // 1e01, 1e02, 1e03, 1e12, 1e13, 1e23
        
        List<Double> values = new ArrayList<>();
        values.add(plucker.values[0]);
        values.add(plucker.values[1]);
        values.add(plucker.values[2]);
        values.add(plucker.values[3]);
        values.add(plucker.values[4]);
        values.add(plucker.values[5]);
        return  PgaFactory.instance.create(pluckerIndices(), values);
    }
    private static PgaMvValue createLine(GeometricObject obj){
        //TODO
        // aus location und attitude die Plucker coordinates beschaffen
        throw new RuntimeException("not yet implemented!");
    }
    @Override
    public GeometricObject decompose(boolean isIPNS) {
        
        //PgaMvValue probePoint; // = constants2().getBaseVectorOrigin();
        //TODO
        Tuple probePoint = null;
        //probePoint = createPoint(new Tuple(new double[]{0,0,0}), 1d);
        switch (grade()){
            case 0:
                // scalar
                System.out.println("Scalar (grade 0) found: "+toString()+
                                   ". No geometric object for visualization available!");
                return null;
                
            // plane
            case 1:
                //return decomposePlane(true, probePoint);
                //TODO
                Tuple attitude = null;
                Tuple location = null;
                double squaredWeight = 0d;
                return new GeometricObject(GeometricObject.GeometricType.PLANE, isIPNS,
                        attitude, location,
                        true, squaredWeight, GeometricObject.Sign.UNKNOWN, grade());
                
            // line
            case 2:
                //this.elements()
                Tuple plucker =  decomposeLine();
                Tuple[] attitudeAndLocation = decomposePlucker(plucker, probePoint);
                squaredWeight = 0d; // ToDo func. of attitude?
                return new GeometricObject(GeometricObject.GeometricType.LINE, isIPNS,
                        attitudeAndLocation[0], 
                        attitudeAndLocation[1],
                        true, squaredWeight, GeometricObject.Sign.UNKNOWN, grade());
                
            // point 
            case 3:
                //TODO
                location = null;
                squaredWeight = 0d;
                return new GeometricObject(GeometricObject.GeometricType.ROUND_POINT, isIPNS, null, 
                        location,
                        0d, squaredWeight,
                        GeometricObject.Sign.UNKNOWN, grade());
                 
            case 4:
                // pseudo scalar
                System.out.println("Pseudoscalar (grade 4) found: "+toString()+
                                   ". No geometric object for visualization available!");
                return null;
            default:
                System.out.println("Illegal object of unknown grade found: "+toString());
        }
        return null;
    }

    private Tuple decomposeLine(){
        /*if (grade() != 2) throw new IllegalArgumentException("Lines are of grade 2!");
        List<Integer> indices = pluckerIndices();
        for (int i)
        double value = getDM().at(i, 0).scalar();
        */
        throw new RuntimeException("not yet implemeted!");
        
    }
    // plucker: attitude, moment
    private Tuple[] decomposePlucker(Tuple plucker, Tuple probePoint){
        Tuple[] result = new Tuple[2]; // attitude, location
        result[0] = new Tuple(new double[]{plucker.values[0], plucker.values[1], plucker.values[2]});
        // wenn das Moment == 0 dann geht die Gerade durch den Ursprung
        if (plucker.values[3] == 0 && plucker.values[4] == 0 && plucker.values[5] == 0) result[1] = new Tuple(
            new double[]{0d,0d,0d});
        else {
            // (u x m)/u
            //TODO
        }
        return result;
    }
    
    @Override
    public boolean isNull(double precision) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

}
