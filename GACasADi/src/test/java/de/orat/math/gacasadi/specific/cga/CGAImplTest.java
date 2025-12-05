package de.orat.math.gacasadi.specific.cga;

import de.dhbw.rahmlab.casadi.api.Trigometry;
import de.orat.math.gacalc.api.GAFactory;
import de.orat.math.gacalc.api.GAFunction;
import de.orat.math.gacalc.api.MultivectorExpression;
import de.orat.math.gacalc.api.MultivectorValue;
import de.orat.math.gacalc.api.MultivectorVariable;
import de.orat.math.sparsematrix.ColumnVectorSparsity;
import de.orat.math.sparsematrix.DenseDoubleColumnVector;
import de.orat.math.sparsematrix.MatrixSparsity;
import de.orat.math.sparsematrix.SparseDoubleColumnVector;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.PrimitiveIterator.OfDouble;
import java.util.Random;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import util.cga.CGACayleyTable;
import util.cga.CGACayleyTableGeometricProduct;
import util.cga.CGAMultivectorSparsity;
import util.cga.SparseCGAColumnVector;

/**
 * @author Oliver Rettig (Oliver.Rettig@orat.de)
 */
public class CGAImplTest {

    final static CGACayleyTableGeometricProduct baseCayleyTable = CGACayleyTableGeometricProduct.instance();

    public CGAImplTest() {
        //ExprGraphFactory exprGraphFactory = ExprGraphFactory.get(new CGAExprGraphFactory());
    }

    @Test
    public void testAdd() {
        GAFactory exprGraphFactory = TestExprGraphFactory.instance();
        CGAMultivectorSparsity sparsity_a = new CGAMultivectorSparsity(new int[]{1, 2, 3});
        MultivectorVariable mvsa = exprGraphFactory.createVariable("a", sparsity_a);
        CGAMultivectorSparsity sparsity_b = new CGAMultivectorSparsity(new int[]{1, 3, 4});
        MultivectorVariable mvsb = exprGraphFactory.createVariable("b", sparsity_b);

        MultivectorExpression mvsc = mvsa.addition(mvsb);

        List<MultivectorVariable> parameters = new ArrayList<>();
        parameters.add(mvsa);
        parameters.add(mvsb);
        List<MultivectorExpression> returns = new ArrayList<>();
        returns.add(mvsc);

        GAFunction f = exprGraphFactory.createFunction("c", parameters, returns);

        List<MultivectorValue> arguments = new ArrayList<>();

        double[] values_A = new double[baseCayleyTable.getBladesCount()];
        values_A[1] = 1;
        values_A[2] = 2;
        values_A[3] = 3;
        MultivectorValue arg_a = createValue(exprGraphFactory, values_A);
        System.out.println("a=" + arg_a.toString());
        arguments.add(arg_a);

        double[] values_B = new double[baseCayleyTable.getBladesCount()];
        values_B[1] = 1;
        values_B[3] = 1;
        values_B[4] = 1;
        MultivectorValue arg_b = createValue(exprGraphFactory, values_B);
        System.out.println("b=" + arg_b.toString());
        arguments.add(arg_b);

        double[] test = add(values_A, values_B);
        DenseDoubleColumnVector testMatrix = new DenseDoubleColumnVector(test);

        try {
            List<MultivectorValue> mv = f.callValue(arguments);
            System.out.println("c=a+b=" + mv.iterator().next().toString());
            System.out.println("test=" + testMatrix.toString());
            assertTrue(equals((new SparseDoubleColumnVector(mv.iterator().next().elements())).toArray(), test));
        } catch (Exception e) {
        }
    }

    @Test
    public void testSub() {
        GAFactory exprGraphFactory = TestExprGraphFactory.instance();
        CGAMultivectorSparsity sparsity_a = new CGAMultivectorSparsity(new int[]{1, 2, 3});
        MultivectorVariable mvsa = exprGraphFactory.createVariable("a", sparsity_a);
        CGAMultivectorSparsity sparsity_b = new CGAMultivectorSparsity(new int[]{1, 3, 4});
        MultivectorVariable mvsb = exprGraphFactory.createVariable("b", sparsity_b);

        MultivectorExpression mvsc = mvsa.subtraction(mvsb);

        List<MultivectorVariable> parameters = new ArrayList<>();
        parameters.add(mvsa);
        parameters.add(mvsb);
        List<MultivectorExpression> returns = new ArrayList<>();
        returns.add(mvsc);

        GAFunction f = exprGraphFactory.createFunction("c", parameters, returns);

        List<MultivectorValue> arguments = new ArrayList<>();

        double[] values_A = new double[baseCayleyTable.getBladesCount()];
        values_A[1] = 1;
        values_A[2] = 2;
        values_A[3] = 3;
        MultivectorValue arg_a = createValue(exprGraphFactory, values_A);
        System.out.println("a=" + arg_a.toString());
        arguments.add(arg_a);

        double[] values_B = new double[baseCayleyTable.getBladesCount()];
        values_B[1] = 1;
        values_B[3] = 1;
        values_B[4] = 1;
        MultivectorValue arg_b = createValue(exprGraphFactory, values_B);
        System.out.println("b=" + arg_b.toString());
        arguments.add(arg_b);

        double[] test = sub(values_A, values_B);
        DenseDoubleColumnVector testMatrix = new DenseDoubleColumnVector(test);

        try {
            List<MultivectorValue> mv = f.callValue(arguments);
            System.out.println("c=a-b=" + mv.iterator().next().toString());
            System.out.println("test=" + testMatrix.toString());
            assertTrue(equals((new SparseDoubleColumnVector(mv.iterator().next().elements())).toArray(), test));
        } catch (Exception e) {
        }
    }

    @Test
    public void testOP() {
        GAFactory exprGraphFactory = TestExprGraphFactory.instance();
        CGAMultivectorSparsity sparsity_a = new CGAMultivectorSparsity(new int[]{1, 2, 3});
        MultivectorVariable mvsa = exprGraphFactory.createVariable("a", sparsity_a);
        CGAMultivectorSparsity sparsity_b = new CGAMultivectorSparsity(new int[]{1, 3, 4});
        MultivectorVariable mvsb = exprGraphFactory.createVariable("b", sparsity_b);

        MultivectorExpression mvsc = mvsa.outerProduct(mvsb);

        List<MultivectorVariable> parameters = new ArrayList<>();
        parameters.add(mvsa);
        parameters.add(mvsb);
        List<MultivectorExpression> returns = new ArrayList<>();
        returns.add(mvsc);

        GAFunction f = exprGraphFactory.createFunction("f", parameters, returns);

        List<MultivectorValue> arguments = new ArrayList<>();

        double[] values_A = new double[baseCayleyTable.getBladesCount()];
        values_A[1] = 1;
        values_A[2] = 2;
        values_A[3] = 3;
        //values_A = exprGraphFactory.createRandomCGAKVector(1);

        MultivectorValue arg_a = createValue(exprGraphFactory, values_A);
        System.out.println("a=" + arg_a.toString());
        arguments.add(arg_a);

        double[] values_B = new double[baseCayleyTable.getBladesCount()];
        values_B[1] = 1;
        values_B[3] = 1;
        values_B[4] = 1;
        //values_B = exprGraphFactory.createRandomCGAKVector(1);

        MultivectorValue arg_b = createValue(exprGraphFactory, values_B);
        System.out.println("b=" + arg_b.toString());
        arguments.add(arg_b);

        double[] test = op(values_A, values_B);
        DenseDoubleColumnVector testMatrix = new DenseDoubleColumnVector(test);

        try {
            List<MultivectorValue> result2 = f.callValue(arguments);
            MultivectorValue mv = result2.iterator().next();
            System.out.println("a^b=" + mv.toString());
            System.out.println("test==" + testMatrix.toString());
            assertTrue(equals((new SparseDoubleColumnVector(mv.elements())).toArray(), test));
        } catch (Exception e) {
        }
    }

    @Test
    public void testGradeSelectionRandom() {
        GAFactory exprGraphFactory = TestExprGraphFactory.instance();
        CGAMultivectorSparsity sparsity_a = CGAMultivectorSparsity.dense();
        MultivectorVariable mva = exprGraphFactory.createVariable("a", sparsity_a);

        List<MultivectorValue> arguments = new ArrayList<>();

        double[] values_A = createValueRandom();
        MultivectorValue arg_a = createValue(exprGraphFactory, values_A);
        arguments.add(arg_a);

        //TODO
        // random auswählen
        int grade = 5;
        MultivectorExpression res = mva.gradeExtraction(grade);

        List<MultivectorVariable> parameters = new ArrayList<>();
        parameters.add(mva);

        List<MultivectorExpression> result = new ArrayList<>();
        result.add(res);
        GAFunction f = exprGraphFactory.createFunction("f", parameters, result);

        double[] test = gradeSelection(values_A, grade);
        DenseDoubleColumnVector testMatrix = new DenseDoubleColumnVector(test);
        //System.out.println(testMatrix.toString());

        try {
            List<MultivectorValue> result2 = f.callValue(arguments);
            MultivectorValue mv = result2.iterator().next();
            System.out.println("gradeSelection()=" + mv.toString());
            System.out.println("test=" + testMatrix.toString());
            assertTrue(equals((new SparseDoubleColumnVector(mv.elements())).toArray(), test));
        } catch (Exception e) {
        }
    }

    @Test
    public void testGPVec1Fix() {

        GAFactory exprGraphFactory = TestExprGraphFactory.instance();
        //CGAMultivectorSparsity sparsity_a = new CGAMultivectorSparsity(new int[]{1,2,3});
        //MultivectorSymbolic mva = CGAExprGraphFactory.createMultivectorSymbolic("a", sparsity_a);

        MultivectorVariable mva = exprGraphFactory.createVariable("a", 1);
        //System.out.println("a (sparsity): "+mva.getSparsity().toString());
        //System.out.println("a: "+mva.toString());

        MultivectorVariable mvb = exprGraphFactory.createVariable("b", 1);
        //System.out.println("b (sparsity): "+mvb.getSparsity().toString());
        //System.out.println("b: "+mvb.toString());

        MultivectorExpression res = mva.geometricProduct(mvb);
        System.out.println("result (sym vec1fix): " + res.toString());

        List<MultivectorVariable> parameters = new ArrayList<>();
        parameters.add(mva);
        parameters.add(mvb); // vertauschen von a und be hatte keinen Effekt

        List<MultivectorExpression> result = new ArrayList<>();
        result.add(res);
        GAFunction f = exprGraphFactory.createFunction("f", parameters, result);

        List<MultivectorValue> arguments = new ArrayList<>();

        double[] values_A = new double[baseCayleyTable.getBladesCount()];
        values_A[1] = 1;
        values_A[2] = 2;
        values_A[3] = 3;
        MultivectorValue arg_a = createValue(exprGraphFactory, values_A);
        //System.out.println("a="+arg_a.toString());
        arguments.add(arg_a);

        double[] values_B = new double[baseCayleyTable.getBladesCount()];
        values_B[1] = 1;
        values_B[3] = 1;
        values_B[4] = 1;
        MultivectorValue arg_b = createValue(exprGraphFactory, values_B);
        //System.out.println("b="+arg_b.toString());
        arguments.add(arg_b);

        double[] test = gp(values_A, values_B);
        DenseDoubleColumnVector testMatrix = new DenseDoubleColumnVector(test);
        //System.out.println(testMatrix.toString());

        try {
            List<MultivectorValue> result2 = f.callValue(arguments);
            MultivectorValue mv = result2.iterator().next();
            System.out.println("a b=" + mv.toString());
            System.out.println("test=" + testMatrix.toString());

            assertTrue(equals((new SparseDoubleColumnVector(mv.elements())).toArray(), test));
        } catch (Exception e) {
        }
    }

    @Test
    public void testGPRandom() {

        GAFactory exprGraphFactory = TestExprGraphFactory.instance();
        MultivectorVariable mva = exprGraphFactory.createVariableDense("a"/*, 1*/);
        MultivectorVariable mvb = exprGraphFactory.createVariableDense("b"/*, 1*/);

        List<MultivectorVariable> parameters = new ArrayList<>();
        parameters.add(mva);
        parameters.add(mvb);

        MultivectorExpression res = mva.geometricProduct(mvb);
        System.out.println("gprandom: " + res.toString());

        List<MultivectorExpression> result = new ArrayList<>();
        result.add(res);
        GAFunction f = exprGraphFactory.createFunction("f", parameters, result);

        List<MultivectorValue> arguments = new ArrayList<>();

        double[] values_A = createValueRandom();
        MultivectorValue arg_a = createValue(exprGraphFactory, values_A);
        arguments.add(arg_a);

        double[] values_B = createValueRandom();
        MultivectorValue arg_b = createValue(exprGraphFactory, values_B);
        arguments.add(arg_b);

        double[] test = gp(values_A, values_B);
        DenseDoubleColumnVector testMatrix = new DenseDoubleColumnVector(test);
        //System.out.println(testMatrix.toString());

        try {
            System.out.println("a=" + arg_a.toString());
            System.out.println("b=" + arg_b.toString());
            List<MultivectorValue> result2 = f.callValue(arguments);
            MultivectorValue mv = result2.iterator().next();
            System.out.println("random (gp): a b=" + mv.toString());
            System.out.println("test=" + testMatrix.toString());

            double eps = 0.00001;
            assertTrue(equals((new SparseDoubleColumnVector(mv.elements())).toArray(), test, eps));
        } catch (Exception e) {
        }
    }

    @Test
    public void testScalarProductRandom() {

        GAFactory exprGraphFactory = TestExprGraphFactory.instance();
        MultivectorVariable mva = exprGraphFactory.createVariableDense("a"/*, 1*/);
        MultivectorVariable mvb = exprGraphFactory.createVariableDense("b"/*, 1*/);

        List<MultivectorVariable> parameters = new ArrayList<>();
        parameters.add(mva);
        parameters.add(mvb);

        MultivectorExpression res = mva.scalarProduct(mvb);
        System.out.println("scprandom: " + res.toString());

        List<MultivectorExpression> result = new ArrayList<>();
        result.add(res);
        GAFunction f = exprGraphFactory.createFunction("f", parameters, result);

        List<MultivectorValue> arguments = new ArrayList<>();

        double[] values_A = createValueRandom();
        MultivectorValue arg_a = createValue(exprGraphFactory, values_A);
        arguments.add(arg_a);

        double[] values_B = createValueRandom();
        MultivectorValue arg_b = createValue(exprGraphFactory, values_B);
        arguments.add(arg_b);

        double test = scp(values_A, values_B);
        double[] testMatrix = new double[32];
        testMatrix[0] = test;

        try {
            System.out.println("a=" + arg_a.toString());
            System.out.println("b=" + arg_b.toString());
            List<MultivectorValue> result2 = f.callValue(arguments);
            MultivectorValue mv = result2.iterator().next();
            System.out.println("random (scp): a*b=" + mv.toString());
            System.out.println("test=" + String.valueOf(test));

            //TODO
            // Wie kann ich überprüfen, ob mv (MultivectorNumeric) ein scalar ist?
            double eps = 0.00001;
            assertTrue(equals((new SparseDoubleColumnVector(mv.elements())).toArray(), testMatrix, eps));
        } catch (Exception e) {
        }
    }

    @Test
    public void testAbsRandom() {

        GAFactory exprGraphFactory = TestExprGraphFactory.instance();
        MultivectorVariable mva = exprGraphFactory.createVariable("a", 0);

        List<MultivectorVariable> parameters = new ArrayList<>();
        parameters.add(mva);

        MultivectorExpression res = mva.scalarAbs();
        System.out.println("absRandom: " + res.toString());

        List<MultivectorExpression> result = new ArrayList<>();
        result.add(res);
        GAFunction f = exprGraphFactory.createFunction("f", parameters, result);

        List<MultivectorValue> arguments = new ArrayList<>();

        double[] values_A = createValueRandom(0);
        MultivectorValue arg_a = createValue(exprGraphFactory, values_A);
        arguments.add(arg_a);

        double[] test = abs(values_A);
        DenseDoubleColumnVector testMatrix = new DenseDoubleColumnVector(test);
        //System.out.println(testMatrix.toString());

        try {
            System.out.println("a=" + arg_a.toString());
            List<MultivectorValue> result2 = f.callValue(arguments);
            MultivectorValue mv = result2.iterator().next();
            System.out.println("random (abs): =" + mv.toString());
            System.out.println("test=" + testMatrix.toString());

            double eps = 0.00001;
            assertTrue(equals((new SparseDoubleColumnVector(mv.elements())).toArray(), test, eps));
        } catch (Exception e) {
        }
    }

    @Test
    //@Disabled
    public void testExpOfBivectorRandom() {

        GAFactory exprGraphFactory = TestExprGraphFactory.instance();
        // input is bivector is grade 2
        MultivectorVariable mva = exprGraphFactory.createVariable("a",2);
        
        List<MultivectorVariable> parameters = new ArrayList<>();
        parameters.add(mva);
        
        MultivectorExpression res = mva.exp();
        System.out.println("expOfBivectorRandom: " + res.toString());

        List<MultivectorExpression> result = new ArrayList<>();
        result.add(res);
        GAFunction f = exprGraphFactory.createFunction("f", parameters, result);

        List<MultivectorValue> arguments = new ArrayList<>();

        // input is bivector
        double[] bivector = createValueRandom(2);
        MultivectorValue arg_a = createValue(exprGraphFactory, bivector);
        arguments.add(arg_a);

        double[] test = exp(bivector);
        DenseDoubleColumnVector testMatrix = new DenseDoubleColumnVector(test);
        //System.out.println(testMatrix.toString());

        try {
            System.out.println("random a (bivector)=" + arg_a.toString());
            List<MultivectorValue> result2 = f.callValue(arguments);
            MultivectorValue mv = result2.iterator().next();
            System.out.println("exp(a)=" + mv.toString());
            System.out.println("test exp(a)=" + testMatrix.toString());

            double eps = 0.00001;
            assertTrue(equals((new SparseDoubleColumnVector(mv.elements())).toArray(), test, eps));
        } catch (Exception e) {
        }
    }
    
