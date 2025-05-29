package com.company.DataTypes.Loot;

import com.company.DataTypes.LootType;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString(callSuper = true)
@JsonDeserialize(using = JsonDeserializer.None.class)
public class MapLoot extends Loot {
    @JsonProperty("tier")
    protected final int tier;
    @JsonProperty("layout")
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
}
