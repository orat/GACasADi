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
    public static PgaMvValue createPoint(Tuple location, double signedWeight) {
        // dual(1e0 + xe1 + ye2 + ze3)
        //PgaMvValue mv;
        //mv.dual();
        //TODO
        //PgaMvValue inf = constants2().getBaseVectorInfinity();
        //return o.add(c).add(inf.gpWithScalar(0.5*location.squaredNorm())).
        //    gpWithScalar(signedWeight);
        throw new RuntimeException("not yet implemented!");
    }
    private static PgaMvValue createPoint(GeometricObject obj) {
        return createPoint(obj.location[0], obj.getSignedWeight());
    }
    
    public static PgaMvValue createPlane(Tuple abcd){
        // a*1e1 + b*1e2 + c*1e3 + d*1e0
        //TODO
        throw new RuntimeException("not yet implemented!");
    }
    private static PgaMvValue createPlane(GeometricObject obj) {
        //TODO 
        // aus location und attitude die 3 Parameter a,b,c,d beschaffen
        throw new RuntimeException("not yet implemented!");
    }
    private static PgaMvValue createLine(Tuple plucker){
        // TODO 6 plucker coordinates an als coefficienten der entsprechenden Blades setzen
        // 1e01, 1e02, 1e03, 1e12, 1e13, 1e23
        throw new RuntimeException("not yet implemented!");
    }
    private static PgaMvValue createLine(GeometricObject obj){
        //TODO
        // aus location und attitude die Plucker coordinates beschaffen
        throw new RuntimeException("not yet implemented!");
    }
    @Override
    public GeometricObject decompose(boolean isIPNS) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public boolean isNull(double precision) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

}
