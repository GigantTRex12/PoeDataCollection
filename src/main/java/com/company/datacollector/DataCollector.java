package com.company.datacollector;

import berlin.yuna.typemap.model.LinkedTypeMap;
import com.company.datasets.annotations.InputProperty;
import com.company.datasets.builder.DataSetBuilderInterface;
import com.company.datasets.datasets.DataSet;
import com.company.datasets.other.metadata.Strategy;
import com.company.exceptions.InvalidInputFormatException;
import com.company.exceptions.SomethingIsWrongWithMyCodeException;
import com.company.exceptions.StrategyCreationInterruptedException;
import com.company.utils.ParseUtils;
import com.company.utils.Utils;
import com.company.yuna.Question;
import com.company.yuna.Survey;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.Getter;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.company.utils.FileUtils.append;
import static com.company.utils.IOUtils.*;
import static java.util.Map.entry;

public abstract class DataCollector<T extends DataSet> {
    @Getter
    private static final Map<String, String> actions = Map.ofEntries(
            entry("AddData", "a"),
            entry("AddDataFunctionl", "af"),
            entry("ClearData", "c"),
            entry("Save", "s"),
            entry("PrintData", "p"),
            entry("AddStrat", "as"),
            entry("ChangeStrat", "cs"),
            entry("FixChoices", "fc"),
            entry("ClearFixedChoices", "cc"),
            entry("Exit", "e")
    );

    protected List<T> data = new ArrayList<>();
    protected Strategy currStrat;
    private Map<Integer, Strategy> strategyIds;
    private final Map<InputProperty, String> preSetChoices = new HashMap<>();

    private final Class<T> t;
    private final Supplier<DataSetBuilderInterface<T>> builderSupplier;

    public DataCollector(Class<T> t, Supplier<DataSetBuilderInterface<T>> builderSupplier) {
        this.t = t;
        this.builderSupplier = builderSupplier;
    }

    public void collectData(String filename) {
        initializeAndPickStrat();
        while (true) {
            String action = input("What would you like to do?", actions).toLowerCase();
            boolean exit = false;
            switch (action) {
                case ("adddata"):
                case ("a"):
                    this.addDataReflection();
                    break;
                case ("AddDataFunctionl"):
                case ("af"):
                    this.addDataFunctional();
                    break;
                case ("cleardata"):
                case ("c"):
                    this.data.clear();
                    break;
                case ("save"):
                case ("s"):
                    this.addAllDataToFile(filename);
                    break;
                case ("printdata"):
                case ("p"):
                    for (DataSet ds : this.data) {
                        print(ds);
                    }
                    break;
                case ("addstrat"):
                case ("as"):
                    this.addStrat();
                    break;
                case ("changestrat"):
                case ("cs"):
                    this.currStrat = pickStrat();
                    break;
                case ("fixchoices"):
                case ("fc"):
                    this.setPreSetChoices();
                    break;
                case ("clearfixedchoices"):
                case ("cc"):
                    this.preSetChoices.clear();
                    break;
                case ("exit"):
                case ("e"):
                    this.addAllDataToFile(filename);
                    exit = true;
                    break;
            }
            if (exit) {
                break;
            }
        }
    }

    private void addAllDataToFile(String filename) {
        append(filename, this.data);
        this.data.clear();
    }

    private Strategy pickStrat() {
        printList(new ArrayList<>(strategyIds.values()));
        List<String> stratIds = strategyIds.keySet().stream().map(Object::toString).collect(Collectors.toList());
        return strategyIds.get(Integer.parseInt(input("Pick one of the above strategies by id", stratIds)));
    }

    private void initializeAndPickStrat() {
        strategyIds = Strategy.getAll();
        this.currStrat = pickStrat();
    }

    private void addStrat() {
        Strategy newStrat;
        try {
            newStrat = Strategy.create();
        } catch (StrategyCreationInterruptedException e) {
            print("Strategy creation stopped");
            return;
        }
        this.strategyIds.put(newStrat.getId(), newStrat);
        currStrat = newStrat;
        print("Added new Strategy and switched to new Strategy");
    }

    protected void beforeAddData() {
    }

    protected DataSetBuilderInterface<T> finalizeData(DataSetBuilderInterface<T> builder) {
        return builder;
    }

