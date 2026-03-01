package com.company.datasets.datasets;

import com.company.datasets.annotations.InputProperty;
import com.company.datasets.builder.DataSetBuilderInterface;
import com.company.datasets.other.metadata.Strategy;
import com.company.datasets.other.UniqueAndGoldCostPair;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(force = true)
@Getter
@ToString(callSuper = true)
public class CadiroDataSet extends DataSet {

    @JsonProperty("tier")
    @InputProperty(order = 1, parsingFunc = "toInt", regex = "^[1-3]$", message = "What is the tier?")
    private final Integer tier;

    @JsonProperty("uniquesWithCost")
    @InputProperty(order = 2, parsingFunc = "toUniqueCostPairs", multiline = true, regex = "^.* \\d+$", message = "Enter the prices.")
    private final List<UniqueAndGoldCostPair> uniquesWithCost;

    @Builder
    public CadiroDataSet(Strategy strategy, Integer tier, List<UniqueAndGoldCostPair> uniquesWithCost) {
        super(strategy);
        this.tier = tier;
        this.uniquesWithCost = uniquesWithCost;
    }

    public static class CadiroDataSetBuilder implements DataSetBuilderInterface<CadiroDataSet> {

        public CadiroDataSetBuilder() {
            uniquesWithCost = new ArrayList<>();
        }

        public CadiroDataSetBuilder uniqueAndCost(String unique, int cost) {
            uniquesWithCost.add(new UniqueAndGoldCostPair(unique, cost));
            return this;
        }

    }

}
