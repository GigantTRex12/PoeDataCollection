package com.company.dataset_lib.datasets;

import com.company.dataset_lib.DataSet;
import com.company.dataset_lib.Strategy;
import com.company.datasets.other.loot.Loot;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString(callSuper = true)
public class BossDropDataSet extends DataSet {

    private final String bossName;
    private final boolean uber;
    private final boolean witnessed;
    private final Loot guaranteedDrop;
    private final List<Loot> extraDrops;
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

    public String lowerCaseBossname() {
        if (uber) return "UBER " + bossName.toLowerCase();
        return bossName.toLowerCase();
    }

    public Integer quantInStepsOfTen() {
        return quantity != null ? (quantity / 10) * 10 : null;
    }

}
