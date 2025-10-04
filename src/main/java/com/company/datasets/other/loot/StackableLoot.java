package com.company.datasets.other.loot;

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
@EqualsAndHashCode(callSuper = true)
@JsonDeserialize()
public class StackableLoot extends Loot {
    @JsonProperty("stacksize")
    private final int stackSize;

    public StackableLoot(String name, LootType type, int stackSize) {
        super(name, type);
        this.stackSize = stackSize;
    }
}
