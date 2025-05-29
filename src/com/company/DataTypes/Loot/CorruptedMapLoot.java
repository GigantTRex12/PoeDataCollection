package com.company.DataTypes.Loot;

import com.company.DataTypes.LootType;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor(force = true)
@Getter
@ToString(callSuper = true)
@JsonDeserialize(using = JsonDeserializer.None.class)
public class CorruptedMapLoot extends MapLoot {
    @JsonProperty("implicitAmount")
    private final int implicitAmount;

    public CorruptedMapLoot(String name, LootType type, int tier, String layout, int implicitAmount) {
        super(name, type, tier, layout);
        this.implicitAmount = implicitAmount;
    }
}
