package com.company.dataset_lib;

import dataset.Metadata;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class Strategy extends Metadata {

    private Integer id;
    private final String league;
    private final String tree;
    private final String treeUrl;
    private final String[] scarabs;
    private final String mapLayout;
    private final String mapRolling;
    private final String mapCraft;

    public static class StrategyBuilder {

        private List<String> scarabs;

        public StrategyBuilder() {}

        public StrategyBuilder(Integer id) {
            this.id = id;
        }

        public StrategyBuilder zeroScarabs() {
            this.scarabs = new ArrayList<>();
            return this;
        }

        public StrategyBuilder scarab(String scarab, int amount) {
            if (this.scarabs == null) this.scarabs = new ArrayList<>();
            for (int i = 0; i < amount; i++) this.scarabs.add(scarab);
            return this;
        }

        public Strategy build() {
            return new Strategy(
                    this.id,
                    this.league,
                    this.tree,
                    this.treeUrl,
                    this.scarabs == null ? null : this.scarabs.toArray(new String[0]),
                    this.mapLayout,
                    this.mapRolling,
                    this.mapCraft
            );
        }

    }

}
