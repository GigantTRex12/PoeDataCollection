package main.com.company.datasets.loot;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString(callSuper = true)
@JsonDeserialize(using = JsonDeserializer.None.class)
public class GemLoot extends Loot {
    @JsonProperty("level")
    private final int level;
    @JsonProperty("quality")
    private final int quality;

    public GemLoot() {
        super();
        this.level = 1;
        this.quality = 0;
    }

    public GemLoot(String name, LootType type) {
        super(name, type);
        this.level = 1;
        this.quality = 0;
    }

    public GemLoot(String name, LootType type, int level, int quality) {
        super(name, type);
        this.level = level;
        this.quality = quality;
    }
}
