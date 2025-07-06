package com.company.datasets.datasets;

import com.company.datasets.other.loot.Loot;
import com.company.datasets.other.metadata.Strategy;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.company.utils.IOUtils.input;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@NoArgsConstructor(force = true)
@Getter
@ToString(callSuper = true)
public abstract class JunDataSet extends DataSet {

    @JsonProperty("encountersSinceLastSet")
    private final LastEncountered encountersSinceLastSet;

    @JsonProperty("leagueId")
    private final int leagueId;

    @JsonProperty("leagueName")
    private final String leagueName;

    @JsonProperty("encounterId")
    private final int encounterId;

    @JsonProperty("boardBefore")
    private final Board boardBefore;

    @JsonProperty("boardAfter")
    private final Board boardAfter;

    public JunDataSet(Strategy strategy, LastEncountered encountersSinceLastSet, int leagueId, String leagueName, int encounterId, Board boardBefore, Board boardAfter) {
        super(strategy);
        this.encountersSinceLastSet = encountersSinceLastSet;
        this.leagueId = leagueId;
        this.leagueName = leagueName;
        this.encounterId = encounterId;
        this.boardBefore = boardBefore;
        this.boardAfter = boardAfter;
    }

    @NoArgsConstructor(force = true)
    @AllArgsConstructor
    @Getter
    @ToString(callSuper = true)
    @Builder
    public static class Board {

        @JsonProperty("safehouseMembers")
        private final Map<Safehouse, List<Member>> safehouseMembers;

        @JsonProperty("memberRanks")
        private final Map<Member, Integer> memberRanks;

        @JsonProperty("imprisonedMembers")
        private final Map<Member, Integer> imprisonedMembers;

        @JsonProperty("safehouseIntelligence")
        private final Map<Safehouse, Integer> safehouseIntelligence;

        @JsonProperty("relationships")
        private final List<Relationship> relationships;

        public BoardBuilder makeBuilder() {
            return Board.builder()
                    .safehouseMembers(safehouseMembers)
                    .memberRanks(memberRanks)
                    .imprisonedMembers(imprisonedMembers)
                    .safehouseIntelligence(safehouseIntelligence)
                    .relationships(relationships);
        }

        public static class BoardBuilder {

            @Setter
            private boolean junTree;

            public void member(Member member, int rank, Safehouse safehouse) {

            }

            public void actions(List<JunEncounterDataSet.Action> actions, Safehouse safehouse) {
                for (JunEncounterDataSet.Action action : actions) {
                    action(action.getDoneAction(), safehouse);
                }
            }

            private void action(JunEncounterDataSet.Action action, Safehouse safehouse) {
                switch (action.getType()) {
                    case INTERROGATE -> {
                        if (imprisonedMembers.size() >= 3) {
                            imprisonedMembers.remove(Member.valueOf(
                                    input("Who was released from prison after action " + action + "?",
                                            imprisonedMembers.keySet().stream()
                                                    .map(Enum::toString).collect(Collectors.toList()))
                                            .toUpperCase()
                            ));
                        }
                        imprisonedMembers.put(action.getMember(), 3);
                    }
                    case EXECUTE -> {
                        int oldRank = memberRanks.get(action.getMember());
                        if (oldRank == 0) {
                            safehouseMembers.get(safehouse).add(action.getMember());
                        }
                        int newRank = oldRank + 1;
                        if (junTree) {
                            newRank++;
                        }
                        if (newRank > 3) {
                            newRank = 3;
                        }
                        memberRanks.put(action.getMember(), newRank);
                    }
                    case BETRAY__INTELLIGENCE -> {
                        Safehouse safehouseTarget = List.of(Safehouse.RESEARCH, Safehouse.INTERVENTION, Safehouse.FORTIFICATION, Safehouse.TRANSPORTATION)
                                .stream()
                                .filter(sh -> safehouseMembers.get(sh).contains(action.getTarget()))
                                .findAny().get();
                        safehouseIntelligence.put(safehouseTarget, safehouseIntelligence.get(safehouseTarget) + action.getValue());
                        relationships = relationships.stream()
                                .map(r -> r.hasMembers(action.getMember(), action.getTarget()) ? r.toRival() : r)
                        .collect(Collectors.toList());
                    }
                    case BETRAY__RANKS -> {
                        
                    }
                }
            }

