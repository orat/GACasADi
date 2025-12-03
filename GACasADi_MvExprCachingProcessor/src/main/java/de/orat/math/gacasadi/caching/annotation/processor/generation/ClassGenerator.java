package de.orat.math.gacasadi.caching.annotation.processor.generation;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import static de.orat.math.gacasadi.caching.annotation.processor.generation.Classes.T_List;
import static de.orat.math.gacasadi.caching.annotation.processor.generation.Classes.T_Override;
import static de.orat.math.gacasadi.caching.annotation.processor.generation.Classes.T_SX;
import static de.orat.math.gacasadi.caching.annotation.processor.generation.Classes.T_String;
import de.orat.math.gacasadi.caching.annotation.processor.representation.Clazz;
import de.orat.math.gacasadi.caching.annotation.processor.representation.Method;
import de.orat.math.gacasadi.caching.annotation.processor.representation.Parameter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;
import static de.orat.math.gacasadi.caching.annotation.processor.generation.Classes.T_CgaFunctionCache;

final class ClassGenerator {

    private ClassGenerator() {

    }

    protected static void generate(Clazz c, Filer filer) throws IOException, ClassNotFoundException {
        String packageName = String.format("%s.gen", c.enclosingQualifiedName);
        String className = "Cached" + c.simpleName;
        ClassName genClass = ClassName.get(packageName, className);
        ClassName T_c = ClassName.get(c.enclosingQualifiedName, c.simpleName);

        FieldSpec CACHE = FieldSpec.builder(T_CgaFunctionCache, "CACHE", Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
            .initializer("new $T()", T_CgaFunctionCache)
            .build();

        MethodSpec getCache = ClassGenerator.getCache();

        MethodSpec constructor1 = ClassGenerator.constructor1(T_c);
        MethodSpec constructor2 = ClassGenerator.constructor2();

        List<MethodSpec> methods = new ArrayList<>(c.methods.size() * 2);
        for (Method m : c.methods) {
            MethodSpec cacheMethod = cacheMethod(c, m, genClass, T_c);
            MethodSpec superMethod = superMethod(m);
            methods.add(cacheMethod);
            methods.add(superMethod);
        }

        TypeSpec genClassSpec = TypeSpec.classBuilder(genClass)
            .addModifiers(Modifier.PUBLIC)
            .superclass(T_c)
            .addField(CACHE)
            .addMethod(getCache)
            .addMethod(constructor1)
            .addMethod(constructor2)
            .addMethods(methods)
            .build();

        JavaFile javaFile = JavaFile.builder(packageName, genClassSpec)
            .skipJavaLangImports(true)
            .indent("\t")
            .build();

        javaFile.writeTo(filer);
    }

    private static MethodSpec getCache() {
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("getCache");

        // Signature
        methodBuilder
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .returns(Classes.T_IFunctionCache);

        // Body
        methodBuilder
            .addStatement("return CACHE");

        return methodBuilder.build();
    }

    private static MethodSpec constructor1(ClassName T_c) throws ClassNotFoundException {
        MethodSpec.Builder constructorBuilder = MethodSpec.constructorBuilder();

        // Signature
        constructorBuilder
            .addModifiers(Modifier.PUBLIC)
            .addParameter(T_c, "mv");

        // Body
        constructorBuilder
            .addStatement("super(mv)");

        return constructorBuilder.build();
    }

    private static MethodSpec constructor2() throws ClassNotFoundException {
        MethodSpec.Builder constructorBuilder = MethodSpec.constructorBuilder();

        // Signature
        constructorBuilder
            .addModifiers(Modifier.PUBLIC)
            .addParameter(T_SX, "sx");

        // Body
        constructorBuilder
            .addStatement("super(sx)");

        return constructorBuilder.build();
    }

    private static MethodSpec cacheMethod(Clazz c, Method m, ClassName genClass, ClassName T_c) {
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(m.name);

        TypeName T_ret = betterGuess(m.returnType);

        Set<Modifier> modifiers = new HashSet<>(m.modifiers);
        modifiers.remove(Modifier.DEFAULT);

        // Signature
        methodBuilder
            .addJavadoc("@see $L#$L", m.enclosingType, m.name)
            .addAnnotation(T_Override)
            .addModifiers(modifiers)
            .returns(T_ret);

        for (Parameter parameter : m.parameters) {
            TypeName T_param = betterGuess(parameter.type);
            methodBuilder
                .addParameter(T_param, parameter.identifier);
        }

        // Body
        String args = Stream.concat(Stream.of("this"),
            m.parameters.stream().map(p -> p.identifier))
            .collect(Collectors.joining(", "));
        String superTypeArgs = Stream.concat(Stream.of("this"),
            m.parameters.stream().filter(p -> p.type.equals(c.qualifiedName)).map(p -> p.identifier))
            .collect(Collectors.joining(", "));

        StringBuilder paramsBuilder = new StringBuilder();
        int paramPos = 1;
        for (Parameter p : m.parameters) {
            if (p.type.equals(T_c.canonicalName())) {
                paramsBuilder.append(String.format("params.get(%s)", paramPos));
                ++paramPos;
            } else {
                paramsBuilder.append(p.identifier);
            }
            paramsBuilder.append(", ");
        }
        if (!paramsBuilder.isEmpty()) {
            paramsBuilder.delete(paramsBuilder.length() - 2, paramsBuilder.length());
        }
        String params = paramsBuilder.toString();

        methodBuilder
            .addStatement("String funName = CACHE.createFuncName($S, $L)", m.name, args)
            .addCode("""
                return CACHE.getOrCreateSymbolicFunction(funName, List.of($L),
                    ($T<? extends $T> params) -> params.get(0).$L($L));""",
                superTypeArgs, T_List, genClass, m.name + "_super", params);

        //
        return methodBuilder.build();
    }

    private static MethodSpec superMethod(Method m) {
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(m.name + "_super");

        TypeName T_ret = betterGuess(m.returnType);

        // Signature
        methodBuilder
            .addModifiers(Modifier.PROTECTED)
            .returns(T_ret);

        for (Parameter parameter : m.parameters) {
            TypeName T_param = betterGuess(parameter.type);
            methodBuilder
                .addParameter(T_param, parameter.identifier);
        }

        // Body
        String args = m.parameters.stream().map(p -> p.identifier).collect(Collectors.joining(", "));
        methodBuilder
            .addStatement("return super.$L($L)", m.name, args);

        //
        return methodBuilder.build();
    }

    private static TypeName betterGuess(String name) {
        TypeName T_param;
        if (name.equals("int")) {
            T_param = TypeName.INT;
        } else {
            T_param = ClassName.bestGuess(name);
        }
        return T_param;
    }
}
