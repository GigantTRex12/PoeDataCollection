package com.company.dataset_lib.datasets;

import com.company.dataset_lib.DataSet;
import com.company.dataset_lib.Strategy;
import com.company.datasets.other.loot.Loot;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@ToString(callSuper = true)
public class UltimatumDataSet extends DataSet {

    private final List<Loot> rewards;
    private final boolean boss;
    private final List<Loot> bossLoot;

    @Builder
    public UltimatumDataSet(Strategy metadata, List<Loot> rewards, boolean boss, List<Loot> bossLoot) {
        super(metadata);
        this.rewards = rewards;
        this.boss = boss;
        this.bossLoot = bossLoot;
    }

    public static class UltimatumDataSetBuilder {

        private Strategy strategy;
        private Map<Integer, Loot> waveToLoot;
        private int maxIndex;

        public UltimatumDataSetBuilder strategy(Strategy strategy) {
            this.strategy = strategy;
            return this;
        }

        public UltimatumDataSetBuilder boss() {
            boss = true;
            bossLoot = new ArrayList<>();
            return this;
        }

        public UltimatumDataSetBuilder boss(boolean boss) {
            this.boss = boss;
            return this;
        }

        public UltimatumDataSetBuilder bossDrop(Loot loot) {
            bossLoot.add(loot);
            return this;
        }

        public boolean isBoss() {
            return this.boss;
        }

        public UltimatumDataSetBuilder waveLoot(int wave, Loot loot) {
            if (waveToLoot == null) waveToLoot = new HashMap<>();
            waveToLoot.merge(wave, loot, (_, _) -> {
                throw new IllegalArgumentException("Duplicate key '" + wave + "'.");
            });
            if (wave > maxIndex) maxIndex = wave;
            return this;
        }

        public UltimatumDataSet build() {
            if (waveToLoot != null) {
                rewards = new ArrayList<>();
                for (int i = 1; i <= maxIndex; i++) {
                    rewards.add(waveToLoot.get(i));
                }
            }
            return new UltimatumDataSet(strategy, rewards, boss, bossLoot);
        }

    }

}
