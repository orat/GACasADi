package de.orat.math.gacasadi.generic;

import de.dhbw.rahmlab.casadi.SxStatic;
import de.dhbw.rahmlab.casadi.impl.casadi.Function;
import de.dhbw.rahmlab.casadi.impl.casadi.SX;
import de.dhbw.rahmlab.casadi.impl.std.StdVectorCasadiInt;
import de.dhbw.rahmlab.casadi.impl.std.StdVectorSX;
import de.orat.math.gacalc.spi.ILoopService;
import de.orat.math.gacalc.spi.IMultivectorExpressionArray;
import de.orat.math.gacalc.spi.IMultivectorVariable;
import static de.orat.math.gacasadi.generic.CasADiUtil.areMVSparsitiesSupersetsOfSubsets;
import static de.orat.math.gacasadi.generic.GaFunction.transformImpl;
import java.util.List;
import java.util.stream.LongStream;
import java.util.stream.Stream;

/**
 * <pre>
 * https://web.casadi.org/docs/#for-loop-equivalents
 * https://web.casadi.org/api/html/da/da4/classcasadi_1_1Function.html
 * </pre>
 */
public class GaLoopService<EXPR extends GaMvExpr<EXPR>, VAR extends GaMvVariable<EXPR>>
    implements ILoopService<EXPR, VAR, GaExprArray<EXPR>> {

    private final GaFactory<EXPR, VAR, ?> fac;

    public GaLoopService(GaFactory<EXPR, VAR, ?> fac) {
        this.fac = fac;
    }

    @Override
    public IMultivectorExpressionArray<EXPR> toExprArray(List<EXPR> from) {
        return new GaExprArray(from);
    }

    @Override
    public List<GaExprArray<EXPR>> map(
        List<VAR> paramsSimple,
        List<VAR> paramsArray,
        List<EXPR> returnsArray,
        List<EXPR> argsSimple,
        List<GaExprArray<EXPR>> argsArray,
        int iterations) {
        return mapImpl(paramsSimple, paramsArray, returnsArray, argsSimple, argsArray, iterations);
    }

    @Override
    public AccumArrayListReturn<EXPR, GaExprArray<EXPR>> fold(
        List<VAR> paramsAccum,
        List<VAR> paramsSimple,
        List<VAR> paramsArray,
        List<EXPR> returnsAccum,
        List<EXPR> returnsArray,
        List<EXPR> argsAccumInitial,
        List<EXPR> argsSimple,
        List<GaExprArray<EXPR>> argsArray,
        int iterations) {
        return foldImpl(paramsAccum, paramsSimple, paramsArray, returnsAccum, returnsArray, argsAccumInitial, argsSimple, argsArray, iterations);
    }

    @Override
    public AccumArrayListReturn<GaExprArray<EXPR>, GaExprArray<EXPR>> mapaccum(
        List<VAR> paramsAccum,
        List<VAR> paramsSimple,
        List<VAR> paramsArray,
        List<EXPR> returnsAccum,
        List<EXPR> returnsArray,
        List<EXPR> argsAccumInitial,
        List<EXPR> argsSimple,
        List<GaExprArray<EXPR>> argsArray,
        int iterations) {
        return mapaccumImpl(paramsAccum, paramsSimple, paramsArray, returnsAccum, returnsArray, argsAccumInitial, argsSimple, argsArray, iterations);
    }

    /**
     * <pre>
     * for-loop equivalent.
     *
     * The map operation exhibits constant graph size and initialization time.
     * </pre>
     *
     * @param paramsSimple Plain variables.
     * @param paramsArray Array variables.
     * @param argsArray Hint: Index of element used in the computation is equal to the current iteration.
     * @return One array element for each iteration for the variables of the returnsArray parameter.
     */
    public <MV extends IGetSX & IGetSparsityCasadi & IMultivectorVariable> List<GaExprArray<EXPR>> mapImpl(
        List<MV> paramsSimple,
        List<MV> paramsArray,
        List<? extends EXPR> returnsArray,
        List<? extends EXPR> argsSimple,
        List<GaExprArray<EXPR>> argsArray,
        int iterations) {
        assert iterations >= 1;
        assert paramsSimple.size() == argsSimple.size();
        assert paramsArray.size() == argsArray.size();
        for (int i = 0; i < paramsArray.size(); ++i) {
            var param = paramsArray.get(i);
            var argsArr = argsArray.get(i);
            assert argsArr.size() == iterations;
            assert argsArr.areSparsitiesSubsetsOf(param.getSX().sparsity());
        }
        assert areMVSparsitiesSupersetsOfSubsets(paramsSimple, argsSimple);

        var def_sym_in = new StdVectorSX(
            Stream.concat(
                paramsSimple.stream().map(IGetSX::getSX),
                paramsArray.stream().map(IGetSX::getSX)
            ).toList()
        );
        var def_sym_out = new StdVectorSX(returnsArray.stream().map(IGetSX::getSX).toList());
        var f_sym_casadi = new Function("MapBase", def_sym_in, def_sym_out);

        var call_sym_in = new StdVectorSX(
            Stream.concat(
                argsSimple.stream().map(IGetSX::getSX),
                argsArray.stream().map(GaLoopService::horzcat)
            ).toList()
        );
        var call_sym_out = new StdVectorSX();
        // Works as long as they are first in def_sym_in and call_sym_in.
        var nonRepeated = new StdVectorCasadiInt(LongStream.range(0, paramsSimple.size()).boxed().toList());
        // parallelization = unroll|serial|openmp
        f_sym_casadi.map("MapMap", "serial", iterations, nonRepeated, new StdVectorCasadiInt()).call(call_sym_in, call_sym_out);

        var call_out = call_sym_out.stream().map(sx -> horzsplit(sx)).map(GaExprArray::new).toList();
        return call_out;
    }

    private static <T> Stream<T> StreamConcat(Stream<? extends T> a, Stream<? extends T> b, Stream<? extends T> c) {
        return Stream.concat(a, Stream.concat(b, c));
    }

    /**
     * <pre>
     * for-loop equivalent.
     *
     * The fold operation exhibits a graph size and initialization time that scales logarithmically with n.
     * </pre>
     *
     * @param paramsAccum Variables (fold) or array elements (mapaccum) which depend on the previous
     * iteration.
     * @param paramsSimple Plain variables.
     * @param paramsArray Array variables.
     * @param argsArray Hint: Index of element used in the computation is equal to the current iteration.
     * @return Only end results of accum Variables. One array element for each iteration for the variables of
     * the returnsArray parameter.
     */
    public <MV extends IGetSX & IGetSparsityCasadi & IMultivectorVariable> AccumArrayListReturn<EXPR, GaExprArray<EXPR>> foldImpl(
        List<MV> paramsAccum,
        List<MV> paramsSimple,
        List<MV> paramsArray,
        List<? extends EXPR> returnsAccum,
        List<? extends EXPR> returnsArray,
        List<? extends EXPR> argsAccumInitial,
        List<? extends EXPR> argsSimple,
        List<GaExprArray<EXPR>> argsArray,
        int iterations) {
        assert iterations >= 1;
        assert paramsAccum.size() >= 1;
        assert paramsAccum.size() == returnsAccum.size();
        assert paramsAccum.size() == argsAccumInitial.size();
        assert paramsSimple.size() == argsSimple.size();
        assert paramsArray.size() == argsArray.size();
        for (int i = 0; i < paramsArray.size(); ++i) {
            var param = paramsArray.get(i);
            var argsArr = argsArray.get(i);
            assert argsArr.size() == iterations;
            assert argsArr.areSparsitiesSubsetsOf(param.getSX().sparsity());
        }
        assert areMVSparsitiesSupersetsOfSubsets(paramsAccum, returnsAccum);
        assert areMVSparsitiesSupersetsOfSubsets(paramsAccum, argsAccumInitial);
        assert areMVSparsitiesSupersetsOfSubsets(paramsSimple, argsSimple);

        var def_sym_in = new StdVectorSX(
            StreamConcat(
                Stream.of(GaLoopService.horzcat(paramsAccum)),
                paramsSimple.stream().map(IGetSX::getSX),
                paramsArray.stream().map(IGetSX::getSX)
            ).toList()
        );
        var def_sym_out = new StdVectorSX(
            Stream.concat(
                Stream.of(GaLoopService.horzcat(returnsAccum)),
                returnsArray.stream().map(IGetSX::getSX)
            ).toList()
        );
        var f_sym_casadi = new Function("foldBase", def_sym_in, def_sym_out);

        var call_sym_in = new StdVectorSX(
            StreamConcat(
                Stream.of(GaLoopService.horzcat(argsAccumInitial)),
                // CasADi treats a SX as an arbitrary long List of SxStatic.
                // No need to use repmat.
                argsSimple.stream().map(IGetSX::getSX),
                argsArray.stream().map(GaLoopService::horzcat)
            ).toList()
        );
        var call_sym_out = new StdVectorSX();
        f_sym_casadi.fold(iterations).call(call_sym_in, call_sym_out);

        var call_out_all = call_sym_out.stream().toList();
        var call_out_accum = this.horzsplit(call_out_all.get(0));
        var call_out_array = call_out_all.subList(1, call_out_all.size()).stream().map(sx -> horzsplit(sx)).map(GaExprArray::new).toList();
        var call_out = new AccumArrayListReturn(call_out_accum, call_out_array);
        return call_out;
    }

    /**
     * <pre>
     * for-loop equivalent.
     *
     * The mapaccum operation exhibits a graph size and initialization time that scales logarithmically with n.
     * </pre>
     *
     * @param paramsAccum Variables (fold) or array elements (mapaccum) which depend on the previous
     * iteration.
     * @param paramsSimple Plain variables.
     * @param paramsArray Array variables.
     * @param argsArray Hint: Index of element used in the computation is equal to the current iteration.
     * @return Results of all iterations of accum Variables. One array element for each iteration for the
     * variables of the returnsArray parameter.
     */
    public <MV extends IGetSX & IGetSparsityCasadi & IMultivectorVariable> AccumArrayListReturn<GaExprArray<EXPR>, GaExprArray<EXPR>> mapaccumImpl(
        List<MV> paramsAccum,
        List<MV> paramsSimple,
        List<MV> paramsArray,
        List<? extends EXPR> returnsAccum,
        List<? extends EXPR> returnsArray,
        List<? extends EXPR> argsAccumInitial,
        List<? extends EXPR> argsSimple,
        List<GaExprArray<EXPR>> argsArray,
        int iterations) {
        assert iterations >= 1;
        assert paramsAccum.size() >= 1;
        assert paramsAccum.size() == returnsAccum.size();
        assert paramsAccum.size() == argsAccumInitial.size();
        assert paramsSimple.size() == argsSimple.size();
        assert paramsArray.size() == argsArray.size();
        for (int i = 0; i < paramsArray.size(); ++i) {
            var param = paramsArray.get(i);
            var argsArr = argsArray.get(i);
            assert argsArr.size() == iterations;
            assert argsArr.areSparsitiesSubsetsOf(param.getSX().sparsity());
        }
        assert areMVSparsitiesSupersetsOfSubsets(paramsAccum, returnsAccum);
        assert areMVSparsitiesSupersetsOfSubsets(paramsAccum, argsAccumInitial);
        assert areMVSparsitiesSupersetsOfSubsets(paramsSimple, argsSimple);

        var def_sym_in = new StdVectorSX(
            StreamConcat(
                paramsAccum.stream().map(IGetSX::getSX),
                paramsSimple.stream().map(IGetSX::getSX),
                paramsArray.stream().map(IGetSX::getSX)
            ).toList()
        );
        var def_sym_out = new StdVectorSX(
            Stream.concat(
                returnsAccum.stream().map(IGetSX::getSX),
                returnsArray.stream().map(IGetSX::getSX)
            ).toList()
        );
        var f_sym_casadi = new Function("MapaccumBase", def_sym_in, def_sym_out);

        var call_sym_in = new StdVectorSX(
            StreamConcat(
                argsAccumInitial.stream().map(IGetSX::getSX),
                // CasADi treats a SX as an arbitrary long List of SxStatic.
                // No need to use repmat.
                argsSimple.stream().map(IGetSX::getSX),
                argsArray.stream().map(GaLoopService::horzcat)
            ).toList()
        );
        var call_sym_out = new StdVectorSX();
        // Works as long as they are first in def_sym_in and call_sym_in.
        var accumVars = new StdVectorCasadiInt(LongStream.range(0, paramsAccum.size()).boxed().toList());
        f_sym_casadi.mapaccum("MapaccumMapaccum", iterations, accumVars, accumVars).call(call_sym_in, call_sym_out);

        var call_out_all = call_sym_out.stream().toList();
        var call_out_accum = call_out_all.subList(0, returnsAccum.size()).stream().map(sx -> horzsplit(sx)).map(GaExprArray::new).toList();
        var call_out_array = call_out_all.subList(returnsAccum.size(), call_out_all.size()).stream().map(sx -> horzsplit(sx)).map(GaExprArray::new).toList();
        var call_out = new AccumArrayListReturn(call_out_accum, call_out_array);
        return call_out;
    }

    public static SX horzcat(List<? extends IGetSX> mvs) {
        StdVectorSX stdVec = transformImpl(mvs);
        SX sxHorzcat = SxStatic.horzcat(stdVec);
        return sxHorzcat;
    }

    public List<? extends EXPR> horzsplit(SX sxHorzcat) {
        StdVectorSX stdVec = SxStatic.horzsplit_n(sxHorzcat, sxHorzcat.columns());
        var mvs = stdVec.stream().map(sx -> fac.SXtoEXPR(sx)).toList();
        return mvs;
    }
}
