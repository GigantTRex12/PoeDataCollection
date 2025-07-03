package com.company.datasets.datasets;

import com.company.datasets.annotations.InputProperty;
import com.company.datasets.datasets.DataSet;
import com.company.datasets.other.loot.LootType;
import com.company.datasets.other.metadata.Strategy;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.*;

@NoArgsConstructor(force = true)
@Getter
@ToString(callSuper = true)
public class KalandraMistDataSet extends DataSet {
    @JsonProperty("type")
    @InputProperty(message = "What type of mist does this item come from?", regex = "^in map$|^itemized$|^lake \\d+$",
            order = 0, parsingFunc = "parseMistType")
    private final MistType type;

    @JsonProperty("lakeTier")
    @InputProperty(order = 0, groupOrder = 1, parsingFunc = "parseLakeTier")
    private final Integer lakeTier;

    @JsonProperty("amountPositive")
    @InputProperty(message = "Enter how many mods are positive, negative or neutral", regex = "^\\d+\\/\\d+(\\/\\d+)?$",
            order = 1, parsingFunc = "parseAmountPos")
    private final int amountPositive;

    @JsonProperty("amountNegative")
    @InputProperty(order = 1, groupOrder = 1, parsingFunc = "parseAmountNeg")
    private final int amountNegative;

    @JsonProperty("amountNeutral")
    @InputProperty(order = 1, groupOrder = 2, parsingFunc = "parseAmountNeut")
    private final int amountNeutral;

    @JsonProperty("itemText")
    @InputProperty(message = "Paste the item text", order = 4, multiline = true)
    private final String itemText;

    @JsonProperty("itemType")
    @InputProperty(message = "What type is the item?",
            options = {"a", "r"}, options2 = {"amulet", "ring"}, forceOptions = false,
            order = 3, parsingFunc = "parseToMistLoottype", emptyToNull = true)
    private final LootType itemType;

    @JsonProperty("multiplier")
    @InputProperty(message = "Enter the multiplier. Leave Empty to skip", regex = "^$|^\\d+\\.?\\d*$", order = 2,
            emptyToNull = true)
    private final String multiplier;

    @JsonInclude(NON_NULL)
    @JsonProperty("inMap")
    @Deprecated
    private final Boolean inMap;

    @Builder
    public KalandraMistDataSet(Strategy strategy, MistType type, Integer lakeTier, int amountPositive, int amountNegative, int amountNeutral, String itemText, LootType itemType, String multiplier) {
        super(strategy);
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
