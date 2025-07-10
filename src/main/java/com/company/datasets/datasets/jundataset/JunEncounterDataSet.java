package com.company.datasets.datasets.jundataset;

import com.company.datasets.annotations.InputProperty;
import com.company.datasets.builder.JunDataSetBuilderInterface;
import com.company.datasets.other.jun.Board;
import com.company.datasets.other.jun.Encounter;
import com.company.datasets.other.metadata.Strategy;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(force = true)
@Getter
@ToString(callSuper = true)
@JsonDeserialize()
public class JunEncounterDataSet extends JunDataSet {

    @JsonProperty("encounter1")
    @InputProperty(message = "Input the first encounter", regex = "\\w+\\: \\w+(\\([0-3]?b?f?\\))?(;\\w+(\\([0-3]?b?f?\\))?)*",
            order = 0, parsingFunc = "toEncounter", cleanUpFunc = "cleanupEncounter")
    private final Encounter encounter1;

    @JsonProperty("encounter2")
    @InputProperty(message = "Input the second encounter", regex = "^$|\\w+\\: \\w+(\\([0-3]?b?f?\\))?(;\\w+(\\([0-3]?b?f?\\))?)*",
            order = 1, parsingFunc = "toEncounter", emptyToNull = true, cleanUpFunc = "cleanupEncounter")
    private final Encounter encounter2;

    @JsonProperty("encounter3")
    @InputProperty(message = "Input the second encounter", regex = "^$|\\w+\\: \\w+(\\([0-3]?b?f?\\))?(;\\w+(\\([0-3]?b?f?\\))?)*",
            order = 1, parsingFunc = "toEncounter", emptyToNull = true, checkCondition = "secondNull", cleanUpFunc = "cleanupEncounter")
    private final Encounter encounter3;

    @Builder
    public JunEncounterDataSet(Strategy strategy, LastEncountered encountersSinceLastSet, String leagueId, int encounterId, Board boardBefore, Board boardAfter, Encounter encounter1, Encounter encounter2, Encounter encounter3) {
        super(strategy, encountersSinceLastSet, leagueId, encounterId, boardBefore, boardAfter);
        this.encounter1 = encounter1;
        this.encounter2 = encounter2;
        this.encounter3 = encounter3;
    }

    public static class JunEncounterDataSetBuilder implements JunDataSetBuilderInterface<JunEncounterDataSet> {
        public List<Encounter> getEncounters() {
            List<Encounter> encounterList = new ArrayList<>();

            if (encounter1 != null) {
                encounterList.add(encounter1);
            }
            if (encounter2 != null) {
                encounterList.add(encounter2);
            }
            if (encounter3 != null) {
                encounterList.add(encounter3);
            }

            return encounterList;
        }

        public boolean secondNull() {
            return encounter2 != null;
        }
    }

}
