package com.company.datasets.datasets;

import com.company.datasets.annotations.Groupable;
import com.company.datasets.other.metadata.Strategy;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public abstract class DataSet {

    @JsonProperty("strategy")
    @Groupable(order = 1, filterByValue = true)
    private final Strategy strategy;

    public DataSet() {
        strategy = new Strategy(null, null, null, null, null, null, null, null);
    }

    public DataSet(Strategy strategy) {
        this.strategy = strategy.copyWithoutId();
    }

    @Groupable(order = 2, filterByValue = true, ignoreNulls = true)
    public String league() {
        return strategy.getLeague();
    }

}