package com.company.datacollector_lib.collectors;

import collector.Question;
import com.company.api.DbWriter;
import com.company.datacollector_lib.DataCollector;
import com.company.datacollector_lib.Normalizers;
import com.company.dataset_lib.datasets.DivCardDataSet;
import com.company.datasets.other.loot.Loot;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class DivCardDataCollector extends DataCollector<DivCardDataSet> {

    @Override
    protected List<Question> getQuestions() {
        return List.of(
                Question.ask("cardName", "Enter the name of the card")
                        .validate((s, _) -> s.isBlank() ? Optional.of("The card name cannot be empty") : Optional.empty())
                        .build(),
                Question.ask("result", "Enter the reward from the card")
                        .normalize((s, m) -> m.put("result", Normalizers.toDivCardLoot(s, (String) m.get("cardName"))))
                        .build(),
                Question.ask("characterLevel", "What level is the character that turned the card in")
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
                (String) map.get("cardName"),
                (Loot) map.get("result"),
                (Integer) map.get("characterLevel")
        );
    }

    @Override
    protected void saveData() {
        DbWriter.writeDivCardDataSets(this.data);
        this.data.clear();
    }

}
