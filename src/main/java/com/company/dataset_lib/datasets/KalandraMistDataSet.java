package com.company.dataset_lib.datasets;

import com.company.dataset_lib.DataSet;
import com.company.dataset_lib.Strategy;
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
    private final ItemType itemType;
    private final String multiplier;

    @Builder
    public KalandraMistDataSet(Strategy strategy, MistType type, Integer tier, int amountPositive, int amountNegative, int amountNeutral, String itemText, ItemType itemType, String multiplier) {
        super(strategy);
        this.type = type;
        this.tier = tier;
        this.amountPositive = amountPositive;
        this.amountNegative = amountNegative;
        this.amountNeutral = amountNeutral;
        this.itemText = itemText;
        this.itemType = itemType;
        this.multiplier = multiplier;
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

    public enum ItemType {
        RING, AMULET
    }

}
