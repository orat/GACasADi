package de.orat.math.gacasadi.specific.pga;

import de.dhbw.rahmlab.casadi.SxStatic;
import static de.dhbw.rahmlab.casadi.api.Util.toStdVectorDouble;
import de.dhbw.rahmlab.casadi.impl.casadi.DM;
import de.dhbw.rahmlab.casadi.impl.casadi.Sparsity;
import de.orat.math.gacalc.api.MultivectorValue;
import de.orat.math.gacalc.spi.IMultivectorValue;
import de.orat.math.gacalc.util.GeometricObject;
import de.orat.math.gacalc.util.Tuple;
import de.orat.math.gacasadi.algebraGeneric.api.IAlgebra;
import de.orat.math.gacasadi.delegating.annotation.api.GenerateDelegate;
import de.orat.math.gacasadi.generic.CasADiUtil;
import static de.orat.math.gacasadi.generic.CasADiUtil.toCasADiSparsity;
import de.orat.math.gacasadi.generic.ComposableImmutableBinaryTree;
import de.orat.math.gacasadi.generic.IGaMvValue;
import de.orat.math.gacasadi.generic.IGetSparsityCasadi;
import de.orat.math.gacasadi.specific.cga.CgaCasADiUtil;
import de.orat.math.gacasadi.specific.pga.gen.DelegatingPgaMvValue;
import de.orat.math.sparsematrix.ColumnVectorSparsity;
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
    
     /**
     * Creates a leaf. Only to be used by static create Method with DM input.
     */
    private PgaMvValue(DM dm) {
        super(dmToPureSym(dm));
        this.lazyDM = dm;
        // Not "leaking this", because the passed "this" will not be used before fully constructed.
        // Because ComposableImmutableBinaryTree instance just stores the "this" and is itself not visible from outside the constructor.
        this.inputs = new ComposableImmutableBinaryTree<>(this);
    }
    
    private static int num = 0;

    private static PgaMvVariable dmToPureSym(DM dm) {
        var nameSym = String.format("x%s", String.valueOf(num));
        ++num;
        var pureSym = new PgaMvVariable(nameSym, dm.sparsity());
        return pureSym;
    }

    public PgaConstantsValue constants() {
        return PgaFactory.instance.constantsValue();
    }

    public static PgaMvValue create(DM dm) {
        return new PgaMvValue(dm);
    }
   
    public static PgaMvValue create(double scalar) {
        //CGAMultivectorSparsity sparsity = new CGAMultivectorSparsity(new int[]{0});
        ColumnVectorSparsity sparsity = CasADiUtil.determineSparsity(0, PgaFactory.instance.getIAlgebra());
        SparseDoubleMatrix sdm = new SparseDoubleMatrix(sparsity, new double[]{scalar});
        return create(sdm);
    }
    /**
     * Only to be used from DelegatingGaMvValue! Otherwise will lead to inconsistencies!
     */
    @Deprecated
    @Override
    protected PgaMvValue create(PgaMvExpr sym) {
        // Call permitted here.
        return new PgaMvValue(sym, this.inputs);
    }
    
    public static PgaMvValue create(SparseDoubleMatrix vec) {
        double[] nonzeros = vec.nonzeros();
        int[] rows = vec.getSparsity().getrow();
        int bladesCount = PgaFactory.instance.getIAlgebra().getBladesCount();
        if (bladesCount/*baseCayleyTable.getBladesCount()*/ < nonzeros.length) {
            throw new IllegalArgumentException("Construction of PGA multivevector failed because given array has wrong length "
                + String.valueOf(nonzeros.length));
        }
        if (nonzeros.length != rows.length) {
            throw new IllegalArgumentException("Construction of PGA multivector failed because nonzeros.length != rows.length!");
        }
        var dm = /*CgaCasADiUtil.*/toDM(bladesCount, nonzeros, rows);
        return create(dm);
    }

    // wohin damit, das ist algebra unabhängig
    //TODO
    private static DM toDM(int n_row, double[] nonzeros, int[] rows) {
        ColumnVectorSparsity sparsity = new ColumnVectorSparsity(n_row, rows);
        return new DM(toCasADiSparsity(sparsity), toStdVectorDouble(nonzeros), false);
    }
    
    /**
     * Only to be used from DelegatingGaMvValue! Otherwise will lead to inconsistencies!
     */
    @Deprecated
    @Override
    protected PgaMvValue create(PgaMvExpr sym, PgaMvValue other) {
        var combinedInputs = this.inputs.append(other.inputs);
        // Call permitted here.
        return new PgaMvValue(sym, combinedInputs);
    }

    
    private MultivectorValue.Callback callback;

    @Override
    public void init(MultivectorValue.Callback callback) {
        this.callback = callback;
    }

    @Override
    public SparseDoubleMatrix elements() {
         return CasADiUtil.elements(this.getDM());
    }

    // can be expensive
    @Override
    public PgaMvExpr toExpr() {
        var dm = this.getDM();
        var mv = PgaMvExpr.create(dm);
        return mv;
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
            case GeometricObject.GeometricType.POINT:
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
    /**
     * Get base vector indices.
     * 
     * @return  e0,e1,e2,e3
     */
    private static List<Integer> getBaseVectorIndizes(){
        List<Integer> result = new ArrayList();
        result.add(PgaFactory.instance.getIAlgebra().indexOfBlade("e0"));
        result.add(PgaFactory.instance.getIAlgebra().indexOfBlade("e1"));
        result.add(PgaFactory.instance.getIAlgebra().indexOfBlade("e2"));
        result.add(PgaFactory.instance.getIAlgebra().indexOfBlade("e3"));
        return result;
    }
    private static List<Integer> getBaseVectorIndizes2(){
        List<Integer> result = new ArrayList();
        result.add(PgaFactory.instance.getIAlgebra().indexOfBlade("e1"));
        result.add(PgaFactory.instance.getIAlgebra().indexOfBlade("e2"));
        result.add(PgaFactory.instance.getIAlgebra().indexOfBlade("e3"));
        result.add(PgaFactory.instance.getIAlgebra().indexOfBlade("e0"));
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
        // e0, e1, e2, e3
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
        switch (grade()){
            case 0:
                // scalar
                System.out.println("Scalar (grade 0) found: "+toString()+
                                   ". No geometric object for visualization available!");
                return null;
                
            // plane
            case 1:
                Tuple[] attitudeAndLocation = decomposePlane();
                //TODO
                double squaredWeight = 0d;
                return new GeometricObject(GeometricObject.GeometricType.PLANE, isIPNS,
                        attitudeAndLocation[0], attitudeAndLocation[1],
                        true, squaredWeight, GeometricObject.Sign.UNKNOWN, grade());
                
            // line
            case 2:
                attitudeAndLocation = decomposePlucker(decomposeLine());
                squaredWeight = 0d; // ToDo squaredWeight as func. of attitude?
                return new GeometricObject(GeometricObject.GeometricType.LINE, isIPNS,
                        attitudeAndLocation[0], 
                        attitudeAndLocation[1],
                        true, squaredWeight, GeometricObject.Sign.UNKNOWN, grade());
                
            // point 
            case 3:
                //TODO
                squaredWeight = 0d;
                return new GeometricObject(GeometricObject.GeometricType.POINT, isIPNS, null, 
                        decomposePoint(),
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
    
    /**
     * Decompose plane.
     * 
     * @return attitude (normal vector of the plane), location (projection of the origin into the plane)
     */
    private Tuple[] decomposePlane(){
        Tuple[] result = new Tuple[2];
        // e1,e2,e3,e0
        List<Integer> indices = getBaseVectorIndizes2();
        // a*1e1 + b*1e2 + c*1e3 + d*1e0
        Double[] abcd = new Double[4];
        get(indices).toArray(abcd);
        result[0] = new Tuple(abcd);
        // (-D/n^2)n_vec
        double[] p = new double[3];
        double fac = -abcd[3]/(abcd[0]*abcd[0]+abcd[1]*abcd[1]+abcd[2]*abcd[2]);
        p[0] = abcd[0]*fac;
        p[1] = abcd[1]*fac;
        p[2] = abcd[2]*fac;
        result[1] = new Tuple(p);
        return result;
    }

    private Tuple decomposePoint(){
        // dual(1e0 + xe1 + ye2 + ze3)
        Double[] p = new Double[4];
        // e0,e1,e2,e3
        undual().get(getBaseVectorIndizes()).toArray(p);
        //TODO
        // squaredWeight mit rausziehen und irgendwie weiterreichen
        return new Tuple(new double[]{p[1], p[2],p[3]});
    }
    
    /**
     * Decompose this pga multivector value into a tuple representing plucker coordinates of a line.
     * 
     * @return plucker coordinates
     */
    private Tuple decomposeLine(){
        if (grade() != 2) throw new IllegalArgumentException("Lines are of grade 2!");
        Double[] result = new Double[6];
        get(pluckerIndices()).toArray(result);
        return new Tuple(result);
    }
    
    // plucker: attitude, moment
    private static Tuple[] decomposePlucker(Tuple plucker){
        Tuple[] result = new Tuple[2]; // attitude, location
        
        // attitude, u
        result[0] = new Tuple(new double[]{plucker.values[0], plucker.values[1], plucker.values[2]});
        
        // wenn das Moment m == 0 dann geht die Gerade durch den Ursprung
        //TODO eventuell nicht auf exakte Gleichheit testen sondern auf einen Mindestabstand?
        if (plucker.values[3] == 0 && plucker.values[4] == 0 && plucker.values[5] == 0) 
            result[1] = new Tuple(new double[]{0d,0d,0d});
        else {
            // (u x m)/u^2
            double u2 = plucker.values[0]*plucker.values[0]+ 
                plucker.values[1]*plucker.values[1] + plucker.values[2]*plucker.values[2];
            double[] p = new double[3];
            // cross product: y1z2-z1y2, z1x2-x1z2, x1y2-y1x2
            p[0] = (plucker.values[1]*plucker.values[5] - plucker.values[2]*plucker.values[4])/u2;
            p[1] = (plucker.values[2]*plucker.values[3] - plucker.values[0]*plucker.values[5])/u2;
            p[2] = (plucker.values[0]*plucker.values[4] - plucker.values[1]*plucker.values[3])/u2;
            result[1] = new Tuple(p);
        }
        return result;
    }
    
    @Override
    public boolean isNull(double precision) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    
    /**
     * Nullable!
     */
    private DM lazyDM = null;
    
    /**
     * Expensive for MVnum, which are not created directly from a numerical constructor, but through method
     * chaining.
     */
    public DM getDM() {
        if (this.lazyDM == null) {
            var allInputs = this.inputs.computeUniqueLeafs().stream().toList();
            var allInputsParams = allInputs.stream().map(PgaMvValue::delegatePurelySym).toList();
            var func = PgaFactory.instance.createFunction("getDM", allInputsParams, List.of(this.delegate));
            var evalMV = func.callValue(allInputs).get(0);
            // lazyDM is non-null for all leafs.
            this.lazyDM = evalMV.lazyDM;
        }
        return lazyDM;
    }

    
    /**
     * Only works on MVnum which were constructed from a DM.
     */
    private PgaMvVariable delegatePurelySym() {
        return (PgaMvVariable) super.delegate;
    }

    public static PgaMvValue createFrom(PgaMvExpr sym) {
        /*
         * https://github.com/casadi/casadi/wiki/L_rf
         * Evaluates the expression numerically.
         * An error is raised when the expression contains symbols.
         */
        var dm = SxStatic.evalf(sym.getSX());
        return create(dm);
    }
}
