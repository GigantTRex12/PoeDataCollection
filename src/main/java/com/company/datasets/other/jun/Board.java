package com.company.datasets.other.jun;

import com.company.exceptions.BoardStateDoesntMatchException;
import com.company.exceptions.SomethingIsWrongWithMyCodeException;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.company.datasets.other.jun.Member.MemberName.UNKNOWN;
import static com.company.datasets.other.jun.Safehouse.SafehouseType.*;
import static com.company.utils.IOUtils.input;
import static com.company.utils.IOUtils.print;
import static java.util.Map.entry;

@Getter
@AllArgsConstructor
public class Board {

    private static final Map<Integer, Integer> rankToIntelligencePerTurn = Map.ofEntries(
            entry(0, 1),
            entry(1, 2),
            entry(2, 4),
            entry(3, 6)
    );

    @JsonProperty("transportation")
    private final Safehouse transportation;

    @JsonProperty("fortification")
    private final Safehouse fortification;

    @JsonProperty("research")
    private final Safehouse research;

    @JsonProperty("intervention")
    private final Safehouse intervention;

    @JsonProperty("freeMembers")
    private final ArrayList<Member> freeMembers;

    @JsonIgnore
    private final List<Member> allMembers;

    public Board(Safehouse transportation, Safehouse fortification, Safehouse research, Safehouse intervention, ArrayList<Member> freeMembers) {
        this.transportation = transportation;
        this.fortification = fortification;
        this.research = research;
        this.intervention = intervention;
        this.freeMembers = freeMembers;
        allMembers = getAllMembers();
    }

    public Board() {
        // FIXME: not all safehouses start with 2 members
        Member transportationLeader = new Member(2, true);
        transportation = new Safehouse(TRANSPORTATION, transportationLeader, new ArrayList<>(), 0);
        transportation.addMember(transportationLeader);
        transportation.addMember(new Member(1, false));
        transportation.addMember(new Member(1, false));

        Member fortificationLeader = new Member(2, true);
        fortification = new Safehouse(FORTIFICATION, fortificationLeader, new ArrayList<>(), 0);
        fortification.addMember(fortificationLeader);
        fortification.addMember(new Member(1, false));
        fortification.addMember(new Member(1, false));

        Member researchLeader = new Member(2, true);
        research = new Safehouse(RESEARCH, researchLeader, new ArrayList<>(), 0);
        research.addMember(researchLeader);
        research.addMember(new Member(1, false));
        research.addMember(new Member(1, false));

        Member interventionLeader = new Member(2, true);
        intervention = new Safehouse(INTERVENTION, interventionLeader, new ArrayList<>(), 0);
        intervention.addMember(interventionLeader);
        intervention.addMember(new Member(1, false));
        intervention.addMember(new Member(1, false));

        freeMembers = new ArrayList<>(List.of(new Member(), new Member()));

        allMembers = getAllMembers();
    }

    @Override
    public String toString() {
        String rep = "Safehouses:\n";
        rep += transportation.toString() + "\n";
        rep += fortification.toString() + "\n";
        rep += research.toString() + "\n";
        rep += intervention.toString() + "\n";

        rep += "No Safehouse:\n";
        for (Member member : allMembers) {
            if (member.getSafehouse() == null) {
                rep += member.getName().name() + "\n";
            }
        }

        rep += "Prison:";
        for (Member member : allMembers) {
            if (member.getPrisonTurnsLeft() > 0) {
                rep += member.getName().name() + " (" + member.getPrisonTurnsLeft() + " rounds; " +
                        member.getIntelligencePerTurn() + " intelligence for " + member.getPrisonSafehouse().name() +
                        " safehouse per turn.)";
            }
        }

        rep += "Trusted:";
        for (Member member : allMembers) {
            for (Member trusted : member.getTrusted()) {
                if (member.getName().name().compareTo(trusted.getName().name()) < 0) {
                    rep += member.getName().name() + "-" + trusted.getName().name();
                }
            }
        }

        rep += "Rivals:";
        for (Member member : allMembers) {
            for (Member rival : member.getRivals()) {
                if (member.getName().name().compareTo(rival.getName().name()) < 0) {
                    rep += member.getName().name() + "-" + rival.getName().name();
                }
            }
        }

        return rep;
    }

