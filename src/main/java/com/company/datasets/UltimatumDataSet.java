package com.company.datasets;

import com.company.datasets.loot.Loot;
import com.company.datasets.metadata.Strategy;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@NoArgsConstructor(force = true)
@Getter
@ToString(callSuper = true)
public class UltimatumDataSet extends DataSet {
    @JsonProperty("rewardList")
    private final List<Loot> rewards;
    @JsonProperty("boss")
    private final boolean boss;
    @JsonProperty("bossLoot")
    private final List<Loot> bossLoot;

    public UltimatumDataSet(Strategy strategy, List<Loot> rewards) {
        super(strategy);
        this.rewards = rewards;
        this.boss = false;
        this.bossLoot = null;
    }

    public UltimatumDataSet(Strategy strategy, List<Loot> rewards, List<Loot> bossLoot) {
        super(strategy);
        this.rewards = rewards;
        this.boss = true;
        this.bossLoot = bossLoot;
    }

    public UltimatumDataSet(Strategy strategy, List<Loot> rewards, boolean boss, List<Loot> bossLoot) {
        super(strategy);
        this.rewards = rewards;
        this.boss = boss;
        this.bossLoot = bossLoot;
    }
}
