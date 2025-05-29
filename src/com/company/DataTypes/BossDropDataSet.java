package com.company.DataTypes;

import com.company.DataTypes.Loot.Loot;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@NoArgsConstructor(force = true)
@Getter
@ToString(callSuper = true)
public class BossDropDataSet extends DataSet {
    private final String bossName;
    private final boolean uber;
    private final boolean pinnacle;
    private final Loot guaranteedDrop;
    private final List<Loot> extraDrops;

    public BossDropDataSet(Strategy strategy, String bossName, boolean uber, boolean pinnacle, Loot guaranteedDrop, List<Loot> extraDrops) {
        super(strategy);
        this.bossName = bossName;
        this.uber = uber;
        this.pinnacle = pinnacle;
        this.guaranteedDrop = guaranteedDrop;
        this.extraDrops = extraDrops;
    }
}
