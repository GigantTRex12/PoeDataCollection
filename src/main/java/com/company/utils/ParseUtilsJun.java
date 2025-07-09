package com.company.utils;

import com.company.datasets.other.jun.Action;
import com.company.datasets.other.jun.Encounter;
import com.company.datasets.other.jun.Member;
import com.company.datasets.other.jun.Safehouse;
import com.company.exceptions.InvalidInputFormatException;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.company.datasets.other.jun.Action.ActionType.*;
import static com.company.datasets.other.jun.Action.ActionType.BETRAY__RAISE_ALL;
import static com.company.utils.IOUtils.input;
import static com.company.utils.IOUtils.multilineInput;
import static com.company.utils.ParseUtils.toSafeHouse;

public class ParseUtilsJun {

    public static Encounter toEncounter(String string) throws InvalidInputFormatException {
        String[] split = string.split(":");
        Safehouse.SafehouseType safehouse;
        try {
            safehouse = Safehouse.SafehouseType.valueOf(split[0].toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidInputFormatException(split[0] + " is not a valid safehouse name");
        }

        List<Member> members = new ArrayList<>();
        for (String memberRep : split[1].split(";")) {
            members.add(toMember(memberRep.trim()));
        }

        List<Action> actions = new ArrayList<>();
        String actionsRep = multilineInput("Input the actions").trim();
        for (String line : actionsRep.split("\\n")) {
            actions.add(toAction(line.trim()));
        }

        String revealedRep = input("Give additional revealed members", "^$|\\w+\\(\\d[tfri]b?\\)(;\\w+\\(\\d[tfri]b?\\))?");
        List<Member> revealed = new ArrayList<>();
        if (!revealedRep.isEmpty()) {
            for (String singleRevealed : revealedRep.split(";")) {
                Matcher memberMatcher = Pattern.compile("(\\w+)\\((\\d)([trfi])(b?)\\)").matcher(singleRevealed);
                if (memberMatcher.find()) {
                    revealed.add(new Member(
                            toMemberName(memberMatcher.group(1)),
                            Integer.parseInt(memberMatcher.group(2)),
                            toSafeHouse(memberMatcher.group(3)),
                            memberMatcher.group(4) != null
                    ));
                }
            }
        }

        return new Encounter(safehouse, members, actions, false, revealed);
    }

    private static Member toMember(String string) throws InvalidInputFormatException {
        Matcher matcher = Pattern.compile("([\\w\\-]+)(\\(([1-3])(b?)\\))?").matcher(string);
        if (!matcher.find()) {
            throw new InvalidInputFormatException("Input doesn't match regex");
        }
        Member.MemberName member;
        try {
            member = Member.MemberName.valueOf(matcher.group(1).toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidInputFormatException(matcher.group(1) + " is not a valid member name");
        }

        int rank = 0;
        boolean leader = false;
        if (matcher.groupCount() == 4) {
            rank = Integer.parseInt(matcher.group(3));
            leader = matcher.group(4).toLowerCase().equals("b");
        }

        return new Member(member, rank, leader);
    }

    private static Action toAction(String string) throws InvalidInputFormatException {
        if (string.isEmpty()) {
            throw new InvalidInputFormatException("At least one action needs to be specified");
        }
        String[] actionReps = string.split("\\s+-\\s+");
        Member.MemberName currMember = null;
        List<Action> actions = new ArrayList<>();

        for (String actionRep : actionReps) {
            Matcher matcher = Pattern.compile("^(\\w+ )?(\\w+)(\\(.+\\))?$|^\\((\\w+)\\)$").matcher(actionRep);
            if (!matcher.find()) {
                throw new InvalidInputFormatException("Action " + actionRep + "is not valid");
            }

            if (matcher.group(4) == null) {
                if (matcher.group(1) != null) {
                    try {
                        currMember = Member.MemberName.valueOf(matcher.group(1).toUpperCase());
                    } catch (IllegalArgumentException e) {
                        throw new InvalidInputFormatException(matcher.group(1) + " is not a valid member name");
                    }
                }
                if (matcher.group(2).equalsIgnoreCase("kill")) {
                    actions.add(new Action(currMember, EXECUTE, null, null, null, null));
                } else if (matcher.group(2).equalsIgnoreCase("bargain")) {
                    actions.add(parseBargain(matcher.group(3), currMember));
                } else if (matcher.group(2).equalsIgnoreCase("betray")) {
                    actions.add(parseBetray(matcher.group(3), currMember));
                } else {
                    throw new InvalidInputFormatException(matcher.group(2) + " is not a valid action");
                }
            } else {
                if (matcher.group(4).equalsIgnoreCase("interrogate")) {
                    actions.add(new Action(currMember, INTERROGATE, null, null, null, null));
                } else if (matcher.group(4).equalsIgnoreCase("release")) {
                    actions.add(new Action(currMember, RELEASE, null, null, null, null));
                } else {
                    throw new InvalidInputFormatException(matcher.group(4) + " is not a valid action");
                }
            }
        }

        for (int i = 0; i < actions.size() - 1; i++) {
            actions.get(i).setDoneAction(actions.get(i + 1));
        }

        return actions.get(0);
    }

    private static Action parseBargain(String string, Member.MemberName member) throws InvalidInputFormatException {
        if (string == null || string.length() <= 2) {
            throw new InvalidInputFormatException("Bargain needs arguments");
        }
        String rep = string.substring(1, string.length() - 1).trim().toLowerCase();

        Matcher matcherIntel = Pattern.compile("(\\d+)([tfir])?").matcher(rep);
        Matcher matcherRecruit = Pattern.compile("move (\\w+)( [tfir])").matcher(rep);
        Matcher matcherSwitch = Pattern.compile("switch (\\w+)(\\([tfir]\\))?").matcher(rep);
        Matcher matcherDestroy = Pattern.compile("destroy\\(([tfir])\\)").matcher(rep);
        Matcher matcherNeutral = Pattern.compile("neutral (\\w+)\\((\\d+)([tfir])\\)").matcher(rep);
        Matcher matcherRemoveRivalries = Pattern.compile("remove rivalries\\(([tfir])\\)").matcher(rep);

        if (matcherIntel.find()) {
            return new Action(member, BARGAIN__INTELLIGENCE, null,
                    Integer.parseInt(matcherIntel.group(2)), null,
                    Safehouse.SafehouseType.fromLetter(matcherIntel.group(2)));
        }
        if (matcherRecruit.find()) {
            return new Action(member, BARGAIN__RECRUIT, null, null,
                    toMemberName(matcherRecruit.group(1)),
                    Safehouse.SafehouseType.fromLetter(matcherRecruit.group(2)));
        }
        if (matcherSwitch.find()) {
            return new Action(member, BARGAIN__SWITCH, null, null,
                    toMemberName(matcherSwitch.group(1)),
                    matcherSwitch.group(2) == null ? null : Safehouse.SafehouseType.fromLetter(matcherRecruit.group(2).substring(1, 2)));
        }
        if (rep.equals("remove self")) {
            return new Action(member, BARGAIN__LEAVE, null, null, null, null);
        }
        if (rep.equals("free all")) {
            return new Action(member, BARGAIN__FREE_ALL, null, null, null, null);
        }
        if (matcherDestroy.find()) {
            return new Action(member, BARGAIN__DESTROY, null, null, null,
                    Safehouse.SafehouseType.fromLetter(matcherRecruit.group(1)));
        }
        if (matcherNeutral.find()) {
            return new Action(member, BARGAIN__INTELLIGENCE_NEUTRAL, null,
                    Integer.parseInt(matcherNeutral.group(2)),
                    toMemberName(matcherNeutral.group(1)),
                    Safehouse.SafehouseType.fromLetter(matcherNeutral.group(2)));
        }
        if (matcherRemoveRivalries.find()) {
            return new Action(member, BARGAIN__REMOVE_RIVALRIES, null, null, null,
                    Safehouse.SafehouseType.fromLetter(matcherRemoveRivalries.group(1)));
        }
        if (rep.equals("veiled items")) {
            return new Action(member, BARGAIN__VEILED_ITEMS, null, null, null, null);
        }
        if (rep.equals("currency")) {
            return new Action(member, BARGAIN__CURRENCY, null, null, null, null);
        }
        if (rep.equals("map")) {
            return new Action(member, BARGAIN__MAP, null, null, null, null);
        }
        if (rep.equals("unique")) {
            return new Action(member, BARGAIN__UNIQUE, null, null, null, null);
        }
        if (rep.equals("scarabs")) {
            return new Action(member, BARGAIN__SCARABS, null, null, null, null);
        }

        throw new InvalidInputFormatException("Couldn't parse " + string + " to bargain");
    }

    private static Action parseBetray(String string, Member.MemberName member) throws InvalidInputFormatException {
        if (string == null || string.length() <= 2) {
            throw new InvalidInputFormatException("Betrayal needs arguments");
        }
        String rep = string.substring(1, string.length() - 1).trim().toLowerCase();

        Matcher matcherIntelligence = Pattern.compile("(\\w+),(\\d+)([tfir])").matcher(rep);
        Matcher matcherRanks = Pattern.compile("\\+(\\d) (\\w+) looses all ranks").matcher(rep);
        Matcher matcherLeader = Pattern.compile("leader,(\\w+)").matcher(rep);
        Matcher matcherRemove = Pattern.compile("remove (\\w+)").matcher(rep);
        Matcher matcherDestroy = Pattern.compile("(\\w+),destroy ([tfir])").matcher(rep);
        Matcher matcherRaiseAll = Pattern.compile("(\\w+)\\+-").matcher(rep);

        if (matcherIntelligence.find()) {
            return new Action(member, BETRAY__INTELLIGENCE, null,
                    Integer.parseInt(matcherIntelligence.group(2)),
                    toMemberName(matcherIntelligence.group(1)),
                    Safehouse.SafehouseType.fromLetter(matcherIntelligence.group(3)));
        }
        if (matcherRanks.find()) {
            return new Action(member, BETRAY__RANKS, null,
                    Integer.parseInt(matcherRanks.group(1)),
                    toMemberName(matcherRanks.group(2)), null);
        }
        if (matcherLeader.find()) {
            return new Action(member, BETRAY__LEADER, null, null,
                    toMemberName(matcherLeader.group(1)), null);
        }
        if (matcherRemove.find()) {
            return new Action(member, BETRAY__REMOVE, null, null,
                    toMemberName(matcherRemove.group(1)), null);
        }
        if (matcherDestroy.find()) {
            return new Action(member, BETRAY__DESTROY, null, null,
                    toMemberName(matcherDestroy.group(1)),
                    Safehouse.SafehouseType.fromLetter(matcherDestroy.group(2)));
        }
        if (matcherRaiseAll.find()) {
            return new Action(member, BETRAY__RAISE_ALL, null, null,
                    toMemberName(matcherRaiseAll.group(1)), null);
        }

        throw new InvalidInputFormatException("Couldn't parse " + string + " to betrayal");
    }

    private static Member.MemberName toMemberName(String string) throws InvalidInputFormatException {
        try {
            return Member.MemberName.valueOf(string.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidInputFormatException(string + " is not a valid member name");
        }
    }

}
