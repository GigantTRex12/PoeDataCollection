package com.company.dataset.annotations;

import com.company.datasets.annotations.Groupable;
import com.company.testutils.TestUtils;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class GroupableTest extends AnnotationTest {

    private Map<Method, Groupable> getAnnotations(Class<?> clazz) {
        Map<Method, Groupable> annotations = new HashMap<>(getAnnotatedMethods(clazz, Groupable.class));
        getAnnotatedFields(clazz, Groupable.class).forEach((f, a) -> {
            annotations.put(TestUtils.getGetter(f), a);
        });
        return annotations;
    }

    @ParameterizedTest
    @MethodSource("provideAnnotatedClasses")
    void check(Class<?> clazz) {
        // given
        Map<Method, Groupable> annotations = getAnnotations(clazz);

        // then
        annotations.forEach((m, a) -> {
            assertFalse(a.force() && a.filterBool(), "Should not filter and force at the same time");
            assertFalse(a.force() && a.ignoreNulls(), "Should not filter and force at the same time");
            checkFilterBool(m, a);
        });
        assertEquals(annotations.size(), annotations.values().stream().distinct().count(), "All order values should be distinct");
    }

    private void checkFilterBool(Method method, Groupable annotation) {
        if (annotation.filterBool()) {
            assertFalse(annotation.filterByValue(), "Should not use two different filters");
            assertTrue(method.getReturnType() == Boolean.class || method.getReturnType() == Boolean.TYPE, "Method needs to return boolean for this filter");
        }
    }

}
