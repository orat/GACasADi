package de.orat.math.gacasadi.caching.annotation.processor.representation;

import de.orat.math.gacasadi.caching.annotation.api.Uncached;
import de.orat.math.gacasadi.caching.annotation.processor.GenerateCachedProcessor.Utils;
import de.orat.math.gacasadi.caching.annotation.processor.common.ErrorException;
import de.orat.math.gacasadi.caching.annotation.processor.common.UncachedException;
import de.orat.math.gacasadi.caching.annotation.processor.common.FailedToCacheException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;

public final class Method {

    public final String name;
    public final String returnType;
    public final Set<Modifier> modifiers;

    /**
     * Unmodifiable
     */
    public final List<Parameter> parameters;
    public final String enclosingType;

    protected Method(ExecutableElement correspondingElement, String enclosingClassQualifiedName, TypeParametersToArguments typeParametersToArguments, Utils utils, Map<String, List<ExecutableElement>> uncachedMethods) throws ErrorException, UncachedException, FailedToCacheException {
        assert correspondingElement.getKind() == ElementKind.METHOD : String.format(
            "Expected \"%s\" to be a method, but was \"%s\".",
            correspondingElement.getSimpleName(), correspondingElement.getKind());

        this.enclosingType = ((TypeElement) correspondingElement.getEnclosingElement()).getQualifiedName().toString();
        this.name = correspondingElement.getSimpleName().toString();
        this.returnType = typeParametersToArguments.clearTypeParameterIfPresent(correspondingElement.getReturnType().toString());
        this.modifiers = correspondingElement.getModifiers();

        // Needs to be the first check. Precedes over other checks.
        // A method does not override itself. Thus the next @Uncached check cannot check this case.
        Uncached uncached = correspondingElement.getAnnotation(Uncached.class);
        if (uncached != null) {
            throw UncachedException.create(correspondingElement,
                "\"%s\": @Uncached.", this.name);
        }

        // Check: Overridden method was annotated with @Uncached.
        final Elements elems = utils.elementUtils();
        final TypeElement enclosingTypeElement = (TypeElement) correspondingElement.getEnclosingElement();
        List<ExecutableElement> uncachedParents = uncachedMethods.getOrDefault(correspondingElement.getSimpleName().toString(), Collections.emptyList());
        for (var up : uncachedParents) {
            if (elems.overrides(correspondingElement, up, enclosingTypeElement)) {
                throw UncachedException.create(correspondingElement,
                    "\"%s\": @Uncached.", this.name);
            }
        }

        if (this.modifiers.contains(Modifier.PRIVATE)) {
            throw FailedToCacheException.create(correspondingElement,
                "\"%s\": private method will not be cached.", this.name);
        }
        if (this.modifiers.contains(Modifier.STATIC)) {
            throw FailedToCacheException.create(correspondingElement,
                "\"%s\": static method will not be cached.", this.name);
        }
        if (this.modifiers.contains(Modifier.FINAL)) {
            throw FailedToCacheException.create(correspondingElement,
                "\"%s\": final method will not be cached.", this.name);
        }

        if (!this.returnType.equals(enclosingClassQualifiedName)) {
            throw FailedToCacheException.create(correspondingElement,
                "\"%s\": Return type \"%s\" was not the expected one \"%s\".", this.name, this.returnType, enclosingClassQualifiedName);
        }

        this.parameters = computeParameters(correspondingElement, enclosingClassQualifiedName, typeParametersToArguments, utils);
    }

    private static List<Parameter> computeParameters(ExecutableElement correspondingElement, String enclosingClassQualifiedName, TypeParametersToArguments typeParametersToArguments, Utils utils) throws ErrorException {
        List<VariableElement> parameterElements = (List<VariableElement>) correspondingElement.getParameters();
        List<Parameter> parameters = new ArrayList<>(parameterElements.size());
        for (VariableElement parameterElement : parameterElements) {
            utils.exceptionHandler().handle(() -> {
                Parameter parameter = new Parameter(parameterElement, enclosingClassQualifiedName, typeParametersToArguments, utils);
                parameters.add(parameter);
            });
        }

        return Collections.unmodifiableList(parameters);
    }
}
