package com.company.dataanalyzer;

import berlin.yuna.typemap.model.Pair;
import com.company.datasets.annotations.Evaluate;
import com.company.datasets.annotations.Groupable;
import com.company.datasets.datasets.DataSet;
import com.company.exceptions.SomethingIsWrongWithMyCodeException;
import com.company.utils.FileUtils;
import com.company.utils.Grouper;
import com.company.utils.Utils;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.company.utils.IOUtils.*;

public abstract class DataAnalyzer<T extends DataSet> {

    protected final List<T> data;

    private final Class<T> t;

    public DataAnalyzer(String filename, Class<T> t) throws FileNotFoundException, JsonProcessingException {
        this.t = t;
        data = new ArrayList<T>();
        List<String> lines = FileUtils.readLines(filename);
        for (String line : lines) {
            data.add(Utils.parseJson(line, t));
        }
    }

    public void analyzeData() {
        Grouper<T> grouper = groupData();
        evaluateData(grouper);
    }

    private Grouper<T> groupData() {
        Grouper<T> grouper = new Grouper<>(this.data);

        getGroupableMethods().forEach(p -> {
            Method m = p.getKey();
            Groupable ann = p.getValue();
            Function<T, Object> f = t -> {
                try {
                    return m.invoke(t);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new SomethingIsWrongWithMyCodeException("Method " + m.getName() + " with annotation @Groupable is not invokable.");
                }
            };

            if (!ann.force()) {
                String message;
                if (ann.filterBool()) message = "Want to only evaluate Datasets where " + m.getName() + "is true?";
                else message = "Want to group by " + m.getName() + "?";
                if (!inputBool(message)) return;
            }

            if (ann.filterByValue()) {
                Map<String, Object> stringToReturn = grouper.getAllReturns(f, ann.ignoreNulls()).stream().collect(Collectors.toMap(
                        o -> o.toString().toLowerCase(Locale.ROOT),
                        Function.identity()
                ));
                String input = input(
                        "Want to filter by value for " + m.getName() + "?\nEnter the value or nothing to not filter.",
                        stringToReturn.keySet(),
                        false
                );
                if (stringToReturn.containsKey(input.toLowerCase())) {
                    grouper.filterValue(f, stringToReturn.get(input.toLowerCase()));
                } else {
                    grouper.groupBy(f, ann.ignoreNulls());
                }
            } else if (ann.filterBool()) {
                grouper.filterBool(t -> {
                    try {
                        return (boolean) m.invoke(t);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new SomethingIsWrongWithMyCodeException("Method " + m.getName() + " with annotation @Groupable is not invokable.");
                    } catch (ClassCastException e) {
                        throw new SomethingIsWrongWithMyCodeException("Method " + m.getName() + " with annotation @Groupable and flag filterBool does not return boolean.");
                    }
                });
            } else {
                grouper.groupBy(f, ann.ignoreNulls());
            }
        });

        return grouper;
    }

    private void evaluateData(Grouper<T> grouper) {
        print("Evaluating Datasets of type " + getGenericClass().getSimpleName() + " with groupings:");
        printList(grouper.getGroupings());
        grouper.forEach((groupValues, datasets) -> {
            print("Groupings values:");
            printList(groupValues);
            getEvaluatableMethods().forEach(p -> {
                Method m = p.getKey();
                Evaluate ann = p.getValue();
                Function<T, Object> f = t -> {
                    try {
                        return m.invoke(t);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new SomethingIsWrongWithMyCodeException("Method " + m.getName() + " with annotation @Evaluate is not invokable.");
                    }
                };

                switch (ann.evaluationMode()) {
                    case PERCENTAGE_BASED -> {
                        percentageBased(datasets.stream().map(f).toList());
                        break;
                    }
                    case COUNTER_BASED -> {
                        // TODO casting thing
                        break;
                    }
                    case NUMBER_STATISTIC_GRAPH -> {
                        // TODO casting thing
                        break;
                    }
                    case CUSTOM -> custom(datasets, f);
                }
            });
        });
        print("---Evaluation complete---");
    }

    protected Class<T> getGenericClass() {
        return t;
    }

    private List<Pair<Method, Groupable>> getGroupableMethods() {
        return Stream.concat(
                Arrays.stream(getGenericClass().getDeclaredFields())
                        .filter(f -> f.isAnnotationPresent(Groupable.class))
                        .map(f -> new Pair<>(Utils.getGetter(f), f.getAnnotation(Groupable.class))),
                Arrays.stream(getGenericClass().getDeclaredMethods())
                        .filter(m -> m.isAnnotationPresent(Groupable.class))
                        .map(m -> new Pair<>(m, m.getAnnotation(Groupable.class)))
        ).sorted(Comparator.comparingInt(p -> p.value().order()))
                .collect(Collectors.toList());
    }

    private List<Pair<Method, Evaluate>> getEvaluatableMethods() {
        return Stream.concat(
                Arrays.stream(getGenericClass().getDeclaredFields())
                        .filter(f -> f.isAnnotationPresent(Evaluate.class))
                        .map(f -> new Pair<>(Utils.getGetter(f), f.getAnnotation(Evaluate.class))),
                Arrays.stream(getGenericClass().getDeclaredMethods())
                        .filter(m -> m.isAnnotationPresent(Evaluate.class))
                        .map(m -> new Pair<>(m, m.getAnnotation(Evaluate.class)))
        ).collect(Collectors.toList());
    }

    protected void percentageBased(List<?> values) {

    }

    protected void counterBased(List<Collection<?>> values) {
        // TODO
    }

    protected void numberStatisticsGraph(List<Long> values) {
        // TODO
    }

    protected void custom(List<T> datasets, Function<T, ?> function) {
        throw new UnsupportedOperationException("Analyzer needs to overwrite this method to use custom evaluation mode");
    }

}
