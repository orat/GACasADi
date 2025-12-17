package de.orat.math.gacasadi.delegating.annotation.processor.generation;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import static de.orat.math.gacasadi.delegating.annotation.processor.generation.Classes.T_Override;
import de.orat.math.gacasadi.delegating.annotation.processor.representation.Clazz;
import de.orat.math.gacasadi.delegating.annotation.processor.representation.Method;
import de.orat.math.gacasadi.delegating.annotation.processor.representation.Parameter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.processing.Filer;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

final class ClassGenerator {

    private ClassGenerator() {

    }

    protected static void generate(Clazz c, Filer filer) throws IOException, ClassNotFoundException {
        String packageName = String.format("%s.gen", c.enclosingQualifiedName);
        String className = "Delegating" + c.simpleName;
        ClassName genClass = ClassName.get(packageName, className);
        ClassName T_c = ClassName.get(c.enclosingQualifiedName, c.simpleName);

        TypeName T_to = TypeName.get(c.to);
        FieldSpec delegate = FieldSpec.builder(T_to, "delegate", Modifier.PROTECTED, Modifier.FINAL)
            .build();

        List<MethodSpec> ctors = constructors(T_to, c);

        MethodSpec createMethod = MethodSpec.methodBuilder("create")
            .addModifiers(Modifier.PROTECTED, Modifier.ABSTRACT)
            .addParameter(T_to, "delegate")
            .returns(T_c)
            .build();

        MethodSpec create2Method = MethodSpec.methodBuilder("create")
            .addModifiers(Modifier.PROTECTED, Modifier.ABSTRACT)
            .addParameter(T_to, "delegate")
            .addParameter(T_c, "other")
            .returns(T_c)
            .build();

        List<MethodSpec> methods = new ArrayList<>(c.methods.size() + 1);
        methods.add(createMethod);
        methods.add(create2Method);
        for (Method m : c.methods) {
            MethodSpec delegateMethod = delegateMethod(m, T_c);
            methods.add(delegateMethod);
        }

        ClassName T_extend = ClassName.get((TypeElement) c.extend.asElement());
        ParameterizedTypeName T_extendFull = ParameterizedTypeName.get(T_extend, T_c, T_to);

        TypeSpec genClassSpec = TypeSpec.classBuilder(genClass)
            .addJavadoc("@see $L", T_c.canonicalName())
            .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
            .superclass(T_extendFull)
            .addSuperinterfaces(c.commonSuperTypes.stream().map(TypeName::get).toList())
            .addField(delegate)
            .addMethods(ctors)
            .addMethods(methods)
            .build();

        JavaFile javaFile = JavaFile.builder(packageName, genClassSpec)
            .skipJavaLangImports(true)
            .indent("\t")
            .build();

        javaFile.writeTo(filer);
    }

    private static List<MethodSpec> constructors(TypeName T_to, Clazz c) throws ClassNotFoundException {
        List<MethodSpec> ctors = new ArrayList<>(c.extendConstructors.size());
        if (c.extendConstructors.isEmpty()) {
            MethodSpec ctor = ClassGenerator.constructor1(T_to);
            ctors.add(ctor);
        }
        for (ExecutableElement exCtor : c.extendConstructors) {
            MethodSpec.Builder constructorBuilder = MethodSpec.constructorBuilder();

            // Signature
            constructorBuilder
                .addModifiers(Modifier.PROTECTED)
                .addParameter(T_to, "delegate");

            List<String> paramNames = new ArrayList<>(exCtor.getParameters().size());
            for (VariableElement param : exCtor.getParameters()) {
                var type = TypeName.get(param.asType());
                String name = param.getSimpleName().toString();
                paramNames.add(name);
                constructorBuilder.addParameter(type, name);
            }

            // Body
            String argsString = paramNames.stream().collect(Collectors.joining(", "));
            constructorBuilder
                .addStatement("super($L)", argsString)
                .addStatement("this.delegate = delegate");

            MethodSpec ctor = constructorBuilder.build();
            ctors.add(ctor);
        }
        return ctors;
    }

    private static MethodSpec constructor1(TypeName annotatedToType) throws ClassNotFoundException {
        MethodSpec.Builder constructorBuilder = MethodSpec.constructorBuilder();

        // Signature
        constructorBuilder
            .addModifiers(Modifier.PROTECTED)
            .addParameter(annotatedToType, "delegate");

        // Body
        constructorBuilder
            .addStatement("this.delegate = delegate");

        return constructorBuilder.build();
    }

    private static MethodSpec delegateMethod(Method m, ClassName T_c) {
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(m.name);

        TypeName T_ret = TypeName.get(m.returnType);

        // Signature
        methodBuilder
            .addJavadoc("@see $L#$L", m.enclosingType, m.name)
            .addAnnotation(T_Override)
            .addModifiers(m.modifiers)
            .returns(T_ret);

        for (Parameter parameter : m.parameters) {
            TypeName T_param = TypeName.get(parameter.type);
            methodBuilder
                .addParameter(T_param, parameter.identifier);
        }

        // Body
        String annotatedClassName = T_c.canonicalName();

        List<String> args = new ArrayList<>(m.parameters.size());
        for (var param : m.parameters) {
            String arg;
            if (param.type.toString().equals(annotatedClassName)) {
                arg = String.format("%s.delegate", param.identifier);
            } else {
                arg = param.identifier;
            }
            args.add(arg);
        }
        String argsString = args.stream().collect(Collectors.joining(", "));

        boolean binOp = false;
        if (m.parameters.size() == 1) {
            if (m.parameters.get(0).type.toString().equals(annotatedClassName)) {
                binOp = true;
            }
        }

        if (m.returnType.toString().equals(annotatedClassName)) {
            if (binOp) {
                methodBuilder.addStatement("return create(this.delegate.$L($L), $L)", m.name, argsString, m.parameters.get(0).identifier);
            } else {
                methodBuilder.addStatement("return create(this.delegate.$L($L))", m.name, argsString);
            }
        } else {
            methodBuilder.addStatement("return this.delegate.$L($L)", m.name, argsString);
        }

        //
        return methodBuilder.build();
    }
}
