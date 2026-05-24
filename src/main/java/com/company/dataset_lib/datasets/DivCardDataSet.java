package com.company.dataset_lib.datasets;

import com.company.dataset_lib.DataSet;
import com.company.dataset_lib.Strategy;
import com.company.datasets.other.loot.Loot;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString(callSuper = true)
public class DivCardDataSet extends DataSet {

    private final String cardName;
    private final Loot result;
    private final Integer characterLevel;

    public DivCardDataSet(Strategy metadata, String cardName, Loot result, Integer characterLevel) {
        super(metadata);
        this.cardName = cardName;
        this.result = result;
        this.characterLevel = characterLevel;
    }

}
