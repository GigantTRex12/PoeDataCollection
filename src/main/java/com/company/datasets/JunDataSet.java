package com.company.datasets;

import com.company.datasets.loot.Loot;
import com.company.datasets.metadata.Strategy;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@NoArgsConstructor(force = true)
@Getter
@ToString(callSuper = true)
public abstract class JunDataSet extends DataSet {
    private final Integer encountersSinceLastSet;
    private final int leagueId;
    private final int encounterId;
    private final Board boardBefore;
    private final Board boardAfter;

    public JunDataSet(Strategy strategy, Integer encountersSinceLastSet, int leagueId, int encounterId, Board boardBefore, Board boardAfter) {
        super(strategy);
        this.encountersSinceLastSet = encountersSinceLastSet;
        this.leagueId = leagueId;
        this.encounterId = encounterId;
        this.boardBefore = boardBefore;
        this.boardAfter = boardAfter;
    }

    @NoArgsConstructor(force = true)
    @AllArgsConstructor
    @Getter
    @ToString(callSuper = true)
    public static class Board {
        @JsonProperty("safehouseMembers")
        private final Map<Safehouse, List<Member>> safehouseMembers;
        @JsonProperty("memberRanks")
        private final Map<Member, Integer> memberRanks;
        @JsonProperty("imprisonedMembers")
        private final List<Member> imprisonedMembers;
        @JsonProperty("safehouseIntelligence")
        private final Map<Safehouse, Integer> safehouseIntelligence;
    }

    public enum Safehouse {
        RESEARCH, INTERVENTION, FORTIFICATION, TRANSPORTATION, MASTERMIND, NONE
    }

    public enum Member {
        AISLING, CAMERIA, ELREON, GRAVICIUS, GUFF, HAKU, HILLOCK, IT_THAT_FLED, JANUS, KORELL, LEO, RIKER, RIN, JORGIN,
        TORA, VAGAN, VORICI, CATARINA, UNKNOWN
    }
}

@NoArgsConstructor(force = true)
@Getter
@ToString(callSuper = true)
class JunEncounterDataSet extends JunDataSet {
    private final LinkedHashMap<Safehouse, List<Action>> encounters;

    public JunEncounterDataSet(Strategy strategy, Integer encountersSinceLastSet, int leagueId, int encounterId, Board boardBefore, Board boardAfter, LinkedHashMap<Safehouse, List<Action>> encounters) {
        super(strategy, encountersSinceLastSet, leagueId, encounterId, boardBefore, boardAfter);
        this.encounters = encounters;
    }

    @NoArgsConstructor(force = true)
    @AllArgsConstructor
    @Getter
    @ToString(callSuper = true)
    public static class Action {
        @JsonProperty("options")
        private final Map<Member, ActionType> options;
        @JsonProperty("pickedMember")
        private final Member pickedMember;
        @JsonProperty("pickedAction")
        private final ActionType pickedAction;
        @JsonProperty("loot")
        private final List<Loot> loot;
    }

    public enum ActionType {
        INTERROGATE,
        EXECUTE,
        BETRAY,
        BARGAIN,
        RELEASE
    }
}

@NoArgsConstructor(force = true)
@Getter
@ToString(callSuper = true)
class SafehouseEncounterDataSet extends JunDataSet {
    @JsonProperty("safehouse")
    private final Safehouse safehouse;
    @JsonProperty("loot")
    private final Map<Member, List<Loot>> loot;
    @JsonProperty("areaLevel")
    private final Integer areaLevel;

    public SafehouseEncounterDataSet(Strategy strategy, Integer encountersSinceLastSet, int leagueId, int encounterId, Board boardBefore, Board boardAfter, Safehouse safehouse, Map<Member, List<Loot>> loot, Integer areaLevel) {
        super(strategy, encountersSinceLastSet, leagueId, encounterId, boardBefore, boardAfter);
        this.safehouse = safehouse;
        this.loot = loot;
        this.areaLevel = areaLevel;
    }
}