package com.company.dataset_lib.datasets;

import com.company.dataset_lib.DataSet;
import com.company.dataset_lib.Strategy;
import com.company.dataset_lib.other.UniqueAndGoldCostPair;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@ToString(callSuper = true)
public class CadiroDataSet extends DataSet {

    private final Integer tier;
    private final List<UniqueAndGoldCostPair> uniquesWithCost;

    @Builder
    public CadiroDataSet(Strategy metadata, Integer tier, List<UniqueAndGoldCostPair> uniquesWithCost) {
        super(metadata);
        this.tier = tier;
        this.uniquesWithCost = uniquesWithCost;
    }

    public static class CadiroDataSetBuilder {

        public CadiroDataSetBuilder() {
            uniquesWithCost = new ArrayList<>();
        }

        public CadiroDataSetBuilder uniqueAndCost(String unique, int cost) {
            uniquesWithCost.add(new UniqueAndGoldCostPair(unique, cost));
            return this;
        }

    }

}
