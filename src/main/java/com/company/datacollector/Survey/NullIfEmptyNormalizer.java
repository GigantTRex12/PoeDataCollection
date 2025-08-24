package com.company.datacollector.Survey;

import berlin.yuna.typemap.model.LinkedTypeMap;
import berlin.yuna.typemap.model.Type;
import com.company.exceptions.InvalidInputFormatException;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class NullIfEmptyNormalizer implements NormalizerBiFunction {

    private final NormalizerBiFunction function;

    @Override
    public Object apply(Type<String> s, LinkedTypeMap m) throws InvalidInputFormatException {
        if ("".equals(s.orElse(""))) return null;
        return function.apply(s, m);
    }

}
