package de.orat.math.gacasadi.specific.cga;

import com.google.auto.service.AutoService;
import de.dhbw.rahmlab.casadi.impl.casadi.DM;
import de.dhbw.rahmlab.casadi.impl.casadi.SX;
import de.orat.math.gacalc.spi.IGAFactory;
import de.orat.math.gacasadi.algebraGeneric.api.IAlgebra;
import de.orat.math.gacasadi.algebraGeneric.impl.gaalop.GaalopAlgebra;
import de.orat.math.gacasadi.generic.GaFactory;
import de.orat.math.gacasadi.generic.GaFunction;
import de.orat.math.gacasadi.generic.GaLoopService;
import de.orat.math.gacasadi.specific.cga.gen.CachedCgaMvExpr;
import de.orat.math.sparsematrix.ColumnVectorSparsity;
import de.orat.math.sparsematrix.MatrixSparsity;
import de.orat.math.sparsematrix.SparseDoubleColumnVector;
import de.orat.math.sparsematrix.SparseDoubleMatrix;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import util.cga.CGACayleyTable;
import util.cga.CGACayleyTableGeometricProduct;
import util.cga.CGAMultivectorSparsity;

@AutoService(IGAFactory.class)
public class CgaFactory extends GaFactory<CgaMvExpr, CachedCgaMvExpr, CgaMvVariable, CgaMvValue> {

    /**
     * Needs to be public in order to make ServiceLoader work.
     */
    public CgaFactory() {

    }

    // cga_2 hat den Basiswechsel nicht und hat dadurch das gleiche gp wie vorherige Implementierung.
    protected final IAlgebra alDef = new GaalopAlgebra("cga_2");
    protected final Optional<Path> alLibFile = ((GaalopAlgebra) alDef).algebraLibFile;

    private static final CGACayleyTableGeometricProduct baseCayleyTable = CGACayleyTableGeometricProduct.instance();

    public static final CgaFactory instance = new CgaFactory();

    @Override
    public Optional<Path> getAlgebraLibFile() {
        return alLibFile;
    }

    /**
     * TODO: With the current implementation, they might depend on the specific definition of cga used.
     */
    private Map<String, CgaMvExpr> createConstants() {
        var map = new HashMap<String, CgaMvExpr>();

        map.put("ε₀", createValue(createBaseVectorOrigin(1d)).toExpr());
        map.put("εᵢ", createValue(createBaseVectorInfinity(1d)).toExpr());
        map.put("ε₁", createValue(createBaseVectorX(1d)).toExpr());
        map.put("ε₂", createValue(createBaseVectorY(1d)).toExpr());
        map.put("ε₃", createValue(createBaseVectorZ(1d)).toExpr());
        map.put("ε₊", createValue(createEpsilonPlus()).toExpr());
        map.put("ε₋", createValue(createEpsilonMinus()).toExpr());
        map.put("π", createValue(createScalar(Math.PI)).toExpr());
        map.put("∞", createValue(createBaseVectorInfinityDorst()).toExpr());
        map.put("o", createValue(createBaseVectorOriginDorst()).toExpr());
        map.put("n", createValue(createBaseVectorInfinityDoran()).toExpr());
        map.put("ñ", createValue(createBaseVectorOriginDoran()).toExpr());
        map.put("E₀", createValue(createMinkovskiBiVector()).toExpr());
        map.put("E₃", createValue(createEuclideanPseudoscalar()).toExpr());
        map.put("E", createValue(createPseudoscalar()).toExpr());

        return map;
    }

    public Map<String, CgaMvExpr> constants = null;

    @Override
    public Map<String, CgaMvExpr> getConstants() {
        if (constants == null) {
            constants = createConstants();
        }
        return constants;
    }

    // create symbolic multivectors
    @Override
    public CgaMvVariable createVariable(String name, CgaMvExpr from) {
        return new CgaMvVariable(name, from);
    }

    @Override
    public CgaMvVariable createVariable(String name, MatrixSparsity sparsity) {
        return CgaMvExpr.create(name, ColumnVectorSparsity.instance(sparsity));
    }

    @Override
    public CgaMvVariable createVariable(String name, int grade) {
        return CgaMvExpr.create(name, grade);
    }

    @Override
    public CgaMvVariable createVariable(String name, int[] grades) {
        return CgaMvExpr.create(name, grades);
    }

