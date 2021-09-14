package com.blueveery.springrest2ts.converters;

import com.blueveery.springrest2ts.Rest2tsGenerator;
import com.blueveery.springrest2ts.filters.JavaTypeSetFilter;
import com.blueveery.springrest2ts.tsmodel.TSField;
import com.blueveery.springrest2ts.tsmodel.TSInterface;
import com.blueveery.springrest2ts.tsmodel.TSModule;
import com.blueveery.springrest2ts.tsmodel.TSUnion;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class GsonObjectMapperTest {

    private Rest2tsGenerator tsGenerator;
    private GsonObjectMapper gsonObjectMapper;
    private Set<String> javaPackageSet;

    @Before
    public void setUp() {
        tsGenerator = new Rest2tsGenerator();
        tsGenerator.setModelClassesCondition(new JavaTypeSetFilter(Collections.singleton(Product.class)));
        gsonObjectMapper = new GsonObjectMapper();
        ModelClassesAbstractConverter modelClassesConverter = new ModelClassesToTsInterfacesConverter(gsonObjectMapper);
        tsGenerator.setModelClassesConverter(modelClassesConverter);
        javaPackageSet = Collections.singleton("com.blueveery.springrest2ts.converters");
    }

    @After
    public void cleanUp() {
        TypeMapper.resetTypeMapping();
    }

    @Test
    public void testGson() {
        GsonBuilder builder = new GsonBuilder();
        builder.excludeFieldsWithoutExposeAnnotation();
        Gson gson = builder.create();
        System.out.println(gson.toJson(new Product()));
    }

    @Test
    public void transientFieldsAreSkippedInTsCode() throws IOException {
        TSInterface productTsInterface = convertProductToTsInterface();
        assertTrue(productTsInterface.getTsFields().stream().allMatch(f -> !"tempName".equals(f.getName())));
    }

    @Test
    public void nonTransientFieldsAreIncludedInTsCode() throws IOException {
        TSInterface productTsInterface = convertProductToTsInterface();
        assertTrue(productTsInterface.getTsFields().stream().anyMatch(f -> "name".equals(f.getName())));
    }

    @Test
    public void serializedNameChangesFieldName() throws IOException {
        TSInterface productTsInterface = convertProductToTsInterface();
        assertTrue(productTsInterface.getTsFields().stream().anyMatch(f -> "year".equals(f.getName())));
        assertTrue(productTsInterface.getTsFields().stream().noneMatch(f -> "productionYear".equals(f.getName())));
    }

    @Test
    public void exposeFiltersFields() throws IOException {
        List<String> exposedFields = Arrays.asList("exposedName", "serializedOnly", "deserializedOnly");
        gsonObjectMapper.setExcludeFieldsWithoutExposeAnnotation(true);
        TSInterface productTsInterface = convertProductToTsInterface();
        SortedSet<TSField> tsFields = productTsInterface.getTsFields();
        assertTrue(tsFields.stream().allMatch(f -> exposedFields.contains(f.getName())));
        Optional<TSField> exposedNameField = tsFields.stream().filter(f -> "exposedName".equals(f.getName())).findFirst();
        assertEquals(exposedNameField.get().getType(), TypeMapper.tsString);
    }

    @Test
    public void serializedOnlyFieldIsReadonly() throws IOException {
        gsonObjectMapper.setExcludeFieldsWithoutExposeAnnotation(true);
        TSInterface productTsInterface = convertProductToTsInterface();
        SortedSet<TSField> tsFields = productTsInterface.getTsFields();
        Optional<TSField> serializedOnlyField = tsFields.stream().filter(f -> "serializedOnly".equals(f.getName())).findFirst();
        assertEquals(serializedOnlyField.get().getType(), TypeMapper.tsString);
        assertTrue(serializedOnlyField.get().getReadOnly());
    }

    @Test
    public void deserializedOnlyFieldIsOptional() throws IOException {
        gsonObjectMapper.setExcludeFieldsWithoutExposeAnnotation(true);
        TSInterface productTsInterface = convertProductToTsInterface();
        SortedSet<TSField> tsFields = productTsInterface.getTsFields();
        Optional<TSField> deserializedOnlyField = tsFields.stream().filter(f -> "deserializedOnly".equals(f.getName())).findFirst();
        assertEquals(deserializedOnlyField.get().getType(), new TSUnion(TypeMapper.tsUndefined ,TypeMapper.tsString));
    }

    private TSInterface convertProductToTsInterface() throws IOException {
        SortedSet<TSModule> tsModules = tsGenerator.convert(javaPackageSet);
        return (TSInterface) tsModules.first().getScopedTypesSet().first();
    }
}