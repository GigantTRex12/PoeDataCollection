package com.company.testutils;

import lombok.SneakyThrows;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.beans.*;

public class TestUtils {

    @SneakyThrows
    public static Method getGetter(Field field) {
        for (PropertyDescriptor pd : Introspector.getBeanInfo(field.getDeclaringClass()).getPropertyDescriptors()) {
            if (pd.getReadMethod() != null && !"class".equals(pd.getName()))
                return pd.getReadMethod();
        }
        throw new IntrospectionException("Couldn't find Getter");
    }

}
