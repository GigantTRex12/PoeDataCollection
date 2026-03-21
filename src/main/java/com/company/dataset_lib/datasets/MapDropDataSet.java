package com.company.dataset_lib.datasets;

import com.company.dataset_lib.DataSet;
import com.company.dataset_lib.Strategy;
import com.company.datasets.annotations.Legacy;
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

    @Legacy(legacySince = "3.28")
    private final int conversionChance;
    @Legacy(legacySince = "3.28")
    private final MapType conversionType;
    private final List<MapType> mapsInOrder;
    private final Collection<MapType> bossMapDrops;

    @Builder
    public MapDropDataSet(Strategy strategy, int conversionChance, MapType conversionType, List<MapType> mapsInOrder, Collection<MapType> bossMapDrops) {
        super(strategy);
        this.conversionChance = conversionChance;
        this.conversionType = conversionType;
        this.mapsInOrder = mapsInOrder;
        this.bossMapDrops = bossMapDrops;
    }

    public MapDropDataSet(Strategy metadata, List<MapType> mapsInOrder, Collection<MapType> bossMapDrops) {
        super(metadata);
        conversionChance = 0;
        conversionType = null;
        this.mapsInOrder = mapsInOrder;
        this.bossMapDrops = bossMapDrops;
    }

    public enum MapType {
        ELDER, SHAPER, SYNTH, CONQUEROR,
        UNIQUE, REGULAR, NIGHTMARE, ORIGINATOR,
        NON_GUARDIAN_ELDER, NON_GUARDIAN_SHAPER,
        ORIGINATOR_ELDER, ORIGINATOR_SHAPER, ORIGINATOR_CONQUEROR,
        ORIGINATOR_NON_GUARDIAN_ELDER, ORIGINATOR_NON_GUARDIAN_SHAPER
    }

    public static class MapDropDataSetBuilder {

        public MapDropDataSetBuilder() {
            mapsInOrder = new ArrayList<>();
        }

        public MapDropDataSetBuilder mapDrop(MapType newMap) {
            mapsInOrder.add(newMap);
            return this;
        }

        public MapDropDataSetBuilder zeroBossDrops() {
            bossMapDrops = new ArrayList<>();
            return this;
        }

        public MapDropDataSetBuilder bossDrop(MapType bossDrop) {
            if (bossMapDrops == null) bossMapDrops = new ArrayList<>();
            bossMapDrops.add(bossDrop);
            return this;
        }
    }

}
