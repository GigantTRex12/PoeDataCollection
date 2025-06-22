package com.company.datasets;

import com.company.datasets.annotations.InputProperty;
import com.company.datasets.loot.Loot;
import com.company.datasets.metadata.Strategy;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@NoArgsConstructor(force = true)
@Getter
@EqualsAndHashCode
@ToString(callSuper = true)
public class BossDropDataSet extends DataSet {
    @JsonProperty("boss")
    @InputProperty(message = "Enter the name of the boss.", order = 0)
    private final String bossName;
    @JsonProperty("uber")
    @InputProperty(message = "Is the boss uber?", options = {"y", "n"}, order = 1, parsingFunc = "toBool")
    private final boolean uber;
    @JsonProperty("pinnacle")
    @InputProperty(message = "Is the boss a pinnacle boss?", options = {"y", "n"}, order = 2, parsingFunc = "toBool")
    private final boolean pinnacle;
    @JsonProperty("guaranteedDrop")
    @InputProperty(message = "What was the guaranteed drop?", order = 3, parsingFunc = "parseToLoot")
    private final Loot guaranteedDrop;
    @JsonProperty("extraDrops")
    @InputProperty(message = "Input extra drops to track.", order = 4, parsingFunc = "toLootList", multiline = true)
    private final List<Loot> extraDrops;

    @Builder
    public BossDropDataSet(Strategy strategy, String bossName, boolean uber, boolean pinnacle, Loot guaranteedDrop, List<Loot> extraDrops) {
        super(strategy);
        this.bossName = bossName;
        this.uber = uber;
        this.pinnacle = pinnacle;
        this.guaranteedDrop = guaranteedDrop;
        this.extraDrops = extraDrops;
    }
}
