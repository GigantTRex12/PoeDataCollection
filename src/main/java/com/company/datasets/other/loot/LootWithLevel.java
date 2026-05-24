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
public class LootWithLevel extends Loot {
    @JsonProperty("level")
    private final int level;

    public LootWithLevel(String name, LootType type, int level) {
        super(name, type);
        this.level = level;
    }
}
