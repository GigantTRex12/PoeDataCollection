package com.company.dataanalyzer_lib.analyzers;

import analyzer.GroupingDefinition;
import analyzer.Question;
import com.company.api.DbReader;
import com.company.dataanalyzer_lib.DataAnalyzer;
import com.company.dataset_lib.datasets.BossDropDataSet;
import com.company.datasets.other.loot.Loot;
import com.company.datasets.other.loot.LootType;
import com.company.datasets.other.loot.StackableLoot;
import com.company.utils.Counter;
import com.company.utils.Utils;

import java.util.List;
import java.util.Map;

public class BossDropDataAnalyzer extends DataAnalyzer<BossDropDataSet> {

    public BossDropDataAnalyzer() {
        super(DbReader::readBossDropDataSets);
    }

    @Override
    protected List<Question<BossDropDataSet>> getQuestions() {
        Class<BossDropDataSet> c = BossDropDataSet.class;
        return List.of(
                Question.ask("boss_unique", c)
                        .forcedGrouping(BossDropDataSet::lowerCaseBossname)
                        .groupings(leagueGrouper, new GroupingDefinition<>("witness", BossDropDataSet::isWitnessed))
                        .evaluator(d -> d.getGuaranteedDrop().getName(), WILSON_CONFIDENCE)
                        .conditionAll(d -> d.getGuaranteedDrop() != null)
                        .build(),
                Question.ask("extra_drops", c)
                        .forcedGrouping(BossDropDataSet::lowerCaseBossname)
                        .groupings(leagueGrouper)
                        .evaluator(BossDropDataSet::getExtraDrops, this::countExtraDrops)
                        .build()
        );
    }

    private void countExtraDrops(List<List<Loot>> drops) {
        Counter<String> lootCounter = new Counter<>();
        for (List<Loot> dropList : drops) {
            for (Loot loot : dropList) {
                if (loot instanceof StackableLoot l) lootCounter.add(l.getName(), l.getStackSize());
                else if (loot.getType() == LootType.BOSS_UNIQUE_ITEM) lootCounter.add(loot.getName());
                else lootCounter.add(loot.getType().name());
            }
        }

        int size = drops.size();
        for (Map.Entry<String, Integer> entry : lootCounter.entrySet()) {
            IO.println(entry.getKey() + ": " + entry.getValue() + "/" + size + " (" + Utils.toPercentage(entry.getValue(), size, 2) + ")");
        }
    }

}
