package de.orat.math.gacasadi.impl;

import de.dhbw.rahmlab.casadi.impl.casadi.Function;
import de.dhbw.rahmlab.casadi.impl.casadi.SX;
import de.dhbw.rahmlab.casadi.impl.casadi.Sparsity;
import de.dhbw.rahmlab.casadi.impl.std.StdVectorSX;
import de.dhbw.rahmlab.casadi.implUtil.WrapUtil;
import de.orat.math.gacasadi.CasADiUtil;
import de.orat.math.gacalc.api.GAFunction;
import java.util.List;
import de.orat.math.gacalc.spi.IGAFunction;
import de.orat.math.gacalc.spi.IMultivectorVariable;

/**
 * @author Oliver Rettig (Oliver.Rettig@orat.de)
 */
public class GaFunction implements IGAFunction<GaMvExpr, GaMvValue> {

    private final String name;
    private final int arity;
    private final int resultCount;
    private final List<Sparsity> paramsSparsities;

    // available after plugging the impl into the api object
    private GAFunction.Callback callback;

    private final Function f_sym_casadi;

    protected Function getCasADiFunction() {
        return this.f_sym_casadi;
    }

    /**
     * @param name A valid CasADi function name starts with a letter followed by letters, numbers or
     * non-consecutive underscores.
     */
    public <MV extends IGetSX & IMultivectorVariable> GaFunction(String name, List<MV> parameters, List<? extends GaMvExpr> returns) {
        try {
            this.paramsSparsities = parameters.stream().map(IGetSX::getSX).map(SX::sparsity).toList();
            StdVectorSX def_sym_in = transformImpl(parameters);
            StdVectorSX def_sym_out = transformImpl(returns);
            this.name = name;
            arity = parameters.size();
            resultCount = returns.size();
            this.f_sym_casadi = new Function(name, def_sym_in, def_sym_out);
        } finally {
            WrapUtil.MANUAL_CLEANER.cleanupUnreachable();
        }
    }

    protected static StdVectorSX transformImpl(List<? extends IGetSX> mvs) {
        List<SX> sxs = mvs.stream().map(IGetSX::getSX).toList();
        return new StdVectorSX(sxs);
    }

    @Override
    public List<GaMvExpr> callExpr(List<? extends GaMvExpr> arguments) {
        try {
            if (arguments.size() != this.arity) {
                throw new IllegalArgumentException(String.format("Expected %s arguments, but got %s.",
                    this.arity, arguments.size()));
            }
            assert CasADiUtil.areSparsitiesSupersetsOfSubsets(this.paramsSparsities, CasADiUtil.toSparsities(arguments));

            StdVectorSX call_sym_in = transformImpl(arguments);
            StdVectorSX call_sym_out = new StdVectorSX();
            this.f_sym_casadi.call(call_sym_in, call_sym_out);
            return call_sym_out.stream().map(GaMvExpr::create).toList();
        } finally {
            WrapUtil.MANUAL_CLEANER.cleanupUnreachable();
        }
    }

    @Override
    public List<GaMvValue> callValue(List<? extends GaMvValue> arguments) {
        try {
            if (arguments.size() != this.arity) {
                throw new IllegalArgumentException(String.format("Expected %s arguments, but got %s.",
                    this.arity, arguments.size()));
            }
            assert CasADiUtil.areSparsitiesSupersetsOfSubsets(this.paramsSparsities, CasADiUtil.toSparsities(arguments));

            // For unknown reasons under certain circumstances, calling with DM produces NaN, while calling with SX produces the correct value.
            StdVectorSX call_num_in = new StdVectorSX(arguments.stream()
                .map(GaMvValue::getDM)
                .map(CasADiUtil::toSX)
                .toList()
            );
            StdVectorSX call_num_out = new StdVectorSX();
            this.f_sym_casadi.call(call_num_in, call_num_out);
            return call_num_out.stream()
                .map(CasADiUtil::toDM)
                .map(GaMvValue::create)
                .toList();
        } finally {
            WrapUtil.MANUAL_CLEANER.cleanupUnreachable();
        }
    }

    @Override
    public String toString() {
        return f_sym_casadi.toString();
    }

    @Override
    public void init(GAFunction.Callback callback) {
        this.callback = callback;
    }

    @Override
    public int getArity() {
        return arity;
    }

    @Override
    public int getResultCount() {
        return resultCount;
    }

    @Override
    public String getName() {
        return name;
    }
}
