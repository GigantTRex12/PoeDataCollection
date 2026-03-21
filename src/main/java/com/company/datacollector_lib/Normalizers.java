package com.company.datacollector_lib;

import com.company.datasets.other.UniqueAndGoldCostPair;
import com.company.datasets.other.loot.Loot;
import com.company.datasets.other.loot.LootType;
import com.company.exceptions.InvalidLootFormatException;
import exceptions.InvalidInputFormatException;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.company.utils.IOUtils.print;

public class Normalizers {

    public static boolean toBool(String string) throws InvalidInputFormatException {
        if (string.equalsIgnoreCase("y") || string.equalsIgnoreCase("yes")) {
            return true;
        } else if (string.equalsIgnoreCase("n") || string.equalsIgnoreCase("no")) {
            return false;
        }
        throw new InvalidInputFormatException("Input cannot be converted to boolean");
    }

    public static Loot parseToBossLoot(String string) {
        return new Loot(string, LootType.BOSS_UNIQUE_ITEM);
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

    public static List<UniqueAndGoldCostPair> toUniqueCostPairs(String string) {
        if (string.isEmpty()) return List.of();
        List<UniqueAndGoldCostPair> pairs = new ArrayList<>();
        for (String s : string.split("\n")) {
            Matcher matcher = Pattern.compile("^(.*) (\\d+)$").matcher(s);
            if (matcher.find()) pairs.add(new UniqueAndGoldCostPair(matcher.group(1), Integer.parseInt(matcher.group(2))));
            else print("Couldn't parse \"" + s + "\" to Cost. (skipped)");
        }
        return pairs;
    }

    private Normalizers() {}

}
