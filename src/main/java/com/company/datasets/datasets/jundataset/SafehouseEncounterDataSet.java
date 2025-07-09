package com.company.datasets.datasets.jundataset;

import com.company.datasets.annotations.InputProperty;
import com.company.datasets.builder.JunDataSetBuilderInterface;
import com.company.datasets.other.jun.Board;
import com.company.datasets.other.jun.Member;
import com.company.datasets.other.jun.Safehouse;
import com.company.datasets.other.loot.Loot;
import com.company.datasets.other.metadata.Strategy;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@NoArgsConstructor(force = true)
@Getter
@ToString(callSuper = true)
public class SafehouseEncounterDataSet extends JunDataSet {

    @JsonProperty("safehouse")
    @InputProperty(message = "Which safehouse is it?", options = {"r", "i", "t", "f"},
            options2 = {"research", "intervention", "transportation", "fortification"},
            order = 0)
    private final Safehouse.SafehouseType safehouse;

    @JsonProperty("leader")
    @InputProperty(message = "Who is the leader?", order = 1, parsingFunc = "toMember")
    private final Member.MemberName leader;

    @JsonProperty("loot")
    @InputProperty(message = "Input relevant Loot from the Leader", order = 2, parsingFunc = "toLootList", multiline = true)
    private final List<Loot> leaderLoot;

    @Builder
    public SafehouseEncounterDataSet(Strategy strategy, LastEncountered encountersSinceLastSet, String leagueId, int encounterId, Board boardBefore, Board boardAfter, Safehouse.SafehouseType safehouse, Member.MemberName leader, List<Loot> leaderLoot) {
        super(strategy, encountersSinceLastSet, leagueId, encounterId, boardBefore, boardAfter);
        this.safehouse = safehouse;
        this.leader = leader;
        this.leaderLoot = leaderLoot;
    }

    public static class SafehouseEncounterDataSetBuilder implements JunDataSetBuilderInterface<SafehouseEncounterDataSet> {}

}
