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
import de.orat.math.sparsematrix.ColumnVectorSparsity;
import de.orat.math.sparsematrix.MatrixSparsity;
import de.orat.math.sparsematrix.SparseDoubleMatrix;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@AutoService(IGAFactory.class)
public class CgaFactory extends GaFactory<CgaMvExpr, CgaMvVariable, CgaMvValue> {

    /**
     * Needs to be public in order to make ServiceLoader work.
     */
    public CgaFactory() {

    }

    // cga_2 hat den Basiswechsel nicht und hat dadurch das gleiche gp wie vorherige Implementierung.
    protected final IAlgebra alDef = new GaalopAlgebra("cga_2");
    protected final Optional<Path> alLibFile = ((GaalopAlgebra) alDef).algebraLibFile;

    public static final CgaFactory instance = new CgaFactory();

    @Override
    public Optional<Path> getAlgebraLibFile() {
        return alLibFile;
    }

    private Map<String, CgaMvExpr> createConstants() {
        var map = new HashMap<String, CgaMvExpr>();

        map.put("ε₀", createBaseVectorOrigin().toExpr());
        map.put("εᵢ", createBaseVectorInfinity().toExpr());
        map.put("ε₁", createBaseVectorX().toExpr());
        map.put("ε₂", createBaseVectorY().toExpr());
        map.put("ε₃", createBaseVectorZ().toExpr());
        map.put("ε₊", createEpsilonPlus().toExpr());
        map.put("ε₋", createEpsilonMinus().toExpr());
        map.put("π", createScalar(Math.PI).toExpr());
        map.put("∞", createBaseVectorInfinityDorst().toExpr());
        map.put("o", createBaseVectorOriginDorst().toExpr());
        map.put("n", createBaseVectorInfinityDoran().toExpr());
        map.put("ñ", createBaseVectorOriginDoran().toExpr());
        map.put("E₀", createMinkovskiBiVector().toExpr());
        map.put("E₃", createEuclideanPseudoscalar().toExpr());
        map.put("I", createPseudoscalar().toExpr());

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
    public GaFunction<CgaMvExpr, CgaMvVariable, CgaMvValue> createFunction(String name,
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

    private final GaLoopService<CgaMvExpr, CgaMvVariable, CgaMvValue> loopService = new GaLoopService<>(this);

    @Override
    public GaLoopService<CgaMvExpr, CgaMvVariable, CgaMvValue> getLoopService() {
        return this.loopService;
    }

    /*
    public static void main(String[] args) {
        var fac = new CgaFactory();
        var al = fac.alDef;
        int[] values = {1, 2, 3, 4, 5, 15, 16};
        for (int value : values) {
            List<String> base = al.bladeOfBasevectorsFromIndex(value);
            System.out.println(String.format("%s: %s", value, base));
        }
    }
     */
    // create constants
    protected CgaMvValue createBaseVectorOrigin() {
        int[] indices = super.baseVectorsToIndices("e4", "e5");
        double[] values = {-0.5d, 0.5d};
        return super.createValue(indices, values);
    }

    protected CgaMvValue createBaseVectorInfinity() {
        int[] indices = super.baseVectorsToIndices("e4", "e5");
        double[] values = {1d, 1d};
        return super.createValue(indices, values);
    }

    protected CgaMvValue createBaseVectorX() {
        int[] indices = super.baseVectorsToIndices("e1");
        double[] values = {1d};
        return super.createValue(indices, values);
    }

    protected CgaMvValue createBaseVectorY() {
        int[] indices = super.baseVectorsToIndices("e2");
        double[] values = {1d};
        return super.createValue(indices, values);
    }

    protected CgaMvValue createBaseVectorZ() {
        int[] indices = super.baseVectorsToIndices("e3");
        double[] values = {1d};
        return super.createValue(indices, values);
    }

    protected CgaMvValue createScalar(double scalar) {
        return createValue(scalar);
    }

    protected CgaMvValue createEpsilonPlus() {
        int[] indices = super.baseVectorsToIndices("e4", "e5");
        double[] values = {1d, 0d};
        return super.createValue(indices, values);
    }

    protected CgaMvValue createEpsilonMinus() {
        int[] indices = super.baseVectorsToIndices("e4", "e5");
        double[] values = {0d, 1d};
        return super.createValue(indices, values);
    }

    protected CgaMvValue createEuclideanPseudoscalar() {
        int[] indices = {this.alDef.indexOfBlade("e1", "e2", "e3")};
        double[] values = {1d};
        return super.createValue(indices, values);
    }

    protected CgaMvValue createPseudoscalar() {
        int[] indices = {this.alDef.getBladesCount() - 1};
        double[] values = {1d};
        return super.createValue(indices, values);
    }

    //TODO
    // In Gameron steht aber pseudoscalar().reverse()/(pseudoscalar left contraction pseudoscalar().reverse())
    // vielleicht ist das die Impl. die unabhängig von ga model ist und die impl hier
    // geht nur für CGA?
    protected CgaMvValue createInversePseudoscalar() {
        return createPseudoscalar().reverse();
    }

    /**
     * Minkovski Bivector.
     *
     * This is the flat point origin, corresponding to einf^e0=e4^e5.
     *
     * @return
     */
    protected CgaMvValue createMinkovskiBiVector() {
        int[] indices = {this.alDef.indexOfBlade("e4", "e5")};
        double[] values = {2d};
        return super.createValue(indices, values);
    }

    protected CgaMvValue createE(double x, double y, double z) {
        int[] indices = super.baseVectorsToIndices("e1", "e2", "e3");
        double[] values = {x, y, z};
        return super.createValue(indices, values);
    }

    // die folgenden Defs sind noch nicht überprüft
    protected CgaMvValue createBaseVectorInfinityDorst() {
        int[] indices = super.baseVectorsToIndices("e4", "e5");
        double[] values = {-1d, 1d};
        return super.createValue(indices, values);
    }

    protected CgaMvValue createBaseVectorOriginDorst() {
        int[] indices = super.baseVectorsToIndices("e4", "e5");
        double[] values = {0.5d, 0.5d};
        return super.createValue(indices, values);
    }

    protected CgaMvValue createBaseVectorInfinityDoran() {
        int[] indices = super.baseVectorsToIndices("e4", "e5");
        double[] values = {1d, 1d};
        return super.createValue(indices, values);
    }

    protected CgaMvValue createBaseVectorOriginDoran() {
        int[] indices = super.baseVectorsToIndices("e4", "e5");
        double[] values = {1d, -1d};
        return super.createValue(indices, values);
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
    public IAlgebra getIAlgebra() {
        return this.alDef;
    }
}
