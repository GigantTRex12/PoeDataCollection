package com.company.datasets.builder;

import com.company.datasets.datasets.jundataset.JunDataSet;
import com.company.datasets.other.jun.Board;

public interface JunDataSetBuilderInterface<T extends JunDataSet> extends DataSetBuilderInterface<JunDataSet> {

    JunDataSetBuilderInterface<T> encountersSinceLastSet(JunDataSet.LastEncountered encountersSinceLastSet);

    JunDataSetBuilderInterface<T> leagueId(String leagueId);

    JunDataSetBuilderInterface<T> encounterId(int encounterId);

    JunDataSetBuilderInterface<T> boardBefore(Board boardBefore);

    JunDataSetBuilderInterface<T> boardAfter(Board boardAfter);

}
