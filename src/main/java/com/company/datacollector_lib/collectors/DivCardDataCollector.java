package com.company.datacollector_lib.collectors;

import collector.Question;
import collector.Survey;
import com.company.api.DbWriter;
import com.company.datacollector_lib.DataCollector;
import com.company.datacollector_lib.Normalizers;
import com.company.dataset_lib.datasets.DivCardDataSet;
import com.company.datasets.other.loot.Loot;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class DivCardDataCollector extends DataCollector<DivCardDataSet> {

    private static final String CARD_NAME = "cardName";
    private static final String RESULT = "result";
    private static final String RESULTS = "results";
    private static final String CHARACTER_LEVEL = "characterLevel";


    private final Survey multipleSurvey = new Survey(List.of(
            Question.ask(CARD_NAME, "Enter the name of the card")
                    .validate((s, _) -> s.isBlank() ? Optional.of("The card name cannot be empty") : Optional.empty())
                    .build(),
            Question.ask(RESULTS, "Enter the rewards from the card")
                    .multiline()
                    .normalize((s, m) -> m.put(RESULTS, Normalizers.toDivCardLootList(s, (String) m.get(CARD_NAME))))
                    .build(),
            Question.ask(CHARACTER_LEVEL, "What level is the character that turned the card in?")
                    .regex("^\\d+$|^$")
                    .normalize(s -> s.isEmpty() ? null : Integer.parseInt(s))
                    //.emptyToNull()
                    .build()

    ));

    @Override
    protected List<Question> getQuestions() {
        return List.of(
                Question.ask(CARD_NAME, "Enter the name of the card")
                        .validate((s, _) -> s.isBlank() ? Optional.of("The card name cannot be empty") : Optional.empty())
                        .build(),
                Question.ask(RESULT, "Enter the reward from the card")
                        .normalize((s, m) -> m.put(RESULT, Normalizers.toDivCardLoot(s, (String) m.get(CARD_NAME))))
                        .build(),
                Question.ask(CHARACTER_LEVEL, "What level is the character that turned the card in?")
                        .regex("^\\d+$|^$")
                        .normalize(s -> s.isEmpty() ? null : Integer.parseInt(s))
                        //.emptyToNull()
                        .build()
        );
    }

    @Override
    protected DivCardDataSet mapToDataset(Map<String, Object> map) {
        return new DivCardDataSet(
                this.getMetadata(),
                (String) map.get(CARD_NAME),
                (Loot) map.get(RESULT),
                (Integer) map.get(CHARACTER_LEVEL)
        );
    }

    @Override
    protected void saveData() {
        DbWriter.writeDivCardDataSets(this.data);
        this.data.clear();
    }

    @Override
    protected void addMultipleDatasets() {
        Map<String, Object> map = multipleSurvey.run();
        String name = (String) map.get(CARD_NAME);
        Integer level = (Integer) map.get(CHARACTER_LEVEL);
        List<Loot> results = (List) map.get(RESULTS);
        results.forEach(l -> this.data.add(new DivCardDataSet(this.getMetadata(), name, l, level)));
    }
}