    @Test
    //@Disabled
    public void testExpLogOfNormalizedRotorRandom() {

        GAFactory exprGraphFactory = TestExprGraphFactory.instance();
        MultivectorVariable mva = exprGraphFactory.createVariable("a",CGACayleyTable.getEvenGrades());
        
        List<MultivectorVariable> parameters = new ArrayList<>();
        parameters.add(mva);
        
        MultivectorExpression res = mva.log().exp();
        System.out.println("ExpLogOfNormalizedRotorRandom: " + res.toString());

        List<MultivectorExpression> result = new ArrayList<>();
        result.add(res);
        GAFunction f = exprGraphFactory.createFunction("f", parameters, result);

        List<MultivectorValue> arguments = new ArrayList<>();

        double[] normalizedRotor = normalizeRotor(createValueRandom(CGACayleyTable.getEvenIndizes()));
        MultivectorValue arg_a = createValue(exprGraphFactory, normalizedRotor);
        arguments.add(arg_a);

        double[] test = exp(log(normalizedRotor));
        DenseDoubleColumnVector testMatrix = new DenseDoubleColumnVector(test);
        //System.out.println(testMatrix.toString());

        try {
            System.out.println("random a (normalized rotor)=" + arg_a.toString());
            List<MultivectorValue> result2 = f.callValue(arguments);
            MultivectorValue mv = result2.iterator().next();
            System.out.println("exp(log(normalizedRotor(a)))=" + mv.toString());
            System.out.println("test exp(log(normalizedRotor(a)))=" + testMatrix.toString());

            double eps = 0.00001;
            assertTrue(equals((new SparseDoubleColumnVector(mv.elements())).toArray(), test, eps));
        } catch (Exception e) {
        }
    }
    
    @Test
    //@Disabled
    public void testLogExpOfBivectorRandom() {

        GAFactory exprGraphFactory = TestExprGraphFactory.instance();
        MultivectorVariable mva = exprGraphFactory.createVariable("a",2);
        
        List<MultivectorVariable> parameters = new ArrayList<>();
        parameters.add(mva);
        
        MultivectorExpression res = mva.exp().log();
        System.out.println("logExpOfBivectorRandom: " + res.toString());

        List<MultivectorExpression> result = new ArrayList<>();
        result.add(res);
        GAFunction f = exprGraphFactory.createFunction("f", parameters, result);

        List<MultivectorValue> arguments = new ArrayList<>();

        // input is bivector
        double[] values_A = createValueRandom(2);
        MultivectorValue arg_a = createValue(exprGraphFactory, values_A);
        arguments.add(arg_a);

        double[] test = log(exp(values_A));
        DenseDoubleColumnVector testMatrix = new DenseDoubleColumnVector(test);
        //System.out.println(testMatrix.toString());

        try {
            System.out.println("random a=" + arg_a.toString());
            List<MultivectorValue> result2 = f.callValue(arguments);
            MultivectorValue mv = result2.iterator().next();
            System.out.println("log(exp(a))=" + mv.toString());
            System.out.println("test log(exp(a))=" + testMatrix.toString());

            double eps = 0.00001;
            assertTrue(equals((new SparseDoubleColumnVector(mv.elements())).toArray(), test, eps));
        } catch (Exception e) {
        }
    }
    
    // test only array based implementations
    @Test
    public void testLogExpOfBivectorRandomArrayBased() {
        GAFactory exprGraphFactory = TestExprGraphFactory.instance();
        // input is bivector
        double[] values_A = createValueRandom(2);
        DenseDoubleColumnVector valuesMatrix = new DenseDoubleColumnVector(values_A);
        double[] test = log(exp(values_A));
        DenseDoubleColumnVector testMatrix = new DenseDoubleColumnVector(test);
        try {
            System.out.println("random a=" + valuesMatrix.toString());
            System.out.println("test log(exp(a)==" + testMatrix.toString());
            double eps = 0.00001;
            assertTrue(equals(values_A, test, eps));
        } catch (Exception e) {}
    }
    
    public void testExpOfBivectorRandomArrayBasedByTaylorSeries() {
        GAFactory exprGraphFactory = TestExprGraphFactory.instance();
        // input is bivector
        double[] values_A = createValueRandom(2);
        DenseDoubleColumnVector valuesMatrix = new DenseDoubleColumnVector(values_A);
        double[] test = exp(values_A);
        double[] testTaylorSeries = expSeries(values_A);
        DenseDoubleColumnVector testMatrix = new DenseDoubleColumnVector(test);
        DenseDoubleColumnVector test2Matrix = new DenseDoubleColumnVector(testTaylorSeries);
        try {
            System.out.println("random a=" + valuesMatrix.toString());
            System.out.println("test exp(a)==" + testMatrix.toString());
            System.out.println("test expSeries(a)==" + test2Matrix.toString());
            double eps = 0.00001;
            assertTrue(equals(test, testTaylorSeries, eps));
        } catch (Exception e) {}
    }
        
    @Test
    //@Disabled
    public void testLogOfNormalizedRotorRandom() {

        GAFactory exprGraphFactory = TestExprGraphFactory.instance();
        // input is rotor 
        MultivectorVariable mva = exprGraphFactory.createVariable("a", CGACayleyTable.getEvenGrades());
        
        List<MultivectorVariable> parameters = new ArrayList<>();
        parameters.add(mva);
        
        MultivectorExpression res = mva.log();
        System.out.println("logOfNormalizedRotorRandom: " + res.toString());

        List<MultivectorExpression> result = new ArrayList<>();
        result.add(res);
        GAFunction f = exprGraphFactory.createFunction("f", parameters, result);

        List<MultivectorValue> arguments = new ArrayList<>();

        double[] normalizedRotor = normalizeRotor(createRandomRotor());
        
        MultivectorValue arg_a = createValue(exprGraphFactory, normalizedRotor);
        arguments.add(arg_a);

        double[] test = log(normalizedRotor);
        DenseDoubleColumnVector testMatrix = new DenseDoubleColumnVector(test);
        try {
            System.out.println("random a (normalized rotor) =" + arg_a.toString());
            List<MultivectorValue> result2 = f.callValue(arguments);
            MultivectorValue mv = result2.iterator().next();
            System.out.println("log(normalizedRotor(a))=" + mv.toString());
            System.out.println("test log(normalizedRotor(a))=" + testMatrix.toString());

            double eps = 0.00001;
            assertTrue(equals((new SparseDoubleColumnVector(mv.elements())).toArray(), test, eps));
        } catch (Exception e) {
        }
    }
    
    /**
     * A versor is a multivector that can be expressed as the geometric product of a number of non-null
     * 1-vectors.
     *
     * A sum of two versors does not in general result in a versor!
     * <p>
     *
     * @return inverse of this (assuming, it is a versor, no check is made!)
     * @throws java.lang.ArithmeticException if the multivector is not invertable
     */
    private static double[] versorInverse(double[] mv) {
        double[] rev = reverse(mv);
        double s = scp(mv, rev);
        if (s == 0.0) {
            throw new java.lang.ArithmeticException("non-invertible multivector");
        }
        return muls(mv, 1.0 / s);

        //iMultivectorSymbolic rev = reverse();
        //return rev.gp(gp(rev).scalarInverse());
        // wo kommt diese Implementierung her?
        // im Test wird reverse mit ip() verwendet
    }

    /**
     * Scalar product.
     *
     * @param x
     * @return scalar product of this with a 'x' but without respecting a metric.
     */
    private static double scp(double[] a, double[] b) {
        return dot(a, b)[0];
    }

    private double[] abs(double[] values_A) {
        double[] result = Arrays.copyOf(values_A, values_A.length);
        result[0] = Math.abs(result[0]);
        return result;
    }

    @Test
    public void testOPRandom() {

        GAFactory exprGraphFactory = TestExprGraphFactory.instance();
        MultivectorVariable mva = exprGraphFactory.createVariableDense("a");
        MultivectorVariable mvb = exprGraphFactory.createVariableDense("b");

        List<MultivectorVariable> parameters = new ArrayList<>();
        parameters.add(mva);
        parameters.add(mvb);

        MultivectorExpression res = mva.outerProduct(mvb);
        System.out.println("oprandom: " + res.toString());

        List<MultivectorExpression> result = new ArrayList<>();
        result.add(res);
        GAFunction f = exprGraphFactory.createFunction("f", parameters, result);

        List<MultivectorValue> arguments = new ArrayList<>();

        double[] values_A = createValueRandom();
        MultivectorValue arg_a = createValue(exprGraphFactory, values_A);
        arguments.add(arg_a);

        double[] values_B = createValueRandom();
        MultivectorValue arg_b = createValue(exprGraphFactory, values_B);
        arguments.add(arg_b);

        double[] test = op(values_A, values_B);
        DenseDoubleColumnVector testMatrix = new DenseDoubleColumnVector(test);
        //System.out.println(testMatrix.toString());

        try {
            System.out.println("a=" + arg_a.toString());
            System.out.println("b=" + arg_b.toString());
            List<MultivectorValue> result2 = f.callValue(arguments);
            MultivectorValue mv = result2.iterator().next();
            System.out.println("random (op): a^b=" + mv.toString());
            System.out.println("test=" + testMatrix.toString());

            double eps = 0.00001;
            assertTrue(equals((new SparseDoubleColumnVector(mv.elements())).toArray(), test, eps));
        } catch (Exception e) {
        }
    }

    //TODO
    //@Test
    public void testLCRandom() {

        GAFactory exprGraphFactory = TestExprGraphFactory.instance();
        MultivectorVariable mva = exprGraphFactory.createVariableDense("a");
        MultivectorVariable mvb = exprGraphFactory.createVariableDense("b");

        List<MultivectorVariable> parameters = new ArrayList<>();
        parameters.add(mva);
        parameters.add(mvb);

        MultivectorExpression res = mva.leftContraction(mvb);
        System.out.println("lcrandom: " + res.toString());

        List<MultivectorExpression> result = new ArrayList<>();
        result.add(res);
        GAFunction f = exprGraphFactory.createFunction("f", parameters, result);

        List<MultivectorValue> arguments = new ArrayList<>();

        double[] values_A = createValueRandom();
        MultivectorValue arg_a = createValue(exprGraphFactory, values_A);
        arguments.add(arg_a);

        double[] values_B = createValueRandom();
        MultivectorValue arg_b = createValue(exprGraphFactory, values_B);
        arguments.add(arg_b);

        double[] test = dot(values_A, values_B);
        DenseDoubleColumnVector testMatrix = new DenseDoubleColumnVector(test);

        try {
            System.out.println("a=" + arg_a.toString());
            System.out.println("b=" + arg_b.toString());
            List<MultivectorValue> result2 = f.callValue(arguments);
            MultivectorValue mv = result2.iterator().next();
            System.out.println("random (lc): " + mv.toString());
            System.out.println("test=" + testMatrix.toString());

            // nur der scalar stimmt alle anderen Werte sind falsch
            //TODO
            double eps = 0.00001;
            assertTrue(equals((new SparseDoubleColumnVector(mv.elements())).toArray(), test, eps));
        } catch (Exception e) {
        }
    }

    //TODO
    //@Test
    public void testIPRandom() {

        GAFactory exprGraphFactory = TestExprGraphFactory.instance();
        MultivectorVariable mva = exprGraphFactory.createVariableDense("a");
        MultivectorVariable mvb = exprGraphFactory.createVariableDense("b");

        List<MultivectorVariable> parameters = new ArrayList<>();
        parameters.add(mva);
        parameters.add(mvb);

        MultivectorExpression res = mva.innerProduct(mvb);
        System.out.println("iprandom: " + res.toString());

        List<MultivectorExpression> result = new ArrayList<>();
        result.add(res);
        GAFunction f = exprGraphFactory.createFunction("f", parameters, result);

        List<MultivectorValue> arguments = new ArrayList<>();

        double[] values_A = createValueRandom();
        MultivectorValue arg_a = createValue(exprGraphFactory, values_A);
        arguments.add(arg_a);

        double[] values_B = createValueRandom();
        MultivectorValue arg_b = createValue(exprGraphFactory, values_B);
        arguments.add(arg_b);

        double[] test = dot(values_A, values_B);
        DenseDoubleColumnVector testMatrix = new DenseDoubleColumnVector(test);

        try {
            System.out.println("a=" + arg_a.toString());
            System.out.println("b=" + arg_b.toString());
            List<MultivectorValue> result2 = f.callValue(arguments);
            MultivectorValue mv = result2.iterator().next();
            System.out.println("random (ip): " + mv.toString());
            System.out.println("test=" + testMatrix.toString());

            // nur der scalar stimmt alle anderen Werte sind falsch
            //TODO
            double eps = 0.00001;
            assertTrue(equals((new SparseDoubleColumnVector(mv.elements())).toArray(), test, eps));
        } catch (Exception e) {
        }
    }

    @Test
    public void testDotRandom() {

        GAFactory exprGraphFactory = TestExprGraphFactory.instance();
        MultivectorVariable mva = exprGraphFactory.createVariableDense("a");
        MultivectorVariable mvb = exprGraphFactory.createVariableDense("b");

        List<MultivectorVariable> parameters = new ArrayList<>();
        parameters.add(mva);
        parameters.add(mvb);

        MultivectorExpression res = mva.dotProduct(mvb);
        System.out.println("dotRandom: " + res.toString());

        List<MultivectorExpression> result = new ArrayList<>();
        result.add(res);
        GAFunction f = exprGraphFactory.createFunction("f", parameters, result);

        List<MultivectorValue> arguments = new ArrayList<>();

        double[] values_A = createValueRandom();
        MultivectorValue arg_a = createValue(exprGraphFactory, values_A);
        arguments.add(arg_a);

        double[] values_B = createValueRandom();
        MultivectorValue arg_b = createValue(exprGraphFactory, values_B);
        arguments.add(arg_b);

        double[] test = dot(values_A, values_B);
        DenseDoubleColumnVector testMatrix = new DenseDoubleColumnVector(test);

        try {
            System.out.println("a=" + arg_a.toString());
            System.out.println("b=" + arg_b.toString());
            List<MultivectorValue> result2 = f.callValue(arguments);
            MultivectorValue mv = result2.iterator().next();
            System.out.println("random (dot): " + mv.toString());
            System.out.println("test=" + testMatrix.toString());

            // nur der scalar stimmt alle anderen Werte sind falsch
            //TODO
            double eps = 0.00001;
            assertTrue(equals((new SparseDoubleColumnVector(mv.elements())).toArray(), test, eps));
        } catch (Exception e) {
        }
    }

    //TODO
    //@Test
    public void testRCRandom() {

        GAFactory exprGraphFactory = TestExprGraphFactory.instance();
        MultivectorVariable mva = exprGraphFactory.createVariableDense("a");
        MultivectorVariable mvb = exprGraphFactory.createVariableDense("b");

        List<MultivectorVariable> parameters = new ArrayList<>();
        parameters.add(mva);
        parameters.add(mvb);

        MultivectorExpression res = mva.rightContraction(mvb);
        System.out.println("rcrandom: " + res.toString());

        List<MultivectorExpression> result = new ArrayList<>();
        result.add(res);
        GAFunction f = exprGraphFactory.createFunction("f", parameters, result);

        List<MultivectorValue> arguments = new ArrayList<>();

        double[] values_A = createValueRandom();
        MultivectorValue arg_a = createValue(exprGraphFactory, values_A);
        arguments.add(arg_a);

        double[] values_B = createValueRandom();
        MultivectorValue arg_b = createValue(exprGraphFactory, values_B);
        arguments.add(arg_b);

        double[] test = rc(values_A, values_B);
        DenseDoubleColumnVector testMatrix = new DenseDoubleColumnVector(test);

        try {
            System.out.println("a=" + arg_a.toString());
            System.out.println("b=" + arg_b.toString());
            List<MultivectorValue> result2 = f.callValue(arguments);
            MultivectorValue mv = result2.iterator().next();
            System.out.println("random (rc): " + mv.toString());
            System.out.println("test=" + testMatrix.toString());

            double eps = 0.00001;
            assertTrue(equals((new SparseDoubleColumnVector(mv.elements())).toArray(), test, eps));
        } catch (Exception e) {
        }
    }

    @Test
    public void testRegressiveRandom() {

        GAFactory exprGraphFactory = TestExprGraphFactory.instance();
        MultivectorVariable mva = exprGraphFactory.createVariableDense("a");
        MultivectorVariable mvb = exprGraphFactory.createVariableDense("b");

        List<MultivectorVariable> parameters = new ArrayList<>();
        parameters.add(mva);
        parameters.add(mvb);

        MultivectorExpression res = mva.regressiveProduct(mvb);
        System.out.println("regressiverandom: " + res.toString());

        List<MultivectorExpression> result = new ArrayList<>();
        result.add(res);
        GAFunction f = exprGraphFactory.createFunction("f", parameters, result);

        List<MultivectorValue> arguments = new ArrayList<>();
        double[] values_A = createValueRandom();
        MultivectorValue arg_a = createValue(exprGraphFactory, values_A);
        arguments.add(arg_a);
        double[] values_B = createValueRandom();
        MultivectorValue arg_b = createValue(exprGraphFactory, values_B);
        arguments.add(arg_b);

        double[] test = vee(values_A, values_B);
        DenseDoubleColumnVector testMatrix = new DenseDoubleColumnVector(test);

        try {
            System.out.println("a=" + arg_a.toString());
            System.out.println("b=" + arg_b.toString());
            List<MultivectorValue> result2 = f.callValue(arguments);
            MultivectorValue mv = result2.iterator().next();
            System.out.println("random (vee): " + mv.toString());
            System.out.println("test=" + testMatrix.toString());

            double eps = 0.00001;
            assertTrue(equals((new SparseDoubleColumnVector(mv.elements())).toArray(), test, eps));
        } catch (Exception e) {
        }
    }

