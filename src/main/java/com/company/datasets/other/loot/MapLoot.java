package com.company.datasets.other.loot;

import com.company.datasets.annotations.Legacy;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@JsonDeserialize()
public class MapLoot extends Loot {
    @JsonProperty("tier")
    protected final Integer tier;
    @JsonProperty("layout")
    @Legacy(legacySince = "3.28")
    protected final String layout;

    public MapLoot() {
        super();
        this.layout = null;
        this.tier = 16;
    }

    public MapLoot(String name, LootType type, int tier, String layout) {
        super(name, type);
        this.tier = tier;
        this.layout = layout;
    }

    public MapLoot(String name, LootType type, int tier) {
        super(name, type);
        this.tier = tier;
        this.layout = null;
    }

    public MapLoot(String name, LootType type) {
        super(name, type);
        this.tier = null;
        this.layout = null;
    }
}
