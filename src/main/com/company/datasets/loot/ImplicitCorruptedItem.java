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
public class ImplicitCorruptedItem extends Loot {
    @JsonProperty("implicitAmount")
    private final int implicitAmount;

    public ImplicitCorruptedItem(String name, LootType type, int implicitAmount) {
        super(name, type);
        this.implicitAmount = implicitAmount;
    }
}