            public void prisonRound() {

            }

        }

        @NoArgsConstructor(force = true)
        @AllArgsConstructor
        @Getter
        @ToString(callSuper = true)
        public static class Relationship {

            @JsonProperty("member1")
            private final Member member1;

            @JsonProperty("member2")
            private final Member member2;

            @JsonProperty("type")
            private final RelationType type;

            public boolean hasMembers(Member member1, Member member2) {
                return ((this.member1 == member1 && this.member2 == member2) || (this.member2 == member1 && this.member1 == member2));
            }

            public Relationship toRival() {
                return new Relationship(member1, member2, RelationType.RIVALS);
            }

            public enum RelationType {
                TRUSTING, RIVALS
            }
        }

    }

    public enum LastEncountered {
        NOTHING_IN_BETWEEN, LOGOUT_IN_BETWEEN, MAPS_IN_BETWEEN, ENCOUNTERS_IN_BETWEEN_UNKNOWN, ENCOUNTERS_IN_BETWEEN_KNOWN
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

    private final LinkedHashMap<Safehouse, List<Action>> actions;

    public JunEncounterDataSet(Strategy strategy, LastEncountered encountersSinceLastSet, int leagueId, String leagueName, int encounterId, Board boardBefore, Board boardAfter, LinkedHashMap<Safehouse, List<Action>> actions) {
        super(strategy, encountersSinceLastSet, leagueId, leagueName, encounterId, boardBefore, boardAfter);
        this.actions = actions;
    }

    @NoArgsConstructor(force = true)
    @AllArgsConstructor
    @Getter
    @ToString(callSuper = true)
    public static class Action {

        @JsonProperty("member")
        private final Member member;

        @JsonProperty("type")
        private final ActionType type;

        @JsonProperty("value")
        private final Integer value;

        @JsonProperty("target")
        private final Member target;

        @JsonProperty("instead")
        @JsonInclude(NON_NULL)
        private final Action instead;

        public Action getDoneAction() {
            if (instead == null) {
                return this;
            }
            return instead.getDoneAction();
        }

        public enum ActionType {
            INTERROGATE,
            EXECUTE,
            BETRAY__INTELLIGENCE, BETRAY__RANKS, BETRAY__LEADER, BETRAY__REMOVE, BETRAY__DESTROY, BETRAY__RAISE_ALL,
            BARGAIN__INTELLIGENCE, BARGAIN__RECRUIT, BARGAIN__SWITCH, BARGAIN__LEAVE, BARGAIN__FREE_ALL,
            BARGAIN__DESTROY, BARGAIN__INTELLIGENCE_NEUTRAL, BARGAIN__REMOVE_RIVALRIES,
            BARGAIN__VEILED_ITEMS, BARGAIN__CURRENCY, BARGAIN__MAP, BARGAIN__UNIQUE, BARGAIN__SCARABS,
            RELEASE
        }

    }

}

@NoArgsConstructor(force = true)
@Getter
@ToString(callSuper = true)
class SafehouseEncounterDataSet extends JunDataSet {

    @JsonProperty("safehouse")
    private final Safehouse safehouse;

    @JsonProperty("leader")
    private final Member leader;

    @JsonProperty("loot")
    private final List<Loot> leaderLoot;

    @JsonProperty("areaLevel")
    private final Integer areaLevel;

    public SafehouseEncounterDataSet(Strategy strategy, LastEncountered encountersSinceLastSet, int leagueId, String leagueName, int encounterId, Board boardBefore, Board boardAfter, Safehouse safehouse, Member leader, List<Loot> leaderLoot, Integer areaLevel) {
        super(strategy, encountersSinceLastSet, leagueId, leagueName, encounterId, boardBefore, boardAfter);
        this.safehouse = safehouse;
        this.leader = leader;
        this.leaderLoot = leaderLoot;
        this.areaLevel = areaLevel;
    }
}