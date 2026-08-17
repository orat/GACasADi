package de.orat.math.gacasadi.specific.pga;

import com.google.auto.service.AutoService;
import de.dhbw.rahmlab.casadi.impl.casadi.DM;
import de.dhbw.rahmlab.casadi.impl.casadi.SX;
import de.orat.math.gacalc.spi.IGAFactory;
import de.orat.math.gacalc.spi.ILoopService;
import de.orat.math.gacasadi.algebraGeneric.api.IAlgebra;
import de.orat.math.gacasadi.algebraGeneric.impl.gaalop.GaalopAlgebra;
import de.orat.math.gacasadi.generic.CasADiUtil;
import de.orat.math.gacasadi.generic.GaFactory;
import de.orat.math.gacasadi.generic.GaFunction;
import de.orat.math.gacasadi.generic.GaLoopService;
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

@AutoService(IGAFactory.class)
public class PgaFactory extends GaFactory<PgaMvExpr, PgaMvVariable, PgaMvValue> {

    public final static PgaFactory instance = new PgaFactory();

    protected final IAlgebra alDef = new GaalopAlgebra("3dpga");
    protected final Optional<Path> alLibFile = ((GaalopAlgebra) alDef).algebraLibFile;

    /**
     * Needs to be public in order to make ServiceLoader work.
     */
    public PgaFactory() {

    }

    @Override
    public Optional<Path> getAlgebraLibFile() {
        return alLibFile;
    }

    @Override
    protected PgaMvExpr SXtoEXPR(SX sx) {
        return PgaMvExpr.createFromSX(sx);
    }

    @Override
    protected PgaMvValue DMtoVAL(DM dm) {
        return PgaMvValue.create(dm);
    }

    @Override
    public PgaMvVariable EXPRtoVAR(String name, PgaMvExpr from) {
        return createVariable(name, from);
    }

    // create function
    @Override
    public GaFunction<PgaMvExpr, PgaMvVariable, PgaMvValue> createFunction(String name,
        List<? extends PgaMvVariable> parameters,
        List<? extends PgaMvExpr> returns) {
        return new GaFunction<>(this, name, parameters, returns);
    }

    @Override
    public String getAlgebra() {
        return "pga";
    }

    @Override
    public String getImplementationName() {
        return "pgacasadisx";
    }

    private final GaLoopService<PgaMvExpr, PgaMvVariable, PgaMvValue> loopService = new GaLoopService<>(this);

    @Override
    public ILoopService getLoopService() {
        return this.loopService;
    }

    public PgaConstantsExpr constantsExpr() {
        return PgaConstantsExpr.instance;
    }

    public PgaConstantsValue constantsValue() {
        return PgaConstantsValue.instance;
    }

    @Override
    public PgaMvVariable createVariable(String name, PgaMvExpr from) {
        return new PgaMvVariable(name, from);
    }

    @Override
    public PgaMvVariable createVariable(String name, MatrixSparsity sparsity) {
        return PgaMvExpr.create(name, ColumnVectorSparsity.instance(sparsity));
    }

    @Override
    public PgaMvVariable createVariableDense(String name) {
        return PgaMvVariable.createDense(name);
    }

    @Override
    public PgaMvVariable createVariableSparse(String name) {
        return PgaMvVariable.createSparse(name);
    }

    @Override
    public PgaMvVariable createVariable(String name, int grade) {
        return PgaMvExpr.create(name, grade);
    }

    @Override
    public PgaMvVariable createVariable(String name, int[] grades) {
        return PgaMvExpr.create(name, grades);
    }

    @Override
    public PgaMvValue createValue(SparseDoubleMatrix vec) {
        return PgaMvValue.create(vec);
    }

    @Override
    public PgaMvValue createValue(double scalar) {
        return PgaMvValue.create(scalar);
    }

    @Override
    public PgaMvValue createValueRandom() {
        final int basisBladesCount = this.alDef.getBladesCount();
        double[] result = new Random().doubles(-1, 1).limit(basisBladesCount).toArray();
        var sdm = new SparseDoubleColumnVector(ColumnVectorSparsity.dense(basisBladesCount), result);
        var val = createValue(sdm);
        return val;
    }

    @Override
    public PgaMvValue createValueRandom(int[] grades) {
        Random random = new Random();
        int[] indizes = PgaFactory.instance.getIAlgebra().getIndizes(grades);
        double[] values = random.doubles(-1, 1).limit(indizes.length).toArray();
        var sparsity = CasADiUtil.determineSparsity(grades, PgaFactory.instance.getIAlgebra());
        //var sparsity = CasADiUtil.toColumnVectorSparsity(sxSparsity)new CGAMultivectorSparsity(indizes);
        var sdm = new SparseDoubleColumnVector(sparsity, values);
        var val = createValue(sdm);
        return val;
    }

    public PgaMvValue createE(double x, double y, double z) {
        int[] indices = super.baseVectorsToIndices("e1", "e2", "e3");
        double[] values = {x, y, z};
        return super.createValue(indices, values);
    }

    public PgaMvValue createBaseVectorOrigin() {
        int[] indices = super.baseVectorsToIndices("e0");
        double[] values = {1d};
        return super.createValue(indices, values);
    }

    public PgaMvValue createScalar(double scalar) {
        return createValue(scalar);
    }

    public PgaMvValue createBaseVectorX() {
        int[] indices = super.baseVectorsToIndices("e1");
        double[] values = {1d};
        return super.createValue(indices, values);
    }

    public PgaMvValue createBaseVectorY() {
        int[] indices = super.baseVectorsToIndices("e2");
        double[] values = {1d};
        return super.createValue(indices, values);
    }

    public PgaMvValue createBaseVectorZ() {
        int[] indices = super.baseVectorsToIndices("e3");
        double[] values = {1d};
        return super.createValue(indices, values);
    }

    public PgaMvValue createEuclideanPseudoscalar() {
        int[] indices = {this.alDef.indexOfBlade("e1", "e2", "e3")};
        double[] values = {1d};
        return super.createValue(indices, values);
    }

    public PgaMvValue createPseudoscalar() {
        int[] indices = {alDef.indexOfBlade("e0", "e1", "e2", "e3")};
        double[] values = {1d};
        return super.createValue(indices, values);
    }

    @Override
    public IAlgebra getIAlgebra() {
        return this.alDef;
    }

    public Map<String, PgaMvExpr> constants = null;

    @Override
    public Map<String, PgaMvExpr> getConstants() {
        if (constants == null) {
            constants = createConstants();
        }
        return constants;
    }

    private Map<String, PgaMvExpr> createConstants() {
        Map<String, PgaMvExpr> map = new HashMap<>();

        map.put("ε₀", createBaseVectorOrigin().toExpr());
        map.put("ε₁", createBaseVectorX().toExpr());
        map.put("ε₂", createBaseVectorY().toExpr());
        map.put("ε₃", createBaseVectorZ().toExpr());
        map.put("π", createScalar(Math.PI).toExpr());
        map.put("E₃", createEuclideanPseudoscalar().toExpr());
        map.put("I", createPseudoscalar().toExpr());

        return map;
    }
}
