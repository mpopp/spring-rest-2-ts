package com.blueveery.springrest2ts.converters;

import com.blueveery.springrest2ts.implgens.ImplementationGenerator;
import com.blueveery.springrest2ts.tsmodel.TSComplexElement;
import com.blueveery.springrest2ts.tsmodel.TSField;
import com.blueveery.springrest2ts.tsmodel.TSType;
import com.blueveery.springrest2ts.tsmodel.TSUnion;
import com.google.gson.ExclusionStrategy;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.annotations.Since;
import com.google.gson.annotations.Until;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

public class GsonObjectMapper implements ObjectMapper {

    private boolean excludeFieldsWithoutExposeAnnotation;
    private Double forVersion;
    private ExclusionStrategy ExclusionStrategy;

    public GsonObjectMapper() {
    }

    public boolean isExcludeFieldsWithoutExposeAnnotation() {
        return excludeFieldsWithoutExposeAnnotation;
    }

    public void setExcludeFieldsWithoutExposeAnnotation(boolean excludeFieldsWithoutExposeAnnotation) {
        this.excludeFieldsWithoutExposeAnnotation = excludeFieldsWithoutExposeAnnotation;
    }

    public Double getForVersion() {
        return forVersion;
    }

    public void setForVersion(Double forVersion) {
        this.forVersion = forVersion;
    }

    public ExclusionStrategy getExclusionStrategy() {
        return ExclusionStrategy;
    }

    public void setExclusionStrategy(ExclusionStrategy exclusionStrategy) {
        ExclusionStrategy = exclusionStrategy;
    }

    @Override
    public List<TSField> addTypeLevelSpecificFields(
            Class javaType, TSComplexElement tsComplexType
    ) {
        return Collections.emptyList();
    }

    @Override
    public boolean filterClass(Class clazz) {
        return true;
    }

    @Override
    public boolean filter(Field field) {
        if (forVersion != null) {
            Since since = field.getAnnotation(Since.class);
            if (since != null && since.value() > forVersion) {
                return false;
            }
            Until until = field.getAnnotation(Until.class);
            if (until != null && until.value() < forVersion) {
                return false;
            }
        }
        if (excludeFieldsWithoutExposeAnnotation) {
            Expose exposeAnnotation = field.getAnnotation(Expose.class);
            return exposeAnnotation != null && (exposeAnnotation.serialize() || exposeAnnotation.deserialize());
        }
        return true;
    }

    @Override
    public boolean filter(Method method, boolean isGetter) {
        return false;
    }

    @Override
    public String getPropertyName(Field field) {
        SerializedName serializedNameAnnotation = field.getAnnotation(SerializedName.class);
        return serializedNameAnnotation != null ? serializedNameAnnotation.value() : field.getName();
    }

    @Override
    public void setIfIsIgnored(Property property, AnnotatedElement annotatedElement) {
    }

    @Override
    public List<TSField> mapJavaPropertyToField(
            Property property, TSComplexElement tsComplexType, ComplexTypeConverter complexTypeConverter,
            ImplementationGenerator implementationGenerator, NullableTypesStrategy nullableTypesStrategy
    ) {
        TSType fieldBaseType = TypeMapper.map(property.getField().getType());
        TSField tsField = new TSField(property.getName(), tsComplexType, fieldBaseType);
        tsField.addAllAnnotations(property.getDeclaredAnnotations());

        applyExpose(tsField, property);
        applySince(tsField, property);
        applyUntil(tsField, property);
        return Collections.singletonList(tsField);
    }

    private void applySince(TSField tsField, Property property) {
        Since sinceAnnotation = property.getDeclaredAnnotation(Since.class);
        if (sinceAnnotation != null) {
            StringBuilder commentText = tsField.getTsComment().getTsCommentSection("version").getCommentText();
            commentText.append("Since version: ").append(sinceAnnotation.value());
        }
    }

    private void applyUntil(TSField tsField, Property property) {
        Until untilAnnotation = property.getDeclaredAnnotation(Until.class);
        if (untilAnnotation != null) {
            StringBuilder commentText = tsField.getTsComment().getTsCommentSection("version").getCommentText();
            if (commentText.length() > 0) {
                commentText.append("\t");
            }
            commentText.append("Until version: ").append(untilAnnotation.value());
        }
    }

    private void applyExpose(TSField tsField, Property property) {
        Expose exposeAnnotation = property.getDeclaredAnnotation(Expose.class);
        if (exposeAnnotation != null) {
            if (!exposeAnnotation.deserialize()) {
                tsField.setReadOnly(true);
            }
            if (!exposeAnnotation.serialize()) {
                TSType fieldType = tsField.getType();
                tsField.setType(new TSUnion(TypeMapper.tsUndefined, fieldType));
            }
        }
    }

    @Override
    public String getPropertyName(Method method, boolean isGetter) {
        throw new UnsupportedOperationException();
    }
}