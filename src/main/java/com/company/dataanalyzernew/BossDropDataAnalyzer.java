package com.company.dataanalyzernew;

import analyzer.GroupingDefinition;
import analyzer.Question;
import com.company.datasets.datasetsnew.BossDropDataSet;
import com.company.datasets.other.loot.Loot;
import com.company.datasets.other.loot.LootType;
import com.company.datasets.other.loot.StackableLoot;
import com.company.utils.Utils;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BossDropDataAnalyzer extends DataAnalyzer<BossDropDataSet> {

    private static final Class<BossDropDataSet> clazz = BossDropDataSet.class;

    public BossDropDataAnalyzer(String filename) throws FileNotFoundException, JsonProcessingException {
        super(filename, clazz);
    }

    @Override
    protected List<Question<BossDropDataSet>> getQuestions() {
        return List.of(
                Question.ask("main_drop", clazz)
                        .evaluator(t -> t.getGuaranteedDrop().getName(), WILSON_CONFIDENCE)
                        .groupings(
                                new GroupingDefinition<>("boss", BossDropDataSet::lowerCaseBossName, true),
                                new GroupingDefinition<>("league", BossDropDataSet::getLeague)
                        )
                        .conditionAll(t -> t.getGuaranteedDrop() != null)
                        .build(),
                Question.ask("extra_bossunique", clazz)
                        .evaluator(t -> t.getExtraDrops().stream()
                                        .filter(l -> l.getType() == LootType.BOSS_UNIQUE_ITEM)
                                        .map(Loot::getName)
                                        .collect(Collectors.joining(" & ")),
                                WILSON_CONFIDENCE)
                        .groupings(
                                new GroupingDefinition<>("boss", BossDropDataSet::lowerCaseBossName, true),
                                new GroupingDefinition<>("league", BossDropDataSet::getLeague)
                        )
                        .build(),
                Question.ask("parse", clazz)
                        .evaluator(t -> {
                            List<String> strings = new ArrayList<>();
                            if (t.getGuaranteedDrop() != null) {
                                strings.add(t.getGuaranteedDrop().getName());
                            }
                            for (Loot l : t.getExtraDrops()) {
                                if (l instanceof StackableLoot sl) strings.add(sl.getStackSize() + " * " + sl.getName());
                                else strings.add(l.getName() + " (" + l.getType() + ")");
                            }
                            return strings.stream().collect(Collectors.joining("; "));
                        }, l -> {
                            l.forEach(s -> IO.println(s));
                            IO.println("---------");
                        })
                        .groupings(
                                new GroupingDefinition<>("league", BossDropDataSet::getLeague, true),
                                new GroupingDefinition<>("boss", BossDropDataSet::lowerCaseBossName, true)
                        )
                        .build()
        );
    }

    @Override
    protected BossDropDataSet parseJson(String json) {
        com.company.datasets.datasets.BossDropDataSet old;
        try {
            old = Utils.parseJson(json, com.company.datasets.datasets.BossDropDataSet.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return new BossDropDataSet(
                old.getStrategy(),
                old.getBossName(),
                old.isUber(),
                old.isWitnessed(),
                old.getGuaranteedDrop(),
                old.getExtraDrops(),
                old.getQuantity()
        );
    }
}
