package com.company.annotations;

import com.company.datasets.datasets.*;
import org.junit.jupiter.api.Assertions;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.function.Function.identity;

// Abstract class for classes validating uses of the annotations
public abstract class AnnotationTest {

    protected static Stream<Class<?>> provideAnnotatedClasses() {
        return Stream.of(
                DataSet.class,
                BossDropDataSet.class,
                MapDropDataSet.class,
                KalandraMistDataSet.class,
                UltimatumDataSet.class
        );
    }

    protected <A extends Annotation> Map<Field, A> getAnnotatedFields(Class<?> clazz, Class<A> annotation) {
        // annotations should only be on classes extending DataSet
        Assertions.assertTrue(DataSet.class.isAssignableFrom(clazz));

        return Arrays.stream(clazz.getDeclaredFields())
                .filter(f -> f.isAnnotationPresent(annotation))
                .collect(Collectors.toMap(identity(), f -> f.getAnnotation(annotation)));
    }

    protected <A extends Annotation> Map<Method, A> getAnnotatedMethods(Class<?> clazz, Class<A> annotation) {
        // annotations should only be on classes extending DataSet
        Assertions.assertTrue(DataSet.class.isAssignableFrom(clazz));

        return Arrays.stream(clazz.getDeclaredMethods())
                .filter(f -> f.isAnnotationPresent(annotation))
                .collect(Collectors.toMap(identity(), m -> m.getAnnotation(annotation)));
    }

    protected boolean canAssignTo(Class<?> classToAssignTo, Class<?> classToAssign) {
        if (classToAssignTo.isAssignableFrom(classToAssign)) {
            return true;
        }
        if (classToAssign.isPrimitive()) {
            return primitiveAndWrapper(classToAssign, classToAssignTo);
        }
        if (classToAssignTo.isPrimitive()) {
            return primitiveAndWrapper(classToAssignTo, classToAssign);
        }
        return false;
    }

    private boolean primitiveAndWrapper(Class<?> primitive, Class<?> wrapper) {
        return primitive == Boolean.TYPE ? wrapper == Boolean.class
                : primitive == Character.TYPE ? wrapper == Character.class
                : primitive == Byte.TYPE ? wrapper == Byte.class
                : primitive == Short.TYPE ? wrapper == Short.class
                : primitive == Integer.TYPE ? wrapper == Integer.class
                : primitive == Long.TYPE ? wrapper == Long.class
                : primitive == Float.TYPE ? wrapper == Float.class
                : primitive == Double.TYPE ? wrapper == Double.class : false;
    }

}
