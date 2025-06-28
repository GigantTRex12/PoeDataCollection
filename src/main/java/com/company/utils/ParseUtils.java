package com.company.utils;

import com.company.datasets.other.loot.Loot;
import com.company.exceptions.InvalidLootFormatException;

import java.util.ArrayList;
import java.util.List;

import static com.company.utils.IOUtils.print;

public class ParseUtils {
    public static boolean toBool(String string) {
        return string.equalsIgnoreCase("y") || string.equalsIgnoreCase("yes");
    }

    public static List<Loot> toLootList(String string) {
        if (string.trim().isEmpty()) {
            return List.of();
        }
        String[] reps = string.strip().split("\n");
        List<Loot> loot = new ArrayList<>();

        for (String rep : reps) {
            try {
                Loot newLoot = Loot.parseToLoot(rep);
                loot.add(newLoot);
            }
            catch (InvalidLootFormatException e) {
                print("Couldn't parse \"" + rep + "\" to Loot. (skipped)");
            }
        }

        return loot;
    }

    public static Loot parseToLoot(String string) {
        try {
            if (string.trim().isEmpty()) {
                return null;
            }
            return Loot.parseToLoot(string);
        }
        catch (InvalidLootFormatException e) {
            print("Couldn't parse \"" + string + "\" to Loot. (skipped)");
            return null;
        }
    }
}
