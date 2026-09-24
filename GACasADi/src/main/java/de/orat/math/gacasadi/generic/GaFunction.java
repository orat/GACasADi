package de.orat.math.gacasadi.generic;

import de.dhbw.rahmlab.casadi.api.core.wrapper.function.FunctionWrapper;
import de.dhbw.rahmlab.casadi.impl.casadi.Function;
import de.dhbw.rahmlab.casadi.impl.casadi.SX;
import de.dhbw.rahmlab.casadi.impl.casadi.Sparsity;
import de.dhbw.rahmlab.casadi.impl.std.StdVectorSX;
import de.dhbw.rahmlab.casadi.implUtil.WrapUtil;
import de.orat.math.gacalc.spi.IGAFunction;
import static de.orat.math.gacasadi.generic.CasADiUtil.areSparsitiesSupersetsOfSubsets;
import static de.orat.math.gacasadi.generic.CasADiUtil.toSparsities;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

/**
 * @author Oliver Rettig (Oliver.Rettig@orat.de)
 */
public class GaFunction<EXPR extends IGaMvExpr<EXPR, VAR, VAL>, VAR extends IGaMvVariable<EXPR, VAR, VAL>, VAL extends IGaMvValue<EXPR, VAR, VAL>>
    implements IGAFunction<EXPR, VAR, VAL> {

    private final String name;
    private final int arity;
    private final int resultCount;
    private final List<Sparsity> paramsSparsities;
    private final List<VAR> params;
    private final GaFactory<EXPR, VAR, VAL> fac;

    private final Function f_sym_casadi;

    protected Function getCasADiFunction() {
        return this.f_sym_casadi;
    }

    /**
     * @param name A valid CasADi function name starts with a letter followed by letters, numbers or
     * non-consecutive underscores.
     */
    public GaFunction(GaFactory<EXPR, VAR, VAL> fac, String name, List<? extends VAR> parameters, List<? extends EXPR> returns) {
        try {
            this.fac = fac;
            this.params = Collections.unmodifiableList(parameters);
            this.paramsSparsities = parameters.stream().map(IGetSX::getSX).map(SX::sparsity).toList();
            StdVectorSX def_sym_in = transformImpl(Stream.concat(parameters.stream(), Stream.of(fac.PI_VAR())).toList());
            StdVectorSX def_sym_out = transformImpl(returns);
            this.name = name;
            this.arity = parameters.size();
            this.resultCount = returns.size();
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

            StdVectorSX call_sym_in = transformImpl(Stream.concat(arguments.stream(), Stream.of(this.fac.PI_EXPR())).toList());
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
            StdVectorSX call_num_in = new StdVectorSX(Stream.concat(
                arguments.stream()
                    .map(IGaMvValue::getDM),
                Stream.of(this.fac.PI_VAL().getDM()))
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

    /*
    public List<Double> callDouble(List<Double> args) {
        StdVectorDouble vecDouble = new StdVectorDouble(args);
        DM call_num_in = new DM(vecDouble);
    }
     */
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

    @Override
    public List<VAR> getParameters() {
        return this.params;
    }

    @Override
    public List<EXPR> toExprs() {
        // These are just the returns given to the constructor.
        // Unsure: caching them might be expensive if they otherwise could be freed.
        // Expected toExprs to be used rarely.
        // And its cheap to call this function anyway.
        // But its good to make more explicit that GaFunction and GaMvExpr kind of isomorphic.
        List<EXPR> paramsAsExpr = this.params.stream().map(VAR::asEXPR).toList();
        return this.callExpr(paramsAsExpr);
    }

    @Override
    public void generateC(String path, String fileName) {
        // C function name will be same as casadi function name.
        new FunctionWrapper(f_sym_casadi).generate(path, fileName);
    }
}
