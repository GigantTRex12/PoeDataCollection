package com.company.dataset_lib.datasets;

import com.company.dataset_lib.DataSet;
import com.company.dataset_lib.Strategy;
import com.company.datasets.annotations.Legacy;
import com.company.datasets.other.loot.LootType;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.*;

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
        ELDER(LootType.ELDER_MAP.name()),
        SHAPER(LootType.SHAPER_MAP.name()),
        SYNTH(LootType.SYNTH_MAP.name()),
        CONQUEROR(LootType.CONQUEROR_MAP.name()),
        UNIQUE(LootType.UNIQUE_MAP.name()),
        REGULAR(LootType.MAP.name()),
        NIGHTMARE(LootType.T17_MAP.name()),
        ORIGINATOR(LootType.ORIGINATOR_MAP.name()),
        NON_GUARDIAN_ELDER(LootType.NON_GUARDIAN_ELDER_MAP.name()),
        NON_GUARDIAN_SHAPER(LootType.NON_GUARDIAN_SHAPER_MAP.name()),
        ORIGINATOR_ELDER(LootType.ORIGINATOR_ELDER_MAP.name()),
        ORIGINATOR_SHAPER(LootType.ORIGINATOR_SHAPER_MAP.name()),
        ORIGINATOR_CONQUEROR,
        ORIGINATOR_NON_GUARDIAN_ELDER(LootType.ORIGINATOR_NON_GUARDIAN_ELDER_MAP.name()),
        ORIGINATOR_NON_GUARDIAN_SHAPER(LootType.ORIGINATOR_NON_GUARDIAN_SHAPER_MAP.name())
        ;

        private static final Map<String, MapType> ALIAS_MAP = new HashMap<>();

        MapType(String... aliases) {
            for (String alias : aliases) {
                putAlias(alias, this);
            }
        }

        private static void putAlias(String alias, MapType type) {
            ALIAS_MAP.put(alias, type);
        }

        public static MapType fromString(String string) {
            MapType type = ALIAS_MAP.get(string);
            return type == null ? MapType.valueOf(string) : type;
        }

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
