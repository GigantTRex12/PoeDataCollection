package com.company.datacollector;

import com.company.datasets.BossDropDataSet;
import com.company.datasets.DataSet;
import com.company.datasets.annotations.InputProperty;
import com.company.exceptions.SomethingIsWrongWithMyCodeException;
import com.company.utils.ParseUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

import static com.company.utils.IOUtils.input;
import static com.company.utils.IOUtils.multilineInput;

public class BossDropDataCollectorNew extends DataCollectorNew<BossDropDataSet> {
    public BossDropDataCollectorNew() {
        super();
    }

    // PROTOTYPE of how this could look. This would only be once in the DataCollector so no more subclasses needed
    @Override
    protected void addData() {
        Class<? extends DataSet> clazz = BossDropDataSet.class;
        Field[] fields = clazz.getDeclaredFields();
        List<Field> fieldList = Arrays.stream(fields)
                .filter(f -> f.isAnnotationPresent(InputProperty.class))
                .sorted(Comparator.comparingInt(f -> f.getAnnotation(InputProperty.class).order()))
                .collect(Collectors.toList());
        BossDropDataSet.BossDropDataSetBuilder builder = BossDropDataSet.builder().strategy(currStrat);

        for (Field field : fieldList) {
            InputProperty ann = field.getAnnotation(InputProperty.class);
            String inp;
            if (ann.message().isEmpty()) {
                inp = input();
            }
            else {
                String msg = ann.message();
                if (ann.multiline()) {
                    inp = multilineInput(msg);
                }
                else {
                    if (ann.options().length > 0) {
                        inp = input(msg, ann.options());
                    }
                    else if (!ann.regex().isEmpty()) {
                        inp = input(msg, ann.regex());
                    }
                    else {
                        inp = input(msg);
                    }
                }
            }
            try {
                Method builderFunc = builder.getClass().getMethod(field.getName(), field.getType());
                if (!ann.parsingFunc().isEmpty()) {
                    // TODO: parsing func could throw an InvalidInputFormatException
                    // also then add tests for that exception
                    builderFunc.invoke(builder, ParseUtils.class.getMethod(ann.parsingFunc(), String.class).invoke(null, inp));
                }
                else if (field.getType() == String.class) {
                    builderFunc.invoke(builder, inp);
                }
                else {
                    throw new SomethingIsWrongWithMyCodeException("¯\\_(ツ)_/¯");
                }
            }
            catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                e.printStackTrace(); // ugly but solve later
            }
        }

        this.data.add(builder.build());
    }
}