    private void addDataReflection() {
        beforeAddData();
        List<Field> fields = getAnnotatedFields();
        DataSetBuilderInterface<T> builder = createBuilder();
        String input = "";
        for (Field field : fields) {
            InputProperty ann = field.getAnnotation(InputProperty.class);
            boolean cont = false;
            if (!ann.checkCondition().isEmpty()) {
                try {
                    if (!(boolean) builder.getClass().getMethod(ann.checkCondition()).invoke(builder)) {
                        cont = true;
                    }
                } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
                    throw new SomethingIsWrongWithMyCodeException("¯\\_(ツ)_/¯");
                }
            }
            if (cont) {
                continue;
            }

            boolean inputNeeded = true;
            while (inputNeeded) {
                try {
                    if (ann.groupOrder() == 0) {
                        input = getUserInput(ann);
                    }
                    parseInputAndCallBuilder(input, builder, field, ann);
                    inputNeeded = false;
                } catch (InvalidInputFormatException e) {
                    if (preSetChoices.remove(ann) == null) {
                        print("Invalid input, try again");
                    }
                }
            }
        }
        builder = finalizeData(builder);
        T dataSet = builder.build();
        if (validateDataSet(dataSet)) {
            this.data.add(dataSet);
        }
    }

    protected Class<? extends T> getGenericClass() {
        return t;
    }

    private List<Field> getAnnotatedFields() {
        return Arrays.stream(getGenericClass().getDeclaredFields())
                .filter(f -> f.isAnnotationPresent(InputProperty.class))
                .sorted(
                        (f1, f2) -> {
                            InputProperty ann1 = f1.getAnnotation(InputProperty.class);
                            InputProperty ann2 = f2.getAnnotation(InputProperty.class);
                            if (ann1.order() == ann2.order()) {
                                return Integer.compare(ann1.groupOrder(), ann2.groupOrder());
                            } else {
                                return Integer.compare(ann1.order(), ann2.order());
                            }
                        }
                )
                .collect(Collectors.toList());
    }

    protected DataSetBuilderInterface<T> createBuilder() {
        return builderSupplier.get().strategy(currStrat);
    }

    private String getUserInput(InputProperty ann) {
        if (preSetChoices.get(ann) != null) {
            return preSetChoices.get(ann);
        }

        if (ann.message().isEmpty()) {
            if (ann.multiline()) return multilineInput();
            else return input();
        } else {
            String msg = ann.message();
            if (ann.multiline()) {
                return ann.regex().isEmpty() ? multilineInput(msg) : multilineInput(msg, ann.regex());
            } else {
                if (ann.options().length > 0) {
                    String[] options = ann.options();
                    boolean force = ann.forceOptions();
                    if (ann.options2().length == options.length) {
                        Map<String, String> optionsMap = new HashMap<>();
                        for (int i = 0; i < options.length; i++) {
                            optionsMap.put(options[i], ann.options2()[i]);
                        }
                        return input(msg, optionsMap, force);
                    }
                    if (ann.emptyToNull() && !Utils.contains(options, "")) {
                        List<String> optionsList = new ArrayList<>(Arrays.asList(options));
                        optionsList.add("");
                        return input(msg, optionsList, force);
                    }
                    return input(msg, options, force);
                } else if (!ann.regex().isEmpty()) {
                    return input(msg, ann.regex());
                } else {
                    return input(msg);
                }
            }
        }
    }

    private void parseInputAndCallBuilder(String inp, DataSetBuilderInterface<T> builder, Field field, InputProperty ann) throws InvalidInputFormatException {
        try {
            Method builderFunc = builder.getClass().getMethod(field.getName(), field.getType());
            if (ann.emptyToNull() && inp.isEmpty()) {
                builderFunc.invoke(builder, new Object[]{null});
            } else if (!ann.parsingFunc().isEmpty()) {
                builderFunc.invoke(
                        builder,
                        ParseUtils.class.getMethod(ann.parsingFunc(), String.class).invoke(null, inp)
                );
            } else if (field.getType() == String.class) {
                builderFunc.invoke(builder, inp);
            } else {
                throw new SomethingIsWrongWithMyCodeException("¯\\_(ツ)_/¯");
            }
        } catch (InvocationTargetException e) {
            Throwable newE = e.getCause();
            if (newE instanceof InvalidInputFormatException) {
                throw (InvalidInputFormatException) newE;
            } else {
                e.printStackTrace();
                newE.printStackTrace();
                throw new SomethingIsWrongWithMyCodeException(newE.getMessage());
            }
        } catch (NoSuchMethodException | IllegalAccessException e) {
            throw new SomethingIsWrongWithMyCodeException(e.getMessage());
        }
    }

    protected boolean validateDataSet(T dataSet) {
        return true;
    }

    private void addDataFunctional() {
        LinkedTypeMap typeMap = Survey.run(getQuestions(), currStrat);
        T dataSet = mapToDataset(typeMap);
        if (validateDataSet(dataSet)) {
            this.data.add(dataSet);
        }
    }

    protected List<Question> getQuestions() {
        throw new UnsupportedOperationException("Functional Data collecting not supported yet");
    }

    protected T mapToDataset(LinkedTypeMap map) {
        try {
            return Utils.parseJson(map.toJson(), getGenericClass());
        } catch (JsonProcessingException e) {
            throw new SomethingIsWrongWithMyCodeException(e.getMessage());
        }
    }

    private void setPreSetChoices() {
        preSetChoices.clear();
        print("Pick responses to always set, empty to not set and \\ for empty response");
        getAnnotatedFields().stream().map(f -> f.getAnnotation(InputProperty.class)).forEach(ann -> {
            if (ann.groupOrder() == 0) {
                String response;
                if (ann.multiline()) {
                    response = multilineInput(ann.message());
                } else {
                    response = input(ann.message());
                }
                if (response.equals("\\")) {
                    preSetChoices.put(ann, "");
                } else if (!response.isEmpty()) {
                    preSetChoices.put(ann, response);
                }
            }
        });
    }

}