    /*@Test
    public void testGeneralInverseRandom() {
       
        ExprGraphFactory exprGraphFactory = TestExprGraphFactory.instance();
        MultivectorSymbolic mva = exprGraphFactory.createMultivectorSymbolic("a");
        
        List<MultivectorSymbolic> parameters = new ArrayList<>();
        parameters.add(mva);
        
        MultivectorSymbolic res = mva.generalInverse();
        System.out.println("generalInverseRandom: "+res.toString());
        
        List<MultivectorSymbolic> result = new ArrayList<>();
        result.add(res);
        FunctionSymbolic f = exprGraphFactory.createFunctionSymbolic("f", parameters, result);
        
        List<MultivectorNumeric> arguments = new ArrayList<>();
        
        double[] normalizedRotor = exprGraphFactory.createRandomMultivector();
        MultivectorNumeric arg_a = exprGraphFactory.createMultivectorNumeric(normalizedRotor);
        arguments.add(arg_a);
        
        double[] test = generalInverse(normalizedRotor);
        DenseDoubleColumnVector testMatrix = new DenseDoubleColumnVector(test);
        
        try {
            System.out.println("a="+arg_a.toString());
            List<MultivectorNumeric> result2 = f.callNumeric(arguments);
            MultivectorNumeric mv = result2.iterator().next();
            System.out.println("random (generalInverse): "+mv.toString());
            System.out.println("test="+testMatrix.toString());
           
            double eps = 0.00001;
            assertTrue(equals(mv.elements().toArray(), test, eps));
        } catch (Exception e){}
	}*/
    /**
     * Vee.
     *
     * The regressive product. (JOIN)
     *
     * @param a
     * @param b
     * @return a & b
     */
    public static double[] vee(double[] a, double[] b) {
        double[] res = new double[a.length];
        res[31] = 1 * (a[31] * b[31]);
        res[30] = 1 * (a[30] * b[31] + a[31] * b[30]);
        res[29] = -1 * (a[29] * -1 * b[31] + a[31] * b[29] * -1);
        res[28] = 1 * (a[28] * b[31] + a[31] * b[28]);
        res[27] = -1 * (a[27] * -1 * b[31] + a[31] * b[27] * -1);
        res[26] = 1 * (a[26] * b[31] + a[31] * b[26]);
        res[25] = 1 * (a[25] * b[31] + a[29] * -1 * b[30] - a[30] * b[29] * -1 + a[31] * b[25]);
        res[24] = -1 * (a[24] * -1 * b[31] + a[28] * b[30] - a[30] * b[28] + a[31] * b[24] * -1);
        res[23] = 1 * (a[23] * b[31] + a[27] * -1 * b[30] - a[30] * b[27] * -1 + a[31] * b[23]);
        res[22] = -1 * (a[22] * -1 * b[31] + a[26] * b[30] - a[30] * b[26] + a[31] * b[22] * -1);
        res[21] = 1 * (a[21] * b[31] + a[28] * b[29] * -1 - a[29] * -1 * b[28] + a[31] * b[21]);
        res[20] = -1 * (a[20] * -1 * b[31] + a[27] * -1 * b[29] * -1 - a[29] * -1 * b[27] * -1 + a[31] * b[20] * -1);
        res[19] = 1 * (a[19] * b[31] + a[26] * b[29] * -1 - a[29] * -1 * b[26] + a[31] * b[19]);
        res[18] = 1 * (a[18] * b[31] + a[27] * -1 * b[28] - a[28] * b[27] * -1 + a[31] * b[18]);
        res[17] = -1 * (a[17] * -1 * b[31] + a[26] * b[28] - a[28] * b[26] + a[31] * b[17] * -1);
        res[16] = 1 * (a[16] * b[31] + a[26] * b[27] * -1 - a[27] * -1 * b[26] + a[31] * b[16]);
        res[15] = 1 * (a[15] * b[31] + a[21] * b[30] - a[24] * -1 * b[29] * -1 + a[25] * b[28] + a[28] * b[25] - a[29] * -1 * b[24] * -1 + a[30] * b[21] + a[31] * b[15]);
        res[14] = -1 * (a[14] * -1 * b[31] + a[20] * -1 * b[30] - a[23] * b[29] * -1 + a[25] * b[27] * -1 + a[27] * -1 * b[25] - a[29] * -1 * b[23] + a[30] * b[20] * -1 + a[31] * b[14] * -1);
        res[13] = 1 * (a[13] * b[31] + a[19] * b[30] - a[22] * -1 * b[29] * -1 + a[25] * b[26] + a[26] * b[25] - a[29] * -1 * b[22] * -1 + a[30] * b[19] + a[31] * b[13]);
        res[12] = 1 * (a[12] * b[31] + a[18] * b[30] - a[23] * b[28] + a[24] * -1 * b[27] * -1 + a[27] * -1 * b[24] * -1 - a[28] * b[23] + a[30] * b[18] + a[31] * b[12]);
        res[11] = -1 * (a[11] * -1 * b[31] + a[17] * -1 * b[30] - a[22] * -1 * b[28] + a[24] * -1 * b[26] + a[26] * b[24] * -1 - a[28] * b[22] * -1 + a[30] * b[17] * -1 + a[31] * b[11] * -1);
        res[10] = 1 * (a[10] * b[31] + a[16] * b[30] - a[22] * -1 * b[27] * -1 + a[23] * b[26] + a[26] * b[23] - a[27] * -1 * b[22] * -1 + a[30] * b[16] + a[31] * b[10]);
        res[9] = -1 * (a[9] * -1 * b[31] + a[18] * b[29] * -1 - a[20] * -1 * b[28] + a[21] * b[27] * -1 + a[27] * -1 * b[21] - a[28] * b[20] * -1 + a[29] * -1 * b[18] + a[31] * b[9] * -1);
        res[8] = 1 * (a[8] * b[31] + a[17] * -1 * b[29] * -1 - a[19] * b[28] + a[21] * b[26] + a[26] * b[21] - a[28] * b[19] + a[29] * -1 * b[17] * -1 + a[31] * b[8]);
        res[7] = -1 * (a[7] * -1 * b[31] + a[16] * b[29] * -1 - a[19] * b[27] * -1 + a[20] * -1 * b[26] + a[26] * b[20] * -1 - a[27] * -1 * b[19] + a[29] * -1 * b[16] + a[31] * b[7] * -1);
        res[6] = 1 * (a[6] * b[31] + a[16] * b[28] - a[17] * -1 * b[27] * -1 + a[18] * b[26] + a[26] * b[18] - a[27] * -1 * b[17] * -1 + a[28] * b[16] + a[31] * b[6]);
        res[5] = 1 * (a[5] * b[31] + a[9] * -1 * b[30] - a[12] * b[29] * -1 + a[14] * -1 * b[28] - a[15] * b[27] * -1 + a[18] * b[25] - a[20] * -1 * b[24] * -1 + a[21] * b[23] + a[23] * b[21] - a[24] * -1 * b[20] * -1 + a[25] * b[18] + a[27] * -1 * b[15] - a[28] * b[14] * -1 + a[29] * -1 * b[12] - a[30] * b[9] * -1 + a[31] * b[5]);
        res[4] = -1 * (a[4] * -1 * b[31] + a[8] * b[30] - a[11] * -1 * b[29] * -1 + a[13] * b[28] - a[15] * b[26] + a[17] * -1 * b[25] - a[19] * b[24] * -1 + a[21] * b[22] * -1 + a[22] * -1 * b[21] - a[24] * -1 * b[19] + a[25] * b[17] * -1 + a[26] * b[15] - a[28] * b[13] + a[29] * -1 * b[11] * -1 - a[30] * b[8] + a[31] * b[4] * -1);
        res[3] = 1 * (a[3] * b[31] + a[7] * -1 * b[30] - a[10] * b[29] * -1 + a[13] * b[27] * -1 - a[14] * -1 * b[26] + a[16] * b[25] - a[19] * b[23] + a[20] * -1 * b[22] * -1 + a[22] * -1 * b[20] * -1 - a[23] * b[19] + a[25] * b[16] + a[26] * b[14] * -1 - a[27] * -1 * b[13] + a[29] * -1 * b[10] - a[30] * b[7] * -1 + a[31] * b[3]);
        res[2] = -1 * (a[2] * -1 * b[31] + a[6] * b[30] - a[10] * b[28] + a[11] * -1 * b[27] * -1 - a[12] * b[26] + a[16] * b[24] * -1 - a[17] * -1 * b[23] + a[18] * b[22] * -1 + a[22] * -1 * b[18] - a[23] * b[17] * -1 + a[24] * -1 * b[16] + a[26] * b[12] - a[27] * -1 * b[11] * -1 + a[28] * b[10] - a[30] * b[6] + a[31] * b[2] * -1);
        res[1] = 1 * (a[1] * b[31] + a[6] * b[29] * -1 - a[7] * -1 * b[28] + a[8] * b[27] * -1 - a[9] * -1 * b[26] + a[16] * b[21] - a[17] * -1 * b[20] * -1 + a[18] * b[19] + a[19] * b[18] - a[20] * -1 * b[17] * -1 + a[21] * b[16] + a[26] * b[9] * -1 - a[27] * -1 * b[8] + a[28] * b[7] * -1 - a[29] * -1 * b[6] + a[31] * b[1]);
        res[0] = 1 * (a[0] * b[31] + a[1] * b[30] - a[2] * -1 * b[29] * -1 + a[3] * b[28] - a[4] * -1 * b[27] * -1 + a[5] * b[26] + a[6] * b[25] - a[7] * -1 * b[24] * -1 + a[8] * b[23] - a[9] * -1 * b[22] * -1 + a[10] * b[21] - a[11] * -1 * b[20] * -1 + a[12] * b[19] + a[13] * b[18] - a[14] * -1 * b[17] * -1 + a[15] * b[16] + a[16] * b[15] - a[17] * -1 * b[14] * -1 + a[18] * b[13] + a[19] * b[12] - a[20] * -1 * b[11] * -1 + a[21] * b[10] - a[22] * -1 * b[9] * -1 + a[23] * b[8] - a[24] * -1 * b[7] * -1 + a[25] * b[6] + a[26] * b[5] - a[27] * -1 * b[4] * -1 + a[28] * b[3] - a[29] * -1 * b[2] * -1 + a[30] * b[1] + a[31] * b[0]);
        return res;
    }

    @Test
    public void testGPRandom1Vec() {

        //TestExprGraphFactory fac = TestExprGraphFactory.instance();
        GAFactory exprGraphFactory = TestExprGraphFactory.instance();
        MultivectorVariable mva = exprGraphFactory.createVariable("a", 1);
        MultivectorVariable mvb = exprGraphFactory.createVariable("b", 1);

        List<MultivectorVariable> parameters = new ArrayList<>();
        parameters.add(mva);
        parameters.add(mvb);

        MultivectorExpression res = mva.geometricProduct(mvb);
        System.out.println("radmon1vec: " + res.toString());

        List<MultivectorExpression> result = new ArrayList<>();
        result.add(res);
        GAFunction f = exprGraphFactory.createFunction("f", parameters, result);

        List<MultivectorValue> arguments = new ArrayList<>();

        double[] values_A = createValueRandom(1);
        MultivectorValue arg_a = createValue(exprGraphFactory, values_A);
        arguments.add(arg_a);

        double[] values_B = createValueRandom(1);
        MultivectorValue arg_b = createValue(exprGraphFactory, values_B);
        arguments.add(arg_b);

        double[] test = gp(values_A, values_B);
        DenseDoubleColumnVector testMatrix = new DenseDoubleColumnVector(test);
        //System.out.println(testMatrix.toString());

        try {
            System.out.println("a=" + arg_a.toString());
            System.out.println("b=" + arg_b.toString());
            List<MultivectorValue> result2 = f.callValue(arguments);
            MultivectorValue mv = result2.iterator().next();
            System.out.println("random 1-vec: a b=" + mv.toString());
            System.out.println("test=" + testMatrix.toString());

            double eps = 0.00001;
            assertTrue(equals((new SparseDoubleColumnVector(mv.elements())).toArray(), test, eps));
        } catch (Exception e) {
        }
    }

