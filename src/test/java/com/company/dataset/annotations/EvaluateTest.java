package com.company.dataset.annotations;

import com.company.datasets.annotations.Evaluate;
import com.company.testutils.TestUtils;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class EvaluateTest extends AnnotationTest {

    private Map<Method, Evaluate> getAnnotations(Class<?> clazz) {
        Map<Method, Evaluate> annotations = new HashMap<>(getAnnotatedMethods(clazz, Evaluate.class));
        getAnnotatedFields(clazz, Evaluate.class).forEach((f, a) -> {
            annotations.put(TestUtils.getGetter(f), a);
        });
        return annotations;
    }

    @ParameterizedTest
    @MethodSource("provideAnnotatedClasses")
    void check(Class<?> clazz) {
        // given
        Map<Method, Evaluate> annotations = getAnnotations(clazz);

        // then
        annotations.forEach((m, a) -> {
            checkEvaluationMode(m, a.evaluationMode());
        });
    }

    private void checkEvaluationMode(Method method, Evaluate.EvaluationMode mode) {
        switch (mode) {
            case NUMBER_STATISTIC_GRAPH:
                assertTrue(
                        List.of(Byte.class, Byte.TYPE, Integer.class, Integer.TYPE, Long.class, Long.TYPE).contains(method.getReturnType()),
                        "Method should return a number for mode " + mode
                );
            default:
                assertTrue(List.of(
                        Evaluate.EvaluationMode.PERCENTAGE_BASED,
                        Evaluate.EvaluationMode.COUNTER_BASED
                ).contains(mode), "Test does not know the mode " + mode);
        }
    }

}
