package de.orat.math.gacasadi.caching.annotation.processor.representation;

import de.orat.math.gacasadi.caching.annotation.processor.GenerateCachedProcessor.Utils;
import de.orat.math.gacasadi.caching.annotation.processor.common.ErrorException;
import de.orat.math.gacasadi.caching.annotation.processor.common.FailedToCacheException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.QualifiedNameable;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import static de.orat.math.gacasadi.caching.annotation.processor.generation.Classes.T_IMultivectorExpression;

public class Clazz {

    public final TypeElement correspondingElement;
    public final String qualifiedName;
    public final String simpleName;
    public final String enclosingQualifiedName;
    /**
     * Unmodifiable
     */
    public final List<Method> methods;

    public Clazz(TypeElement correspondingElement, Utils utils) throws ErrorException, Exception {
        this.correspondingElement = correspondingElement;
        this.simpleName = correspondingElement.getSimpleName().toString();
        this.enclosingQualifiedName = ((QualifiedNameable) correspondingElement.getEnclosingElement()).getQualifiedName().toString();
        this.qualifiedName = correspondingElement.getQualifiedName().toString();

        ElementKind kind = correspondingElement.getKind();
        if (kind != ElementKind.CLASS) {
            throw ErrorException.create(correspondingElement,
                "Expected \"%s\" to be a class, but was \"%s\".",
                this.qualifiedName, kind);
        }

        if (correspondingElement.getModifiers().contains(Modifier.FINAL)) {
            throw ErrorException.create(correspondingElement, "Has prohibited modifier \"final\".");
        }

        List<? extends TypeParameterElement> typeParamsList = correspondingElement.getTypeParameters();
        if (!typeParamsList.isEmpty()) {
            String typeParamsString = typeParamsList.stream()
                .map(tp -> tp.getSimpleName().toString())
                .collect(Collectors.joining(", "));
            throw ErrorException.create(correspondingElement, "Type parameters are prohibited: %s", typeParamsString);
        }

        Set<String> iMultivectorSymbolic = correspondingElement.getInterfaces().stream()
            .map(i -> ((TypeElement) ((DeclaredType) i).asElement()).getQualifiedName().toString())
            .collect(Collectors.toSet());
        if (!iMultivectorSymbolic.contains(T_IMultivectorExpression.canonicalName())) {
            throw ErrorException.create(correspondingElement,
                "Needs to implement \"%s\", but does not.", T_IMultivectorExpression.canonicalName());
        }

        this.methods = Collections.unmodifiableList(Clazz.computeMethods(correspondingElement, this.qualifiedName, utils));
    }

    private static List<Method> computeMethods(TypeElement correspondingElement, String enclosingClassQualifiedName, Utils utils) throws FailedToCacheException, ErrorException {
        // Safe cast because
        // - filtered for Methods
        // - Methods are ExceutableElements.
        List<ExecutableElement> classMethodElements = (List<ExecutableElement>) correspondingElement.getEnclosedElements()
            .stream()
            .filter(el -> el.getKind() == ElementKind.METHOD)
            .toList();

        // Compute all recursive super types
        Map<String, TypeMirror> allSuperTypes = new LinkedHashMap<>();
        {
            var currentSubTypes = List.of(correspondingElement.asType());
            while (!currentSubTypes.isEmpty()) {
                List<TypeMirror> nextSubTypes = new ArrayList<>();
                for (var currentSubType : currentSubTypes) {
                    var previousEntry = allSuperTypes.putIfAbsent(currentSubType.toString(), currentSubType);
                    if (previousEntry != null) {
                        continue;
                    }

                    // Includes substitutions.
                    var currentSuperTypes = utils.typeUtils().directSupertypes(currentSubType);
                    nextSubTypes.addAll(currentSuperTypes);
                }
                currentSubTypes = nextSubTypes;
            }
        }
        allSuperTypes.remove(correspondingElement.toString());
        allSuperTypes.remove("java.lang.Object");
        // allSuperTypes.keySet().forEach(s -> System.out.println("superTypes: " + s.toString() + s.hashCode()));

        Set<String> previousMethodElementsNames = classMethodElements.stream()
            .map(me -> me.getSimpleName().toString())
            .collect(Collectors.toCollection(HashSet::new));

        List<Method> allMethods = new ArrayList<>();
        {
            List<Method> classMethods = checkCreateMethods(classMethodElements, utils, enclosingClassQualifiedName, new TypeParametersToArguments());
            allMethods.addAll(classMethods);
        }

        for (TypeMirror superType : allSuperTypes.values()) {
            List<ExecutableElement> interfaceDefaultMethodElements = ((TypeElement) ((DeclaredType) superType).asElement()).getEnclosedElements().stream()
                .filter(el -> el.getKind() == ElementKind.METHOD)
                .map(m -> (ExecutableElement) m)
                // Remove overrides
                .filter(m -> !previousMethodElementsNames.contains(m.getSimpleName().toString()))
                .toList();

            List<String> methodElementsNames = interfaceDefaultMethodElements.stream()
                .map(me -> me.getSimpleName().toString())
                .toList();
            previousMethodElementsNames.addAll(methodElementsNames);

            TypeParametersToArguments typeParametersToArguments = new TypeParametersToArguments((DeclaredType) superType);
            List<Method> defaultMethods = checkCreateMethods(interfaceDefaultMethodElements, utils, enclosingClassQualifiedName, typeParametersToArguments);
            allMethods.addAll(defaultMethods);
        }

        return allMethods;
    }

    // private static
    private static List<Method> checkCreateMethods(List<ExecutableElement> methodElements, Utils utils, String enclosingClassQualifiedName, TypeParametersToArguments typeParametersToArguments) {
        List<Method> methods = new ArrayList<>(methodElements.size());
        Set<String> methodNames = new HashSet<>(methodElements.size());

        for (ExecutableElement methodElement : methodElements) {
            utils.exceptionHandler().handle(() -> {
                Method methodRepr = new Method(methodElement, enclosingClassQualifiedName, typeParametersToArguments, utils);

                if (methodNames.contains(methodRepr.name)) {
                    throw ErrorException.create(methodElement,
                        "Forbidden overloaded method: \"%s\".",
                        methodElement.getSimpleName());
                }

                methods.add(methodRepr);
                methodNames.add(methodRepr.name);
            });
        }

        return methods;
    }
}
