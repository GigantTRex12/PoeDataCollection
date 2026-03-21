package com.company.dataset_lib.datasets;

import com.company.dataset_lib.DataSet;
import com.company.dataset_lib.Strategy;
import com.company.datasets.other.loot.LootType;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString(callSuper = true)
public class KalandraMistDataSet extends DataSet {

    private final MistType type;
    private final Integer tier;
    private final int amountPositive;
    private final int amountNegative;
    private final int amountNeutral;
    private final String itemText;
    private final LootType itemType;
    private final String multiplier;
    private final Boolean inMap;

    @Builder
    public KalandraMistDataSet(Strategy metadata, MistType type, Integer tier, int amountPositive, int amountNegative, int amountNeutral, String itemText, LootType itemType, String multiplier, Boolean inMap) {
        super(metadata);
        this.type = type;
        this.tier = tier;
        this.amountPositive = amountPositive;
        this.amountNegative = amountNegative;
        this.amountNeutral = amountNeutral;
        this.itemText = itemText;
        this.itemType = itemType;
        this.multiplier = multiplier;
        this.inMap = inMap;
    }

    public int totalMods() {
        return amountPositive + amountNegative + amountNeutral;
    }

    public int amountPositives() {
        return Math.max(amountPositive, amountNegative);
    }

    public enum MistType {
        IN_MAP, ITEMIZED, LAKE, ITEMIZED_GUFF
    }

}
