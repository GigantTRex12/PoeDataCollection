package com.company.dataset_lib.datasets;

import com.company.dataset_lib.DataSet;
import com.company.dataset_lib.Strategy;
import com.company.datasets.other.loot.LootType;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
@ToString(callSuper = true)
public class MapDropDataSet extends DataSet {

    private final int conversionChance;
    private final LootType conversionType;
    private final List<LootType> mapsInOrder;
    private final Collection<LootType> bossMapDrops;

    @Builder
    public MapDropDataSet(Strategy strategy, int conversionChance, LootType conversionType, List<LootType> mapsInOrder, Collection<LootType> bossMapDrops) {
        super(strategy);
        this.conversionChance = conversionChance;
        this.conversionType = conversionType;
        this.mapsInOrder = mapsInOrder;
        this.bossMapDrops = bossMapDrops;
    }

    public static class MapDropDataSetBuilder {

        public MapDropDataSetBuilder() {
            mapsInOrder = new ArrayList<>();
        }

        public MapDropDataSetBuilder mapDrop(LootType newMap) {
            mapsInOrder.add(newMap);
            return this;
        }

        public MapDropDataSetBuilder zeroBossDrops() {
            bossMapDrops = new ArrayList<>();
            return this;
        }

        public MapDropDataSetBuilder bossDrop(LootType bossDrop) {
            if (bossMapDrops == null) bossMapDrops = new ArrayList<>();
            bossMapDrops.add(bossDrop);
            return this;
        }
    }

}
