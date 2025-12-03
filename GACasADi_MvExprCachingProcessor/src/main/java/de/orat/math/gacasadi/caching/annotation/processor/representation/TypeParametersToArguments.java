package de.orat.math.gacasadi.caching.annotation.processor.representation;

import de.orat.math.gacasadi.caching.annotation.processor.common.ErrorException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;

public class TypeParametersToArguments {

    private final Map<String, String> innerMap;

    public TypeParametersToArguments() {
        this.innerMap = Collections.emptyMap();
    }

    public void substitute(TypeParametersToArguments with) {
        var withEntryMap = with.innerMap;
        var onEntrySet = this.innerMap.entrySet();
        for (var onEntry : onEntrySet) {
            String newValue = withEntryMap.get(onEntry.getValue());
            if (newValue != null) {
                onEntry.setValue(newValue);
            }
        }
    }

    public TypeParametersToArguments(DeclaredType i) throws ErrorException {
        Map<String, String> typeParametersToArguments = new HashMap<>();
        List<String> params = ((TypeElement) i.asElement()).getTypeParameters().stream()
            .map(param -> param.getSimpleName().toString())
            .toList();

        List<? extends TypeMirror> typeArgs = i.getTypeArguments();
        List<String> args = new ArrayList<>(typeArgs.size());
        for (TypeMirror typeArg : typeArgs) {
            String name = switch (typeArg.getKind()) {
                case TYPEVAR ->
                    ((TypeParameterElement) ((TypeVariable) typeArg).asElement()).getSimpleName().toString();
                case DECLARED ->
                    ((TypeElement) ((DeclaredType) typeArg).asElement()).getQualifiedName().toString();
                default ->
                    throw new AssertionError();
            };
            args.add(name);
        }

        if (params.size() != args.size()) {
            throw new AssertionError("Incorrect assumption 1 in GenerateCachedProcessor:TypeParametersToArguments:computeMethods.");
        }

        for (int pos = 0; pos < params.size(); ++pos) {
            String param = params.get(pos);
            if (typeParametersToArguments.containsKey(param)) {
                throw new AssertionError("Incorrect assumption 2 in GenerateCachedProcessor:TypeParametersToArguments:computeMethods.");
            }
            typeParametersToArguments.put(param, args.get(pos));
        }

        this.innerMap = typeParametersToArguments;
    }

    public String clearTypeParameterIfPresent(String type) {
        return this.innerMap.getOrDefault(type, type);
    }
}
