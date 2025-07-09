package com.company.datasets.datasets.jundataset;

import com.company.datasets.datasets.DataSet;
import com.company.datasets.other.jun.Board;
import com.company.datasets.other.metadata.Strategy;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor(force = true)
@Getter
@ToString(callSuper = true)
public abstract class JunDataSet extends DataSet {

    @JsonProperty("encountersSinceLastSet")
    private final LastEncountered encountersSinceLastSet;

    @JsonProperty("leagueId")
    private final String leagueId;

    @JsonProperty("encounterId")
    private final int encounterId;

    @JsonProperty("boardBefore")
    private final Board boardBefore;

    @JsonProperty("boardAfter")
    private final Board boardAfter;

    public JunDataSet(Strategy strategy, LastEncountered encountersSinceLastSet, String leagueId, int encounterId, Board boardBefore, Board boardAfter) {
        super(strategy);
        this.encountersSinceLastSet = encountersSinceLastSet;
        this.leagueId = leagueId;
        this.encounterId = encounterId;
        this.boardBefore = boardBefore;
        this.boardAfter = boardAfter;
    }

    public enum LastEncountered {
        NOTHING_IN_BETWEEN, LOGOUT_IN_BETWEEN, MAPS_IN_BETWEEN, ENCOUNTERS_IN_BETWEEN_UNKNOWN, ENCOUNTERS_IN_BETWEEN_KNOWN
    }

}

