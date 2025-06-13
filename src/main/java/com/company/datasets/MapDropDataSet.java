package main.com.company.datasets;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import main.com.company.datasets.loot.LootType;
import main.com.company.datasets.metadata.Strategy;

import java.util.*;

@NoArgsConstructor(force = true)
@Getter
@ToString(callSuper = true)
public class MapDropDataSet extends DataSet {
    private static Map<Character, LootType> lootTypesMap;
    private static boolean mapInitialized = false;

    @JsonProperty("conversionChance")
    private final int conversionChance;
    @JsonProperty("conversionType")
    private final LootType conversionType;
    @JsonProperty("mapsInOrder")
    private final List<LootType> mapsInOrder;
    @JsonProperty("bossDrops")
    private final Collection<LootType> bossDrops;

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
        }
        else {
            this.bossDrops = new ArrayList<>();
            for (char c : bossDrops) {
                this.bossDrops.add(lootTypesMap.get(c));
            }
        }
    }

    private static void initializeMap() {
        if (mapInitialized) {
            return;
        }
        lootTypesMap = new HashMap<>();
        lootTypesMap.put('e', LootType.ELDER_MAP);
        lootTypesMap.put('s', LootType.SHAPER_MAP);
        lootTypesMap.put('y', LootType.SYNTH_MAP);
        lootTypesMap.put('c', LootType.CONQUEROR_MAP);
        lootTypesMap.put('u', LootType.UNIQUE_MAP);
        lootTypesMap.put('r', LootType.MAP);
        lootTypesMap.put('t', LootType.T17_MAP);
        mapInitialized = true;
    }
}
