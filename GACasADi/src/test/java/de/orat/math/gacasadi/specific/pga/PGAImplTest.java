package de.orat.math.gacasadi.specific.pga;

import de.orat.math.gacalc.api.GAFactory;
import de.orat.math.gacalc.api.GAFunction;
import de.orat.math.gacalc.api.MultivectorExpression;
import de.orat.math.gacalc.api.MultivectorValue;
import de.orat.math.gacalc.api.MultivectorVariable;
import de.orat.math.gacasadi.algebraGeneric.api.IAlgebra;
import de.orat.math.sparsematrix.ColumnVectorSparsity;
import de.orat.math.sparsematrix.DenseDoubleColumnVector;
import de.orat.math.sparsematrix.SparseDoubleColumnVector;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

/**
 *
 * @author Oliver Rettig (Oliver.Rettig@orat.de)
 */
public class PGAImplTest {
    
    //private static final IAlgebra algebra = PgaFactory.instance.getIAlgebra();
    
    @Test 
    public void testGetEvenIndizes(){
         IAlgebra algebra = PgaFactory.instance.getIAlgebra();
         System.out.println("even indizes for PGA = "+print(algebra.getEvenIndizes()));
    }
    public String print(int[] arr){
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i=0;i<arr.length;i++){
            sb.append(String.valueOf(arr[i]));
            sb.append(", ");
        }
        sb.deleteCharAt(sb.length()-1);
        sb.deleteCharAt(sb.length()-1);
        sb.append("]");
        return sb.toString();
    }
    @Test
    public void testAdd() {
        IAlgebra algebra = PgaFactory.instance.getIAlgebra();
        int bladesCount = algebra.getBladesCount();
        GAFactory exprGraphFactory = TestExprGraphFactory.instance();
        //CGAMultivectorSparsity sparsity_a = new CGAMultivectorSparsity(new int[]{1, 2, 3});
        ColumnVectorSparsity sparsity_a = new ColumnVectorSparsity(bladesCount, new int[]{1, 2, 3});
        MultivectorVariable mvsa = exprGraphFactory.createVariable("a", sparsity_a);
        //CGAMultivectorSparsity sparsity_b = new CGAMultivectorSparsity(new int[]{1, 3, 4});
        ColumnVectorSparsity sparsity_b = new ColumnVectorSparsity(bladesCount, new int[]{1, 3, 4});
        MultivectorVariable mvsb = exprGraphFactory.createVariable("b", sparsity_b);

        MultivectorExpression mvsc = mvsa.addition(mvsb);

        List<MultivectorVariable> parameters = new ArrayList<>();
        parameters.add(mvsa);
        parameters.add(mvsb);
        List<MultivectorExpression> returns = new ArrayList<>();
        returns.add(mvsc);

        GAFunction f = exprGraphFactory.createFunction("c", parameters, returns);

        List<MultivectorValue> arguments = new ArrayList<>();

        double[] values_A = new double[bladesCount];
        values_A[1] = 1;
        values_A[2] = 2;
        values_A[3] = 3;
        MultivectorValue arg_a = createValue(exprGraphFactory, values_A);
        System.out.println("a=" + arg_a.toString());
        arguments.add(arg_a);

        double[] values_B = new double[bladesCount];
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
        } catch (Exception e) {}
    }
    
    @Test
    public void testSub() {
        IAlgebra algebra = PgaFactory.instance.getIAlgebra();
        int bladesCount = algebra.getBladesCount();
        GAFactory exprGraphFactory = TestExprGraphFactory.instance();
        ColumnVectorSparsity sparsity_a = new ColumnVectorSparsity(bladesCount, new int[]{1, 2, 3});
        MultivectorVariable mvsa = exprGraphFactory.createVariable("a", sparsity_a);
        ColumnVectorSparsity sparsity_b = new ColumnVectorSparsity(bladesCount, new int[]{1, 3, 4});
        MultivectorVariable mvsb = exprGraphFactory.createVariable("b", sparsity_b);

        MultivectorExpression mvsc = mvsa.subtraction(mvsb);

        List<MultivectorVariable> parameters = new ArrayList<>();
        parameters.add(mvsa);
        parameters.add(mvsb);
        List<MultivectorExpression> returns = new ArrayList<>();
        returns.add(mvsc);

        GAFunction f = exprGraphFactory.createFunction("c", parameters, returns);

        List<MultivectorValue> arguments = new ArrayList<>();

        double[] values_A = new double[bladesCount];
        values_A[1] = 1;
        values_A[2] = 2;
        values_A[3] = 3;
        MultivectorValue arg_a = createValue(exprGraphFactory, values_A);
        System.out.println("a=" + arg_a.toString());
        arguments.add(arg_a);

        double[] values_B = new double[bladesCount];
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
        IAlgebra algebra = PgaFactory.instance.getIAlgebra();
        int bladesCount = algebra.getBladesCount();
        GAFactory exprGraphFactory = TestExprGraphFactory.instance();
        ColumnVectorSparsity sparsity_a = new ColumnVectorSparsity(bladesCount, new int[]{1, 2, 3});
        MultivectorVariable mvsa = exprGraphFactory.createVariable("a", sparsity_a);
        ColumnVectorSparsity sparsity_b = new ColumnVectorSparsity(bladesCount, new int[]{1, 3, 4});
        MultivectorVariable mvsb = exprGraphFactory.createVariable("b", sparsity_b);

        MultivectorExpression mvsc = mvsa.outerProduct(mvsb);

        List<MultivectorVariable> parameters = new ArrayList<>();
        parameters.add(mvsa);
        parameters.add(mvsb);
        List<MultivectorExpression> returns = new ArrayList<>();
        returns.add(mvsc);

        GAFunction f = exprGraphFactory.createFunction("f", parameters, returns);

        List<MultivectorValue> arguments = new ArrayList<>();

        double[] values_A = new double[bladesCount];
        values_A[1] = 1;
        values_A[2] = 2;
        values_A[3] = 3;
        //values_A = exprGraphFactory.createRandomCGAKVector(1);

        MultivectorValue arg_a = createValue(exprGraphFactory, values_A);
        System.out.println("a=" + arg_a.toString());
        arguments.add(arg_a);

        double[] values_B = new double[bladesCount];
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

        double[] test = ip/*dot*/(values_A, values_B);
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
    
    /*@Test
    public void testGradeSelectionRandom() {
        GAFactory exprGraphFactory = TestExprGraphFactory.instance();
        ColumnVectorSparsity sparsity_a = ColumVectorSparsity.dense();
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
    }*/
    
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


    //@Test
    //@Disabled
    /*public void testExpOfBivectorRandom() {

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
    }*/
    
    
		// just for debug and print output, the basis names
		//public static string[] _basis = new[] { "1","e0","e1","e2","e3","e01","e02","e03","e12","e31","e23","e021","e013","e032","e123","e0123" };

		private float[] _mVec = new float[16];

		/// <summary>
		/// Ctor
		/// </summary>
		/// <param name="f"></param>
		/// <param name="idx"></param>
		/*public PGA3D(float f = 0f, int idx = 0)
		{
			_mVec[idx] = f;
		}*/

		/*#region Array Access
		public float this[int idx]
		{
			get { return _mVec[idx]; }
			set { _mVec[idx] = value; }
		}
		#endregion
        
		#region Overloaded Operators
        */

		/// <summary>
		/// PGA3D.Reverse : res = ~a
		/// Reverse the order of the basis blades.
		/// </summary>
		public static double[] reverse (double[] a){
			double[] res = new double[16];
			res[0]=a[0];
			res[1]=a[1];
			res[2]=a[2];
			res[3]=a[3];
			res[4]=a[4];
			res[5]=-a[5];
			res[6]=-a[6];
			res[7]=-a[7];
			res[8]=-a[8];
			res[9]=-a[9];
			res[10]=-a[10];
			res[11]=-a[11];
			res[12]=-a[12];
			res[13]=-a[13];
			res[14]=-a[14];
			res[15]=a[15];
			return res;
		}

		/// <summary>
		/// PGA3D.Dual : res = !a
		/// Poincare duality operator.
		/// </summary>
		public static double[] dual(double[] a){
			double[] res = new double[16];
			res[0]=a[15];
			res[1]=a[14];
			res[2]=a[13];
			res[3]=a[12];
			res[4]=a[11];
			res[5]=a[10];
			res[6]=a[9];
			res[7]=a[8];
			res[8]=a[7];
			res[9]=a[6];
			res[10]=a[5];
			res[11]=a[4];
			res[12]=a[3];
			res[13]=a[2];
			res[14]=a[1];
			res[15]=a[0];
			return res;
		}

		/// <summary>
		/// PGA3D.Conjugate : res = a.Conjugate()
		/// Clifford Conjugation
		/// </summary>
		public static double[] conjugate (double[] mv){
			double[] res = new double[16];
			res[0]=mv[0];
			res[1]=-mv[1];
			res[2]=-mv[2];
			res[3]=-mv[3];
			res[4]=-mv[4];
			res[5]=-mv[5];
			res[6]=-mv[6];
			res[7]=-mv[7];
			res[8]=-mv[8];
			res[9]=-mv[9];
			res[10]=-mv[10];
			res[11]=mv[11];
			res[12]=mv[12];
			res[13]=mv[13];
			res[14]=mv[14];
			res[15]=mv[15];
			return res;
		}

		/// <summary>
		/// PGA3D.Involute : res = a.Involute()
		/// Main involution
		/// </summary>
		public static double[] Involute (double[] mv){
			double[] res = new double[16];
			res[0]=mv[0];
			res[1]=-mv[1];
			res[2]=-mv[2];
			res[3]=-mv[3];
			res[4]=-mv[4];
			res[5]=mv[5];
			res[6]=mv[6];
			res[7]=mv[7];
			res[8]=mv[8];
			res[9]=mv[9];
			res[10]=mv[10];
			res[11]=-mv[11];
			res[12]=-mv[12];
			res[13]=-mv[13];
			res[14]=-mv[14];
			res[15]=mv[15];
			return res;
		}

		/// <summary>
		/// PGA3D.Mul : res = a * b
		/// The geometric product.
		/// </summary>
		public static double[] gp (double[] a, double[] b){
			double[] res = new double[16];
			res[0]=b[0]*a[0]+b[2]*a[2]+b[3]*a[3]+b[4]*a[4]-b[8]*a[8]-b[9]*a[9]-b[10]*a[10]-b[14]*a[14];
			res[1]=b[1]*a[0]+b[0]*a[1]-b[5]*a[2]-b[6]*a[3]-b[7]*a[4]+b[2]*a[5]+b[3]*a[6]+b[4]*a[7]+b[11]*a[8]+b[12]*a[9]+b[13]*a[10]+b[8]*a[11]+b[9]*a[12]+b[10]*a[13]+b[15]*a[14]-b[14]*a[15];
			res[2]=b[2]*a[0]+b[0]*a[2]-b[8]*a[3]+b[9]*a[4]+b[3]*a[8]-b[4]*a[9]-b[14]*a[10]-b[10]*a[14];
			res[3]=b[3]*a[0]+b[8]*a[2]+b[0]*a[3]-b[10]*a[4]-b[2]*a[8]-b[14]*a[9]+b[4]*a[10]-b[9]*a[14];
			res[4]=b[4]*a[0]-b[9]*a[2]+b[10]*a[3]+b[0]*a[4]-b[14]*a[8]+b[2]*a[9]-b[3]*a[10]-b[8]*a[14];
			res[5]=b[5]*a[0]+b[2]*a[1]-b[1]*a[2]-b[11]*a[3]+b[12]*a[4]+b[0]*a[5]-b[8]*a[6]+b[9]*a[7]+b[6]*a[8]-b[7]*a[9]-b[15]*a[10]-b[3]*a[11]+b[4]*a[12]+b[14]*a[13]-b[13]*a[14]-b[10]*a[15];
			res[6]=b[6]*a[0]+b[3]*a[1]+b[11]*a[2]-b[1]*a[3]-b[13]*a[4]+b[8]*a[5]+b[0]*a[6]-b[10]*a[7]-b[5]*a[8]-b[15]*a[9]+b[7]*a[10]+b[2]*a[11]+b[14]*a[12]-b[4]*a[13]-b[12]*a[14]-b[9]*a[15];
			res[7]=b[7]*a[0]+b[4]*a[1]-b[12]*a[2]+b[13]*a[3]-b[1]*a[4]-b[9]*a[5]+b[10]*a[6]+b[0]*a[7]-b[15]*a[8]+b[5]*a[9]-b[6]*a[10]+b[14]*a[11]-b[2]*a[12]+b[3]*a[13]-b[11]*a[14]-b[8]*a[15];
			res[8]=b[8]*a[0]+b[3]*a[2]-b[2]*a[3]+b[14]*a[4]+b[0]*a[8]+b[10]*a[9]-b[9]*a[10]+b[4]*a[14];
			res[9]=b[9]*a[0]-b[4]*a[2]+b[14]*a[3]+b[2]*a[4]-b[10]*a[8]+b[0]*a[9]+b[8]*a[10]+b[3]*a[14];
			res[10]=b[10]*a[0]+b[14]*a[2]+b[4]*a[3]-b[3]*a[4]+b[9]*a[8]-b[8]*a[9]+b[0]*a[10]+b[2]*a[14];
			res[11]=b[11]*a[0]-b[8]*a[1]+b[6]*a[2]-b[5]*a[3]+b[15]*a[4]-b[3]*a[5]+b[2]*a[6]-b[14]*a[7]-b[1]*a[8]+b[13]*a[9]-b[12]*a[10]+b[0]*a[11]+b[10]*a[12]-b[9]*a[13]+b[7]*a[14]-b[4]*a[15];
			res[12]=b[12]*a[0]-b[9]*a[1]-b[7]*a[2]+b[15]*a[3]+b[5]*a[4]+b[4]*a[5]-b[14]*a[6]-b[2]*a[7]-b[13]*a[8]-b[1]*a[9]+b[11]*a[10]-b[10]*a[11]+b[0]*a[12]+b[8]*a[13]+b[6]*a[14]-b[3]*a[15];
			res[13]=b[13]*a[0]-b[10]*a[1]+b[15]*a[2]+b[7]*a[3]-b[6]*a[4]-b[14]*a[5]-b[4]*a[6]+b[3]*a[7]+b[12]*a[8]-b[11]*a[9]-b[1]*a[10]+b[9]*a[11]-b[8]*a[12]+b[0]*a[13]+b[5]*a[14]-b[2]*a[15];
			res[14]=b[14]*a[0]+b[10]*a[2]+b[9]*a[3]+b[8]*a[4]+b[4]*a[8]+b[3]*a[9]+b[2]*a[10]+b[0]*a[14];
			res[15]=b[15]*a[0]+b[14]*a[1]+b[13]*a[2]+b[12]*a[3]+b[11]*a[4]+b[10]*a[5]+b[9]*a[6]+b[8]*a[7]+b[7]*a[8]+b[6]*a[9]+b[5]*a[10]-b[4]*a[11]-b[3]*a[12]-b[2]*a[13]-b[1]*a[14]+b[0]*a[15];
			return res;
		}

		/// <summary>
		/// PGA3D.Wedge : res = a ^ b
		/// The outer product. (MEET)
		/// </summary>
		public static double[] op (double[] a, double[] b){
			double[] res = new double[16];
			res[0]=b[0]*a[0];
			res[1]=b[1]*a[0]+b[0]*a[1];
			res[2]=b[2]*a[0]+b[0]*a[2];
			res[3]=b[3]*a[0]+b[0]*a[3];
			res[4]=b[4]*a[0]+b[0]*a[4];
			res[5]=b[5]*a[0]+b[2]*a[1]-b[1]*a[2]+b[0]*a[5];
			res[6]=b[6]*a[0]+b[3]*a[1]-b[1]*a[3]+b[0]*a[6];
			res[7]=b[7]*a[0]+b[4]*a[1]-b[1]*a[4]+b[0]*a[7];
			res[8]=b[8]*a[0]+b[3]*a[2]-b[2]*a[3]+b[0]*a[8];
			res[9]=b[9]*a[0]-b[4]*a[2]+b[2]*a[4]+b[0]*a[9];
			res[10]=b[10]*a[0]+b[4]*a[3]-b[3]*a[4]+b[0]*a[10];
			res[11]=b[11]*a[0]-b[8]*a[1]+b[6]*a[2]-b[5]*a[3]-b[3]*a[5]+b[2]*a[6]-b[1]*a[8]+b[0]*a[11];
			res[12]=b[12]*a[0]-b[9]*a[1]-b[7]*a[2]+b[5]*a[4]+b[4]*a[5]-b[2]*a[7]-b[1]*a[9]+b[0]*a[12];
			res[13]=b[13]*a[0]-b[10]*a[1]+b[7]*a[3]-b[6]*a[4]-b[4]*a[6]+b[3]*a[7]-b[1]*a[10]+b[0]*a[13];
			res[14]=b[14]*a[0]+b[10]*a[2]+b[9]*a[3]+b[8]*a[4]+b[4]*a[8]+b[3]*a[9]+b[2]*a[10]+b[0]*a[14];
			res[15]=b[15]*a[0]+b[14]*a[1]+b[13]*a[2]+b[12]*a[3]+b[11]*a[4]+b[10]*a[5]+b[9]*a[6]+b[8]*a[7]+b[7]*a[8]+b[6]*a[9]+b[5]*a[10]-b[4]*a[11]-b[3]*a[12]-b[2]*a[13]-b[1]*a[14]+b[0]*a[15];
			return res;
		}

		/// <summary>
		/// PGA3D.Vee : res = a & b
		/// The regressive product. (JOIN)
		/// </summary>
		public static double[] vee (double[] a, double[] b){
			double[] res = new double[16];
			res[15]=1*(a[15]*b[15]);
			res[14]=-1*(a[14]*-1*b[15]+a[15]*b[14]*-1);
			res[13]=-1*(a[13]*-1*b[15]+a[15]*b[13]*-1);
			res[12]=-1*(a[12]*-1*b[15]+a[15]*b[12]*-1);
			res[11]=-1*(a[11]*-1*b[15]+a[15]*b[11]*-1);
			res[10]=1*(a[10]*b[15]+a[13]*-1*b[14]*-1-a[14]*-1*b[13]*-1+a[15]*b[10]);
			res[9]=1*(a[9]*b[15]+a[12]*-1*b[14]*-1-a[14]*-1*b[12]*-1+a[15]*b[9]);
			res[8]=1*(a[8]*b[15]+a[11]*-1*b[14]*-1-a[14]*-1*b[11]*-1+a[15]*b[8]);
			res[7]=1*(a[7]*b[15]+a[12]*-1*b[13]*-1-a[13]*-1*b[12]*-1+a[15]*b[7]);
			res[6]=1*(a[6]*b[15]-a[11]*-1*b[13]*-1+a[13]*-1*b[11]*-1+a[15]*b[6]);
			res[5]=1*(a[5]*b[15]+a[11]*-1*b[12]*-1-a[12]*-1*b[11]*-1+a[15]*b[5]);
			res[4]=1*(a[4]*b[15]-a[7]*b[14]*-1+a[9]*b[13]*-1-a[10]*b[12]*-1-a[12]*-1*b[10]+a[13]*-1*b[9]-a[14]*-1*b[7]+a[15]*b[4]);
			res[3]=1*(a[3]*b[15]-a[6]*b[14]*-1-a[8]*b[13]*-1+a[10]*b[11]*-1+a[11]*-1*b[10]-a[13]*-1*b[8]-a[14]*-1*b[6]+a[15]*b[3]);
			res[2]=1*(a[2]*b[15]-a[5]*b[14]*-1+a[8]*b[12]*-1-a[9]*b[11]*-1-a[11]*-1*b[9]+a[12]*-1*b[8]-a[14]*-1*b[5]+a[15]*b[2]);
			res[1]=1*(a[1]*b[15]+a[5]*b[13]*-1+a[6]*b[12]*-1+a[7]*b[11]*-1+a[11]*-1*b[7]+a[12]*-1*b[6]+a[13]*-1*b[5]+a[15]*b[1]);
			res[0]=1*(a[0]*b[15]+a[1]*b[14]*-1+a[2]*b[13]*-1+a[3]*b[12]*-1+a[4]*b[11]*-1+a[5]*b[10]+a[6]*b[9]+a[7]*b[8]+a[8]*b[7]+a[9]*b[6]+a[10]*b[5]-a[11]*-1*b[4]-a[12]*-1*b[3]-a[13]*-1*b[2]-a[14]*-1*b[1]+a[15]*b[0]);
			return res;
		}

		/// <summary>
		/// PGA3D.Dot : res = a | b
		/// The inner product.
		/// </summary>
		public static double[] ip (double[] a, double[] b){
			double[] res = new double[16];
			res[0]=b[0]*a[0]+b[2]*a[2]+b[3]*a[3]+b[4]*a[4]-b[8]*a[8]-b[9]*a[9]-b[10]*a[10]-b[14]*a[14];
			res[1]=b[1]*a[0]+b[0]*a[1]-b[5]*a[2]-b[6]*a[3]-b[7]*a[4]+b[2]*a[5]+b[3]*a[6]+b[4]*a[7]+b[11]*a[8]+b[12]*a[9]+b[13]*a[10]+b[8]*a[11]+b[9]*a[12]+b[10]*a[13]+b[15]*a[14]-b[14]*a[15];
			res[2]=b[2]*a[0]+b[0]*a[2]-b[8]*a[3]+b[9]*a[4]+b[3]*a[8]-b[4]*a[9]-b[14]*a[10]-b[10]*a[14];
			res[3]=b[3]*a[0]+b[8]*a[2]+b[0]*a[3]-b[10]*a[4]-b[2]*a[8]-b[14]*a[9]+b[4]*a[10]-b[9]*a[14];
			res[4]=b[4]*a[0]-b[9]*a[2]+b[10]*a[3]+b[0]*a[4]-b[14]*a[8]+b[2]*a[9]-b[3]*a[10]-b[8]*a[14];
			res[5]=b[5]*a[0]-b[11]*a[3]+b[12]*a[4]+b[0]*a[5]-b[15]*a[10]-b[3]*a[11]+b[4]*a[12]-b[10]*a[15];
			res[6]=b[6]*a[0]+b[11]*a[2]-b[13]*a[4]+b[0]*a[6]-b[15]*a[9]+b[2]*a[11]-b[4]*a[13]-b[9]*a[15];
			res[7]=b[7]*a[0]-b[12]*a[2]+b[13]*a[3]+b[0]*a[7]-b[15]*a[8]-b[2]*a[12]+b[3]*a[13]-b[8]*a[15];
			res[8]=b[8]*a[0]+b[14]*a[4]+b[0]*a[8]+b[4]*a[14];
			res[9]=b[9]*a[0]+b[14]*a[3]+b[0]*a[9]+b[3]*a[14];
			res[10]=b[10]*a[0]+b[14]*a[2]+b[0]*a[10]+b[2]*a[14];
			res[11]=b[11]*a[0]+b[15]*a[4]+b[0]*a[11]-b[4]*a[15];
			res[12]=b[12]*a[0]+b[15]*a[3]+b[0]*a[12]-b[3]*a[15];
			res[13]=b[13]*a[0]+b[15]*a[2]+b[0]*a[13]-b[2]*a[15];
			res[14]=b[14]*a[0]+b[0]*a[14];
			res[15]=b[15]*a[0]+b[0]*a[15];
			return res;
		}

		/// <summary>
		/// PGA3D.Add : res = a + b
		/// Multivector addition
		/// </summary>
		public static double[] add (double[] a, double[] b){
			double[] res = new double[16];
			res[0] = a[0]+b[0];
			res[1] = a[1]+b[1];
			res[2] = a[2]+b[2];
			res[3] = a[3]+b[3];
			res[4] = a[4]+b[4];
			res[5] = a[5]+b[5];
			res[6] = a[6]+b[6];
			res[7] = a[7]+b[7];
			res[8] = a[8]+b[8];
			res[9] = a[9]+b[9];
			res[10] = a[10]+b[10];
			res[11] = a[11]+b[11];
			res[12] = a[12]+b[12];
			res[13] = a[13]+b[13];
			res[14] = a[14]+b[14];
			res[15] = a[15]+b[15];
			return res;
		}

		/// <summary>
		/// PGA3D.Sub : res = a - b
		/// Multivector subtraction
		/// </summary>
		public static double[] sub (double[] a, double[] b){
			double[] res = new double[16];
			res[0] = a[0]-b[0];
			res[1] = a[1]-b[1];
			res[2] = a[2]-b[2];
			res[3] = a[3]-b[3];
			res[4] = a[4]-b[4];
			res[5] = a[5]-b[5];
			res[6] = a[6]-b[6];
			res[7] = a[7]-b[7];
			res[8] = a[8]-b[8];
			res[9] = a[9]-b[9];
			res[10] = a[10]-b[10];
			res[11] = a[11]-b[11];
			res[12] = a[12]-b[12];
			res[13] = a[13]-b[13];
			res[14] = a[14]-b[14];
			res[15] = a[15]-b[15];
			return res;
		}

		/// <summary>
		/// PGA3D.smul : res = a * b
		/// scalar/multivector multiplication
		/// </summary>
		public static double[] smul (float a, double[] b){
			double[] res = new double[16];
			res[0] = a*b[0];
			res[1] = a*b[1];
			res[2] = a*b[2];
			res[3] = a*b[3];
			res[4] = a*b[4];
			res[5] = a*b[5];
			res[6] = a*b[6];
			res[7] = a*b[7];
			res[8] = a*b[8];
			res[9] = a*b[9];
			res[10] = a*b[10];
			res[11] = a*b[11];
			res[12] = a*b[12];
			res[13] = a*b[13];
			res[14] = a*b[14];
			res[15] = a*b[15];
			return res;
		}

		/// <summary>
		/// PGA3D.muls : res = a * b
		/// multivector/scalar multiplication
		/// </summary>
		public static double[] muls (double[] a, float b){
			double[] res = new double[16];
			res[0] = a[0]*b;
			res[1] = a[1]*b;
			res[2] = a[2]*b;
			res[3] = a[3]*b;
			res[4] = a[4]*b;
			res[5] = a[5]*b;
			res[6] = a[6]*b;
			res[7] = a[7]*b;
			res[8] = a[8]*b;
			res[9] = a[9]*b;
			res[10] = a[10]*b;
			res[11] = a[11]*b;
			res[12] = a[12]*b;
			res[13] = a[13]*b;
			res[14] = a[14]*b;
			res[15] = a[15]*b;
			return res;
		}

		/// <summary>
		/// PGA3D.sadd : res = a + b
		/// scalar/multivector addition
		/// </summary>
		public static double[] sadd (float a, double[] b){
			double[] res = new double[16];
			res[0] = a+b[0];
			res[1] = b[1];
			res[2] = b[2];
			res[3] = b[3];
			res[4] = b[4];
			res[5] = b[5];
			res[6] = b[6];
			res[7] = b[7];
			res[8] = b[8];
			res[9] = b[9];
			res[10] = b[10];
			res[11] = b[11];
			res[12] = b[12];
			res[13] = b[13];
			res[14] = b[14];
			res[15] = b[15];
			return res;
		}

		/// <summary>
		/// PGA3D.adds : res = a + b
		/// multivector/scalar addition
		/// </summary>
		public static double[] adds (double[] a, float b){
			double[] res = new double[16];
			res[0] = a[0]+b;
			res[1] = a[1];
			res[2] = a[2];
			res[3] = a[3];
			res[4] = a[4];
			res[5] = a[5];
			res[6] = a[6];
			res[7] = a[7];
			res[8] = a[8];
			res[9] = a[9];
			res[10] = a[10];
			res[11] = a[11];
			res[12] = a[12];
			res[13] = a[13];
			res[14] = a[14];
			res[15] = a[15];
			return res;
		}

		/// <summary>
		/// PGA3D.ssub : res = a - b
		/// scalar/multivector subtraction
		/// </summary>
		public static double[] ssub (float a, double[] b){
			double[] res = new double[16];
			res[0] = a-b[0];
			res[1] = -b[1];
			res[2] = -b[2];
			res[3] = -b[3];
			res[4] = -b[4];
			res[5] = -b[5];
			res[6] = -b[6];
			res[7] = -b[7];
			res[8] = -b[8];
			res[9] = -b[9];
			res[10] = -b[10];
			res[11] = -b[11];
			res[12] = -b[12];
			res[13] = -b[13];
			res[14] = -b[14];
			res[15] = -b[15];
			return res;
		}

		/// <summary>
		/// PGA3D.subs : res = a - b
		/// multivector/scalar subtraction
		/// </summary>
		public static double[] subs (double[] a, float b){
			double[] res = new double[16];
			res[0] = a[0]-b;
			res[1] = a[1];
			res[2] = a[2];
			res[3] = a[3];
			res[4] = a[4];
			res[5] = a[5];
			res[6] = a[6];
			res[7] = a[7];
			res[8] = a[8];
			res[9] = a[9];
			res[10] = a[10];
			res[11] = a[11];
			res[12] = a[12];
			res[13] = a[13];
			res[14] = a[14];
			res[15] = a[15];
			return res;
		}

        /// <summary>
        /// PGA3D.norm()
        /// Calculate the Euclidean norm. (strict positive).
        /// </summary>
		public float norm(double[] mv) { 
            return (float) Math.sqrt(Math.abs(gp(mv, conjugate(mv))[0]));
        }
		
		/// <summary>
		/// PGA3D.inorm()
		/// Calculate the Ideal norm. (signed)
		/// </summary>
		/*public float inorm() { 
            return this[1]!=0.0f?this[1]:this[15]!=0.0f?this[15]:(!this).norm();
        }*/
		
		/// <summary>
		/// PGA3D.normalized()
		/// Returns a normalized (Euclidean) element.
		/// </summary>
		/*public PGA3D normalized() { 
            return this*(1/norm()); 
        }*/
		
		
		// PGA is plane based. Vectors are planes. (think linear functionals)
		/*public static PGA3D e0 = new PGA3D(1f, 1);
		public static PGA3D e1 = new PGA3D(1f, 2);
		public static PGA3D e2 = new PGA3D(1f, 3);
		public static PGA3D e3 = new PGA3D(1f, 4);*/
		
		// PGA lines are bivectors.
		/*public static PGA3D e01 = e0^e1; 
		public static PGA3D e02 = e0^e2;
		public static PGA3D e03 = e0^e3;
		public static PGA3D e12 = e1^e2; 
		public static PGA3D e31 = e3^e1;
		public static PGA3D e23 = e2^e3;*/
		
		// PGA points are trivectors.
		/*public static PGA3D e123 = e1^e2^e3; // the origin
		public static PGA3D e032 = e0^e3^e2;
		public static PGA3D e013 = e0^e1^e3;
		public static PGA3D e021 = e0^e2^e1;*/

		/// <summary>
		/// PGA3D.plane(a,b,c,d)
		/// A plane is defined using its homogenous equation ax + by + cz + d = 0
		/// </summary>
		/*public static double[] plane(float a, float b, float c, float d) { 
            return a*e1 + b*e2 + c*e3 + d*e0; 
        }*/
		
		/// <summary>
		/// PGA3D.point(x,y,z)
		/// A point is just a homogeneous point, euclidean coordinates plus the origin
		/// </summary>
		/*public static PGA3D point(float x, float y, float z) { 
            return e123 + x*e032 + y*e013 + z*e021; 
        }*/
		
		/// <summary>
		/// Rotors (euclidean lines) and translators (ideal lines)
		/// </summary>
		/*public static PGA3D rotor(float angle, PGA3D line) { 
            return ((float) Math.Cos(angle/2.0f)) +  ((float) Math.Sin(angle/2.0f)) * line.normalized(); 
        }*/
		/*public static PGA3D translator(float dist, PGA3D line) { 
            return 1.0f + (dist/2.0f) * line; 
        }*/

		// for our toy problem (generate points on the surface of a torus)
		// we start with a function that generates motors.
		// circle(t) with t going from 0 to 1.
		/*public static PGA3D circle(float t, float radius, PGA3D line) {
		  return rotor(t*2.0f*(float) Math.PI,line) * translator(radius,e1*e0);
		}*/
		
		// a torus is now the product of two circles. 
		/*public static PGA3D torus(float s, float t, float r1, PGA3D l1, float r2, PGA3D l2) {
		  return circle(s,r2,l2)*circle(t,r1,l1);
		}*/
		
		// and to sample its points we simply sandwich the origin ..
		/*public static PGA3D point_on_torus(float s, float t) {
		  var to = torus(s,t,0.25f,e12,0.6f,e31);
		  return to * e123 * ~to;
		}*/

		
		/// string cast
		/*public override string ToString()
		{
			var sb = new StringBuilder();
			var n=0;
			for (int i = 0; i < 16; ++i) 
				if (_mVec[i] != 0.0f) {
					sb.Append($"{_mVec[i]}{(i == 0 ? string.Empty : _basis[i])} + ");
					n++;
			        }
			if (n==0) sb.Append("0");
			return sb.ToString().TrimEnd(' ', '+');
		}*/
        
        
        
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
    
    
    private static double[] createRandomScalar() {
        double[] result = new double[32];
        //TODO scalar, eoinf konstruieren, d.h. die betreffenden blades
        // beschaffen und random werte hineinsetzen
        return result;
    }

    public static double[] createValueRandom() {
        IAlgebra algebra = PgaFactory.instance.getIAlgebra();
        final int basisBladesCount = algebra.getBladesCount(); //baseCayleyTable.getBladesCount();
        double[] result = new Random().doubles(-1, 1).limit(basisBladesCount).toArray();
        return result;
    }

    public static double[] createValueRandom(int grade) {
        IAlgebra algebra = PgaFactory.instance.getIAlgebra();
        final int basisBladesCount = algebra.getBladesCount();//baseCayleyTable.getBladesCount();
        double[] result = new double[basisBladesCount];
        Random random = new Random();
        int[] indizes = algebra.getIndizes(grade); //CGACayleyTableGeometricProduct.getIndizes(grade);
        double[] values = random.doubles(-1, 1).
            limit(indizes.length).toArray();
        for (int i = 0; i < indizes.length; i++) {
            result[indizes[i]] = values[i];
        }
        return result;
    }

    public static double[] createValueRandom(int[] blades) {
        IAlgebra algebra = PgaFactory.instance.getIAlgebra();
        final int basisBladesCount = algebra.getBladesCount();//baseCayleyTable.getBladesCount();
        double[] temp = new Random().doubles(-1, 1).limit(basisBladesCount).toArray();
        double[] result = new double[basisBladesCount];
        for (int i = 0; i < blades.length; i++) {
            result[blades[i]] = temp[blades[i]];
        }
        return result;
    }

    public static MultivectorValue createValue(GAFactory exprGraphFactory, double[] values) {
        //SparseCGAColumnVector sdm = SparseCGAColumnVector.fromValues(values);
        SparseDoubleColumnVector sdm = SparseDoubleColumnVector.fromValues(values);
        return exprGraphFactory.createValue(sdm);
    }

}

	/*class Program
	{
	        

		static void Main(string[] args)
		{
		
			// Elements of the even subalgebra (scalar + bivector + pss) of unit length are motors
			var rot = rotor((float) Math.PI/2.0f,e1*e2);
			
			// The outer product ^ is the MEET. Here we intersect the yz (x=0) and xz (y=0) planes.
			var ax_z = e1 ^ e2;
			
			// line and plane meet in point. We intersect the line along the z-axis (x=0,y=0) with the xy (z=0) plane.
			var orig = ax_z ^ e3;
			
			// We can also easily create points and join them into a line using the regressive (vee, &) product.
			var px = point(1,0,0);
			var line = orig & px;
			
			// Lets also create the plane with equation 2x + z - 3 = 0
			var p = plane(2,0,1,-3);
			
			// rotations work on all elements
			var rotated_plane = rot * p * ~rot;
			var rotated_line  = rot * line * ~rot;
			var rotated_point = rot * px * ~rot;
			
			// See the 3D PGA Cheat sheet for a huge collection of useful formulas
			var point_on_plane = (p | px) * p;
			
			// Some output
			Console.WriteLine("a point       : "+px);
			Console.WriteLine("a line        : "+line);
			Console.WriteLine("a plane       : "+p);
			Console.WriteLine("a rotor       : "+rot);
			Console.WriteLine("rotated line  : "+rotated_line);
			Console.WriteLine("rotated point : "+rotated_point);
			Console.WriteLine("rotated plane : "+rotated_plane);
			Console.WriteLine("point on plane: "+point_on_plane.normalized());
			Console.WriteLine("point on torus: "+point_on_torus(0.0f,0.0f));

		}
	}
}*/

 // [8] M Roelfs and S De Keninck. 2021.
    // Graded Symmetry Groups: Plane and Simple. arXiv:2107.03771 [math-ph]
    // https://arxiv.org/pdf/2107.03771
    // https://enki.ws/ganja.js/examples/coffeeshop.html#NSELGA
/*private exp(B) {
  var l = (B[3]*B[3] + B[4]*B[4] + B[5]*B[5]);
  if (l==0) return rotor(1, B[0], B[1], B[2], 0, 0, 0, 0);
  var m = (B[0]*B[5] + B[1]*B[4] + B[2]*B[3]), a = sqrt(l), c = cos(a), s = sin(a)/a, t = m/l*(c-s);
  return rotor(c, s*B[0] + t*B[5], s*B[1] + t*B[4], s*B[2] + t*B[3], s*B[3], s*B[4], s*B[5], m*s);
}*/

