package com.company.datasets.other.jun;

import com.company.exceptions.BoardStateDoesntMatchException;
import com.company.exceptions.SomethingIsWrongWithMyCodeException;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.company.datasets.other.jun.Member.MemberName.UNKNOWN;
import static com.company.utils.IOUtils.input;

@NoArgsConstructor(force = true)
@AllArgsConstructor
@Getter
@EqualsAndHashCode(of = {"type"})
@JsonDeserialize(using = SafehouseDeserializer.class)
public class Safehouse {

    @JsonProperty("type")
    private final SafehouseType type;

    @JsonProperty("leader")
    private Member leader;

    @JsonProperty("members")
    @NonNull
    private final ArrayList<Member> members;

    @JsonProperty("intelligence")
    @Setter
    private int intelligence;

    // to create deep copy of Board
    public Safehouse(Safehouse safehouse) {
        type = safehouse.type;
        leader = new Member(safehouse.leader);
        members = new ArrayList<>();
        for (Member member : safehouse.members) {
            addMember(new Member(member));
        }
        intelligence = safehouse.intelligence;
    }

    public Safehouse(SafehouseType type, Member leader, List<Member> otherMembers) {
        this.type = type;
        this.leader = leader;
        members = new ArrayList<>();
        otherMembers.forEach(this::addMember);
        addMember(leader);
        intelligence = 0;
    }

    public void setLeader(Member leader) {
        this.leader.setLeader(false);
        this.leader = leader;
        this.leader.setLeader(true);
    }

    @Override
    public String toString() {
        StringBuilder rep = new StringBuilder(type.name() + "\n");
        rep.append("Intelligence: ").append(intelligence).append("\n");
        rep.append("Leader: ").append(leader.getName().name()).append("\nMembers:\n");

        for (Member member : members) {
            rep.append(member.toString()).append("\n");
        }

        return rep.toString();
    }

    public void addMember(Member member) {
        members.add(member);
        member.setSafehouse(type);
    }

    public void removeMember(Member member) {
        members.remove(member);
        member.setSafehouse(null);
        if (member.isLeader()) {
            String newLeader = input("Choose a new leader for " + type.name() + " safehouse.",
                    members.stream().map(m -> m.getName().name()).collect(Collectors.toList())).toUpperCase();
            leader = members.stream()
                    .filter(m -> m.getName().name().equalsIgnoreCase(newLeader))
                    .findAny().orElseThrow(() -> new SomethingIsWrongWithMyCodeException("Member should exist"));
            leader.setLeader(true);
            member.setLeader(false);
        }
    }

    public void revealMember(Member member) {
        if (member.isLeader()) {
            if (leader.getName() == member.getName()) {
                leader.setRank(member.getRank());
            }
            else if (leader.getName() == UNKNOWN) {
                leader.setName(member.getName());
                leader.setRank(member.getRank());
            }
            else {
                throw new BoardStateDoesntMatchException("Leader for this safehouse is already revealed and different");
            }
            return;
        }
        Member thisMember = members.stream()
                .filter(m -> !m.isLeader() && m.getName() == member.getName())
                .findAny().orElse(null);
        if (thisMember == null) {
            thisMember = members.stream()
                    .filter(m -> !m.isLeader() && m.getName() == UNKNOWN)
                    .findAny().orElse(null);
        }
        if (thisMember == null) {
            throw new BoardStateDoesntMatchException("No revealable member found");
        }
        thisMember.setName(member.getName());
        thisMember.setRank(member.getRank());
    }

    public void addIntelligence(int intelligence) {
        this.intelligence = Math.min(this.intelligence + intelligence, 100);
    }

    public void incrementAllRanks() {
        for (Member member : members) {
            member.setRank(Math.min(member.getRank() + 1, 3));
        }
    }

    public void decrementAllRanks() {
        for (Member member : members) {
            int rank = member.getRank() - 1;
            if (rank < 1) {
                member.setRank(0);
                removeMember(member);
            }
            else {
                member.setRank(rank);
            }
        }
    }

    public void removeAllMembers(Member newLeader) {
        for (Member member : members) {
            member.setSafehouse(null);
            member.setRank(0);
        }
        members.clear();
        members.add(newLeader);
        setLeader(newLeader);
    }

    public enum SafehouseType {
        RESEARCH, INTERVENTION, FORTIFICATION, TRANSPORTATION;

        public static SafehouseType fromLetter(String s) {
            if (s == null) {return null;}
            return switch (s.trim().toLowerCase()) {
                case "r" -> RESEARCH;
                case "i" -> INTERVENTION;
                case "f" -> FORTIFICATION;
                case "t" -> TRANSPORTATION;
                default -> null;
            };
        }
    }

}