    @Override
    public CgaMvVariable createVariableSparse(String name) {
        return CgaMvVariable.createSparse(name);
    }

    @Override
    public CgaMvVariable createVariableDense(String name) {
        return CgaMvVariable.createDense(name);
    }

    // create numeric multivectors
    @Override
    public CgaMvValue createValue(double scalar) {
        return CgaMvValue.create(scalar);
    }

    @Override
    public CgaMvValue createValue(SparseDoubleMatrix vec) {
        return CgaMvValue.create(vec);
    }

    // create function
    @Override
    public GaFunction<CgaMvExpr, CgaMvValue> createFunction(String name,
        List<? extends CgaMvVariable> parameters,
        List<? extends CgaMvExpr> returns) {
        return new GaFunction<>(this, name, parameters, returns);
    }

    // methods to describe the functionality of the implementation
    @Override
    public String getAlgebra() {
        return "cga";
    }

    @Override
    public String getImplementationName() {
        return "cgacasadisx";
    }

    private final GaLoopService<CgaMvExpr, CgaMvVariable> loopService = new GaLoopService<>(this);

    @Override
    public GaLoopService<CgaMvExpr, CgaMvVariable> getLoopService() {
        return this.loopService;
    }

    // random multivectors
    @Override
    public CgaMvValue createValueRandom() {
        final int basisBladesCount = this.alDef.getBladesCount();
        double[] result = new Random().doubles(-1, 1).limit(basisBladesCount).toArray();
        var sdm = new SparseDoubleColumnVector(ColumnVectorSparsity.dense(basisBladesCount), result);
        var val = createValue(sdm);
        return val;
    }

    @Override
    public CgaMvValue createValueRandom(int[] grades) {
        Random random = new Random();
        int[] indizes = CGACayleyTableGeometricProduct.getIndizes(grades);
        double[] values = random.doubles(-1, 1).limit(indizes.length).toArray();
        var sparsity = new CGAMultivectorSparsity(indizes);
        var sdm = new SparseDoubleColumnVector(sparsity, values);
        var val = createValue(sdm);
        return val;
    }

    // create constants
    // based on e4e5
    protected static SparseDoubleMatrix createBaseVectorOrigin(double scalar) {
        double[] nonzeros = new double[]{-0.5d * scalar, 0.5d * scalar};
        int[] rows = new int[]{4, 5};
        CGAMultivectorSparsity sparsity = new CGAMultivectorSparsity(rows);
        return new SparseDoubleMatrix(sparsity, nonzeros);
    }

    protected static SparseDoubleMatrix createBaseVectorInfinity(double scalar) {
        double[] nonzeros = new double[]{scalar, scalar};
        int[] rows = new int[]{4, 5};
        CGAMultivectorSparsity sparsity = new CGAMultivectorSparsity(rows);
        return new SparseDoubleMatrix(sparsity, nonzeros);
    }

    protected static SparseDoubleMatrix createBaseVectorX(double scalar) {
        double[] nonzeros = new double[]{scalar};
        int[] rows = new int[]{1};
        CGAMultivectorSparsity sparsity = new CGAMultivectorSparsity(rows);
        return new /*SparseCGAColumnVector*/ SparseDoubleMatrix(sparsity, nonzeros);
    }

    protected static SparseDoubleMatrix createBaseVectorY(double scalar) {
        double[] nonzeros = new double[]{scalar};
        int[] rows = new int[]{2};
        CGAMultivectorSparsity sparsity = new CGAMultivectorSparsity(rows);
        return new /*SparseCGAColumnVector*/ SparseDoubleMatrix(sparsity, nonzeros);
    }

    protected static SparseDoubleMatrix createBaseVectorZ(double scalar) {
        double[] nonzeros = new double[]{scalar};
        int[] rows = new int[]{3};
        CGAMultivectorSparsity sparsity = new CGAMultivectorSparsity(rows);
        return new /*SparseCGAColumnVector*/ SparseDoubleMatrix(sparsity, nonzeros);
    }

    protected static SparseDoubleMatrix createScalar(double scalar) {
        CGAMultivectorSparsity sparsity = new CGAMultivectorSparsity(new int[]{0});
        return new /*SparseCGAColumnVector*/ SparseDoubleMatrix(sparsity, new double[]{scalar});
    }

    protected static SparseDoubleMatrix createEpsilonPlus() {
        CGAMultivectorSparsity sparsity = new CGAMultivectorSparsity(new int[]{4, 5});
        return new SparseDoubleMatrix(sparsity, new double[]{1d, 0d});
    }

