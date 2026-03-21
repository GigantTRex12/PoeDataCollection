package com.company.datacollector_lib.collectors;

import collector.Question;
import com.company.api.DbWriter;
import com.company.datacollector_lib.DataCollector;
import com.company.datacollector_lib.Normalizers;
import com.company.dataset_lib.datasets.BossDropDataSet;
import com.company.datasets.other.loot.Loot;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BossDropDataCollector extends DataCollector<BossDropDataSet> {

    @Override
    protected List<Question> getQuestions() {
        return List.of(
                Question.ask("bossName", "Enter the name of the boss.")
                        .build(),
                Question.ask("uber", "Is the boss uber?")
                        .options(new String[]{"y", "n"})
                        .normalize(Normalizers::toBool)
                        .build(),
                Question.ask("witnessed", "Was the boss witnessed by the Maven?")
                        .options(new String[]{"y", "n"})
                        .normalize(Normalizers::toBool)
                        .build(),
                Question.ask("guaranteedDrop", "Which unique was the guaranteed drop?")
                        .normalize(Normalizers::parseToBossLoot)
                        .emptyToNull()
                        .build(),
                Question.ask("extraDrops", "Input extra drops to track.")
                        .multiline()
                        .normalize(Normalizers::toLootList)
                        .build(),
                Question.ask("quantity", "Enter the area quantity.")
                        .regex("^$|^\\d+$")
                        .normalize(str -> Integer.parseInt(str))
                        .emptyToNull()
                        .build()
        );
    }

    @Override
    protected BossDropDataSet mapToDataset(Map<String, Object> map) {
        return new BossDropDataSet(
                this.getMetadata(),
                (String) map.get("bossName"),
                (Boolean) map.get("uber"),
                (Boolean) map.get("witnessed"),
                (Loot) map.get("guaranteedDrop"),
                ((List<?>) map.get("extraDrops")).stream().map(o -> (Loot) o).collect(Collectors.toList()),
                (Integer) map.get("quantity")
        );
    }

    @Override
    protected void saveData() {
        DbWriter.writeBossDropDataSets(this.data);
    }
}
