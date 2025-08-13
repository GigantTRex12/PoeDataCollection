package com.company.utils;

import com.company.datasets.datasets.KalandraMistDataSet;
import com.company.datasets.datasets.MapDropDataSet;
import com.company.datasets.other.jun.Encounter;
import com.company.datasets.other.jun.Member;
import com.company.datasets.other.jun.Safehouse;
import com.company.datasets.other.loot.Loot;
import com.company.datasets.other.loot.LootType;
import com.company.exceptions.InvalidInputFormatException;
import com.company.exceptions.InvalidLootFormatException;

import java.util.ArrayList;
import java.util.List;

import static com.company.utils.IOUtils.print;
import static com.company.utils.Utils.splitToChars;

public class ParseUtils {

    public static boolean toBool(String string) throws InvalidInputFormatException {
        if (string.equalsIgnoreCase("y") || string.equalsIgnoreCase("yes")) {
            return true;
        } else if (string.equalsIgnoreCase("n") || string.equalsIgnoreCase("no")) {
            return false;
        }
        throw new InvalidInputFormatException("Input cannot be converted to boolean");
    }

    public static int toInt(String string) {
        return Integer.parseInt(string);
    }

    public static List<Loot> toLootList(String string) {
        if (string.trim().isEmpty()) {
            return List.of();
        }
        String[] reps = string.strip().split("\n");
        List<Loot> loot = new ArrayList<>();

        for (String rep : reps) {
            if (rep.equals("-")) {
                loot.add(null);
                continue;
            }
            try {
                Loot newLoot = Loot.parseToLoot(rep);
                loot.add(newLoot);
            } catch (InvalidLootFormatException e) {
                print("Couldn't parse \"" + rep + "\" to Loot. (skipped)");
            }
        }

        return loot;
    }

    public static Loot parseToLoot(String string) throws InvalidInputFormatException {
        try {
            if (string.trim().isEmpty()) {
                return null;
            }
            return Loot.parseToLoot(string);
        } catch (InvalidLootFormatException e) {
            throw new InvalidInputFormatException(e.getMessage());
        }
    }

    public static Loot parseToBossLoot(String string) {
        return new Loot(string, LootType.BOSS_UNIQUE_ITEM);
    }

    public static KalandraMistDataSet.MistType parseMistType(String string) {
        if ("in map".equalsIgnoreCase(string)) {
            return KalandraMistDataSet.MistType.IN_MAP;
        }
        if ("itemized guff".equalsIgnoreCase(string)) {
            return KalandraMistDataSet.MistType.ITEMIZED_GUFF;
        }
        if ("itemized".equalsIgnoreCase(string)) {
            return KalandraMistDataSet.MistType.ITEMIZED;
        }
        if (string.toLowerCase().startsWith("lake")) {
            return KalandraMistDataSet.MistType.LAKE;
        }
        return null;
    }

    public static Integer parseLakeTier(String string) {
        if (string.toLowerCase().startsWith("lake ")) {
            return Integer.parseInt(string.substring(5));
        }
        return null;
    }

    public static int parseAmountPos(String string) {
        return Integer.parseInt(string.split("\\/")[0]);
    }

    public static int parseAmountNeg(String string) {
        return Integer.parseInt(string.split("\\/")[1]);
    }

    public static int parseAmountNeut(String string) {
        String[] split = string.split("\\/");
        if (split.length <= 2) {
            return 0;
        }
        return Integer.parseInt(split[2]);
    }

    public static LootType parseToMistLoottype(String string) {
        return switch (string.toLowerCase()) {
            case ("a"), ("amulet") -> LootType.RARE_JEWELLRY_AMULET;
            case ("r"), ("ring") -> LootType.RARE_JEWELLRY_RING;
            default -> null;
        };
    }

    public static LootType toMapConversionType(String string) {
        if (string.isEmpty()) {
            return null;
        }
        return MapDropDataSet.getLootTypeFromMap(String.valueOf(string.charAt(string.length() - 1)));
    }

    public static int toConversionChance(String string) {
        if (string.isBlank()) {
            return 0;
        }
        return Integer.parseInt(string.substring(0, string.length() - 1));
    }

    public static List<LootType> toMapDropList(String string, char split) throws InvalidInputFormatException {
        if (string.isEmpty()) return List.of();
        List<LootType> list = new ArrayList<>();
        for (String c : string.split(String.valueOf(split))) {
            LootType nextType = MapDropDataSet.getLootTypeFromMap(c);
            if (nextType == null) {
                throw new InvalidInputFormatException(c + "is not a valid combination");
            }
            list.add(nextType);
        }
        return list;
    }

    public static List<LootType> toMapDropList(String string) throws InvalidInputFormatException {
        return toMapDropList(string, '-');
    }

    public static List<LootType> toBossMapDropList(String string) throws InvalidInputFormatException {
        if (string.isEmpty()) {
            return null;
        }
        if (string.equalsIgnoreCase("-")) {
            return List.of();
        }
        return toMapDropList(string, ',');
    }

    public static Safehouse.SafehouseType toSafeHouse(String string) {
        if (string.length() == 1) {
            return Safehouse.SafehouseType.fromLetter(string);
        }
        return Safehouse.SafehouseType.valueOf(string.toUpperCase());
    }

    public static Encounter toEncounter(String string) throws InvalidInputFormatException {
        return ParseUtilsJun.toEncounter(string);
    }

    public static Member.MemberName toMember(String string) throws InvalidInputFormatException {
        try {
            return Member.MemberName.valueOf(string);
        } catch (IllegalArgumentException e) {
            throw new InvalidInputFormatException(string + " is not a valid membername");
        }
    }

}
