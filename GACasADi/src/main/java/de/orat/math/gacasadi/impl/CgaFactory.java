package de.orat.math.gacasadi.impl;

import de.dhbw.rahmlab.casadi.nativelib.NativeLibLoader;
import de.orat.math.gacalc.spi.IGAFactory;
import de.orat.math.sparsematrix.ColumnVectorSparsity;
import de.orat.math.sparsematrix.MatrixSparsity;
import de.orat.math.sparsematrix.SparseDoubleColumnVector;
import de.orat.math.sparsematrix.SparseDoubleMatrix;
import java.util.List;
import java.util.Random;
import util.cga.CGACayleyTable;
import util.cga.CGACayleyTableGeometricProduct;
import util.cga.CGAMultivectorSparsity;

/**
 * @author Oliver Rettig (Oliver.Rettig@orat.de)
 */
public class CgaFactory implements IGAFactory<CgaMvExpr, CgaMvVariable, CgaMvValue> {

    static {
        // Init JCasADi eagerly to improve profiling.
        NativeLibLoader.load();
    }

    private final static CGACayleyTableGeometricProduct baseCayleyTable = CGACayleyTableGeometricProduct.instance();

    public final static CgaFactory instance = new CgaFactory();

    /**
     * Needs to be public in order to make ServiceLoader work.
     */
    public CgaFactory() {

    }

    public int getBasisBladesCount(){
        return baseCayleyTable.getBladesCount();
    }
    
    @Override
    public CgaConstantsExpr constantsExpr() {
        return CgaConstantsExpr.instance;
    }

    @Override
    public CgaConstantsValue constantsValue() {
        return CgaConstantsValue.instance;
    }

