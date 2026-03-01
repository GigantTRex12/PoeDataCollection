package com.company.datasets.datasets;

import com.company.datasets.annotations.InputProperty;
import com.company.datasets.builder.DataSetBuilderInterface;
import com.company.datasets.other.loot.LootType;
import com.company.datasets.other.metadata.Strategy;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.*;

@NoArgsConstructor(force = true)
@Getter
@ToString(callSuper = true)
public class MapDropDataSet extends DataSet {
    private static Map<String, LootType> lootTypesMap;
    private static boolean mapInitialized = false;

    @JsonProperty("conversionChance")
    @InputProperty(order = 0, groupOrder = 1, parsingFunc = "toConversionChance")
    private final int conversionChance;

    @JsonProperty("conversionType")
    @InputProperty(message = "Enter the chance for converting maps from Influencing Scarab of conversion and conversion type.\ns:shaper, e:elder, c:conqueror, y:synthesis",
            regex = "^\\s*$|^[1-9]\\d*[secy]$", order = 0, parsingFunc = "toMapConversionType")
    private final LootType conversionType;

    @JsonProperty("mapsInOrder")
    @InputProperty(message = "Enter maps dropped.\nr:regular, s:shaper, e:elder, c:conqueror, y:synthesis, t:t17, u:unique, o: originator",
            regex = "^$|^[rsecytuo\\-]*$", order = 1, parsingFunc = "toMapDropList")
    private final List<LootType> mapsInOrder;

    @JsonProperty("bossDrops")
    @InputProperty(message = "Enter maps dropped by boss.\nEmpty for not killing boss, - for no drops",
            regex = "^-?$|^[rsecytuo,]*$", order = 2, parsingFunc = "toBossMapDropList")
    private final Collection<LootType> bossDrops;

    @Builder
    public MapDropDataSet(Strategy strategy, int conversionChance, LootType conversionType, List<LootType> mapsInOrder, Collection<LootType> bossDrops) {
        super(strategy);
        this.conversionChance = conversionChance;
        this.conversionType = conversionType;
        this.mapsInOrder = mapsInOrder;
        this.bossDrops = bossDrops;
    }

    public MapDropDataSet(Strategy strat, int conversionChance, char conversionType, char[] maps, char[] bossDrops) {
        super(strat);
        initializeMap();
        this.conversionChance = Math.max(conversionChance, 0);
        this.conversionType = lootTypesMap.get(conversionType);
        this.mapsInOrder = new ArrayList<>();
        for (char c : maps) {
            this.mapsInOrder.add(lootTypesMap.get(c));
        }
        if (bossDrops == null) {
            this.bossDrops = null;
        } else {
            this.bossDrops = new ArrayList<>();
            for (char c : bossDrops) {
                this.bossDrops.add(lootTypesMap.get(c));
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        MapDropDataSet that = (MapDropDataSet) o;
        if (bossDrops == null || that.bossDrops == null) {
            if (!(bossDrops == null && that.bossDrops == null)) return false;
        } else if (bossDrops.size() == that.bossDrops.size()) {
            List<LootType> copy = new ArrayList<>(that.bossDrops);
            for (LootType l : bossDrops) {
                if (!copy.remove(l)) return false;
            }
        } else return false;
        return conversionChance == that.conversionChance && conversionType == that.conversionType && Objects.equals(mapsInOrder, that.mapsInOrder);
    }

    @Override
    public int hashCode() {
        return 49 * super.hashCode()
                + 23 * conversionChance
                + 37 * (conversionType == null ? 41 : conversionType.hashCode())
                + 53 * mapsInOrder.hashCode()
                + 29 * (bossDrops.stream().mapToInt(LootType::hashCode).sum() + 5);
    }

    private static void initializeMap() {
        if (mapInitialized) {
            return;
        }
        lootTypesMap = new HashMap<>();
        lootTypesMap.put("e", LootType.ELDER_MAP);
        lootTypesMap.put("s", LootType.SHAPER_MAP);
        lootTypesMap.put("y", LootType.SYNTH_MAP);
        lootTypesMap.put("c", LootType.CONQUEROR_MAP);
        lootTypesMap.put("u", LootType.UNIQUE_MAP);
        lootTypesMap.put("r", LootType.MAP);
        lootTypesMap.put("t", LootType.T17_MAP);
        lootTypesMap.put("o", LootType.ORIGINATOR_MAP);
        lootTypesMap.put("eo", LootType.ORIGINATOR_ELDER_MAP);
        lootTypesMap.put("os", LootType.ORIGINATOR_SHAPER_MAP);
        lootTypesMap.put("er", LootType.NON_GUARDIAN_ELDER_MAP);
        lootTypesMap.put("rs", LootType.NON_GUARDIAN_SHAPER_MAP);
        lootTypesMap.put("ero", LootType.ORIGINATOR_NON_GUARDIAN_ELDER_MAP);
        lootTypesMap.put("ors", LootType.ORIGINATOR_NON_GUARDIAN_SHAPER_MAP);
        mapInitialized = true;
    }

    public static LootType getLootTypeFromMap(String string) {
        initializeMap();
        char[] ar = string.toCharArray();
        Arrays.sort(ar);
        String key = String.valueOf(ar);
        return lootTypesMap.get(key);
    }

    public static class MapDropDataSetBuilder implements DataSetBuilderInterface<MapDropDataSet> {

        public MapDropDataSetBuilder() {
            mapsInOrder = new ArrayList<>();
        }

        public MapDropDataSetBuilder mapDrop(LootType newMap) {
            mapsInOrder.add(newMap);
            return this;
        }

        public MapDropDataSetBuilder zeroBossDrops() {
            bossDrops = new ArrayList<>();
            return this;
        }

        public MapDropDataSetBuilder bossDrop(LootType bossDrop) {
            if (bossDrops == null) bossDrops = new ArrayList<>();
            bossDrops.add(bossDrop);
            return this;
        }
    }

}
