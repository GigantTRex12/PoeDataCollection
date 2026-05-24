package com.company.datacollector_lib.collectors;

import collector.Question;
import com.company.api.DbWriter;
import com.company.datacollector_lib.DataCollector;
import com.company.datacollector_lib.Normalizers;
import com.company.dataset_lib.datasets.UltimatumDataSet;
import com.company.datasets.other.loot.Loot;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class UltimatumDataCollector extends DataCollector<UltimatumDataSet> {

    @Override
    protected List<Question> getQuestions() {
        return List.of(
                Question.ask("rewards", "Enter rewards from Ultimatum in order. (one line per reward)\nEnter \"-\" to skip reward.")
                        .multiline()
                        .normalize(Normalizers::toLootList)
                        .build(),
                Question.ask("boss", "Was a boss encountered?")
                        .when(map -> ((List<?>) map.get("rewards")).size() == 10)
                        .options(new String[]{"y", "n"})
                        .normalize(Normalizers::toBool)
                        .build(),
                Question.ask("bossLoot", "Enter drops from boss.")
                        .when(map -> (boolean) map.get("boss"))
                        .multiline()
                        .normalize(Normalizers::toLootList)
                        .build()
        );
    }

    @Override
    protected UltimatumDataSet mapToDataset(Map<String, Object> map) {
        return new UltimatumDataSet(
                this.getMetadata(),
                ((List<?>) map.get("rewards")).stream().map(o -> (Loot) o).collect(Collectors.toList()),
                (boolean) map.get("boss"),
                ((List<?>) map.get("bossLoot")).stream().map(o -> (Loot) o).collect(Collectors.toList())
        );
    }

    @Override
    protected void saveData() {
        DbWriter.writeUltimatumDataSets(this.data);
        this.data.clear();
    }

}
