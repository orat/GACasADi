package de.orat.math.gacasadi.specific.pga;

import com.google.auto.service.AutoService;
import de.dhbw.rahmlab.casadi.impl.casadi.DM;
import de.dhbw.rahmlab.casadi.impl.casadi.SX;
import de.orat.math.gacalc.spi.IGAFactory;
import de.orat.math.gacalc.spi.ILoopService;
import de.orat.math.gacasadi.algebraGeneric.api.IAlgebra;
import de.orat.math.gacasadi.algebraGeneric.impl.gaalop.GaalopAlgebra;
import de.orat.math.gacasadi.generic.GaFactory;
import de.orat.math.gacasadi.generic.GaFunction;
import de.orat.math.gacasadi.generic.GaLoopService;
import de.orat.math.gacasadi.specific.pga.gen.CachedPgaMvExpr;
import de.orat.math.sparsematrix.ColumnVectorSparsity;
import de.orat.math.sparsematrix.MatrixSparsity;
import de.orat.math.sparsematrix.SparseDoubleMatrix;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@AutoService(IGAFactory.class)
public class PgaFactory extends GaFactory<PgaMvExpr, CachedPgaMvExpr, PgaMvVariable, PgaMvValue> {

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
    public CachedPgaMvExpr cachedEXPR(PgaMvExpr expr) {
        if (expr instanceof CachedPgaMvExpr cached) {
            return cached;
        }
        return new CachedPgaMvExpr(expr);
    }

    @Override
    public PgaMvVariable EXPRtoVAR(String name, PgaMvExpr from) {
       return createVariable(name, from);
    }

    // create function
    @Override
    public GaFunction<PgaMvExpr, PgaMvValue> createFunction(String name,
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

    @Override
    public int getBasisBladesCount() {
        return alDef.getBaseSize();
    }

    private final GaLoopService<PgaMvExpr, PgaMvVariable> loopService = new GaLoopService<>(this);

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
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public PgaMvVariable createVariableSparse(String name) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public PgaMvVariable createVariable(String name, int grade) {
        return PgaMvExpr.create(name, grade);
    }

    @Override
    public PgaMvVariable createVariable(String name, int[] grades) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public PgaMvValue createValue(SparseDoubleMatrix vec) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public PgaMvValue createValue(double scalar) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public PgaMvValue createValueRandom() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public PgaMvValue createValueRandom(int[] grades) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public SparseDoubleMatrix createE(double x, double y, double z) {
        int index1 = alDef.indexOfBlade("e1");
        int index2 = alDef.indexOfBlade("e2");
        int index3 = alDef.indexOfBlade("e3");
        ColumnVectorSparsity sparsity = new ColumnVectorSparsity(
            alDef.getBladesCount(), new int[]{index1, index2, index3});
        return new SparseDoubleMatrix(sparsity, new double[]{x, y, z});
    }

    public SparseDoubleMatrix createBaseVectorOrigin(double scalar) {
        //PGAMultivectorSparsity sparsity = new PGAMultivectorSparsity(rows);
        int index = alDef.indexOfBlade("e0");
        ColumnVectorSparsity sparsity = new ColumnVectorSparsity(alDef.getBladesCount(), new int[]{index});
        return new SparseDoubleMatrix(sparsity, new double[]{scalar});
    }

    public SparseDoubleMatrix createScalar(double scalar) {
        // the index of the scalar is 0 for all algebras
        ColumnVectorSparsity sparsity = new ColumnVectorSparsity(alDef.getBladesCount(), new int[]{0});
        return new SparseDoubleMatrix(sparsity, new double[]{scalar});
    }

    public SparseDoubleMatrix createBaseVectorX(double scalar) {
        int index = alDef.indexOfBlade("e1");
        ColumnVectorSparsity sparsity = new ColumnVectorSparsity(alDef.getBladesCount(), new int[]{index});
        return new SparseDoubleMatrix(sparsity, new double[]{scalar});
    }

    public SparseDoubleMatrix createBaseVectorY(double scalar) {
        int index = alDef.indexOfBlade("e2");
        ColumnVectorSparsity sparsity = new ColumnVectorSparsity(alDef.getBladesCount(), new int[]{index});
        return new SparseDoubleMatrix(sparsity, new double[]{scalar});
    }

    public SparseDoubleMatrix createBaseVectorZ(double scalar) {
        int index = alDef.indexOfBlade("e3");
        ColumnVectorSparsity sparsity = new ColumnVectorSparsity(alDef.getBladesCount(), new int[]{index});
        return new SparseDoubleMatrix(sparsity, new double[]{scalar});
    }

    public SparseDoubleMatrix createEuclideanPseudoscalar() {
        int index = alDef.indexOfBlade("e1","e2","e3");
        ColumnVectorSparsity sparsity = new ColumnVectorSparsity(alDef.getBladesCount(), new int[]{index});
        return new SparseDoubleMatrix(sparsity, new double[]{1d});
    }

    public SparseDoubleMatrix createPseudoscalar() {
        int index = alDef.indexOfBlade("e0","e1","e2","e3");
        ColumnVectorSparsity sparsity = new ColumnVectorSparsity(alDef.getBladesCount(), new int[]{index});
        return new SparseDoubleMatrix(sparsity, new double[]{1d});
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

        map.put("ε₀", createValue(createBaseVectorOrigin(1d)).toExpr());
        map.put("ε₁", createValue(createBaseVectorX(1d)).toExpr());
        map.put("ε₂", createValue(createBaseVectorY(1d)).toExpr());
        map.put("ε₃", createValue(createBaseVectorZ(1d)).toExpr());
        map.put("π", createValue(createScalar(Math.PI)).toExpr());
        map.put("E₃", createValue(createEuclideanPseudoscalar()).toExpr());
        map.put("E", createValue(createPseudoscalar()).toExpr());

        return map;
    }
}