    public Board deepCopy() {
        List<Member> newMembers = new ArrayList<>();
        Safehouse newTransportation = new Safehouse(transportation);
        newMembers.addAll(newTransportation.getMembers());
        Safehouse newFortification = new Safehouse(fortification);
        newMembers.addAll(newFortification.getMembers());
        Safehouse newResearch = new Safehouse(research);
        newMembers.addAll(newResearch.getMembers());
        Safehouse newIntervention = new Safehouse(intervention);
        newMembers.addAll(newIntervention.getMembers());

        ArrayList<Member> newFreeMembers = new ArrayList<>();
        for (Member freeMember : freeMembers) {
            newFreeMembers.add(new Member(freeMember));
        }
        newMembers.addAll(newFreeMembers);

        // fix relations
        for (Member newMember : newMembers) {
            for (Member oldMember : getAllMembers()) {
                if (newMember.equals(oldMember)) {
                    for (Member rival : oldMember.getRivals()) {
                        for (Member newRival : newMembers) {
                            if (rival.equals(newRival)) {
                                newMember.addRival(newRival);
                                break;
                            }
                        }
                    }
                    for (Member trusted : oldMember.getTrusted()) {
                        for (Member newTrusted : newMembers) {
                            if (trusted.equals(newTrusted)) {
                                newMember.addTrusted(newTrusted);
                                break;
                            }
                        }
                    }
                    break;
                }
            }
        }

        return new Board(newTransportation, newFortification, newResearch, newIntervention, newFreeMembers, newMembers);
    }

    private List<Member> getAllMembers() {
        List<Member> members = new ArrayList<>(freeMembers);
        members.addAll(transportation.getMembers());
        members.addAll(fortification.getMembers());
        members.addAll(research.getMembers());
        members.addAll(intervention.getMembers());
        return members;
    }

    private Safehouse getSafehouse(Safehouse.SafehouseType safehouseType) {
        return switch (safehouseType) {
            case TRANSPORTATION -> transportation;
            case FORTIFICATION -> fortification;
            case RESEARCH -> research;
            case INTERVENTION -> intervention;
        };
    }

    public static Board createBoard() {
        // TODO
        return new Board();
    }

    public void editBoard() {
        print("Editing safehouse");

        boolean editing = true;
        while (editing) {
            String action = input("What would you like to edit?", Map.ofEntries(
                    entry("s", "safehouse"),
                    entry("m", "member"),
                    entry("u", "unknown member"),
                    entry("r", "relation"),
                    entry("e", "exit")
            ), false).toLowerCase();

            switch (action) {
                case ("s"), ("safehouse") -> editSafehouse();
                // TODO
                default -> editing = false;
            }
        }
    }

    private void editSafehouse() {
        String safehouse = input("Which safehouse to edit?", Map.ofEntries(
                entry("t", "transportation"),
                entry("f", "fortification"),
                entry("r", "research"),
                entry("i", "intervention")
        )).toLowerCase();
        switch (safehouse) {
            case ("t"), ("transportation") -> editSafehouse(TRANSPORTATION);
            case ("f"), ("fortification") -> editSafehouse(FORTIFICATION);
            case ("r"), ("research") -> editSafehouse(RESEARCH);
            case ("i"), ("intervention") -> editSafehouse(INTERVENTION);
        }
    }

