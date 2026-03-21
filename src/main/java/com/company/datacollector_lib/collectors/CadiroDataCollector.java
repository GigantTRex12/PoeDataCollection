package com.company.datacollector_lib.collectors;

import collector.Question;
import com.company.api.DbWriter;
import com.company.datacollector_lib.DataCollector;
import com.company.datacollector_lib.Normalizers;
import com.company.dataset_lib.datasets.CadiroDataSet;
import com.company.dataset_lib.other.UniqueAndGoldCostPair;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CadiroDataCollector extends DataCollector<CadiroDataSet> {

    @Override
    protected List<Question> getQuestions() {
        return List.of(
                Question.ask("tier", "What is the tier?")
                        .regex("^[1-3]$")
                        .normalize(str -> Integer.parseInt(str))
                        .build(),
                Question.ask("uniquesWithCost", "Enter the prices.")
                        .multiline()
                        .regex("^.* \\d+$")
                        .normalize(Normalizers::toUniqueCostPairs)
                        .build()
        );
    }

    @Override
    protected CadiroDataSet mapToDataset(Map<String, Object> map) {
        return new CadiroDataSet(
                this.getMetadata(),
                (Integer) map.get("tier"),
                ((List<?>) map.get("uniquesWithCost")).stream().map(o -> (UniqueAndGoldCostPair) o).collect(Collectors.toList())
        );
    }

    @Override
    protected void saveData() {
        DbWriter.writeCadiroDataSets(this.data);
    }
}
