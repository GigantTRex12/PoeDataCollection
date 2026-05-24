package com.company.datasets.other.loot;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor(force = true)
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@JsonDeserialize()
public class CraftingBenchLoot extends Loot {
    @JsonProperty("description")
    private final String description;

    public CraftingBenchLoot(String name, LootType type, String description) {
        super(name, type);
        this.description = description;
    }
}
