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
@ToString
public class Member {

    @JsonProperty("name")
    private MemberName name = UNKNOWN;

    @JsonProperty("rank")
    private int rank;

    @JsonProperty("prisonTurnsLeft")
    @JsonInclude(NON_DEFAULT)
    private int prisonTurnsLeft;

    @JsonProperty("prisonSafehouse")
    @JsonInclude(NON_NULL)
    private Safehouse.SafehouseType prisonSafehouse;

    @JsonProperty("intelligencePerTurn")
    @JsonInclude(NON_NULL)
    private Integer intelligencePerTurn;

    @JsonProperty("rivals")
    private final HashSet<Member> rivals;

    @JsonProperty("trusted")
    private final HashSet<Member> trusted;

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
        rivals = new HashSet<>();
        trusted = new HashSet<>();
        safehouse = member.safehouse;
        leader = member.leader;
    }

    public Member(int rank, boolean leader) {
        this.rank = rank;
        prisonTurnsLeft = 0;
        prisonSafehouse = null;
        intelligencePerTurn = null;
        rivals = new HashSet<>();
        trusted = new HashSet<>();
        safehouse = null;
        this.leader = leader;
    }

    public Member(MemberName name, int rank, boolean leader) {
        this.name = name;
        this.rank = rank;
        this.leader = leader;
        rivals = null;
        trusted = null;
    }

    public Member(MemberName name, int rank, Safehouse.SafehouseType safehouse, boolean leader) {
        this.name = name;
        this.rank = rank;
        this.safehouse = safehouse;
        this.leader = leader;
        rivals = null;
        trusted = null;
    }

    public Member(MemberName name, int rank, int prisonTurnsLeft, Safehouse.SafehouseType prisonSafehouse, Integer intelligencePerTurn, boolean leader) {
        this(name, rank, leader);
        this.prisonTurnsLeft = prisonTurnsLeft;
        this.prisonSafehouse = prisonSafehouse;
        this.intelligencePerTurn = intelligencePerTurn;
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
        trusted.add(member);
    }

    public void addRival(Member member) {
        rivals.add(member);
        trusted.remove(member);
    }

    public void removeRival(Member member) {
        rivals.remove(member);
    }

    public void neutralize(Member member) {
        trusted.remove(member);
        rivals.remove(member);
    }

    public void removeSelf() {
        for (Member member : trusted) {
            member.trusted.remove(this);
        }
        for (Member member : rivals) {
            member.removeRival(this);
        }
    }

    public enum MemberName {
        AISLING, CAMERIA, ELREON, GRAVICIUS, GUFF, HAKU, HILLOCK, IT_THAT_FLED, JANUS, KORELL, LEO, RIKER, RIN, JORGIN,
        TORA, VAGAN, VORICI, CATARINA, UNKNOWN
    }

}