    public void editSafehouse(Safehouse.SafehouseType safehouseType) {
        print("Editing safehouse " + safehouseType.name());
        Safehouse safehouse = getSafehouse(safehouseType);

        String intelligence = input("Enter new intelligence value", "^$|\\d+");
        if (!intelligence.equals("")) {
            safehouse.setIntelligence(Integer.parseInt(intelligence));
        }

        String safehouseState = input("Enter the new state", "^\\w+\\([1-3]b\\)(;\\w+\\([1-3]\\))*");
        String[] split = safehouseState.split(";");
        String memberReg = "(\\w+)\\(([1-3])(b?)\\)";
        Matcher matcherLeader = Pattern.compile(memberReg).matcher(split[0]);
        if (matcherLeader.find()) {
            Member newLeader = findMember(Member.MemberName.valueOf(matcherLeader.group(1).toUpperCase()));
            newLeader.setSafehouse(safehouseType);
            safehouse.removeAllMembers(newLeader);
            newLeader.setRank(Integer.parseInt(matcherLeader.group(2)));
        } else {
            throw new SomethingIsWrongWithMyCodeException("Regex should always match here");
        }
        for (int i = 1; i < split.length; i++) {
            Matcher matcherNewMember = Pattern.compile(memberReg).matcher(split[i]);
            if (matcherNewMember.find()) {
                Member newMember = findMember(Member.MemberName.valueOf(matcherNewMember.group(1).toUpperCase()));
                newMember.setRank(Integer.parseInt(matcherNewMember.group(2)));
                safehouse.addMember(newMember);
            } else {
                throw new SomethingIsWrongWithMyCodeException("Regex should always match here");
            }
        }
    }


    public void applyEncounter(Encounter encounter) {
        encounter.getMembers().forEach(m -> checkMember(m, encounter.getSafehouse()));
        encounter.getRevealed().forEach(m -> checkMember(m, m.getSafehouse()));
        encounter.getActions().forEach(a -> checkAction(a.get(), encounter.isJunTree(), encounter.getSafehouse()));
        prisonRound();
    }

    private Member findMember(Member.MemberName name) {
        return allMembers.stream()
                .filter(m -> m.getName() == name)
                .findAny().orElseThrow(() -> new BoardStateDoesntMatchException("No member " + name.name() + " found"));
    }

    private void checkMember(Member member, Safehouse.SafehouseType safehouse) {
        if (member.getRank() == null) {
            return;
        }
        Member thisMember = allMembers.stream().filter(m -> m.equals(member)).findAny().orElse(null);
        if (thisMember == null) {
            if (member.getRank() == 0) {
                thisMember = freeMembers.stream()
                        .filter(m -> m.getName() == UNKNOWN)
                        .findAny().orElseThrow(() -> new BoardStateDoesntMatchException("No revealable member found"));
                thisMember.setName(member.getName());
            } else {
                getSafehouse(safehouse).revealMember(member);
            }
        } else {
            thisMember.setRank(member.getRank());
        }
    }

    private void checkAction(Action action, boolean junTree, Safehouse.SafehouseType safehouse) {
        if (action.getActionType().isBetray()) {
            checkBetrayal(action);
            return;
        }
        if (action.getActionType().isBargain()) {
            checkBargain(action, getSafehouse(safehouse));
            return;
        }
        Member member = findMember(action.getMember());
        switch (action.getActionType()) {
            case INTERROGATE -> {
                member.setPrisonTurnsLeft(3);
                member.setPrisonSafehouse(safehouse);
                member.setIntelligencePerTurn(rankToIntelligencePerTurn.get(member.getRank()));
            }
            case EXECUTE -> {
                int rank = member.getRank();
                if (junTree) {
                    getSafehouse(safehouse).addIntelligence(rank * 2);
                    rank++;
                }
                rank++;
                if (rank > 3) {
                    rank = 3;
                }
                member.setRank(rank);
            }
        }
    }