    protected static SparseDoubleMatrix createEpsilonMinus() {
        CGAMultivectorSparsity sparsity = new CGAMultivectorSparsity(new int[]{4, 5});
        return new SparseDoubleMatrix(sparsity, new double[]{0d, 1d});
    }

    protected static SparseDoubleMatrix createEuclideanPseudoscalar() {
        CGAMultivectorSparsity sparsity = new CGAMultivectorSparsity(new int[]{16});
        return new SparseDoubleMatrix(sparsity, new double[]{1d});
    }

    protected SparseDoubleMatrix createPseudoscalar() {
        CGAMultivectorSparsity sparsity = new CGAMultivectorSparsity(new int[]{this.alDef.getBladesCount() - 1});
        return new SparseDoubleMatrix(sparsity, new double[]{1d});
    }

    //TODO
    // In Gameron steht aber pseudoscalar().reverse()/(pseudoscalar left contraction pseudoscalar().reverse())
    // vielleicht ist das die Impl. die unabhängig von ga model ist und die impl hier
    // geht nur für CGA?
    protected SparseDoubleMatrix createInversePseudoscalar() {
        return CgaFactory.instance.createValue(createPseudoscalar()).reverse().elements();
    }

    protected static int getMikovskiBivectorIndex() {
        return 15;
    }

    /**
     * Minkovski Bivector.
     *
     * This is the flat point origin, corresponding to einf^e0=e4^e5.
     *
     * @return
     */
    protected static SparseDoubleMatrix createMinkovskiBiVector() {
        CGAMultivectorSparsity sparsity = new CGAMultivectorSparsity(new int[]{getMikovskiBivectorIndex()});
        return new SparseDoubleMatrix(sparsity, new double[]{2d});
    }

    protected static SparseDoubleMatrix createE(double x, double y, double z) {
        double[] nonzeros = new double[]{x, y, z};
        int[] rows = new int[]{1, 2, 3};
        CGAMultivectorSparsity sparsity = new CGAMultivectorSparsity(rows);
        return new SparseDoubleMatrix(sparsity, nonzeros);
    }

    // die folgenden Defs sind noch nicht überprüft
    protected static SparseDoubleMatrix createBaseVectorInfinityDorst() {
        double[] nonzeros = new double[]{-1d, 1d};
        int[] rows = new int[]{4, 5};
        CGAMultivectorSparsity sparsity = new CGAMultivectorSparsity(rows);
        return new SparseDoubleMatrix(sparsity, nonzeros);
    }

    protected static SparseDoubleMatrix createBaseVectorOriginDorst() {
        double[] nonzeros = new double[]{0.5d, 0.5d};
        int[] rows = new int[]{4, 5};
        CGAMultivectorSparsity sparsity = new CGAMultivectorSparsity(rows);
        return new SparseDoubleMatrix(sparsity, nonzeros);
    }

    protected static SparseDoubleMatrix createBaseVectorInfinityDoran() {
        double[] nonzeros = new double[]{1d, 1d};
        int[] rows = new int[]{4, 5};
        CGAMultivectorSparsity sparsity = new CGAMultivectorSparsity(rows);
        return new SparseDoubleMatrix(sparsity, nonzeros);
    }

    protected static SparseDoubleMatrix createBaseVectorOriginDoran() {
        double[] nonzeros = new double[]{1d, -1d};
        int[] rows = new int[]{4, 5};
        CGAMultivectorSparsity sparsity = new CGAMultivectorSparsity(rows);
        return new SparseDoubleMatrix(sparsity, nonzeros);
    }

    @Override
    protected CgaMvExpr SXtoEXPR(SX sx) {
        return CgaMvExpr.createFromSX(sx);
    }

    @Override
    protected CgaMvValue DMtoVAL(DM dm) {
        return CgaMvValue.create(dm);
    }

    @Override
    public CachedCgaMvExpr cachedEXPR(CgaMvExpr expr) {
        if (expr instanceof CachedCgaMvExpr cached) {
            return cached;
        }
        return new CachedCgaMvExpr(expr);
    }

    @Override
    public CgaMvVariable EXPRtoVAR(String name, CgaMvExpr from) {
        return createVariable(name, from);
    }

    @Override
    public IAlgebra getIAlgebra() {
        return this.alDef;
    }
}
