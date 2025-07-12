package com.company.datasets.other.jun;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.HashSet;

import static com.company.datasets.other.jun.Member.MemberName.UNKNOWN;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_DEFAULT;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Getter
@Setter
public class Member {

    @JsonProperty("name")
    private MemberName name = UNKNOWN;

    @JsonProperty("rank")
    private Integer rank;

    @JsonProperty("prisonTurnsLeft")
    @JsonInclude(NON_DEFAULT)
    private int prisonTurnsLeft;

    @JsonProperty("prisonSafehouse")
    @JsonInclude(NON_NULL)
    private Safehouse.SafehouseType prisonSafehouse;

    @JsonProperty("intelligencePerTurn")
    @JsonInclude(NON_NULL)
    private Integer intelligencePerTurn;

    @JsonIgnore
    private final HashSet<MemberName> rivals;

    @JsonIgnore
    private final HashSet<MemberName> trusted;

    @JsonProperty("failed")
    @JsonInclude(NON_NULL)
    private final Boolean failed;

    @JsonIgnore
    private Safehouse.SafehouseType safehouse;

    @JsonIgnore
    private boolean leader;

    public Member() {
        this(0, false);
    }

    // to create deep copy of Board
    public Member(Member member) {
        name = member.name;
        rank = member.rank;
        prisonTurnsLeft = member.prisonTurnsLeft;
        prisonSafehouse = member.prisonSafehouse;
        intelligencePerTurn = member.intelligencePerTurn;
        rivals = new HashSet<>(member.getRivals());
        trusted = new HashSet<>(member.getTrusted());
        safehouse = member.safehouse;
        leader = member.leader;
        failed = null;
    }

    public Member(Integer rank, boolean leader) {
        this.rank = rank;
        prisonTurnsLeft = 0;
        prisonSafehouse = null;
        intelligencePerTurn = null;
        rivals = new HashSet<>();
        trusted = new HashSet<>();
        safehouse = null;
        this.leader = leader;
        failed = null;
    }

    public Member(MemberName name, Integer rank, boolean leader) {
        this.name = name;
        this.rank = rank;
        this.leader = leader;
        rivals = null;
        trusted = null;
        failed = null;
    }

    public Member(MemberName name, Integer rank, boolean leader, Boolean failed) {
        this.name = name;
        this.rank = rank;
        this.failed = failed;
        this.leader = leader;
        rivals = null;
        trusted = null;
    }

    public Member(MemberName name, Integer rank, Safehouse.SafehouseType safehouse, boolean leader) {
        this.name = name;
        this.rank = rank;
        this.safehouse = safehouse;
        this.leader = leader;
        rivals = null;
        trusted = null;
        failed = null;
    }

    @Override
    public String toString() {
        return name.name() + " (Rank: " + rank + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Member member = (Member) o;

        return name == member.name && name != UNKNOWN;
    }

    @Override
    public int hashCode() {
        if (name == UNKNOWN) {
            return (42 + rank) * (42 + safehouse.hashCode());
        }
        return name.hashCode();
    }

    public void decrementPrisonTurns() {
        this.prisonTurnsLeft--;
        if (prisonTurnsLeft < 0) {
            prisonSafehouse = null;
            intelligencePerTurn = null;
            rank--;
        }
    }

    public void addTrusted(Member member) {
        trusted.add(member.getName());
    }

    public void addRival(Member member) {
        rivals.add(member.getName());
        trusted.remove(member.getName());
    }

    public void removeRival(Member member) {
        rivals.remove(member.getName());
    }

    public void neutralize(Member member) {
        trusted.remove(member.getName());
        rivals.remove(member.getName());
    }

    public enum MemberName {
        AISLING, CAMERIA, ELREON, GRAVICIUS, GUFF, HAKU, HILLOCK, ITTHATFLED, JANUS, KORELL, LEO, RIKER, RIN, JORGIN,
        TORA, VAGAN, VORICI, CATARINA, UNKNOWN
    }

}
