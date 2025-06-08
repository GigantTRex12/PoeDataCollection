package main.com.company.datasets.loot;

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
public class StackableLoot extends Loot {
    @JsonProperty("stacksize")
    private final int stackSize;

    public StackableLoot(String name, LootType type, int stackSize) {
        super(name, type);
        this.stackSize = stackSize;
    }
}
