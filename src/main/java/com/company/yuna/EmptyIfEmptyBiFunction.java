package com.company.yuna;

import berlin.yuna.typemap.model.LinkedTypeMap;
import berlin.yuna.typemap.model.Type;
import lombok.AllArgsConstructor;

import java.util.function.BiFunction;

@AllArgsConstructor
public class EmptyIfEmptyBiFunction implements BiFunction<Type<String>, LinkedTypeMap, Type<String>> {

    private final BiFunction<Type<String>, LinkedTypeMap, Type<String>> function;

    @Override
    public Type<String> apply(Type<String> s, LinkedTypeMap m) {
        if ("".equals(s.orElse(""))) return Type.empty();
        return function.apply(s, m);
    }

}
