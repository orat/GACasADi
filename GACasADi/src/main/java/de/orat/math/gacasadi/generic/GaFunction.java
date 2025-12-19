package de.orat.math.gacasadi.generic;

import de.dhbw.rahmlab.casadi.impl.casadi.Function;
import de.dhbw.rahmlab.casadi.impl.casadi.SX;
import de.dhbw.rahmlab.casadi.impl.casadi.Sparsity;
import de.dhbw.rahmlab.casadi.impl.std.StdVectorSX;
import de.dhbw.rahmlab.casadi.implUtil.WrapUtil;
import de.orat.math.gacalc.api.GAFunction;
import de.orat.math.gacalc.spi.IGAFunction;
import de.orat.math.gacalc.spi.IMultivectorVariable;
import static de.orat.math.gacasadi.generic.CasADiUtil.areSparsitiesSupersetsOfSubsets;
import static de.orat.math.gacasadi.generic.CasADiUtil.toSparsities;
import java.util.List;

/**
 * @author Oliver Rettig (Oliver.Rettig@orat.de)
 */
public class GaFunction<EXPR extends IGaMvExpr<EXPR>, VAL extends IGaMvValue<VAL, EXPR>> implements IGAFunction<EXPR, VAL> {

    private final String name;
    private final int arity;
    private final int resultCount;
    private final List<Sparsity> paramsSparsities;
    private final GaFactory<EXPR, ?, ?, VAL> fac;

    private final Function f_sym_casadi;

    protected Function getCasADiFunction() {
        return this.f_sym_casadi;
    }

    /**
     * @param name A valid CasADi function name starts with a letter followed by letters, numbers or
     * non-consecutive underscores.
     */
    public <MV extends IGetSX & IMultivectorVariable> GaFunction(GaFactory<EXPR, ?, ?, VAL> fac, String name, List<MV> parameters, List<? extends IGaMvExpr> returns) {
        try {
            this.fac = fac;
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
    public List<EXPR> callExpr(List<? extends EXPR> arguments) {
        try {
            if (arguments.size() != this.arity) {
                throw new IllegalArgumentException(String.format("Expected %s arguments, but got %s.",
                    this.arity, arguments.size()));
            }
            assert areSparsitiesSupersetsOfSubsets(this.paramsSparsities, toSparsities(arguments));

            StdVectorSX call_sym_in = transformImpl(arguments);
            StdVectorSX call_sym_out = new StdVectorSX();
            this.f_sym_casadi.call(call_sym_in, call_sym_out);
            return call_sym_out.stream().map(sx -> fac.SXtoEXPR(sx)).toList();
        } finally {
            WrapUtil.MANUAL_CLEANER.cleanupUnreachable();
        }
    }

    @Override
    public List<VAL> callValue(List<? extends VAL> arguments) {
        try {
            if (arguments.size() != this.arity) {
                throw new IllegalArgumentException(String.format("Expected %s arguments, but got %s.",
                    this.arity, arguments.size()));
            }
            assert areSparsitiesSupersetsOfSubsets(this.paramsSparsities, toSparsities(arguments));

            // For unknown reasons under certain circumstances, calling with DM produces NaN, while calling with SX produces the correct value.
            StdVectorSX call_num_in = new StdVectorSX(arguments.stream()
                .map(IGaMvValue::getDM)
                .map(CasADiUtil::toSX)
                .toList()
            );
            StdVectorSX call_num_out = new StdVectorSX();
            this.f_sym_casadi.call(call_num_in, call_num_out);
            return call_num_out.stream()
                .map(CasADiUtil::toDM)
                .map(dm -> fac.DMtoVAL(dm))
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
