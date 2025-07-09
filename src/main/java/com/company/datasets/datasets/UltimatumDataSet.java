package com.company.datasets.datasets;

import com.company.datasets.annotations.InputProperty;
import com.company.datasets.builder.DataSetBuilderInterface;
import com.company.datasets.datasets.DataSet;
import com.company.datasets.other.loot.Loot;
import com.company.datasets.other.metadata.Strategy;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@NoArgsConstructor(force = true)
@Getter
@ToString(callSuper = true)
public class UltimatumDataSet extends DataSet {

    @JsonProperty("rewardList")
    @InputProperty(message = "Enter rewards from Ultimatum in order. (one line per reward)\nEnter \"-\" to skip reward.",
            order = 0, parsingFunc = "toLootList", multiline = true)
    private final List<Loot> rewards;

    @JsonProperty("boss")
    @InputProperty(message = "Was a boss encountered?", options = {"y", "n"}, options2 = {"yes", "no"}, order = 1,
            parsingFunc = "toBool", checkCondition = "canBoss")
    private final boolean boss;

    @JsonProperty("bossLoot")
    @InputProperty(message = "Enter drops from boss.", order = 2, parsingFunc = "toLootList", multiline = true,
            checkCondition = "isBoss")
    private final List<Loot> bossLoot;

    public UltimatumDataSet(Strategy strategy, List<Loot> rewards) {
        super(strategy);
        this.rewards = rewards;
        this.boss = false;
        this.bossLoot = null;
    }

    @Builder
    public UltimatumDataSet(Strategy strategy, List<Loot> rewards, boolean boss, List<Loot> bossLoot) {
        super(strategy);
        this.rewards = rewards;
        this.boss = boss;
        this.bossLoot = bossLoot;
    }

    public static class UltimatumDataSetBuilder implements DataSetBuilderInterface<UltimatumDataSet> {
        public boolean canBoss() {
            return this.rewards.size() == 10;
        }

        public boolean isBoss() {
            return this.boss;
        }
    }
}
