package com.company.datasets.datasets;

import com.company.datasets.other.metadata.Strategy;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public abstract class DataSet {

    @JsonProperty("strategy")
    private final Strategy strategy;

    public DataSet() {
        strategy = new Strategy(null, null, null, null, null, null, null);
    }

    public DataSet(Strategy strategy) {
        this.strategy = strategy.copyWithoutId();
    }

}