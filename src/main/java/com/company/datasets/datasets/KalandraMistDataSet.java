package com.company.datasets.datasets;

import com.company.datasets.datasets.DataSet;
import com.company.datasets.other.loot.LootType;
import com.company.datasets.other.metadata.Strategy;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.*;

@NoArgsConstructor(force = true)
@Getter
@ToString(callSuper = true)
public class KalandraMistDataSet extends DataSet {
    @JsonProperty("type")
    private final MistType type;
    @JsonProperty("lakeTier")
    private final Integer lakeTier;
    @JsonProperty("amountPositive")
    private final int amountPositive;
    @JsonProperty("amountNegative")
    private final int amountNegative;
    @JsonProperty("amountNeutral")
    private final int amountNeutral;
    @JsonProperty("itemText")
    private final String itemText;
    @JsonProperty("itemType")
    private final LootType itemType;
    @JsonProperty("multiplier")
    private final String multiplier;

    @JsonInclude(NON_NULL)
    @JsonProperty("inMap")
    @Deprecated
    private final Boolean inMap;

    public KalandraMistDataSet(Strategy strat, MistType type, Integer lakeTier, int amountPositive, int amountNegative, int amountNeutral, String itemText, LootType itemType, String multiplier) {
        super(strat);
        this.inMap = null;
        this.type = type;
        this.lakeTier = lakeTier;
        this.amountPositive = amountPositive;
        this.amountNegative = amountNegative;
        this.amountNeutral = amountNeutral;
        this.itemText = itemText;
        this.itemType = itemType;
        this.multiplier = multiplier;
    }

    public enum MistType {
        IN_MAP, ITEMIZED, LAKE
    }

    @Deprecated
    public KalandraMistDataSet(boolean inMap, int amountPositive, int amountNegative, int amountNeutral, String itemText, LootType itemType) {
        this.inMap = inMap;
        if (inMap) {
            this.type = MistType.IN_MAP;
        }
        else {
            this.type = MistType.ITEMIZED;
        }
        this.lakeTier = null;
        this.amountPositive = amountPositive;
        this.amountNegative = amountNegative;
        this.amountNeutral = amountNeutral;
        this.itemText = itemText;
        this.itemType = itemType;
        this.multiplier = null;
    }
}
