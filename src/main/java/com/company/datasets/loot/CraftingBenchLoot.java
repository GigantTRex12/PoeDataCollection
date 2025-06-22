package com.company.datasets.loot;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor(force = true)
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode
@JsonDeserialize(using = JsonDeserializer.None.class)
public class CraftingBenchLoot extends Loot {
    @JsonProperty("description")
    private final String description;

    public CraftingBenchLoot(String name, LootType type, String description) {
        super(name, type);
        this.description = description;
    }
}
