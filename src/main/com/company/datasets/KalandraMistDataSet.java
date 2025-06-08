package main.com.company.datasets;

import main.com.company.datasets.loot.LootType;
import main.com.company.datasets.metadata.Strategy;
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
    private final byte amountPositive;
    @JsonProperty("amountNegative")
    private final byte amountNegative;
    @JsonProperty("amountNeutral")
    private final byte amountNeutral;
    @JsonProperty("itemText")
    private final String itemText;
    @JsonProperty("itemType")
    private final LootType itemType;
    @JsonProperty("multiplier")
    private final String multiplier;

    @JsonInclude(NON_NULL)
    @JsonProperty("inMap")
    private final Boolean inMap; //depcrecated, only left for downwards compatibility

    public KalandraMistDataSet(Strategy strat, MistType type, Integer lakeTier, byte amountPositive, byte amountNegative, byte amountNeutral, String itemText, LootType itemType, String multiplier) {
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
    public KalandraMistDataSet(boolean inMap, byte amountPositive, byte amountNegative, byte amountNeutral, String itemText, LootType itemType) {
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
