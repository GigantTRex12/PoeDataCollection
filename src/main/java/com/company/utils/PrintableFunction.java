package com.company.utils;

import com.company.exceptions.SomethingIsWrongWithMyCodeException;
import lombok.AllArgsConstructor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Function;

@AllArgsConstructor
public class PrintableFunction<T> implements Function<T, Object> {

    private final Function<T, Object> function;
    private final String string;

    public PrintableFunction(Method method) {
        function = t -> {
            try {
                return method.invoke(t);
            } catch (InvocationTargetException | IllegalAccessException e) {
                throw new SomethingIsWrongWithMyCodeException("Method " + method.getName() + " is not invokable.");
            }
        };
        string = method.getName();
    }

    @Override
    public String toString() {
        return string;
    }

    @Override
    public Object apply(T t) {
        return function.apply(t);
    }

    @Override
    public <V> Function<V, Object> compose(Function<? super V, ? extends T> before) {
        return function.compose(before);
    }

    @Override
    public <V> Function<T, V> andThen(Function<? super Object, ? extends V> after) {
        return function.andThen(after);
    }
}