    /**
     * The geometric product.
     *
     * @param a
     * @param b
     * @return a * b
     */
    private static double[] gp(double[] a, double[] b) {
        double[] res = new double[a.length];

        res[0] = b[0] * a[0] + b[1] * a[1] + b[2] * a[2] + b[3] * a[3] + b[4] * a[4] - b[5] * a[5] - b[6] * a[6] - b[7] * a[7] - b[8] * a[8] + b[9] * a[9] - b[10] * a[10] - b[11] * a[11] + b[12] * a[12] - b[13] * a[13] + b[14] * a[14] + b[15] * a[15] - b[16] * a[16] - b[17] * a[17] + b[18] * a[18] - b[19] * a[19] + b[20] * a[20] + b[21] * a[21] - b[22] * a[22] + b[23] * a[23] + b[24] * a[24] + b[25] * a[25] + b[26] * a[26] - b[27] * a[27] - b[28] * a[28] - b[29] * a[29] - b[30] * a[30] - b[31] * a[31];
        res[1] = b[1] * a[0] + b[0] * a[1] - b[6] * a[2] - b[7] * a[3] - b[8] * a[4] + b[9] * a[5] + b[2] * a[6] + b[3] * a[7] + b[4] * a[8] - b[5] * a[9] - b[16] * a[10] - b[17] * a[11] + b[18] * a[12] - b[19] * a[13] + b[20] * a[14] + b[21] * a[15] - b[10] * a[16] - b[11] * a[17] + b[12] * a[18] - b[13] * a[19] + b[14] * a[20] + b[15] * a[21] + b[26] * a[22] - b[27] * a[23] - b[28] * a[24] - b[29] * a[25] - b[22] * a[26] + b[23] * a[27] + b[24] * a[28] + b[25] * a[29] - b[31] * a[30] - b[30] * a[31];
        res[2] = b[2] * a[0] + b[6] * a[1] + b[0] * a[2] - b[10] * a[3] - b[11] * a[4] + b[12] * a[5] - b[1] * a[6] + b[16] * a[7] + b[17] * a[8] - b[18] * a[9] + b[3] * a[10] + b[4] * a[11] - b[5] * a[12] - b[22] * a[13] + b[23] * a[14] + b[24] * a[15] + b[7] * a[16] + b[8] * a[17] - b[9] * a[18] - b[26] * a[19] + b[27] * a[20] + b[28] * a[21] - b[13] * a[22] + b[14] * a[23] + b[15] * a[24] - b[30] * a[25] + b[19] * a[26] - b[20] * a[27] - b[21] * a[28] + b[31] * a[29] + b[25] * a[30] + b[29] * a[31];
        res[3] = b[3] * a[0] + b[7] * a[1] + b[10] * a[2] + b[0] * a[3] - b[13] * a[4] + b[14] * a[5] - b[16] * a[6] - b[1] * a[7] + b[19] * a[8] - b[20] * a[9] - b[2] * a[10] + b[22] * a[11] - b[23] * a[12] + b[4] * a[13] - b[5] * a[14] + b[25] * a[15] - b[6] * a[16] + b[26] * a[17] - b[27] * a[18] + b[8] * a[19] - b[9] * a[20] + b[29] * a[21] + b[11] * a[22] - b[12] * a[23] + b[30] * a[24] + b[15] * a[25] - b[17] * a[26] + b[18] * a[27] - b[31] * a[28] - b[21] * a[29] - b[24] * a[30] - b[28] * a[31];
        res[4] = b[4] * a[0] + b[8] * a[1] + b[11] * a[2] + b[13] * a[3] + b[0] * a[4] + b[15] * a[5] - b[17] * a[6] - b[19] * a[7] - b[1] * a[8] - b[21] * a[9] - b[22] * a[10] - b[2] * a[11] - b[24] * a[12] - b[3] * a[13] - b[25] * a[14] - b[5] * a[15] - b[26] * a[16] - b[6] * a[17] - b[28] * a[18] - b[7] * a[19] - b[29] * a[20] - b[9] * a[21] - b[10] * a[22] - b[30] * a[23] - b[12] * a[24] - b[14] * a[25] + b[16] * a[26] + b[31] * a[27] + b[18] * a[28] + b[20] * a[29] + b[23] * a[30] + b[27] * a[31];
        res[5] = b[5] * a[0] + b[9] * a[1] + b[12] * a[2] + b[14] * a[3] + b[15] * a[4] + b[0] * a[5] - b[18] * a[6] - b[20] * a[7] - b[21] * a[8] - b[1] * a[9] - b[23] * a[10] - b[24] * a[11] - b[2] * a[12] - b[25] * a[13] - b[3] * a[14] - b[4] * a[15] - b[27] * a[16] - b[28] * a[17] - b[6] * a[18] - b[29] * a[19] - b[7] * a[20] - b[8] * a[21] - b[30] * a[22] - b[10] * a[23] - b[11] * a[24] - b[13] * a[25] + b[31] * a[26] + b[16] * a[27] + b[17] * a[28] + b[19] * a[29] + b[22] * a[30] + b[26] * a[31];
        res[6] = b[6] * a[0] + b[2] * a[1] - b[1] * a[2] + b[16] * a[3] + b[17] * a[4] - b[18] * a[5] + b[0] * a[6] - b[10] * a[7] - b[11] * a[8] + b[12] * a[9] + b[7] * a[10] + b[8] * a[11] - b[9] * a[12] - b[26] * a[13] + b[27] * a[14] + b[28] * a[15] + b[3] * a[16] + b[4] * a[17] - b[5] * a[18] - b[22] * a[19] + b[23] * a[20] + b[24] * a[21] + b[19] * a[22] - b[20] * a[23] - b[21] * a[24] + b[31] * a[25] - b[13] * a[26] + b[14] * a[27] + b[15] * a[28] - b[30] * a[29] + b[29] * a[30] + b[25] * a[31];
        res[7] = b[7] * a[0] + b[3] * a[1] - b[16] * a[2] - b[1] * a[3] + b[19] * a[4] - b[20] * a[5] + b[10] * a[6] + b[0] * a[7] - b[13] * a[8] + b[14] * a[9] - b[6] * a[10] + b[26] * a[11] - b[27] * a[12] + b[8] * a[13] - b[9] * a[14] + b[29] * a[15] - b[2] * a[16] + b[22] * a[17] - b[23] * a[18] + b[4] * a[19] - b[5] * a[20] + b[25] * a[21] - b[17] * a[22] + b[18] * a[23] - b[31] * a[24] - b[21] * a[25] + b[11] * a[26] - b[12] * a[27] + b[30] * a[28] + b[15] * a[29] - b[28] * a[30] - b[24] * a[31];
        res[8] = b[8] * a[0] + b[4] * a[1] - b[17] * a[2] - b[19] * a[3] - b[1] * a[4] - b[21] * a[5] + b[11] * a[6] + b[13] * a[7] + b[0] * a[8] + b[15] * a[9] - b[26] * a[10] - b[6] * a[11] - b[28] * a[12] - b[7] * a[13] - b[29] * a[14] - b[9] * a[15] - b[22] * a[16] - b[2] * a[17] - b[24] * a[18] - b[3] * a[19] - b[25] * a[20] - b[5] * a[21] + b[16] * a[22] + b[31] * a[23] + b[18] * a[24] + b[20] * a[25] - b[10] * a[26] - b[30] * a[27] - b[12] * a[28] - b[14] * a[29] + b[27] * a[30] + b[23] * a[31];
        res[9] = b[9] * a[0] + b[5] * a[1] - b[18] * a[2] - b[20] * a[3] - b[21] * a[4] - b[1] * a[5] + b[12] * a[6] + b[14] * a[7] + b[15] * a[8] + b[0] * a[9] - b[27] * a[10] - b[28] * a[11] - b[6] * a[12] - b[29] * a[13] - b[7] * a[14] - b[8] * a[15] - b[23] * a[16] - b[24] * a[17] - b[2] * a[18] - b[25] * a[19] - b[3] * a[20] - b[4] * a[21] + b[31] * a[22] + b[16] * a[23] + b[17] * a[24] + b[19] * a[25] - b[30] * a[26] - b[10] * a[27] - b[11] * a[28] - b[13] * a[29] + b[26] * a[30] + b[22] * a[31];
        res[10] = b[10] * a[0] + b[16] * a[1] + b[3] * a[2] - b[2] * a[3] + b[22] * a[4] - b[23] * a[5] - b[7] * a[6] + b[6] * a[7] - b[26] * a[8] + b[27] * a[9] + b[0] * a[10] - b[13] * a[11] + b[14] * a[12] + b[11] * a[13] - b[12] * a[14] + b[30] * a[15] + b[1] * a[16] - b[19] * a[17] + b[20] * a[18] + b[17] * a[19] - b[18] * a[20] + b[31] * a[21] + b[4] * a[22] - b[5] * a[23] + b[25] * a[24] - b[24] * a[25] - b[8] * a[26] + b[9] * a[27] - b[29] * a[28] + b[28] * a[29] + b[15] * a[30] + b[21] * a[31];
        res[11] = b[11] * a[0] + b[17] * a[1] + b[4] * a[2] - b[22] * a[3] - b[2] * a[4] - b[24] * a[5] - b[8] * a[6] + b[26] * a[7] + b[6] * a[8] + b[28] * a[9] + b[13] * a[10] + b[0] * a[11] + b[15] * a[12] - b[10] * a[13] - b[30] * a[14] - b[12] * a[15] + b[19] * a[16] + b[1] * a[17] + b[21] * a[18] - b[16] * a[19] - b[31] * a[20] - b[18] * a[21] - b[3] * a[22] - b[25] * a[23] - b[5] * a[24] + b[23] * a[25] + b[7] * a[26] + b[29] * a[27] + b[9] * a[28] - b[27] * a[29] - b[14] * a[30] - b[20] * a[31];
        res[12] = b[12] * a[0] + b[18] * a[1] + b[5] * a[2] - b[23] * a[3] - b[24] * a[4] - b[2] * a[5] - b[9] * a[6] + b[27] * a[7] + b[28] * a[8] + b[6] * a[9] + b[14] * a[10] + b[15] * a[11] + b[0] * a[12] - b[30] * a[13] - b[10] * a[14] - b[11] * a[15] + b[20] * a[16] + b[21] * a[17] + b[1] * a[18] - b[31] * a[19] - b[16] * a[20] - b[17] * a[21] - b[25] * a[22] - b[3] * a[23] - b[4] * a[24] + b[22] * a[25] + b[29] * a[26] + b[7] * a[27] + b[8] * a[28] - b[26] * a[29] - b[13] * a[30] - b[19] * a[31];
        res[13] = b[13] * a[0] + b[19] * a[1] + b[22] * a[2] + b[4] * a[3] - b[3] * a[4] - b[25] * a[5] - b[26] * a[6] - b[8] * a[7] + b[7] * a[8] + b[29] * a[9] - b[11] * a[10] + b[10] * a[11] + b[30] * a[12] + b[0] * a[13] + b[15] * a[14] - b[14] * a[15] - b[17] * a[16] + b[16] * a[17] + b[31] * a[18] + b[1] * a[19] + b[21] * a[20] - b[20] * a[21] + b[2] * a[22] + b[24] * a[23] - b[23] * a[24] - b[5] * a[25] - b[6] * a[26] - b[28] * a[27] + b[27] * a[28] + b[9] * a[29] + b[12] * a[30] + b[18] * a[31];
        res[14] = b[14] * a[0] + b[20] * a[1] + b[23] * a[2] + b[5] * a[3] - b[25] * a[4] - b[3] * a[5] - b[27] * a[6] - b[9] * a[7] + b[29] * a[8] + b[7] * a[9] - b[12] * a[10] + b[30] * a[11] + b[10] * a[12] + b[15] * a[13] + b[0] * a[14] - b[13] * a[15] - b[18] * a[16] + b[31] * a[17] + b[16] * a[18] + b[21] * a[19] + b[1] * a[20] - b[19] * a[21] + b[24] * a[22] + b[2] * a[23] - b[22] * a[24] - b[4] * a[25] - b[28] * a[26] - b[6] * a[27] + b[26] * a[28] + b[8] * a[29] + b[11] * a[30] + b[17] * a[31];
        res[15] = b[15] * a[0] + b[21] * a[1] + b[24] * a[2] + b[25] * a[3] + b[5] * a[4] - b[4] * a[5] - b[28] * a[6] - b[29] * a[7] - b[9] * a[8] + b[8] * a[9] - b[30] * a[10] - b[12] * a[11] + b[11] * a[12] - b[14] * a[13] + b[13] * a[14] + b[0] * a[15] - b[31] * a[16] - b[18] * a[17] + b[17] * a[18] - b[20] * a[19] + b[19] * a[20] + b[1] * a[21] - b[23] * a[22] + b[22] * a[23] + b[2] * a[24] + b[3] * a[25] + b[27] * a[26] - b[26] * a[27] - b[6] * a[28] - b[7] * a[29] - b[10] * a[30] - b[16] * a[31];
        res[16] = b[16] * a[0] + b[10] * a[1] - b[7] * a[2] + b[6] * a[3] - b[26] * a[4] + b[27] * a[5] + b[3] * a[6] - b[2] * a[7] + b[22] * a[8] - b[23] * a[9] + b[1] * a[10] - b[19] * a[11] + b[20] * a[12] + b[17] * a[13] - b[18] * a[14] + b[31] * a[15] + b[0] * a[16] - b[13] * a[17] + b[14] * a[18] + b[11] * a[19] - b[12] * a[20] + b[30] * a[21] - b[8] * a[22] + b[9] * a[23] - b[29] * a[24] + b[28] * a[25] + b[4] * a[26] - b[5] * a[27] + b[25] * a[28] - b[24] * a[29] + b[21] * a[30] + b[15] * a[31];
        res[17] = b[17] * a[0] + b[11] * a[1] - b[8] * a[2] + b[26] * a[3] + b[6] * a[4] + b[28] * a[5] + b[4] * a[6] - b[22] * a[7] - b[2] * a[8] - b[24] * a[9] + b[19] * a[10] + b[1] * a[11] + b[21] * a[12] - b[16] * a[13] - b[31] * a[14] - b[18] * a[15] + b[13] * a[16] + b[0] * a[17] + b[15] * a[18] - b[10] * a[19] - b[30] * a[20] - b[12] * a[21] + b[7] * a[22] + b[29] * a[23] + b[9] * a[24] - b[27] * a[25] - b[3] * a[26] - b[25] * a[27] - b[5] * a[28] + b[23] * a[29] - b[20] * a[30] - b[14] * a[31];
        res[18] = b[18] * a[0] + b[12] * a[1] - b[9] * a[2] + b[27] * a[3] + b[28] * a[4] + b[6] * a[5] + b[5] * a[6] - b[23] * a[7] - b[24] * a[8] - b[2] * a[9] + b[20] * a[10] + b[21] * a[11] + b[1] * a[12] - b[31] * a[13] - b[16] * a[14] - b[17] * a[15] + b[14] * a[16] + b[15] * a[17] + b[0] * a[18] - b[30] * a[19] - b[10] * a[20] - b[11] * a[21] + b[29] * a[22] + b[7] * a[23] + b[8] * a[24] - b[26] * a[25] - b[25] * a[26] - b[3] * a[27] - b[4] * a[28] + b[22] * a[29] - b[19] * a[30] - b[13] * a[31];
        res[19] = b[19] * a[0] + b[13] * a[1] - b[26] * a[2] - b[8] * a[3] + b[7] * a[4] + b[29] * a[5] + b[22] * a[6] + b[4] * a[7] - b[3] * a[8] - b[25] * a[9] - b[17] * a[10] + b[16] * a[11] + b[31] * a[12] + b[1] * a[13] + b[21] * a[14] - b[20] * a[15] - b[11] * a[16] + b[10] * a[17] + b[30] * a[18] + b[0] * a[19] + b[15] * a[20] - b[14] * a[21] - b[6] * a[22] - b[28] * a[23] + b[27] * a[24] + b[9] * a[25] + b[2] * a[26] + b[24] * a[27] - b[23] * a[28] - b[5] * a[29] + b[18] * a[30] + b[12] * a[31];
        res[20] = b[20] * a[0] + b[14] * a[1] - b[27] * a[2] - b[9] * a[3] + b[29] * a[4] + b[7] * a[5] + b[23] * a[6] + b[5] * a[7] - b[25] * a[8] - b[3] * a[9] - b[18] * a[10] + b[31] * a[11] + b[16] * a[12] + b[21] * a[13] + b[1] * a[14] - b[19] * a[15] - b[12] * a[16] + b[30] * a[17] + b[10] * a[18] + b[15] * a[19] + b[0] * a[20] - b[13] * a[21] - b[28] * a[22] - b[6] * a[23] + b[26] * a[24] + b[8] * a[25] + b[24] * a[26] + b[2] * a[27] - b[22] * a[28] - b[4] * a[29] + b[17] * a[30] + b[11] * a[31];
        res[21] = b[21] * a[0] + b[15] * a[1] - b[28] * a[2] - b[29] * a[3] - b[9] * a[4] + b[8] * a[5] + b[24] * a[6] + b[25] * a[7] + b[5] * a[8] - b[4] * a[9] - b[31] * a[10] - b[18] * a[11] + b[17] * a[12] - b[20] * a[13] + b[19] * a[14] + b[1] * a[15] - b[30] * a[16] - b[12] * a[17] + b[11] * a[18] - b[14] * a[19] + b[13] * a[20] + b[0] * a[21] + b[27] * a[22] - b[26] * a[23] - b[6] * a[24] - b[7] * a[25] - b[23] * a[26] + b[22] * a[27] + b[2] * a[28] + b[3] * a[29] - b[16] * a[30] - b[10] * a[31];
        res[22] = b[22] * a[0] + b[26] * a[1] + b[13] * a[2] - b[11] * a[3] + b[10] * a[4] + b[30] * a[5] - b[19] * a[6] + b[17] * a[7] - b[16] * a[8] - b[31] * a[9] + b[4] * a[10] - b[3] * a[11] - b[25] * a[12] + b[2] * a[13] + b[24] * a[14] - b[23] * a[15] + b[8] * a[16] - b[7] * a[17] - b[29] * a[18] + b[6] * a[19] + b[28] * a[20] - b[27] * a[21] + b[0] * a[22] + b[15] * a[23] - b[14] * a[24] + b[12] * a[25] - b[1] * a[26] - b[21] * a[27] + b[20] * a[28] - b[18] * a[29] - b[5] * a[30] - b[9] * a[31];
        res[23] = b[23] * a[0] + b[27] * a[1] + b[14] * a[2] - b[12] * a[3] + b[30] * a[4] + b[10] * a[5] - b[20] * a[6] + b[18] * a[7] - b[31] * a[8] - b[16] * a[9] + b[5] * a[10] - b[25] * a[11] - b[3] * a[12] + b[24] * a[13] + b[2] * a[14] - b[22] * a[15] + b[9] * a[16] - b[29] * a[17] - b[7] * a[18] + b[28] * a[19] + b[6] * a[20] - b[26] * a[21] + b[15] * a[22] + b[0] * a[23] - b[13] * a[24] + b[11] * a[25] - b[21] * a[26] - b[1] * a[27] + b[19] * a[28] - b[17] * a[29] - b[4] * a[30] - b[8] * a[31];
        res[24] = b[24] * a[0] + b[28] * a[1] + b[15] * a[2] - b[30] * a[3] - b[12] * a[4] + b[11] * a[5] - b[21] * a[6] + b[31] * a[7] + b[18] * a[8] - b[17] * a[9] + b[25] * a[10] + b[5] * a[11] - b[4] * a[12] - b[23] * a[13] + b[22] * a[14] + b[2] * a[15] + b[29] * a[16] + b[9] * a[17] - b[8] * a[18] - b[27] * a[19] + b[26] * a[20] + b[6] * a[21] - b[14] * a[22] + b[13] * a[23] + b[0] * a[24] - b[10] * a[25] + b[20] * a[26] - b[19] * a[27] - b[1] * a[28] + b[16] * a[29] + b[3] * a[30] + b[7] * a[31];
        res[25] = b[25] * a[0] + b[29] * a[1] + b[30] * a[2] + b[15] * a[3] - b[14] * a[4] + b[13] * a[5] - b[31] * a[6] - b[21] * a[7] + b[20] * a[8] - b[19] * a[9] - b[24] * a[10] + b[23] * a[11] - b[22] * a[12] + b[5] * a[13] - b[4] * a[14] + b[3] * a[15] - b[28] * a[16] + b[27] * a[17] - b[26] * a[18] + b[9] * a[19] - b[8] * a[20] + b[7] * a[21] + b[12] * a[22] - b[11] * a[23] + b[10] * a[24] + b[0] * a[25] - b[18] * a[26] + b[17] * a[27] - b[16] * a[28] - b[1] * a[29] - b[2] * a[30] - b[6] * a[31];
        res[26] = b[26] * a[0] + b[22] * a[1] - b[19] * a[2] + b[17] * a[3] - b[16] * a[4] - b[31] * a[5] + b[13] * a[6] - b[11] * a[7] + b[10] * a[8] + b[30] * a[9] + b[8] * a[10] - b[7] * a[11] - b[29] * a[12] + b[6] * a[13] + b[28] * a[14] - b[27] * a[15] + b[4] * a[16] - b[3] * a[17] - b[25] * a[18] + b[2] * a[19] + b[24] * a[20] - b[23] * a[21] - b[1] * a[22] - b[21] * a[23] + b[20] * a[24] - b[18] * a[25] + b[0] * a[26] + b[15] * a[27] - b[14] * a[28] + b[12] * a[29] - b[9] * a[30] - b[5] * a[31];
        res[27] = b[27] * a[0] + b[23] * a[1] - b[20] * a[2] + b[18] * a[3] - b[31] * a[4] - b[16] * a[5] + b[14] * a[6] - b[12] * a[7] + b[30] * a[8] + b[10] * a[9] + b[9] * a[10] - b[29] * a[11] - b[7] * a[12] + b[28] * a[13] + b[6] * a[14] - b[26] * a[15] + b[5] * a[16] - b[25] * a[17] - b[3] * a[18] + b[24] * a[19] + b[2] * a[20] - b[22] * a[21] - b[21] * a[22] - b[1] * a[23] + b[19] * a[24] - b[17] * a[25] + b[15] * a[26] + b[0] * a[27] - b[13] * a[28] + b[11] * a[29] - b[8] * a[30] - b[4] * a[31];
        res[28] = b[28] * a[0] + b[24] * a[1] - b[21] * a[2] + b[31] * a[3] + b[18] * a[4] - b[17] * a[5] + b[15] * a[6] - b[30] * a[7] - b[12] * a[8] + b[11] * a[9] + b[29] * a[10] + b[9] * a[11] - b[8] * a[12] - b[27] * a[13] + b[26] * a[14] + b[6] * a[15] + b[25] * a[16] + b[5] * a[17] - b[4] * a[18] - b[23] * a[19] + b[22] * a[20] + b[2] * a[21] + b[20] * a[22] - b[19] * a[23] - b[1] * a[24] + b[16] * a[25] - b[14] * a[26] + b[13] * a[27] + b[0] * a[28] - b[10] * a[29] + b[7] * a[30] + b[3] * a[31];
        res[29] = b[29] * a[0] + b[25] * a[1] - b[31] * a[2] - b[21] * a[3] + b[20] * a[4] - b[19] * a[5] + b[30] * a[6] + b[15] * a[7] - b[14] * a[8] + b[13] * a[9] - b[28] * a[10] + b[27] * a[11] - b[26] * a[12] + b[9] * a[13] - b[8] * a[14] + b[7] * a[15] - b[24] * a[16] + b[23] * a[17] - b[22] * a[18] + b[5] * a[19] - b[4] * a[20] + b[3] * a[21] - b[18] * a[22] + b[17] * a[23] - b[16] * a[24] - b[1] * a[25] + b[12] * a[26] - b[11] * a[27] + b[10] * a[28] + b[0] * a[29] - b[6] * a[30] - b[2] * a[31];
        res[30] = b[30] * a[0] + b[31] * a[1] + b[25] * a[2] - b[24] * a[3] + b[23] * a[4] - b[22] * a[5] - b[29] * a[6] + b[28] * a[7] - b[27] * a[8] + b[26] * a[9] + b[15] * a[10] - b[14] * a[11] + b[13] * a[12] + b[12] * a[13] - b[11] * a[14] + b[10] * a[15] + b[21] * a[16] - b[20] * a[17] + b[19] * a[18] + b[18] * a[19] - b[17] * a[20] + b[16] * a[21] + b[5] * a[22] - b[4] * a[23] + b[3] * a[24] - b[2] * a[25] - b[9] * a[26] + b[8] * a[27] - b[7] * a[28] + b[6] * a[29] + b[0] * a[30] + b[1] * a[31];
        res[31] = b[31] * a[0] + b[30] * a[1] - b[29] * a[2] + b[28] * a[3] - b[27] * a[4] + b[26] * a[5] + b[25] * a[6] - b[24] * a[7] + b[23] * a[8] - b[22] * a[9] + b[21] * a[10] - b[20] * a[11] + b[19] * a[12] + b[18] * a[13] - b[17] * a[14] + b[16] * a[15] + b[15] * a[16] - b[14] * a[17] + b[13] * a[18] + b[12] * a[19] - b[11] * a[20] + b[10] * a[21] - b[9] * a[22] + b[8] * a[23] - b[7] * a[24] + b[6] * a[25] + b[5] * a[26] - b[4] * a[27] + b[3] * a[28] - b[2] * a[29] + b[1] * a[30] + b[0] * a[31];
        return res;
    }

    
    @Test
    public void testGPSparsity() {

        //TestExprGraphFactory fac = TestExprGraphFactory.instance();
        GAFactory exprGraphFactory = TestExprGraphFactory.instance();
        MatrixSparsity sparsity = new ColumnVectorSparsity(new double[]{0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, true); // e2
        System.out.println("sparsity = "+sparsity.toString());
        // mva hat generisch grade 1 sparsity also e1-e5 und nicht nur e2!!! Ursache: Cache
        MultivectorVariable mva = exprGraphFactory.createVariable("a", sparsity); 
        
        //sparsity = new ColumnVectorSparsity(new double[]{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}); // E3
// E3

        sparsity = new ColumnVectorSparsity(new double[]{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, true); // scalar
        MultivectorVariable mvb = exprGraphFactory.createVariable("b", sparsity); // E3
        System.out.println("testGPSparsity:");
        MultivectorExpression res = mva.geometricProduct(mvb);
        // a b = T{00, 00, 00, 00, 00, 00, null, (-(b_0*a_0)), null, null, null, null, null, null, null, null, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, null, null, null, null, null, 00}
        // aus debugger stimmt überein
        // [00, 00, 00, 00, 00, 00, 0, -1, 0, 0, 0, 0, 0, 0, 0, 0, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 0, 0, 0, 0, 0, 00]
        //FIXME mir scheint es, dass die null-Werte 00 sein sollten
        System.out.println("a b = " + res.toString());
        System.out.println(res.getSparsity().toString());
    }

    
     @Test
    public void testGPSparsity2() {

        //TestExprGraphFactory fac = TestExprGraphFactory.instance();
        GAFactory exprGraphFactory = TestExprGraphFactory.instance();
         MatrixSparsity sparsity = new ColumnVectorSparsity(new double[]{0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, true); // e2+e2+e3
        System.out.println("sparsity = "+sparsity.toString());
        // mva hat generisch grade 1 sparsity also e1-e5 und nicht nur e2!!! Ursache: Cache
        MultivectorVariable mva = exprGraphFactory.createVariable("a", sparsity); 
       
        
        MultivectorVariable mvb = exprGraphFactory.createVariable("b", sparsity); 
        System.out.println("testGPSparsity:");
        MultivectorExpression res = mva.geometricProduct(mvb);
        
        System.out.println("a b = " + res.toString());
        System.out.println(res.getSparsity().toString());
    }
    
    // gradeInvolution==gradeInversion
    @Test
    public void testInvoluteRandom() {
        GAFactory exprGraphFactory = TestExprGraphFactory.instance();
        MultivectorVariable mv = exprGraphFactory.createVariable("mv", 1);
        System.out.println("mv (sparsity): " + mv.getSparsity().toString());
        System.out.println("mv: " + mv.toString());
        MultivectorExpression result = mv.gradeInversion();
        System.out.println("result (sym): " + result.toString());

        List<MultivectorVariable> parameters = new ArrayList<>();
        parameters.add(mv);

        List<MultivectorExpression> res = new ArrayList<>();
        res.add(result);
        GAFunction f = exprGraphFactory.createFunction("f", parameters, res);

        List<MultivectorValue> arguments = new ArrayList<>();
        double[] randomValues = createValueRandom(1);
        MultivectorValue arg = createValue(exprGraphFactory, randomValues);
        arguments.add(arg);

        try {
            List<MultivectorValue> result2 = f.callValue(arguments);
            MultivectorValue out = result2.iterator().next();
            //System.out.println("b=reverse(a)="+out.toString());
            double[] values = (new SparseDoubleColumnVector(out.elements())).toArray();
            assertTrue(equals(values, involute(randomValues), ColumnVectorSparsity.instance(mv.getSparsity())));
        } catch (Exception e) {
        }
    }

    @Test
    public void testNegate14Random() {
        GAFactory exprGraphFactory = TestExprGraphFactory.instance();
        MultivectorVariable mv = exprGraphFactory.createVariable("mv", 1);
        System.out.println("mv (sparsity): " + mv.getSparsity().toString());
        System.out.println("mv: " + mv.toString());
        MultivectorExpression result = mv.negate14();
        System.out.println("result (sym): " + result.toString());

        List<MultivectorVariable> parameters = new ArrayList<>();
        parameters.add(mv);

        List<MultivectorExpression> res = new ArrayList<>();
        res.add(result);
        GAFunction f = exprGraphFactory.createFunction("f", parameters, res);

        List<MultivectorValue> arguments = new ArrayList<>();
        double[] randomValues = createValueRandom(1);
        MultivectorValue arg = createValue(exprGraphFactory, randomValues);
        arguments.add(arg);

        try {
            List<MultivectorValue> result2 = f.callValue(arguments);
            MultivectorValue out = result2.iterator().next();
            System.out.println("negate14()=" + out.toString());
            double[] values = (new SparseDoubleColumnVector(out.elements())).toArray();
            double[] test = negate14(randomValues);
            System.out.println("test=" + out.toString());
            assertTrue(equals(values, test, ColumnVectorSparsity.instance(mv.getSparsity())));
        } catch (Exception e) {
        }
    }

    /**
     * involute.
     *
     * Main involution - grade inversion<p>
     *
     * @param a
     * @return
     */
    private static double[] involute(double[] _mVec) {
        double[] res = new double[32];
        res[0] = _mVec[0];
        res[1] = -_mVec[1];
        res[2] = -_mVec[2];
        res[3] = -_mVec[3];
        res[4] = -_mVec[4];
        res[5] = -_mVec[5];
        res[6] = _mVec[6];
        res[7] = _mVec[7];
        res[8] = _mVec[8];
        res[9] = _mVec[9];
        res[10] = _mVec[10];
        res[11] = _mVec[11];
        res[12] = _mVec[12];
        res[13] = _mVec[13];
        res[14] = _mVec[14];
        res[15] = _mVec[15];
        res[16] = -_mVec[16];
        res[17] = -_mVec[17];
        res[18] = -_mVec[18];
        res[19] = -_mVec[19];
        res[20] = -_mVec[20];
        res[21] = -_mVec[21];
        res[22] = -_mVec[22];
        res[23] = -_mVec[23];
        res[24] = -_mVec[24];
        res[25] = -_mVec[25];
        res[26] = _mVec[26];
        res[27] = _mVec[27];
        res[28] = _mVec[28];
        res[29] = _mVec[29];
        res[30] = _mVec[30];
        res[31] = -_mVec[31];
        return res;
    }

    @Test
    public void testReverseRandom() {
        GAFactory exprGraphFactory = TestExprGraphFactory.instance();
        MultivectorVariable mv = exprGraphFactory.createVariableDense("mv");
        System.out.println("reverse (sparsity): " + mv.getSparsity().toString());
        //System.out.println("mv: "+mv.toString());
        MultivectorExpression result = mv.reverse();
        System.out.println("result (sym): " + result.toString());

        List<MultivectorVariable> parameters = new ArrayList<>();
        parameters.add(mv);

        List<MultivectorExpression> res = new ArrayList<>();
        res.add(result);
        GAFunction f = exprGraphFactory.createFunction("f", parameters, res);

        List<MultivectorValue> arguments = new ArrayList<>();
        double[] randomValues = createValueRandom();
        MultivectorValue arg = createValue(exprGraphFactory, randomValues);
        arguments.add(arg);

        try {
            List<MultivectorValue> result2 = f.callValue(arguments);
            MultivectorValue out = result2.iterator().next();
            System.out.println("reverse(a)=" + out.toString());
            double[] values = (new SparseDoubleColumnVector(out.elements())).toArray();
            assertTrue(equals(values, reverse(randomValues), ColumnVectorSparsity.instance(mv.getSparsity())));
        } catch (Exception e) {
        }
    }

    private static double[] reverse(double[] a) {
        double[] res = new double[32];
        res[0] = a[0];
        res[1] = a[1];
        res[2] = a[2];
        res[3] = a[3];
        res[4] = a[4];
        res[5] = a[5];
        res[6] = -a[6];
        res[7] = -a[7];
        res[8] = -a[8];
        res[9] = -a[9];
        res[10] = -a[10];
        res[11] = -a[11];
        res[12] = -a[12];
        res[13] = -a[13];
        res[14] = -a[14];
        res[15] = -a[15];
        res[16] = -a[16];
        res[17] = -a[17];
        res[18] = -a[18];
        res[19] = -a[19];
        res[20] = -a[20];
        res[21] = -a[21];
        res[22] = -a[22];
        res[23] = -a[23];
        res[24] = -a[24];
        res[25] = -a[25];
        res[26] = a[26];
        res[27] = a[27];
        res[28] = a[28];
        res[29] = a[29];
        res[30] = a[30];
        res[31] = a[31];
        return res;
    }

    /**
     * Grade projection/extraction.
     *
     * Retrives the k-grade part of the multivector.
     *
     * @param grade
     * @return k-grade part of the multivector
     * @throws IllegalArgumentException if grade <0 or grade > 5
     */
    public static double[] gradeSelection(double[] _mVec, int grade) {
        if (grade > 5 || grade < 0) {
            throw new IllegalArgumentException("Grade " + String.valueOf(grade) + " not allowed!");
        }

        double[] arr = new double[32];
        switch (grade) {
            case 0 ->
                arr[0] = _mVec[0];
            case 1 -> {
                for (int i = 1; i <= 5; i++) {
                    arr[i] = _mVec[i];
                }
            }
            case 2 -> {
                for (int i = 6; i <= 15; i++) {
                    arr[i] = _mVec[i];
                }
            }
            case 3 -> {
                for (int i = 16; i <= 25; i++) {
                    arr[i] = _mVec[i];
                }
            }
            case 4 -> {
                for (int i = 26; i <= 30; i++) {
                    arr[i] = _mVec[i];
                }
            }
            case 5 ->
                arr[31] = _mVec[31];
        }
        return arr;
    }

    @Test
    public void testDualRandom() {
        GAFactory exprGraphFactory = TestExprGraphFactory.instance();
        MultivectorVariable mv = exprGraphFactory.createVariableDense("mv");
        System.out.println("mv (sparsity für dual): " + mv.getSparsity().toString());
        System.out.println("mv: (dual) " + mv.toString());
        // dual() basiert derzeit auf dot
        MultivectorExpression result = mv.dual();
        System.out.println("result (dual) (sym): " + result.toString());

        List<MultivectorVariable> parameters = new ArrayList<>();
        parameters.add(mv);

        List<MultivectorExpression> res = new ArrayList<>();
        res.add(result);
        GAFunction f = exprGraphFactory.createFunction("f", parameters, res);

        List<MultivectorValue> arguments = new ArrayList<>();
        double[] randomValues = createValueRandom();
        MultivectorValue arg = createValue(exprGraphFactory, randomValues);
        arguments.add(arg);

        try {
            List<MultivectorValue> result2 = f.callValue(arguments);
            MultivectorValue out = result2.iterator().next();
            System.out.println("dual(a)=" + out.toString());
            double[] values = (new SparseDoubleColumnVector(out.elements())).toArray();

            double[] test = dual(randomValues);
            DenseDoubleColumnVector testMatrix = new DenseDoubleColumnVector(test);
            System.out.println(testMatrix.toString());
            assertTrue(equals(values, test, ColumnVectorSparsity.instance(mv.getSparsity())));
        } catch (Exception e) {
        }
    }

    @Test
    public void testNormRandom() {
        GAFactory exprGraphFactory = TestExprGraphFactory.instance();
        MultivectorVariable mv = exprGraphFactory.createVariableDense("mv");
        MultivectorExpression result = mv.norm();

        List<MultivectorVariable> parameters = new ArrayList<>();
        parameters.add(mv);

        List<MultivectorExpression> res = new ArrayList<>();
        res.add(result);
        GAFunction f = exprGraphFactory.createFunction("f", parameters, res);

        List<MultivectorValue> arguments = new ArrayList<>();
        double[] randomValues = createValueRandom();
        MultivectorValue arg = createValue(exprGraphFactory, randomValues);
        arguments.add(arg);

        try {
            List<MultivectorValue> result2 = f.callValue(arguments);
            MultivectorValue out = result2.iterator().next();
            System.out.println("norm(a)=" + out.toString());
            double[] values = (new SparseDoubleColumnVector(out.elements())).toArray();
            double test = norm(randomValues);
            System.out.println("test norm(a)=" + String.valueOf(test));
            assertTrue(equals(values, new double[]{test}));
        } catch (Exception e) {
        }
    }

    @Test
    public void testNormalizeRandom() {
        GAFactory exprGraphFactory = TestExprGraphFactory.instance();
        MultivectorVariable mv = exprGraphFactory.createVariableDense("mv");
        MultivectorExpression result = mv.normalize(); // normalize by squared norm

        List<MultivectorVariable> parameters = new ArrayList<>();
        parameters.add(mv);

        List<MultivectorExpression> res = new ArrayList<>();
        res.add(result);
        GAFunction f = exprGraphFactory.createFunction("f", parameters, res);

        List<MultivectorValue> arguments = new ArrayList<>();
        double[] randomValues = createValueRandom();
        MultivectorValue arg = createValue(exprGraphFactory, randomValues);
        arguments.add(arg);

        try {
            List<MultivectorValue> result2 = f.callValue(arguments);
            MultivectorValue out = result2.iterator().next();
            System.out.println("normalize(a)=" + out.toString());
            double[] values = (new SparseDoubleColumnVector(out.elements())).toArray();
            double[] test = normalize(randomValues);
            DenseDoubleColumnVector testMatrix = new DenseDoubleColumnVector(test);
            System.out.println("test normalize(a)=" + testMatrix.toString());
            double eps = 0.00001;
            assertTrue(equals(values, test, eps));
        } catch (Exception e) {
        }
    }

    /**
     * Creates a general rotor with 16 indizes, this is not only grade 2
     * 
     * @return random general rotor
     */
    private static double[] createRandomRotor(){
        GAFactory exprGraphFactory = TestExprGraphFactory.instance();
        return createValueRandom(CGACayleyTable.getEvenIndizes());
    }
   
    
    @Test
    public void testNormalizeRotorRandom() {

        GAFactory exprGraphFactory = TestExprGraphFactory.instance();
        
        MultivectorVariable mva = exprGraphFactory.createVariable("a", CGACayleyTable.getEvenGrades());
        
        List<MultivectorVariable> parameters = new ArrayList<>();
        parameters.add(mva);
        
        MultivectorExpression res = mva.normalizeRotor();
        System.out.println("normalizeRotorRandom: " + res.toString());

        List<MultivectorExpression> result = new ArrayList<>();
        result.add(res);
        GAFunction f = exprGraphFactory.createFunction("f", parameters, result);

        List<MultivectorValue> arguments = new ArrayList<>();

        double[] values_A = createRandomRotor();
        MultivectorValue arg_a = createValue(exprGraphFactory, values_A);
        arguments.add(arg_a);

        double[] test = normalizeRotor(values_A);
        DenseDoubleColumnVector testMatrix = new DenseDoubleColumnVector(test);
        
        //FIXME liefert anderes Ergebnis
        double[] test2 = normalize(values_A);
        DenseDoubleColumnVector test2Matrix = new DenseDoubleColumnVector(test2);
        
        try {
            System.out.println("random a=" + arg_a.toString());
            List<MultivectorValue> result2 = f.callValue(arguments);
            MultivectorValue mv = result2.iterator().next();
            System.out.println("normalizeRotor(a)=" + mv.toString());
            System.out.println("test normalizeRotor(a)=" + testMatrix.toString());
            System.out.println("test2 normalize(a)="+test2Matrix.toString());
            
            double eps = 0.00001;
            assertTrue(equals((new SparseDoubleColumnVector(mv.elements())).toArray(), test, eps));
        } catch (Exception e) {
        }
    }
    
    
    @Test
    public void testSqrtRotorRandom() {

        GAFactory exprGraphFactory = TestExprGraphFactory.instance();
        
        MultivectorVariable mva = exprGraphFactory.createVariable("a", CGACayleyTable.getEvenGrades());
        
        List<MultivectorVariable> parameters = new ArrayList<>();
        parameters.add(mva);
        
        MultivectorExpression res = mva.sqrt();
        System.out.println("sqrtRotorRandom: " + res.toString());

        List<MultivectorExpression> result = new ArrayList<>();
        result.add(res);
        GAFunction f = exprGraphFactory.createFunction("f", parameters, result);

        List<MultivectorValue> arguments = new ArrayList<>();

        double[] values_A = createRandomRotor();
        MultivectorValue arg_a = createValue(exprGraphFactory, values_A);
        arguments.add(arg_a);

        double[] test = sqrtRotor(values_A);
        DenseDoubleColumnVector testMatrix = new DenseDoubleColumnVector(test);
        
        try {
            System.out.println("random a=" + arg_a.toString());
            List<MultivectorValue> result2 = f.callValue(arguments);
            MultivectorValue mv = result2.iterator().next();
            System.out.println("sqrtRotor(a)=" + mv.toString());
            System.out.println("test sqrtRotor(a)=" + testMatrix.toString());
            
            double eps = 0.00001;
            assertTrue(equals((new SparseDoubleColumnVector(mv.elements())).toArray(), test, eps));
        } catch (Exception e) {
        }
    }
    
    
    /**
     * Vergleich java reference impl mit versorInverse
     */
    /*@Test
    public void testVersorInverseRandom(){
        ExprGraphFactory exprGraphFactory = TestExprGraphFactory.instance();
        MultivectorSymbolic mv = exprGraphFactory.createMultivectorSymbolic("mv");
        // casadi impl
        MultivectorSymbolic result = mv.versorInverse();
        
        // testweise mit generalInverse vergleichen
        // versorInverse(a)=[-1.74761, 0, 0, 0, 0, 0, 0, 0, -1.04514, -1.04514, 0, -2.52022, -2.52022, 2.18542, 2.18542, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]
        // test versorInverse(a)={-1.7476078159237676, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0451355056345772, 1.0451355056345772, 0.0, 2.520224457151371, 2.520224457151371, -2.185420835978317, -2.185420835978317, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0}
        //--> Vorzeichenfehler
        result = mv.generalInverse();
        
        List<MultivectorSymbolic> parameters = new ArrayList<>();
        parameters.add(mv);
        
        List<MultivectorSymbolic> res = new ArrayList<>();
        res.add(result);
        FunctionSymbolic f = exprGraphFactory.createFunctionSymbolic("f", parameters, res);
        
        List<MultivectorNumeric> arguments = new ArrayList<>();
        double[] randomValues = createRandomTranslator();//createRandomVersor();
        MultivectorNumeric arg = exprGraphFactory.createMultivectorNumeric(randomValues);
        arguments.add(arg);
        
        try {
            List<MultivectorNumeric> result2 = f.callNumeric(arguments);
            MultivectorNumeric out = result2.iterator().next();
            System.out.println("versorInverse(a)="+out.toString());
            double[] values = (new SparseDoubleColumnVector(out.elements())).toArray();
            
            // java reference impl
            double[] test = versorInverse(randomValues);
            
            DenseDoubleColumnVector testMatrix = new DenseDoubleColumnVector(test);
            System.out.println("test versorInverse(a)="+testMatrix.toString());
            double eps = 0.00001;
            assertTrue(equals(values, test,eps));
        } catch (Exception e){}
    }*/
    @Test
    public void testScalarInverseRandom() {
        GAFactory exprGraphFactory = TestExprGraphFactory.instance();
        MultivectorVariable mv = exprGraphFactory.createVariable("mv", 0);
        MultivectorExpression result = mv.scalarInverse();
        //System.out.println("result (sym scalarInverse): "+result.toString());

        List<MultivectorVariable> parameters = new ArrayList<>();
        parameters.add(mv);

        List<MultivectorExpression> res = new ArrayList<>();
        res.add(result);
        GAFunction f = exprGraphFactory.createFunction("f", parameters, res);

        List<MultivectorValue> arguments = new ArrayList<>();
        double[] randomValues = createValueRandom(0);
        MultivectorValue arg = createValue(exprGraphFactory, randomValues);
        arguments.add(arg);

        try {
            List<MultivectorValue> result2 = f.callValue(arguments);
            MultivectorValue out = result2.iterator().next();
            System.out.println("b=scalarInverse(a)=" + out.toString());
            double[] values = (new SparseDoubleColumnVector(out.elements())).toArray();
            assertTrue(values[0] == scalarInverse(randomValues));
        } catch (Exception e) {
        }
    }

    private double scalarInverse(double[] mv) {
        return 1d / mv[0];
    }

    @Test
    public void testConjugateRandom() {
        GAFactory exprGraphFactory = TestExprGraphFactory.instance();
        MultivectorVariable mv = exprGraphFactory.createVariable("mv", 1);
        System.out.println("mv (sparsity): " + mv.getSparsity().toString());
        System.out.println("mv: " + mv.toString());
        MultivectorExpression result = mv.cliffordConjugate();
        System.out.println("result (sym, conjugate): " + result.toString());

        List<MultivectorVariable> parameters = new ArrayList<>();
        parameters.add(mv);

        List<MultivectorExpression> res = new ArrayList<>();
        res.add(result);
        GAFunction f = exprGraphFactory.createFunction("f", parameters, res);

        List<MultivectorValue> arguments = new ArrayList<>();
        double[] randomValues = createValueRandom(1);
        MultivectorValue arg = createValue(exprGraphFactory, randomValues);
        arguments.add(arg);

        try {
            List<MultivectorValue> result2 = f.callValue(arguments);
            MultivectorValue out = result2.iterator().next();
            //System.out.println("b=reverse(a)="+out.toString());
            double[] values = (new SparseDoubleColumnVector(out.elements())).toArray();
            assertTrue(equals(values, conjugate(randomValues), ColumnVectorSparsity.instance(mv.getSparsity())));
        } catch (Exception e) {
        }
    }

    /**
     * Conjugate.
     *
     * Clifford Conjugation
     *
     * @param a
     * @return a.Conjugate()
     */
    private static double[] conjugate(double[] _mVec) {
        double[] res = new double[32];

        res[0] = _mVec[0];

        res[1] = -_mVec[1];
        res[2] = -_mVec[2];
        res[3] = -_mVec[3];
        res[4] = -_mVec[4];
        res[5] = -_mVec[5];
        res[6] = -_mVec[6];
        res[7] = -_mVec[7];
        res[8] = -_mVec[8];
        res[9] = -_mVec[9];
        res[10] = -_mVec[10];
        res[11] = -_mVec[11];
        res[12] = -_mVec[12];
        res[13] = -_mVec[13];
        res[14] = -_mVec[14];
        res[15] = -_mVec[15];

        res[16] = _mVec[16];
        res[17] = _mVec[17];
        res[18] = _mVec[18];
        res[19] = _mVec[19];
        res[20] = _mVec[20];
        res[21] = _mVec[21];
        res[22] = _mVec[22];
        res[23] = _mVec[23];
        res[24] = _mVec[24];
        res[25] = _mVec[25];
        res[26] = _mVec[26];
        res[27] = _mVec[27];
        res[28] = _mVec[28];
        res[29] = _mVec[29];
        res[30] = _mVec[30];

        res[31] = -_mVec[31];
        return res;
    }

    private double[] negate14(double[] _mVec) {
        double[] res = new double[32];
        for (int i = 0; i < 32; i++) {
            int grade = baseCayleyTable.getGrade(i);
            if (grade == 1 || grade == 4) {
                res[i] = -_mVec[i];
            } else {
                res[i] = _mVec[i];
            }
        }
        return res;
    }

    /**
     * Dual.
     *
     * Poincare duality operator.
     *
     * @param a
     * @return !a
     */
    private static double[] dual(double[] values) {
        double[] res = new double[values.length];
        res[0] = -values[31];
        res[1] = -values[30];

        res[2] = values[29];

        res[3] = -values[28];

        res[4] = values[27];
        res[5] = values[26];
        res[6] = values[25];

        res[7] = -values[24];

        res[8] = values[23];
        res[9] = values[22];
        res[10] = values[21];

        res[11] = -values[20];
        res[12] = -values[19];

        res[13] = values[18];
        res[14] = values[17];

        res[15] = -values[16];

        res[16] = values[15];

        res[17] = -values[14];
        res[18] = -values[13];

        res[19] = values[12];
        res[20] = values[11];

        res[21] = -values[10];
        res[22] = -values[9];
        res[23] = -values[8];

        res[24] = values[7];

        res[25] = -values[6];
        res[26] = -values[5];
        res[27] = -values[4];

        res[28] = values[3];

        res[29] = -values[2];

        res[30] = values[1];
        res[31] = values[0];
        return res;
    }

    public static double[] rc(double[] a, double[] b) {
        return reverse(dot(reverse(b), reverse(a)));
    }

    /**
     * Dot.
     *
     * Die Erwartung war inner product defined as left contraction. Was sich allerdings nicht bestätigt hat.
     *
     * @param a
     * @param b
     * @return a | b
     */
    public static double[] dot(double[] a, double[] b) {
        double[] res = new double[a.length];
        res[0] = b[0] * a[0] + b[1] * a[1] + b[2] * a[2] + b[3] * a[3] + b[4] * a[4] - b[5] * a[5] - b[6] * a[6] - b[7] * a[7] - b[8] * a[8] + b[9] * a[9] - b[10] * a[10] - b[11] * a[11] + b[12] * a[12] - b[13] * a[13] + b[14] * a[14] + b[15] * a[15] - b[16] * a[16] - b[17] * a[17] + b[18] * a[18] - b[19] * a[19] + b[20] * a[20] + b[21] * a[21] - b[22] * a[22] + b[23] * a[23] + b[24] * a[24] + b[25] * a[25] + b[26] * a[26] - b[27] * a[27] - b[28] * a[28] - b[29] * a[29] - b[30] * a[30] - b[31] * a[31];
        res[1] = b[1] * a[0] + b[0] * a[1] - b[6] * a[2] - b[7] * a[3] - b[8] * a[4] + b[9] * a[5] + b[2] * a[6] + b[3] * a[7] + b[4] * a[8] - b[5] * a[9] - b[16] * a[10] - b[17] * a[11] + b[18] * a[12] - b[19] * a[13] + b[20] * a[14] + b[21] * a[15] - b[10] * a[16] - b[11] * a[17] + b[12] * a[18] - b[13] * a[19] + b[14] * a[20] + b[15] * a[21] + b[26] * a[22] - b[27] * a[23] - b[28] * a[24] - b[29] * a[25] - b[22] * a[26] + b[23] * a[27] + b[24] * a[28] + b[25] * a[29] - b[31] * a[30] - b[30] * a[31];
        res[2] = b[2] * a[0] + b[6] * a[1] + b[0] * a[2] - b[10] * a[3] - b[11] * a[4] + b[12] * a[5] - b[1] * a[6] + b[16] * a[7] + b[17] * a[8] - b[18] * a[9] + b[3] * a[10] + b[4] * a[11] - b[5] * a[12] - b[22] * a[13] + b[23] * a[14] + b[24] * a[15] + b[7] * a[16] + b[8] * a[17] - b[9] * a[18] - b[26] * a[19] + b[27] * a[20] + b[28] * a[21] - b[13] * a[22] + b[14] * a[23] + b[15] * a[24] - b[30] * a[25] + b[19] * a[26] - b[20] * a[27] - b[21] * a[28] + b[31] * a[29] + b[25] * a[30] + b[29] * a[31];
        res[3] = b[3] * a[0] + b[7] * a[1] + b[10] * a[2] + b[0] * a[3] - b[13] * a[4] + b[14] * a[5] - b[16] * a[6] - b[1] * a[7] + b[19] * a[8] - b[20] * a[9] - b[2] * a[10] + b[22] * a[11] - b[23] * a[12] + b[4] * a[13] - b[5] * a[14] + b[25] * a[15] - b[6] * a[16] + b[26] * a[17] - b[27] * a[18] + b[8] * a[19] - b[9] * a[20] + b[29] * a[21] + b[11] * a[22] - b[12] * a[23] + b[30] * a[24] + b[15] * a[25] - b[17] * a[26] + b[18] * a[27] - b[31] * a[28] - b[21] * a[29] - b[24] * a[30] - b[28] * a[31];
        res[4] = b[4] * a[0] + b[8] * a[1] + b[11] * a[2] + b[13] * a[3] + b[0] * a[4] + b[15] * a[5] - b[17] * a[6] - b[19] * a[7] - b[1] * a[8] - b[21] * a[9] - b[22] * a[10] - b[2] * a[11] - b[24] * a[12] - b[3] * a[13] - b[25] * a[14] - b[5] * a[15] - b[26] * a[16] - b[6] * a[17] - b[28] * a[18] - b[7] * a[19] - b[29] * a[20] - b[9] * a[21] - b[10] * a[22] - b[30] * a[23] - b[12] * a[24] - b[14] * a[25] + b[16] * a[26] + b[31] * a[27] + b[18] * a[28] + b[20] * a[29] + b[23] * a[30] + b[27] * a[31];
        res[5] = b[5] * a[0] + b[9] * a[1] + b[12] * a[2] + b[14] * a[3] + b[15] * a[4] + b[0] * a[5] - b[18] * a[6] - b[20] * a[7] - b[21] * a[8] - b[1] * a[9] - b[23] * a[10] - b[24] * a[11] - b[2] * a[12] - b[25] * a[13] - b[3] * a[14] - b[4] * a[15] - b[27] * a[16] - b[28] * a[17] - b[6] * a[18] - b[29] * a[19] - b[7] * a[20] - b[8] * a[21] - b[30] * a[22] - b[10] * a[23] - b[11] * a[24] - b[13] * a[25] + b[31] * a[26] + b[16] * a[27] + b[17] * a[28] + b[19] * a[29] + b[22] * a[30] + b[26] * a[31];
        res[6] = b[6] * a[0] + b[16] * a[3] + b[17] * a[4] - b[18] * a[5] + b[0] * a[6] - b[26] * a[13] + b[27] * a[14] + b[28] * a[15] + b[3] * a[16] + b[4] * a[17] - b[5] * a[18] + b[31] * a[25] - b[13] * a[26] + b[14] * a[27] + b[15] * a[28] + b[25] * a[31];
        res[7] = b[7] * a[0] - b[16] * a[2] + b[19] * a[4] - b[20] * a[5] + b[0] * a[7] + b[26] * a[11] - b[27] * a[12] + b[29] * a[15] - b[2] * a[16] + b[4] * a[19] - b[5] * a[20] - b[31] * a[24] + b[11] * a[26] - b[12] * a[27] + b[15] * a[29] - b[24] * a[31];
        res[8] = b[8] * a[0] - b[17] * a[2] - b[19] * a[3] - b[21] * a[5] + b[0] * a[8] - b[26] * a[10] - b[28] * a[12] - b[29] * a[14] - b[2] * a[17] - b[3] * a[19] - b[5] * a[21] + b[31] * a[23] - b[10] * a[26] - b[12] * a[28] - b[14] * a[29] + b[23] * a[31];
        res[9] = b[9] * a[0] - b[18] * a[2] - b[20] * a[3] - b[21] * a[4] + b[0] * a[9] - b[27] * a[10] - b[28] * a[11] - b[29] * a[13] - b[2] * a[18] - b[3] * a[20] - b[4] * a[21] + b[31] * a[22] - b[10] * a[27] - b[11] * a[28] - b[13] * a[29] + b[22] * a[31];
        res[10] = b[10] * a[0] + b[16] * a[1] + b[22] * a[4] - b[23] * a[5] - b[26] * a[8] + b[27] * a[9] + b[0] * a[10] + b[30] * a[15] + b[1] * a[16] + b[31] * a[21] + b[4] * a[22] - b[5] * a[23] - b[8] * a[26] + b[9] * a[27] + b[15] * a[30] + b[21] * a[31];
        res[11] = b[11] * a[0] + b[17] * a[1] - b[22] * a[3] - b[24] * a[5] + b[26] * a[7] + b[28] * a[9] + b[0] * a[11] - b[30] * a[14] + b[1] * a[17] - b[31] * a[20] - b[3] * a[22] - b[5] * a[24] + b[7] * a[26] + b[9] * a[28] - b[14] * a[30] - b[20] * a[31];
        res[12] = b[12] * a[0] + b[18] * a[1] - b[23] * a[3] - b[24] * a[4] + b[27] * a[7] + b[28] * a[8] + b[0] * a[12] - b[30] * a[13] + b[1] * a[18] - b[31] * a[19] - b[3] * a[23] - b[4] * a[24] + b[7] * a[27] + b[8] * a[28] - b[13] * a[30] - b[19] * a[31];
        res[13] = b[13] * a[0] + b[19] * a[1] + b[22] * a[2] - b[25] * a[5] - b[26] * a[6] + b[29] * a[9] + b[30] * a[12] + b[0] * a[13] + b[31] * a[18] + b[1] * a[19] + b[2] * a[22] - b[5] * a[25] - b[6] * a[26] + b[9] * a[29] + b[12] * a[30] + b[18] * a[31];
        res[14] = b[14] * a[0] + b[20] * a[1] + b[23] * a[2] - b[25] * a[4] - b[27] * a[6] + b[29] * a[8] + b[30] * a[11] + b[0] * a[14] + b[31] * a[17] + b[1] * a[20] + b[2] * a[23] - b[4] * a[25] - b[6] * a[27] + b[8] * a[29] + b[11] * a[30] + b[17] * a[31];
        res[15] = b[15] * a[0] + b[21] * a[1] + b[24] * a[2] + b[25] * a[3] - b[28] * a[6] - b[29] * a[7] - b[30] * a[10] + b[0] * a[15] - b[31] * a[16] + b[1] * a[21] + b[2] * a[24] + b[3] * a[25] - b[6] * a[28] - b[7] * a[29] - b[10] * a[30] - b[16] * a[31];
        res[16] = b[16] * a[0] - b[26] * a[4] + b[27] * a[5] + b[31] * a[15] + b[0] * a[16] + b[4] * a[26] - b[5] * a[27] + b[15] * a[31];
        res[17] = b[17] * a[0] + b[26] * a[3] + b[28] * a[5] - b[31] * a[14] + b[0] * a[17] - b[3] * a[26] - b[5] * a[28] - b[14] * a[31];
        res[18] = b[18] * a[0] + b[27] * a[3] + b[28] * a[4] - b[31] * a[13] + b[0] * a[18] - b[3] * a[27] - b[4] * a[28] - b[13] * a[31];
        res[19] = b[19] * a[0] - b[26] * a[2] + b[29] * a[5] + b[31] * a[12] + b[0] * a[19] + b[2] * a[26] - b[5] * a[29] + b[12] * a[31];
        res[20] = b[20] * a[0] - b[27] * a[2] + b[29] * a[4] + b[31] * a[11] + b[0] * a[20] + b[2] * a[27] - b[4] * a[29] + b[11] * a[31];
        res[21] = b[21] * a[0] - b[28] * a[2] - b[29] * a[3] - b[31] * a[10] + b[0] * a[21] + b[2] * a[28] + b[3] * a[29] - b[10] * a[31];
        res[22] = b[22] * a[0] + b[26] * a[1] + b[30] * a[5] - b[31] * a[9] + b[0] * a[22] - b[1] * a[26] - b[5] * a[30] - b[9] * a[31];
        res[23] = b[23] * a[0] + b[27] * a[1] + b[30] * a[4] - b[31] * a[8] + b[0] * a[23] - b[1] * a[27] - b[4] * a[30] - b[8] * a[31];
        res[24] = b[24] * a[0] + b[28] * a[1] - b[30] * a[3] + b[31] * a[7] + b[0] * a[24] - b[1] * a[28] + b[3] * a[30] + b[7] * a[31];
        res[25] = b[25] * a[0] + b[29] * a[1] + b[30] * a[2] - b[31] * a[6] + b[0] * a[25] - b[1] * a[29] - b[2] * a[30] - b[6] * a[31];
        res[26] = b[26] * a[0] - b[31] * a[5] + b[0] * a[26] - b[5] * a[31];
        res[27] = b[27] * a[0] - b[31] * a[4] + b[0] * a[27] - b[4] * a[31];
        res[28] = b[28] * a[0] + b[31] * a[3] + b[0] * a[28] + b[3] * a[31];
        res[29] = b[29] * a[0] - b[31] * a[2] + b[0] * a[29] - b[2] * a[31];
        res[30] = b[30] * a[0] + b[31] * a[1] + b[0] * a[30] + b[1] * a[31];
        res[31] = b[31] * a[0] + b[0] * a[31];
        return res;
    }

    /**
     * Wedge.
     *
     * The outer product. (MEET)
     *
     * @param a
     * @param b
     * @return a ^ b
     */
    private static double[] op(double[] a, double[] b) {
        double[] res = new double[a.length];

        // oprandom: T{((((((((((((((((((((((((((((((((b_0*a_0)+(b_1*a_1))+(b_2*a_2))+(b_3*a_3))+(b_4*a_4))-(b_5*a_5))-(b_6*a_6))-(b_7*a_7))-(b_8*a_8))+(b_9*a_9))-(b_10*a_10))-(b_11*a_11))+(b_12*a_12))-(b_13*a_13))+(b_14*a_14))+(b_15*a_15))-(b_16*a_16))-(b_17*a_17))+(b_18*a_18))-(b_19*a_19))+(b_20*a_20))+(b_21*a_21))-(b_22*a_22))+(b_23*a_23))+(b_24*a_24))+(b_25*a_25))+(b_26*a_26))-(b_27*a_27))-(b_28*a_28))-(b_29*a_29))-(b_30*a_30))-(b_31*a_31)), ((((((((((((((((b_0*a_1)+(b_2*a_6))+(b_3*a_7))+(b_4*a_8))-(b_5*a_9))-(b_10*a_16))-(b_11*a_17))+(b_12*a_18))-(b_13*a_19))+(b_14*a_20))+(b_15*a_21))-(b_22*a_26))+(b_23*a_27))+(b_24*a_28))+(b_25*a_29))-(b_30*a_31)), 
        //             ((((((((((((((((b_0*a_2)-(b_1*a_6))+(b_3*a_10))+(b_4*a_11))-(b_5*a_12))+(b_7*a_16))+(b_8*a_17))-(b_9*a_18))-(b_13*a_22))+(b_14*a_23))+(b_15*a_24))+(b_19*a_26))-(b_20*a_27))-(b_21*a_28))+(b_25*a_30))+(b_29*a_31)), ((((((((((((((((b_0*a_3)-(b_1*a_7))-(b_2*a_10))+(b_4*a_13))-(b_5*a_14))-(b_6*a_16))+(b_8*a_19))-(b_9*a_20))+(b_11*a_22))-(b_12*a_23))+(b_15*a_25))-(b_17*a_26))+(b_18*a_27))-(b_21*a_29))-(b_24*a_30))-(b_28*a_31)), ((((((((((((((((b_0*a_4)-(b_1*a_8))-(b_2*a_11))-(b_3*a_13))-(b_5*a_15))-(b_6*a_17))-(b_7*a_19))-(b_9*a_21))-(b_10*a_22))-(b_12*a_24))-(b_14*a_25))+(b_16*a_26))+(b_18*a_28))+(b_20*a_29))+(b_23*a_30))+(b_27*a_31)), ((((((((((((((((b_0*a_5)-(b_1*a_9))-(b_2*a_12))-(b_3*a_14))-(b_4*a_15))-(b_6*a_18))-(b_7*a_20))-(b_8*a_21))-(b_10*a_23))-(b_11*a_24))-(b_13*a_25))+(b_16*a_27))+(b_17*a_28))+(b_19*a_29))+(b_22*a_30))+(b_26*a_31)), ((((((((b_0*a_6)+(b_3*a_16))+(b_4*a_17))-(b_5*a_18))-(b_13*a_26))+(b_14*a_27))+(b_15*a_28))+(b_25*a_31)), ((((((((b_0*a_7)-(b_2*a_16))+(b_4*a_19))-(b_5*a_20))+(b_11*a_26))-(b_12*a_27))+(b_15*a_29))-(b_24*a_31)), ((((((((b_0*a_8)-(b_2*a_17))-(b_3*a_19))-(b_5*a_21))-(b_10*a_26))-(b_12*a_28))-(b_14*a_29))+(b_23*a_31)), ((((((((b_0*a_9)-(b_2*a_18))-(b_3*a_20))-(b_4*a_21))-(b_10*a_27))-(b_11*a_28))-(b_13*a_29))+(b_22*a_31)), ((((((((b_0*a_10)+(b_1*a_16))+(b_4*a_22))-(b_5*a_23))-(b_8*a_26))+(b_9*a_27))+(b_15*a_30))+(b_21*a_31)), ((((((((b_0*a_11)+(b_1*a_17))-(b_3*a_22))-(b_5*a_24))+(b_7*a_26))+(b_9*a_28))-(b_14*a_30))-(b_20*a_31)), ((((((((b_0*a_12)+(b_1*a_18))-(b_3*a_23))-(b_4*a_24))+(b_7*a_27))+(b_8*a_28))-(b_13*a_30))-(b_19*a_31)), ((((((((b_0*a_13)+(b_1*a_19))+(b_2*a_22))-(b_5*a_25))-(b_6*a_26))+(b_9*a_29))+(b_12*a_30))+(b_18*a_31)), ((((((((b_0*a_14)+(b_1*a_20))+(b_2*a_23))-(b_4*a_25))-(b_6*a_27))+(b_8*a_29))+(b_11*a_30))+(b_17*a_31)), ((((((((b_0*a_15)+(b_1*a_21))+(b_2*a_24))+(b_3*a_25))-(b_6*a_28))-(b_7*a_29))-(b_10*a_30))-(b_16*a_31)), ((((b_0*a_16)+(b_4*a_26))-(b_5*a_27))+(b_15*a_31)), ((((b_0*a_17)-(b_3*a_26))-(b_5*a_28))-(b_14*a_31)), ((((b_0*a_18)-(b_3*a_27))-(b_4*a_28))-(b_13*a_31)), ((((b_0*a_19)+(b_2*a_26))-(b_5*a_29))+(b_12*a_31)), ((((b_0*a_20)+(b_2*a_27))-(b_4*a_29))+(b_11*a_31)), ((((b_0*a_21)+(b_2*a_28))+(b_3*a_29))-(b_10*a_31)), ((((b_0*a_22)-(b_1*a_26))-(b_5*a_30))-(b_9*a_31)), ((((b_0*a_23)-(b_1*a_27))-(b_4*a_30))-(b_8*a_31)), ((((b_0*a_24)-(b_1*a_28))+(b_3*a_30))+(b_7*a_31)), ((((b_0*a_25)-(b_1*a_29))-(b_2*a_30))-(b_6*a_31)), ((b_0*a_26)-(b_5*a_31)), ((b_0*a_27)-(b_4*a_31)), ((b_0*a_28)+(b_3*a_31)), ((b_0*a_29)-(b_2*a_31)), ((b_0*a_30)+(b_1*a_31)), (b_0*a_31)}
        // sparsity der product matrix scheint nicht berücksichtigt zu werden
        // 
        res[0] = b[0] * a[0];
        res[1] = b[1] * a[0] + b[0] * a[1];
        res[2] = b[2] * a[0] + b[0] * a[2];
        res[3] = b[3] * a[0] + b[0] * a[3];
        res[4] = b[4] * a[0] + b[0] * a[4];
        res[5] = b[5] * a[0] + b[0] * a[5];
        res[6] = b[6] * a[0] + b[2] * a[1] - b[1] * a[2] + b[0] * a[6];
        res[7] = b[7] * a[0] + b[3] * a[1] - b[1] * a[3] + b[0] * a[7];
        res[8] = b[8] * a[0] + b[4] * a[1] - b[1] * a[4] + b[0] * a[8];
        res[9] = b[9] * a[0] + b[5] * a[1] - b[1] * a[5] + b[0] * a[9];
        res[10] = b[10] * a[0] + b[3] * a[2] - b[2] * a[3] + b[0] * a[10];
        res[11] = b[11] * a[0] + b[4] * a[2] - b[2] * a[4] + b[0] * a[11];
        res[12] = b[12] * a[0] + b[5] * a[2] - b[2] * a[5] + b[0] * a[12];
        res[13] = b[13] * a[0] + b[4] * a[3] - b[3] * a[4] + b[0] * a[13];
        res[14] = b[14] * a[0] + b[5] * a[3] - b[3] * a[5] + b[0] * a[14];
        res[15] = b[15] * a[0] + b[5] * a[4] - b[4] * a[5] + b[0] * a[15];
        res[16] = b[16] * a[0] + b[10] * a[1] - b[7] * a[2] + b[6] * a[3] + b[3] * a[6] - b[2] * a[7] + b[1] * a[10] + b[0] * a[16];
        res[17] = b[17] * a[0] + b[11] * a[1] - b[8] * a[2] + b[6] * a[4] + b[4] * a[6] - b[2] * a[8] + b[1] * a[11] + b[0] * a[17];
        res[18] = b[18] * a[0] + b[12] * a[1] - b[9] * a[2] + b[6] * a[5] + b[5] * a[6] - b[2] * a[9] + b[1] * a[12] + b[0] * a[18];
        res[19] = b[19] * a[0] + b[13] * a[1] - b[8] * a[3] + b[7] * a[4] + b[4] * a[7] - b[3] * a[8] + b[1] * a[13] + b[0] * a[19];
        res[20] = b[20] * a[0] + b[14] * a[1] - b[9] * a[3] + b[7] * a[5] + b[5] * a[7] - b[3] * a[9] + b[1] * a[14] + b[0] * a[20];
        res[21] = b[21] * a[0] + b[15] * a[1] - b[9] * a[4] + b[8] * a[5] + b[5] * a[8] - b[4] * a[9] + b[1] * a[15] + b[0] * a[21];
        res[22] = b[22] * a[0] + b[13] * a[2] - b[11] * a[3] + b[10] * a[4] + b[4] * a[10] - b[3] * a[11] + b[2] * a[13] + b[0] * a[22];
        res[23] = b[23] * a[0] + b[14] * a[2] - b[12] * a[3] + b[10] * a[5] + b[5] * a[10] - b[3] * a[12] + b[2] * a[14] + b[0] * a[23];
        res[24] = b[24] * a[0] + b[15] * a[2] - b[12] * a[4] + b[11] * a[5] + b[5] * a[11] - b[4] * a[12] + b[2] * a[15] + b[0] * a[24];
        res[25] = b[25] * a[0] + b[15] * a[3] - b[14] * a[4] + b[13] * a[5] + b[5] * a[13] - b[4] * a[14] + b[3] * a[15] + b[0] * a[25];
        res[26] = b[26] * a[0] + b[22] * a[1] - b[19] * a[2] + b[17] * a[3] - b[16] * a[4] + b[13] * a[6] - b[11] * a[7] + b[10] * a[8] + b[8] * a[10] - b[7] * a[11] + b[6] * a[13] + b[4] * a[16] - b[3] * a[17] + b[2] * a[19] - b[1] * a[22] + b[0] * a[26];
        res[27] = b[27] * a[0] + b[23] * a[1] - b[20] * a[2] + b[18] * a[3] - b[16] * a[5] + b[14] * a[6] - b[12] * a[7] + b[10] * a[9] + b[9] * a[10] - b[7] * a[12] + b[6] * a[14] + b[5] * a[16] - b[3] * a[18] + b[2] * a[20] - b[1] * a[23] + b[0] * a[27];
        res[28] = b[28] * a[0] + b[24] * a[1] - b[21] * a[2] + b[18] * a[4] - b[17] * a[5] + b[15] * a[6] - b[12] * a[8] + b[11] * a[9] + b[9] * a[11] - b[8] * a[12] + b[6] * a[15] + b[5] * a[17] - b[4] * a[18] + b[2] * a[21] - b[1] * a[24] + b[0] * a[28];
        res[29] = b[29] * a[0] + b[25] * a[1] - b[21] * a[3] + b[20] * a[4] - b[19] * a[5] + b[15] * a[7] - b[14] * a[8] + b[13] * a[9] + b[9] * a[13] - b[8] * a[14] + b[7] * a[15] + b[5] * a[19] - b[4] * a[20] + b[3] * a[21] - b[1] * a[25] + b[0] * a[29];
        res[30] = b[30] * a[0] + b[25] * a[2] - b[24] * a[3] + b[23] * a[4] - b[22] * a[5] + b[15] * a[10] - b[14] * a[11] + b[13] * a[12] + b[12] * a[13] - b[11] * a[14] + b[10] * a[15] + b[5] * a[22] - b[4] * a[23] + b[3] * a[24] - b[2] * a[25] + b[0] * a[30];
        res[31] = b[31] * a[0] + b[30] * a[1] - b[29] * a[2] + b[28] * a[3] - b[27] * a[4] + b[26] * a[5] + b[25] * a[6] - b[24] * a[7] + b[23] * a[8] - b[22] * a[9] + b[21] * a[10] - b[20] * a[11] + b[19] * a[12] + b[18] * a[13] - b[17] * a[14] + b[16] * a[15] + b[15] * a[16] - b[14] * a[17] + b[13] * a[18] + b[12] * a[19] - b[11] * a[20] + b[10] * a[21] - b[9] * a[22] + b[8] * a[23] - b[7] * a[24] + b[6] * a[25] + b[5] * a[26] - b[4] * a[27] + b[3] * a[28] - b[2] * a[29] + b[1] * a[30] + b[0] * a[31];
        return res;
    }

    /**
     * Add.
     *
     * Multivector addition
     *
     * @param a
     * @param b
     * @return a + b
     */
    private static double[] add(double[] a, double[] b) {
        double[] res = new double[a.length];
        res[0] = a[0] + b[0];
        res[1] = a[1] + b[1];
        res[2] = a[2] + b[2];
        res[3] = a[3] + b[3];
        res[4] = a[4] + b[4];
        res[5] = a[5] + b[5];
        res[6] = a[6] + b[6];
        res[7] = a[7] + b[7];
        res[8] = a[8] + b[8];
        res[9] = a[9] + b[9];
        res[10] = a[10] + b[10];
        res[11] = a[11] + b[11];
        res[12] = a[12] + b[12];
        res[13] = a[13] + b[13];
        res[14] = a[14] + b[14];
        res[15] = a[15] + b[15];
        res[16] = a[16] + b[16];
        res[17] = a[17] + b[17];
        res[18] = a[18] + b[18];
        res[19] = a[19] + b[19];
        res[20] = a[20] + b[20];
        res[21] = a[21] + b[21];
        res[22] = a[22] + b[22];
        res[23] = a[23] + b[23];
        res[24] = a[24] + b[24];
        res[25] = a[25] + b[25];
        res[26] = a[26] + b[26];
        res[27] = a[27] + b[27];
        res[28] = a[28] + b[28];
        res[29] = a[29] + b[29];
        res[30] = a[30] + b[30];
        res[31] = a[31] + b[31];
        return res;
    }

    /**
     * Sub.
     *
     * Multivector subtraction
     *
     * @param a
     * @param b
     * @return a - b
     */
    private static double[] sub(double[] a, double[] b) {
        double[] res = new double[a.length];
        res[0] = a[0] - b[0];
        res[1] = a[1] - b[1];
        res[2] = a[2] - b[2];
        res[3] = a[3] - b[3];
        res[4] = a[4] - b[4];
        res[5] = a[5] - b[5];
        res[6] = a[6] - b[6];
        res[7] = a[7] - b[7];
        res[8] = a[8] - b[8];
        res[9] = a[9] - b[9];
        res[10] = a[10] - b[10];
        res[11] = a[11] - b[11];
        res[12] = a[12] - b[12];
        res[13] = a[13] - b[13];
        res[14] = a[14] - b[14];
        res[15] = a[15] - b[15];
        res[16] = a[16] - b[16];
        res[17] = a[17] - b[17];
        res[18] = a[18] - b[18];
        res[19] = a[19] - b[19];
        res[20] = a[20] - b[20];
        res[21] = a[21] - b[21];
        res[22] = a[22] - b[22];
        res[23] = a[23] - b[23];
        res[24] = a[24] - b[24];
        res[25] = a[25] - b[25];
        res[26] = a[26] - b[26];
        res[27] = a[27] - b[27];
        res[28] = a[28] - b[28];
        res[29] = a[29] - b[29];
        res[30] = a[30] - b[30];
        res[31] = a[31] - b[31];
        return res;
    }

    /**
     * Multiplication with an scalar.
     *
     * @param a multivector
     * @param b scalar
     * @return a * b
     */
    private static double[] muls(double[] a, double b) {
        double[] res = new double[a.length];
        res[0] = a[0] * b;
        res[1] = a[1] * b;
        res[2] = a[2] * b;
        res[3] = a[3] * b;
        res[4] = a[4] * b;
        res[5] = a[5] * b;
        res[6] = a[6] * b;
        res[7] = a[7] * b;
        res[8] = a[8] * b;
        res[9] = a[9] * b;
        res[10] = a[10] * b;
        res[11] = a[11] * b;
        res[12] = a[12] * b;
        res[13] = a[13] * b;
        res[14] = a[14] * b;
        res[15] = a[15] * b;
        res[16] = a[16] * b;
        res[17] = a[17] * b;
        res[18] = a[18] * b;
        res[19] = a[19] * b;
        res[20] = a[20] * b;
        res[21] = a[21] * b;
        res[22] = a[22] * b;
        res[23] = a[23] * b;
        res[24] = a[24] * b;
        res[25] = a[25] * b;
        res[26] = a[26] * b;
        res[27] = a[27] * b;
        res[28] = a[28] * b;
        res[29] = a[29] * b;
        res[30] = a[30] * b;
        res[31] = a[31] * b;
        return res;
    }

    //FIXME liefert anderes Ergebnis als normalizeRotor()
    // vergleiche mit der clifford-impl bei up() und mit der default impl in der SparseCGASymbolicMultivector
    // bzw. im zugehörigen interface der GACalcAPI
    // vielleicht ist hier die falsche norm()-function verwendet worden
    private static double[] normalize(double[] X) {
        return muls(X, 1d / norm(X));
    }

    // Multivector with even elements only inclusive the scalar
    private static double[] rotor(double[] X){
        int[] indizes = CGACayleyTable.getEvenIndizes();
        double[] R = new double[indizes.length];
        for (int i=0;i<indizes.length;i++){
            R[i] = X[indizes[i]];
        }
        return R;
    }
    private static double[] bivector(double[] X){
        int[] indizes = CGACayleyTable.getBivectorIndizes();
        double[] B = new double[indizes.length];
        for (int i=0;i<indizes.length;i++){
            B[i] = X[indizes[i]];
        }
        return B;
    }
    
    // https://enki.ws/ganja.js/examples/coffeeshop.html#NSELGA
    // renormalization in R41 (CGA)
    private static double[] normalizeRotor(double[] X) {
        double[] R = rotor(X);
        // var S  = R[0]*R[0]-R[10]*R[10]+R[11]*R[11]-R[12]*R[12]-R[13]*R[13]-R[14]*R[14]-R[15]*R[15]+R[1]*R[1]
        double S  = R[0]*R[0]-R[10]*R[10]+R[11]*R[11]-R[12]*R[12]-R[13]*R[13]-R[14]*R[14]-R[15]*R[15]+R[1]*R[1]
        //   +R[2]*R[2]+R[3]*R[3]-R[4]*R[4]+R[5]*R[5]+R[6]*R[6]-R[7]*R[7]+R[8]*R[8]-R[9]*R[9];
             +R[2]*R[2]+R[3]*R[3]-R[4]*R[4]+R[5]*R[5]+R[6]*R[6]-R[7]*R[7]+R[8]*R[8]-R[9]*R[9];
        
        double T1 = 2d*(R[0]*R[11]-R[10]*R[12]+R[13]*R[9]-R[14]*R[7]+R[15]*R[4]-R[1]*R[8]+R[2]*R[6]-R[3]*R[5]);
        double T2 = 2d*(R[0]*R[12]-R[10]*R[11]+R[13]*R[8]-R[14]*R[6]+R[15]*R[3]-R[1]*R[9]+R[2]*R[7]-R[4]*R[5]);
        double T3 = 2d*(R[0]*R[13]-R[10]*R[1]+R[11]*R[9]-R[12]*R[8]+R[14]*R[5]-R[15]*R[2]+R[3]*R[7]-R[4]*R[6]);
        double T4 = 2d*(R[0]*R[14]-R[10]*R[2]-R[11]*R[7]+R[12]*R[6]-R[13]*R[5]+R[15]*R[1]+R[3]*R[9]-R[4]*R[8]);
        double T5 = 2d*(R[0]*R[15]-R[10]*R[5]+R[11]*R[4]-R[12]*R[3]+R[13]*R[2]-R[14]*R[1]+R[6]*R[9]-R[7]*R[8]);
        double TT = -T1 * T1 + T2 * T2 + T3 * T3 + T4 * T4 + T5 * T5;
        //var N = ((S*S+TT)**.5+S)**.5, N2 = N*N;
        double N = Math.pow((Math.pow(S*S+TT, 0.5d) + S), 0.5d);
        double N2 = N*N;
        //var ND = 2**.5*N/(N2*N2+TT);
        double ND = Math.pow(2d, 0.5) * N/(N2*N2+TT);
        double C = N2 * ND;
        double D1 = -T1 * ND;
        double D2 = -T2 * ND;
        double D3 = -T3 * ND;
        double D4 = -T4 * ND;
        double D5 = -T5 * ND;
        double[] result = new double[32];

        result[0] = C * R[0] + D1 * R[11] - D2 * R[12] - D3 * R[13] - D4 * R[14] - D5 * R[15];
        result[6] = C * R[1] - D1 * R[8] + D2 * R[9] + D3 * R[10] - D4 * R[15] + D5 * R[14];
        result[7] = C * R[2] + D1 * R[6] - D2 * R[7] + D3 * R[15] + D4 * R[10] - D5 * R[13];
        result[8] = C * R[3] - D1 * R[5] - D2 * R[15] - D3 * R[7] - D4 * R[9] + D5 * R[12];
        result[9] = C * R[4] - D1 * R[15] - D2 * R[5] - D3 * R[6] - D4 * R[8] + D5 * R[11];
        result[10] = C * R[5] - D1 * R[3] + D2 * R[4] - D3 * R[14] + D4 * R[13] + D5 * R[10];
        result[11] = C * R[6] + D1 * R[2] + D2 * R[14] + D3 * R[4] - D4 * R[12] - D5 * R[9];
        result[12] = C * R[7] + D1 * R[14] + D2 * R[2] + D3 * R[3] - D4 * R[11] - D5 * R[8];
        result[13] = C * R[8] - D1 * R[1] - D2 * R[13] + D3 * R[12] + D4 * R[4] + D5 * R[7];
        result[14] = C * R[9] - D1 * R[13] - D2 * R[1] + D3 * R[11] + D4 * R[3] + D5 * R[6];
        result[15] = C * R[10] + D1 * R[12] - D2 * R[11] - D3 * R[1] - D4 * R[2] - D5 * R[5];
        result[26] = C * R[11] + D1 * R[0] + D2 * R[10] - D3 * R[9] + D4 * R[7] - D5 * R[4];
        result[27] = C * R[12] + D1 * R[10] + D2 * R[0] - D3 * R[8] + D4 * R[6] - D5 * R[3];
        result[28] = C * R[13] - D1 * R[9] + D2 * R[8] + D3 * R[0] - D4 * R[5] + D5 * R[2];
        result[29] = C * R[14] + D1 * R[7] - D2 * R[6] + D3 * R[5] + D4 * R[0] - D5 * R[1];
        result[30] = C * R[15] - D1 * R[4] + D2 * R[3] - D3 * R[2] + D4 * R[1] + D5 * R[0];
        return result;
    }

    /**
     * Euclidean norm.
     *
     * Calculate the Euclidean norm. (strict positive).
     *
     * Implementation following ganja.js generated code.
     */
    private static double norm(double[] mv) {
        return Math.sqrt(Math.abs(gp(mv, conjugate(mv))[0]));
    }

    // https://enki.ws/ganja.js/examples/coffeeshop.html#NSELGA
    // sqrt of a rotor
    private static double[] sqrtRotor(double[] X){
        double[] one = new double[X.length];
        one[0] = 1d;
        return normalizeRotor(add(one, X));
    }
    
    // https://enki.ws/ganja.js/examples/coffeeshop.html#NSELGA
    // exponential of a bivector only for CGA (R41), results in a multivector with even elements only
    private static double[] exp(double[] X) {
        double[] B = bivector(X);
        
        // var S = -B[0]*B[0]-B[1]*B[1]-B[2]*B[2]+B[3]*B[3]-B[4]*B[4]-B[5]*B[5]+B[6]*B[6]-B[7]*B[7]+B[8]*B[8]+B[9]*B[9];
        double S = -B[0]*B[0]-B[1]*B[1]-B[2]*B[2]+B[3]*B[3]-B[4]*B[4]-B[5]*B[5]+B[6]*B[6]-B[7]*B[7]+B[8]*B[8]+B[9]*B[9];
        // 2*(B[4]*B[9]-B[5]*B[8]+B[6]*B[7]), //e2345
        double T1 = 2d * (B[4] * B[9] - B[5] * B[8] + B[6] * B[7]); //e2345
        // 2*(B[1]*B[9]-B[2]*B[8]+B[3]*B[7]), //e1345
        double T2 = 2d * (B[1] * B[9] - B[2] * B[8] + B[3] * B[7]); //e1345
        // 2*(B[0]*B[9]-B[2]*B[6]+B[3]*B[5]), //e1245
        double T3 = 2d * (B[0] * B[9] - B[2] * B[6] + B[3] * B[5]); //e1245
        // 2*(B[0]*B[8]-B[1]*B[6]+B[3]*B[4]), //e1235
        double T4 = 2d * (B[0] * B[8] - B[1] * B[6] + B[3] * B[4]); 
        //e1235// 2*(B[0]*B[7]-B[1]*B[5]+B[2]*B[4])  //e1234
        double T5 = 2d * (B[0] * B[7] - B[1] * B[5] + B[2] * B[4]); //e1234

        // Calculate the norms of the invariants
        double Tsq = -T1 * T1 - T2 * T2 - T3 * T3 - T4 * T4 + T5 * T5;
        // var norm = sqrt(S*S - Tsq), sc = -0.5/norm, lambdap = 0.5*S+0.5*norm;
        double norm = Math.sqrt(S * S - Tsq);
        double sc = -0.5 / norm;
        double lambdap = 0.5 * S + 0.5 * norm;
        // var [lp, lm] = [sqrt(abs(lambdap)), sqrt(-0.5*S+0.5*norm)]
        double lp = Math.sqrt(Math.abs(lambdap));
        double lm = Math.sqrt(-0.5 * S + 0.5 * norm);
        // The associated trig (depending on sign lambdap)
        //double [cp, sp] = lambdap>0?[cosh(lp), sinh(lp)/lp]:lambdap<0?[cos(lp), sin(lp)/lp]:[1,1]
        double cp, sp;
        if (lambdap > 0) {
            cp = Math.cosh(lp);
            sp = Math.sinh(lp) / lp;
        } else if (lambdap < 0) {
            cp = Math.cos(lp);
            sp = Math.sin(lp) / lp;
        } else {
            cp = 1d;
            sp = 1d;
        }
        
        //var [cm, sm] = [cos(lm), lm==0?1:sin(lm)/lm]
        double cm = Math.cos(lm);
        double sm;
        if (lm == 0) {
            sm = 1d;
        } else {
            sm = Math.sin(lm) / lm;
        }
        // Calculate the mixing factors alpha and beta_i.
        double cmsp = cm * sp;
        double cpsm = cp * sm;
        double spsm = sp * sm / 2d;
        double D = cmsp - cpsm;
        double E = sc * D;
        double alpha = D * (0.5 - sc * S) + cpsm;
        double beta1 = E * T1;
        double beta2 = -E * T2;
        double beta3 = E * T3;
        double beta4 = -E * T4;
        double beta5 = -E * T5;

        // Create the final rotor.
        double[] result = new double[32];
        result[0] = cp  * cm; // cp und cm scheinen beide falsch zu sein
        //result[0] = 1d;
        //result[0] = cm; //cp;
        //result[0] = Tsq;
        
        result[6] = (B[0] * alpha + B[7] * beta5 - B[8] * beta4 + B[9] * beta3);
        result[7] = (B[1] * alpha - B[5] * beta5 + B[6] * beta4 - B[9] * beta2);
        result[8] = (B[2] * alpha + B[4] * beta5 - B[6] * beta3 + B[8] * beta2);
        result[9] = (B[3] * alpha + B[4] * beta4 - B[5] * beta3 + B[7] * beta2);
        result[10] = (B[4] * alpha + B[2] * beta5 - B[3] * beta4 + B[9] * beta1);
        result[11] = (B[5] * alpha - B[1] * beta5 + B[3] * beta3 - B[8] * beta1);
        result[12] = (B[6] * alpha - B[1] * beta4 + B[2] * beta3 - B[7] * beta1);
        result[13] = (B[7] * alpha + B[0] * beta5 - B[3] * beta2 + B[6] * beta1);
        result[14] = (B[8] * alpha + B[0] * beta4 - B[2] * beta2 + B[5] * beta1);
        result[15] = (B[9] * alpha - B[0] * beta3 + B[1] * beta2 - B[4] * beta1);
        result[26] = spsm * T5;
        result[27] = spsm * T4;
        result[28] = spsm * T3;
        result[29] = spsm * T2;
        result[30] = spsm * T1;
        return result;
    }

    // exponential of a bivector implemented by taylor series
    //FIXME funktioniert so noch nicht!
    private static double[] expSeries(double[] B) {
         double[] R = new double[B.length];
         R[0] = 1d;
         double[] C = copy(B);
         double f = 1;
         for (int i=1; i<20; ++i){
             R = adda(R, div(C,f));
             C = mul(C,B);
             f = f * (i+1);
         }
         return R;
    }
    
    private static double[] mul(double[] A, double[] B){
        double[] result = new double[A.length];
        for (int i=0;i<A.length;i++){
            result[i] = A[i]*B[i];
        }
        return result;
    }
    private static double[] adda(double[] A, double[] B){
        double[] result = new double[A.length];
        for (int i=0;i<A.length;i++){
            result[i] = A[i] + B[i];
        }
        return result;
    }
    private static double[] div(double[] A, double B){
        double[] result = new double[A.length];
        for (int i=0;i<A.length;i++){
            result[i] = A[i]/B;
        }
        return result;
    }
    private static double[] copy(double[] A){
        double[] result = new double[A.length];
        System.arraycopy(A, 0, result, 0, A.length);
        return result;
    }
    
    // only for rotors
    private static double[] log(double[] X){
        double[] R = rotor(X);
        
        double S = R[0]*R[0]+R[11]*R[11]-R[12]*R[12]-R[13]*R[13]-R[14]*R[14]-R[15]*R[15]-1d;
  
        double T1 = 2d*R[0]*R[15];   //e2345
        double T2 = 2d*R[0]*R[14];   //e1345
        double T3 = 2d*R[0]*R[13];   //e1245
        double T4 = 2d*R[0]*R[12];   //e1235
        double T5 = 2d*R[0]*R[11];   //e1234
  
        double Tsq      = -T1*T1-T2*T2-T3*T3-T4*T4+T5*T5;
        double norm     = Math.sqrt(S*S - Tsq);
        if (norm==0 && S==0){   // at most a single translation
            double[] result = new double[32];
            result[6] = R[1];
            result[7] = R[2];
            result[8] = R[3];
            result[9] = R[4];
            result[10] = R[5];
            result[11] = R[6];
            result[12] = R[7];
            result[13] = R[8];
            result[14] = R[9];
            result[15] = R[10];
            return result;
        }
             
        double lambdap  = 0.5*S+0.5*norm;
        // lm is always a rotation, lp can be boost, translation, rotation
        double lp = Math.sqrt(Math.abs(lambdap));
        double lm = Math.sqrt(-0.5*S+0.5*norm);
        //double theta2   = lm==0?0:atan2(lm, R[0]); 
        double theta2 = 0d;
        if (lm != 0d) theta2 = Math.atan2(lm, R[0]);
        
        // var theta1   = lambdap<0?asin(lp/cos(theta2)):lambdap>0?atanh(lp/R[0]):lp/R[0];
        double theta1;
        if (lambdap < 0){
            theta1 = Math.asin(lp/Math.cos(theta2));
        } else if (lambdap > 0){
            theta1 = Trigometry.atanh(lp/R[0]);
        } else {
            theta1 = lp/R[0];
        }
        // var [l1, l2] = [lp==0?0:theta1/lp, lm==0?0:theta2/lm]
        double l1=0d;
        if (lp != 0){
            l1 = theta1/lp;
        }
        double l2=0d;
        if (lm != 0){
            l2 = theta2/lm;
        }
        //var [A, B1, B2, B3, B4, B5]   = [
        //  (l1-l2)*0.5*(1+S/norm) + l2,  -0.5*T1*(l1-l2)/norm, -0.5*T2*(l1-l2)/norm, 
        //  -0.5*T3*(l1-l2)/norm,         -0.5*T4*(l1-l2)/norm, -0.5*T5*(l1-l2)/norm, 
        //];
        double A = (l1-l2)*0.5*(1+S/norm) + l2;
        double B1 = -0.5*T1*(l1-l2)/norm;
        double B2 = -0.5*T2*(l1-l2)/norm;
        double B3 = -0.5*T3*(l1-l2)/norm;
        double B4 = -0.5*T4*(l1-l2)/norm;
        double B5 = -0.5*T5*(l1-l2)/norm;
        
        // create the final bivector
        double[] result = new double[32];
        result[6] = (A*R[1]+B3*R[10]+B4*R[9]-B5*R[8]);
        result[7] = (A*R[2]+B2*R[10]-B4*R[7]+B5*R[6]);
        result[8] = (A*R[3]-B2*R[9]-B3*R[7]-B5*R[5]);
        result[9] = (A*R[4]-B2*R[8]-B3*R[6]-B4*R[5]);
        result[10] = (A*R[5]+B1*R[10]+B4*R[4]-B5*R[3]);
        result[11] = (A*R[6]-B1*R[9]+B3*R[4]+B5*R[2]);
        result[12] = (A*R[7]-B1*R[8]+B3*R[3]+B4*R[2]);
        result[13] = (A*R[8]+B1*R[7]+B2*R[4]-B5*R[1]);
        result[14] = (A*R[9]+B1*R[6]+B2*R[3]-B4*R[1]);
        result[15] = (A*R[10]-B1*R[5]-B2*R[2]-B3*R[1]);
        return result;
    }
    
    
    private boolean equals(double[] a, double[] b, double eps) {
        if (a.length != b.length) {
            throw new IllegalArgumentException("a.length != b.length");
        }
        for (int i = 0; i < a.length; i++) {
            if (Math.abs(a[i] - b[i]) > eps) {
                return false;
            }
        }
        return true;
    }

    private boolean equals(double[] a, double[] b) {
        if (a.length != b.length) {
            throw new IllegalArgumentException("a.length != b.length");
        }
        for (int i = 0; i < a.length; i++) {
            if (a[i] != b[i]) {
                return false;
            }
        }
        return true;
    }

    private boolean equals(double[] a, double[] b, ColumnVectorSparsity sparsity) {
        if (a.length != b.length) {
            throw new IllegalArgumentException("a.length != b.length");
        }
        int[] rows = sparsity.getrow();
        for (int i = 0; i < rows.length; i++) {
            if (a[rows[i]] != b[rows[i]]) {
                return false;
            }
        }
        return true;
    }

    /**
     * A versor is a multivector that can be expressed as the geometric product of invertable vectors,
     * especially of non-null 1-vectors.
     *
     * [Dorst2007 p.365]
     *
     * [Dorst2007 p.391] translator, rotor, scalar
     *
     * @return components of a random versor
     */
    /*public double[] createRandomVersor(){
        //ExprGraphFactory exprGraphFactory = TestExprGraphFactory.instance();
        //double[] vec1 = exprGraphFactory.createRandomMultivector(1);
    }*/
    private static double[] createRandomTranslator() {
        double[] result = new double[32];
        Random random = new Random();
        OfDouble rand = random.doubles(-1, 1).iterator();
        //scalar, e1inf, e2inf, e3inf konstruieren, d.h. die betreffenden blades
        result[0] = rand.next();
        result[8] = rand.next();
        result[9] = result[8];
        result[11] = rand.next();
        result[12] = result[11];
        result[13] = rand.next();
        result[14] = result[13];
        //0,8,9,11,12,13,14
        return result;
    }

    private static double[] createRandomScalor() {
        double[] result = new double[32];
        //TODO scalar, eoinf konstruieren, d.h. die betreffenden blades
        // beschaffen und random werte hineinsetzen
        return result;
    }

    public static double[] createValueRandom() {
        final int basisBladesCount = baseCayleyTable.getBladesCount();
        double[] result = new Random().doubles(-1, 1).limit(basisBladesCount).toArray();
        return result;
    }

    public static double[] createValueRandom(int grade) {
        final int basisBladesCount = baseCayleyTable.getBladesCount();
        double[] result = new double[basisBladesCount];
        Random random = new Random();
        int[] indizes = CGACayleyTableGeometricProduct.getIndizes(grade);
        double[] values = random.doubles(-1, 1).
            limit(indizes.length).toArray();
        for (int i = 0; i < indizes.length; i++) {
            result[indizes[i]] = values[i];
        }
        return result;
    }

    public static double[] createValueRandom(int[] blades) {
        final int basisBladesCount = baseCayleyTable.getBladesCount();
        double[] temp = new Random().doubles(-1, 1).limit(basisBladesCount).toArray();
        double[] result = new double[basisBladesCount];
        for (int i = 0; i < blades.length; i++) {
            result[blades[i]] = temp[blades[i]];
        }
        return result;
    }

    public static MultivectorValue createValue(GAFactory exprGraphFactory, double[] values) {
        SparseCGAColumnVector sdm = SparseCGAColumnVector.fromValues(values);
        return exprGraphFactory.createValue(sdm);
    }
}