    private void checkBetrayal(Action action) {
        Member member = findMember(action.getMember());
        Member otherMember = findMember(action.getOtherMember());
        setRivals(member, otherMember);
        switch (action.getActionType()) {
            case BETRAY__INTELLIGENCE -> getSafehouse(action.getSafehouse()).addIntelligence(action.getValue());
            case BETRAY__RANKS -> {
                member.setRank(member.getRank() + action.getValue());
                otherMember.setRank(0);
                Safehouse safehouse = getSafehouse(otherMember.getSafehouse());
                otherMember.setSafehouse(null);
                safehouse.removeMember(otherMember);
                freeMembers.add(otherMember);
            }
            case BETRAY__LEADER -> {
                member.setLeader(true);
                otherMember.setLeader(false);
                getSafehouse(member.getSafehouse()).setLeader(member);
            }
            case BETRAY__REMOVE -> removeMember(otherMember);
            case BETRAY__RAISE_ALL -> {
                getSafehouse(member.getSafehouse()).incrementAllRanks();
                getSafehouse(otherMember.getSafehouse()).decrementAllRanks();
            }
        }
    }

    private void checkBargain(Action action, Safehouse safehouse) {
        Member member = findMember(action.getMember());
        Member otherMember = action.getOtherMember() != null ? findMember(action.getOtherMember()) : null;
        Safehouse otherSafehouse = getSafehouse(action.getSafehouse());
        switch (action.getActionType()) {
            case BARGAIN__INTELLIGENCE -> {
                if (otherMember == null) {
                    safehouse.addIntelligence(action.getValue());
                } else {
                    otherSafehouse.addIntelligence(action.getValue());
                    setTrusted(member, otherMember);
                }
            }
            case BARGAIN__RECRUIT -> {
                otherMember.setRank(1);
                otherSafehouse.addMember(otherMember);
                freeMembers.remove(otherMember);
                setTrusted(member, otherMember);
            }
            case BARGAIN__SWITCH -> {
                Safehouse thisSafehouse = getSafehouse(member.getSafehouse());
                thisSafehouse.removeMember(member);
                otherSafehouse.addMember(member);
                otherSafehouse.removeMember(otherMember);
                thisSafehouse.addMember(otherMember);
                setTrusted(member, otherMember);
            }
            case BARGAIN__LEAVE -> {
                removeMember(member);
            }
            case BARGAIN__FREE_ALL -> {
                prisonRound();
                prisonRound();
                prisonRound();
            }
            case BARGAIN__INTELLIGENCE_NEUTRAL -> {
                otherSafehouse.addIntelligence(action.getValue());
                setNeutral(member, otherMember);
            }
            case BARGAIN__REMOVE_RIVALRIES -> {
                otherSafehouse.getMembers().forEach(m -> m.getRivals().forEach(r -> setNeutral(m, r)));
            }
        }
    }

    private void setTrusted(Member member1, Member member2) {
        member1.addTrusted(member2);
        member2.addTrusted(member1);
    }

    private void setRivals(Member member1, Member member2) {
        member1.addRival(member2);
        member2.addRival(member1);
    }

    private void setNeutral(Member member1, Member member2) {
        member1.neutralize(member2);
        member2.neutralize(member1);
    }

    private void removeMember(Member member) {
        getSafehouse(member.getSafehouse()).removeMember(member);
        member.removeSelf();
        allMembers.remove(member);
        freeMembers.remove(member);
        freeMembers.add(new Member());
    }

    private void prisonRound() {
        allMembers.stream().filter(m -> m.getPrisonTurnsLeft() > 0).forEach(
                m -> {
                    getSafehouse(m.getPrisonSafehouse()).addIntelligence(m.getIntelligencePerTurn());
                    m.decrementPrisonTurns();
                    if (m.getRank() < 1) {
                        getSafehouse(m.getSafehouse()).removeMember(m);
                    }
                }
        );
    }

    public void resetSafeHouseIntelligence(Safehouse.SafehouseType safehouse) {
        getSafehouse(safehouse).setIntelligence(0);
    }

}
