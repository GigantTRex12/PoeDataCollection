package com.company.datasets.datasetsnew;

import com.company.datasets.other.loot.Loot;
import com.company.datasets.other.metadata.Strategy;
import com.fasterxml.jackson.annotation.JsonProperty;
import dataset.BaseDataSet;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@NoArgsConstructor(force = true)
@Getter
@ToString(callSuper = true)
public class BossDropDataSet extends BaseDataSet {

    @JsonProperty("boss")
    private final String bossName;

    @JsonProperty("uber")
    private final boolean uber;

    @JsonProperty("witnessed")
    private final boolean witnessed;

    @JsonProperty("guaranteedDrop")
    private final Loot guaranteedDrop;

    @JsonProperty("extraDrops")
    private final List<Loot> extraDrops;

    @JsonProperty("quantity")
    private final Integer quantity;

    public BossDropDataSet(Strategy metadata, String bossName, boolean uber, boolean witnessed, Loot guaranteedDrop, List<Loot> extraDrops, Integer quantity) {
        super(metadata);
        this.bossName = bossName;
        this.uber = uber;
        this.witnessed = witnessed;
        this.guaranteedDrop = guaranteedDrop;
        this.extraDrops = extraDrops;
        this.quantity = quantity;
    }

    public String lowerCaseBossName() {
        return (isUber() ? "UBER" : "") + getBossName().toLowerCase();
    }

    public String getLeague() {
        return ((Strategy) (getMetadata())).getLeague();
    }

}
