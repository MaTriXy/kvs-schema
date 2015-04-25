package com.rejasupotaro.android.kvs.internal;

import com.rejaupotaro.android.kvs.annotations.Key;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;

public class SchemaWriter {
    private SchemaModel model;

    public SchemaWriter(SchemaModel model) {
        this.model = model;
    }

    public void write(Filer filer) {
        try {
            TypeSpec.Builder classBuilder = TypeSpec.classBuilder(model.getClassName());
            classBuilder.addModifiers(Modifier.PUBLIC, Modifier.FINAL);
            ClassName superClassName = ClassName.get(model.getPackageName(), model.getOriginalClassName());
            classBuilder.superclass(superClassName);

            List<FieldSpec> fieldSpecs = createFields();
            for (FieldSpec fieldSpec : fieldSpecs) {
                classBuilder.addField(fieldSpec);
            }

            List<MethodSpec> methodSpecs = new ArrayList<>();
            methodSpecs.addAll(createConstructors());
            methodSpecs.addAll(createMethods());
            for (MethodSpec methodSpec : methodSpecs) {
                classBuilder.addMethod(methodSpec);
            }

            TypeSpec outClass = classBuilder.build();
            JavaFile.builder(model.getPackageName(), outClass)
                    .build()
                    .writeTo(filer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<FieldSpec> createFields() throws IOException {
        List<FieldSpec> fieldSpecs = new ArrayList<>();
        fieldSpecs.add(FieldSpec.builder(String.class, "tableName")
                .build());
        return fieldSpecs;
    }

    private List<MethodSpec> createConstructors() throws IOException {
        List<MethodSpec> methodSpecs = new ArrayList<>();
        methodSpecs.add(MethodSpec.constructorBuilder()
                .addParameter(ClassName.get("android.content", "Context"), "context")
                .addStatement("init(context, tableName)")
                .build());
        methodSpecs.add(MethodSpec.constructorBuilder()
                .addParameter(ClassName.get("android.content", "SharedPreferences"), "prefs")
                .addStatement("init(prefs)")
                .build());
        return methodSpecs;
    }

    private List<MethodSpec> createMethods() throws IOException {
        List<MethodSpec> methodSpecs = new ArrayList<>();
        for (VariableElement element : model.getKeys()) {
            Key key = element.getAnnotation(Key.class);
            methodSpecs.addAll(createMethod(key, element));
        }
        return methodSpecs;
    }

    private List<MethodSpec> createMethod(Key key, VariableElement element) throws IOException {
        List<MethodSpec> methodSpecs = new ArrayList<>();
        String fieldTypeFqcn = element.asType().toString();
        String fieldName = element.getSimpleName().toString();
        String keyName = key.value();
        switch (fieldTypeFqcn) {
            case "boolean":
                methodSpecs.add(createGetterMethod(boolean.class, "boolean", fieldName, keyName));
                methodSpecs.add(createSetterMethod(boolean.class, "boolean", fieldName, keyName));
                methodSpecs.add(createHasMethod(fieldName, keyName));
                methodSpecs.add(createRemoveMethod(fieldName, keyName));
                break;
            case Classes.STRING:
                methodSpecs.add(createGetterMethod(String.class, "String", fieldName, keyName));
                methodSpecs.add(createSetterMethod(String.class, "String", fieldName, keyName));
                methodSpecs.add(createHasMethod(fieldName, keyName));
                methodSpecs.add(createRemoveMethod(fieldName, keyName));
                break;
            case "float":
                methodSpecs.add(createGetterMethod(float.class, "float", fieldName, keyName));
                methodSpecs.add(createSetterMethod(float.class, "float", fieldName, keyName));
                methodSpecs.add(createHasMethod(fieldName, keyName));
                methodSpecs.add(createRemoveMethod(fieldName, keyName));
                break;
            case "int":
                methodSpecs.add(createGetterMethod(long.class, "int", fieldName, keyName));
                methodSpecs.add(createSetterMethod(long.class, "int", fieldName, keyName));
                methodSpecs.add(createHasMethod(fieldName, keyName));
                methodSpecs.add(createRemoveMethod(fieldName, keyName));
                break;
            case "long":
                methodSpecs.add(createGetterMethod(long.class, "long", fieldName, keyName));
                methodSpecs.add(createSetterMethod(long.class, "long", fieldName, keyName));
                methodSpecs.add(createHasMethod(fieldName, keyName));
                methodSpecs.add(createRemoveMethod(fieldName, keyName));
                break;
            default:
                throw new IllegalArgumentException(fieldTypeFqcn + " is not supported");
        }
        return methodSpecs;
    }

    private MethodSpec createGetterMethod(Type fieldType, String argTypeOfSuperMethod, String fieldName, String keyName) throws IOException {
        String methodName = "get" + StringUtils.capitalize(fieldName);
        String statement = String.format("return get%s(\"%s\", %s)", StringUtils.capitalize(argTypeOfSuperMethod), keyName, fieldName);

        return MethodSpec.methodBuilder(methodName)
                .returns(fieldType)
                .addStatement(statement)
                .build();
    }

    private MethodSpec createSetterMethod(Type fieldType, String argTypeOfSuperMethod, String fieldName, String keyName) throws IOException {
        String methodName = "put" + StringUtils.capitalize(fieldName);
        String statement = String.format("put%s(\"%s\", %s)", StringUtils.capitalize(argTypeOfSuperMethod), keyName, fieldName);

        return MethodSpec.methodBuilder(methodName)
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class)
                .addParameter(fieldType, fieldName)
                .addStatement(statement)
                .build();
    }

    private MethodSpec createHasMethod(String fieldName, String keyName) throws IOException {
        String methodName = "has" + StringUtils.capitalize(fieldName);
        String statement = String.format("return has(\"%s\")", keyName);

        return MethodSpec.methodBuilder(methodName)
                .addModifiers(Modifier.PUBLIC)
                .returns(boolean.class)
                .addStatement(statement)
                .build();
    }

    private MethodSpec createRemoveMethod(String fieldName, String keyName) throws IOException {
        String methodName = "remove" + StringUtils.capitalize(fieldName);
        String statement = String.format("remove(\"%s\")", keyName);

        return MethodSpec.methodBuilder(methodName)
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class)
                .addStatement(statement)
                .build();
    }
}
