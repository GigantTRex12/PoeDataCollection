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
@EqualsAndHashCode
@JsonDeserialize()
public class ImplicitCorruptedItem extends Loot {
    @JsonProperty("implicitAmount")
    private final int implicitAmount;

    public ImplicitCorruptedItem(String name, LootType type, int implicitAmount) {
        super(name, type);
        this.implicitAmount = implicitAmount;
    }
}
