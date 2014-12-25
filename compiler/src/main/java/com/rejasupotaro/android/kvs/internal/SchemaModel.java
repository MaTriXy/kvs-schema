package com.rejasupotaro.android.kvs.internal;

import com.rejaupotaro.android.kvs.annotations.Key;
import com.rejaupotaro.android.kvs.annotations.Table;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;

public class SchemaModel {
    private TypeElement element;
    private String packageName;
    private String originalClassName;
    private String className;
    private String tableName;
    private List<VariableElement> keys = new ArrayList<>();

    public TypeElement getElement() {
        return element;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getOriginalClassName() {
        return originalClassName;
    }

    public String getClassName() {
        return className;
    }

    public String getTableName() {
        return tableName;
    }

    public List<VariableElement> getKeys() {
        return keys;
    }

    public SchemaModel(TypeElement element, Elements elementUtils) {
        this.element = element;
        Table table = element.getAnnotation(Table.class);
        this.tableName = table.value();
        this.packageName = getPackageName(elementUtils, element);
        this.originalClassName = getClassName(element, packageName);
        this.className = originalClassName.replace("Schema", "");

        findAnnotations(element);
    }

    private void findAnnotations(Element element) {
        for (Element enclosedElement : element.getEnclosedElements()) {
            findAnnotations(enclosedElement);

            Key key = enclosedElement.getAnnotation(Key.class);
            if (key != null) {
                keys.add((VariableElement) enclosedElement);
            }
        }
    }

    private String getPackageName(Elements elementUtils, TypeElement type) {
        return elementUtils.getPackageOf(type).getQualifiedName().toString();
    }

    private static String getClassName(TypeElement type, String packageName) {
        int packageLen = packageName.length() + 1;
        return type.getQualifiedName().toString().substring(packageLen).replace('.', '$');
    }
}