    @Override
    public CgaLoopService getLoopService() {
        return CgaLoopService.instance;
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

    // random multivectors
    @Override
    public CgaMvValue createValueRandom() {
        final int basisBladesCount = getBasisBladesCount();
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
    public CgaFunction createFunction(String name, 
        List<? extends CgaMvVariable> parameters,
        List<? extends CgaMvExpr> returns) {
        return new CgaFunction(name, parameters, returns);
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

    // create constants
    @Override
    public SparseDoubleMatrix createBaseVectorOrigin(double scalar) {
        double[] nonzeros = new double[]{-0.5d * scalar, 0.5d * scalar};
        int[] rows = new int[]{4, 5};
        CGAMultivectorSparsity sparsity = new CGAMultivectorSparsity(rows);
        return new SparseDoubleMatrix(sparsity, nonzeros);
    }

    @Override
    public SparseDoubleMatrix createBaseVectorInfinity(double scalar) {
        double[] nonzeros = new double[]{scalar, scalar};
        int[] rows = new int[]{4, 5};
        CGAMultivectorSparsity sparsity = new CGAMultivectorSparsity(rows);
        return new SparseDoubleMatrix(sparsity, nonzeros);
    }

    @Override
    public SparseDoubleMatrix createBaseVectorX(double scalar) {
        double[] nonzeros = new double[]{scalar};
        int[] rows = new int[]{1};
        CGAMultivectorSparsity sparsity = new CGAMultivectorSparsity(rows);
        return new /*SparseCGAColumnVector*/ SparseDoubleMatrix(sparsity, nonzeros);
    }

    @Override
    public SparseDoubleMatrix createBaseVectorY(double scalar) {
        double[] nonzeros = new double[]{scalar};
        int[] rows = new int[]{2};
        CGAMultivectorSparsity sparsity = new CGAMultivectorSparsity(rows);
        return new /*SparseCGAColumnVector*/ SparseDoubleMatrix(sparsity, nonzeros);
    }

    @Override
    public SparseDoubleMatrix createBaseVectorZ(double scalar) {
        double[] nonzeros = new double[]{scalar};
        int[] rows = new int[]{3};
        CGAMultivectorSparsity sparsity = new CGAMultivectorSparsity(rows);
        return new /*SparseCGAColumnVector*/ SparseDoubleMatrix(sparsity, nonzeros);
    }

    @Override
    public SparseDoubleMatrix createScalar(double scalar) {
        CGAMultivectorSparsity sparsity = new CGAMultivectorSparsity(new int[]{0});
        return new /*SparseCGAColumnVector*/ SparseDoubleMatrix(sparsity, new double[]{scalar});
    }

    @Override
    public SparseDoubleMatrix createEpsilonPlus() {
        CGAMultivectorSparsity sparsity = new CGAMultivectorSparsity(new int[]{4, 5});
        return new SparseDoubleMatrix(sparsity, new double[]{1d, 0d});
    }

    @Override
    public SparseDoubleMatrix createEpsilonMinus() {
        CGAMultivectorSparsity sparsity = new CGAMultivectorSparsity(new int[]{4, 5});
        return new SparseDoubleMatrix(sparsity, new double[]{0d, 1d});
    }

    @Override
    public SparseDoubleMatrix createEuclideanPseudoscalar() {
        CGAMultivectorSparsity sparsity = new CGAMultivectorSparsity(new int[]{16});
        return new SparseDoubleMatrix(sparsity, new double[]{1d});
    }

    @Override
    public SparseDoubleMatrix createPseudoscalar() {
        CGAMultivectorSparsity sparsity = new CGAMultivectorSparsity(new int[]{CGACayleyTable.getPseudoScalarIndex()/*31*/});
        return new SparseDoubleMatrix(sparsity, new double[]{1d});
    }

    //TODO
    // In Gameron steht aber pseudoscalar().reverse()/(pseudoscalar left contraction pseudoscalar().reverse())
    // vielleicht ist das die Impl. die unabhängig von ga model ist und die impl hier
    // geht nur für CGA?
    @Override
    public SparseDoubleMatrix createInversePseudoscalar() {
        return this.constantsValue().getPseudoscalar().reverse().elements();
    }

    /**
     * Minkovski Bivector.
     * 
     * This is the flat point origin, corresponding to einf^e0=e4^e5.
     * 
     * @return 
     */
    @Override
    public SparseDoubleMatrix createMinkovskiBiVector() {
        CGAMultivectorSparsity sparsity = new CGAMultivectorSparsity(new int[]{CGACayleyTable.getMikovskiBivectorIndex()});
        return new SparseDoubleMatrix(sparsity, new double[]{2d});
    }

    @Override
    public SparseDoubleMatrix createE(double x, double y, double z) {
        double[] nonzeros = new double[]{x, y, z};
        int[] rows = new int[]{1, 2, 3};
        CGAMultivectorSparsity sparsity = new CGAMultivectorSparsity(rows);
        return new SparseDoubleMatrix(sparsity, nonzeros);
    }

    
    // die folgenden Defs sind noch nicht überprüft
    
    @Override
    public SparseDoubleMatrix createBaseVectorInfinityDorst() {
        double[] nonzeros = new double[]{-1d, 1d};
        int[] rows = new int[]{4, 5};
        CGAMultivectorSparsity sparsity = new CGAMultivectorSparsity(rows);
        return new SparseDoubleMatrix(sparsity, nonzeros);
    }

    @Override
    public SparseDoubleMatrix createBaseVectorOriginDorst() {
        double[] nonzeros = new double[]{0.5d, 0.5d};
        int[] rows = new int[]{4, 5};
        CGAMultivectorSparsity sparsity = new CGAMultivectorSparsity(rows);
        return new SparseDoubleMatrix(sparsity, nonzeros);
    }

    @Override
    public SparseDoubleMatrix createBaseVectorInfinityDoran() {
        double[] nonzeros = new double[]{1d, 1d};
        int[] rows = new int[]{4, 5};
        CGAMultivectorSparsity sparsity = new CGAMultivectorSparsity(rows);
        return new SparseDoubleMatrix(sparsity, nonzeros);
    }

    @Override
    public SparseDoubleMatrix createBaseVectorOriginDoran() {
        double[] nonzeros = new double[]{1d, -1d};
        int[] rows = new int[]{4, 5};
        CGAMultivectorSparsity sparsity = new CGAMultivectorSparsity(rows);
        return new SparseDoubleMatrix(sparsity, nonzeros);
    }
}
