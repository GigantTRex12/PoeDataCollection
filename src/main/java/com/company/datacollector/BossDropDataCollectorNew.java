package com.company.datacollector;

import com.company.datasets.datasets.BossDropDataSet;
import com.company.datasets.datasets.DataSet;
import com.company.datasets.annotations.InputProperty;
import com.company.exceptions.SomethingIsWrongWithMyCodeException;
import com.company.utils.ParseUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
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
        // get annotated Fields and sort them
        List<Field> fieldList = Arrays.stream(clazz.getDeclaredFields())
                .filter(f -> f.isAnnotationPresent(InputProperty.class))
                .sorted(Comparator.comparingInt(f -> f.getAnnotation(InputProperty.class).order()))
                .collect(Collectors.toList());
        // Create Builder
        BossDropDataSet.BossDropDataSetBuilder builder = BossDropDataSet.builder().strategy(currStrat);

        // Iterate over Fields
        for (Field field : fieldList) {
            // Get user input according to annotation
            InputProperty ann = field.getAnnotation(InputProperty.class);
            String inp;
            if (ann.message().isEmpty()) {
                if (ann.multiline()) {
                    inp = multilineInput();
                }
                else {
                    inp = input();
                }
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
            // Parse input to type of the Field and pass it to the Builder
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
