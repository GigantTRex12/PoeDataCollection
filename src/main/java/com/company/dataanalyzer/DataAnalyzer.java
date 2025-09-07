package com.company.dataanalyzer;

import berlin.yuna.typemap.model.Pair;
import com.company.datasets.annotations.Evaluate;
import com.company.datasets.annotations.Groupable;
import com.company.datasets.datasets.DataSet;
import com.company.exceptions.SomethingIsWrongWithMyCodeException;
import com.company.utils.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.Getter;

import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.company.utils.IOUtils.*;
import static java.util.Map.entry;

public abstract class DataAnalyzer<T extends DataSet> {

    @Getter
    private static final Map<String, String> actions = Map.ofEntries(
            entry("AnalyzeData", "a"),
            //entry("AnalyzeDataFunctional", "af"),
            entry("PrintData", "p"),
            entry("Exit", "e")
    );

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
        while (true) {
            String action = input("What would you like to do?", actions).toLowerCase();
            boolean exit = false;
            switch (action) {
                case "analyzedata":
                case "a":
                    Grouper<T> grouper = groupData();
                    evaluateData(grouper);
                    break;
                case ("printdata"):
                case ("p"):
                    printList(this.data);
                    break;
                case ("exit"):
                case ("e"):
                    exit = true;
                    break;
            }
            if (exit) break;
        }
    }

    private Grouper<T> groupData() {
        Grouper<T> grouper = new Grouper<>(this.data);

        getGroupableMethods().forEach(p -> {
            Method m = p.getKey();
            Groupable ann = p.getValue();
            Function<T, Object> f = new PrintableFunction<>(m);

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
        printList(grouper.getGroupings(), ", ");
        getEvaluatableMethods().forEach(p -> {
            Method m = p.getKey();
            if (!inputBool("Want to evaluate " + m.getName() + "?")) return;
            Evaluate ann = p.getValue();
            Function<T, Object> f = new PrintableFunction<>(m);
            grouper.forEach((groupValues, datasets) -> {
                print("Groupings values for " + datasets.size() + " datasets:");
                printList(groupValues, ", ");

                switch (ann.evaluationMode()) {
                    case PERCENTAGE_BASED -> percentageBased(datasets.stream().map(f).toList());
                    case PERCENTAGE_BASED_CONFIDENCE -> percentageBasedConfidence(datasets.stream().map(f).toList());
                    case COUNTER_BASED -> counterBased(datasets.stream().map(f)
                            .map(o -> {
                                try {
                                    return (Iterable<?>) o;
                                } catch (ClassCastException e) {
                                    throw new SomethingIsWrongWithMyCodeException("Return type of " + m.getName() + " needs to be an iterable");
                                }
                            }).toList());
                    case NUMBER_STATISTIC_GRAPH -> numberStatisticsGraph(datasets.stream().map(f)
                            .map(o -> {
                                try {
                                    return (Long) o;
                                } catch (ClassCastException e) {
                                    throw new SomethingIsWrongWithMyCodeException("Return type of " + m.getName() + " needs to be a long");
                                }
                            }).toList());
                    case CUSTOM -> custom(datasets, m);
                }
                print("-----");
            });
        });
        print("---Evaluation complete---");
    }

    protected Class<T> getGenericClass() {
        return t;
    }

    private List<Pair<Method, Groupable>> getGroupableMethods() {
        return Stream.concat(
                Utils.getAllFields(getGenericClass()).stream()
                        .filter(f -> f.isAnnotationPresent(Groupable.class))
                        .map(f -> new Pair<>(Utils.getGetter(f), f.getAnnotation(Groupable.class))),
                Arrays.stream(getGenericClass().getMethods())
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
        ).sorted(Comparator.comparingInt(p -> p.value().order()))
                .collect(Collectors.toList());
    }

    protected <R> void percentageBased(List<R> values) {
        Counter<R> counter = new Counter<>(values);
        int total = counter.sum();
        counter.forEachNonZero((value, amount) -> print(
                (value != null ? value.toString() : "null") + ": "
                        + Utils.toPercentage(amount, total, 1)
        ));
    }

    protected <R> void percentageBasedConfidence(List<R> values) {
        Counter<R> counter = new Counter<>(values);
        int total = counter.sum();
        counter.forEachNonZero((value, amount) -> print(
                (value != null ? value.toString() : "null") + ": "
                        + Utils.toBinomialConfidenceRange(amount, total, 0.95, 2)
        ));
    }

    protected <R> void counterBased(List<Iterable<R>> values) {
        Counter<R> counter = new Counter<>();
        for (Iterable<R> iterable : values) {
            counter.add(iterable);
        }
        counter.forEachNonZero((value, amount) -> print("Total of " + amount + " " + value.toString() + "."));
    }

    protected void numberStatisticsGraph(List<Long> values) {
        // TODO
    }

    protected void custom(List<T> datasets, Method method) {
        throw new UnsupportedOperationException("Analyzer needs to overwrite this method to use custom evaluation mode");
    }

}
