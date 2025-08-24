package com.company.yuna;

import berlin.yuna.typemap.model.LinkedTypeMap;
import berlin.yuna.typemap.model.Type;
import com.company.exceptions.InvalidInputFormatException;

@FunctionalInterface
public interface NormalizerBiFunction {

    Object apply(Type<String> s, LinkedTypeMap m) throws InvalidInputFormatException;

}
