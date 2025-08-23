package com.company.dataset.annotations;

import com.company.datasets.annotations.InputProperty;
import com.company.exceptions.InvalidInputFormatException;
import com.company.utils.ParseUtils;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

public class InputPropertyTest extends AnnotationTest {

    private Map<Field, InputProperty> getAnnotations(Class<?> clazz) {
        return getAnnotatedFields(clazz, InputProperty.class);
    }

    @ParameterizedTest
    @MethodSource("provideAnnotatedClasses")
    void check(Class<?> clazz) {
        // given
        Map<Field, InputProperty> annotations = getAnnotations(clazz);

        // then
        annotations.forEach((f, a) -> {
            checkRegex(a);
            checkOptions(a);
            checkOptions2(a);
            checkParsingFunc(f, a);
            checkCheckCondition(a, clazz);
            checkEmptyToNull(f, a);
        });
        checkOrders(annotations.values());
    }

    private void checkRegex(InputProperty annotation) {
        if (!annotation.regex().isEmpty()) {
            assertFalse(annotation.message().isEmpty(), "regex is ignored if message is empty");
            assertEquals(0, annotation.options().length, "regex is ignored if options is not empty");
        }
    }

    private void checkOptions(InputProperty annotation) {
        if (annotation.options().length != 0) {
            assertFalse(annotation.message().isEmpty(), "options is ignored if message is empty");
            assertFalse(annotation.multiline(), "options is ignored if multiline is true");
        }
    }

    private void checkOptions2(InputProperty annotation) {
        if (annotation.options2().length > 0) {
            assertEquals(annotation.options().length, annotation.options2().length, "options2 is ignored if it doesn't have same length as options");
        }
    }

    private void checkOrders(Collection<InputProperty> annotations) {
        Map<Integer, List<Integer>> orderToGroupOrders = new HashMap<>();
        annotations.forEach(a -> {
            orderToGroupOrders.putIfAbsent(a.order(), new ArrayList<>());
            orderToGroupOrders.get(a.order()).add(a.groupOrder());
        });

        orderToGroupOrders.forEach((o, gos) -> {
            if (gos.size() == 1) {
                assertEquals(0, gos.get(0), "If only one annotation has this order groupOrder should be default (0)");
            }
            else {
                assertEquals(0, gos.stream().sorted().findFirst().get(), "Lowest groupOrder should always be 0");
                assertEquals(gos.size(), Set.copyOf(gos).size(), "groupOrder can't repeat for the same order");
            }
        });
    }

    private void checkParsingFunc(Field field, InputProperty annotation) {
        String parsingFunc = annotation.parsingFunc();

        if (parsingFunc.isEmpty()) {
            assertEquals(field.getType(), String.class, "parsingfunc can only be skipped for String fields");
        }
        else {
            Method m = assertDoesNotThrow(() -> ParseUtils.class.getMethod(parsingFunc, String.class), "parseFunc should be an available Method in ParseUtils");
            for (Class<?> ex : m.getExceptionTypes()) {
                assertTrue(InvalidInputFormatException.class.isAssignableFrom(ex), "parsingMethod should not declare Exceptions except for InvalidInputFormatException");
            }
            assertTrue(canAssignTo(field.getType(), m.getReturnType()), "return type of the parsingMethod (" + m.getReturnType().getSimpleName() + ") needs to match the field type (" + field.getType().getSimpleName() + ")");
            int modifiers = m.getModifiers();
            assertTrue(Modifier.isStatic(modifiers), "parsingMethod should be static");
            assertTrue(Modifier.isPublic(modifiers), "parsingMethod should be public");
        }
    }

    private void checkCheckCondition(InputProperty annotation, Class<?> clazz) {
        // also ensures that the Class has a builder() method
        Class<?> builderClass = assertDoesNotThrow(() -> clazz.getMethod("builder"), "Class needs to have a builder() method").getReturnType();
        if (!annotation.checkCondition().isEmpty()) {
            Method m = assertDoesNotThrow(() -> builderClass.getMethod(annotation.checkCondition()), "checkCondition should be available in Builder");
            assertEquals(Boolean.TYPE, m.getReturnType(), "Return type of checkCondition needs to be boolean");
            assertTrue(Modifier.isPublic(m.getModifiers()), "checkCondition should be public");
        }
    }

    private void checkEmptyToNull(Field field, InputProperty annotation) {
        if (annotation.emptyToNull()) {
            assertFalse(field.getType().isPrimitive(), "cannot assign null to a primitive");
            if (!annotation.regex().isEmpty()) {
                assertTrue(Pattern.compile(annotation.regex(), Pattern.CASE_INSENSITIVE).matcher("").find(), "regex should match an empty string if emptyToNull is true");
            }
        }
    }

}
