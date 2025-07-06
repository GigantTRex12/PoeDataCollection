package com.company.datasets.datasets;

import com.company.datasets.annotations.InputProperty;
import com.company.datasets.other.loot.Loot;
import com.company.datasets.other.metadata.Strategy;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

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

    @JsonProperty("quantity")
    @JsonInclude(NON_NULL)
    @InputProperty(message = "Enter the area quantity.", regex = "^$|^\\d+$", order = 5, parsingFunc = "toInt", emptyToNull = true)
    private final Integer quantity;

    @Builder
    public BossDropDataSet(Strategy strategy, String bossName, boolean uber, boolean pinnacle, Loot guaranteedDrop, List<Loot> extraDrops, Integer quantity) {
        super(strategy);
        this.bossName = bossName;
        this.uber = uber;
        this.pinnacle = pinnacle;
        this.guaranteedDrop = guaranteedDrop;
        this.extraDrops = extraDrops;
        this.quantity = quantity;
    }
}
