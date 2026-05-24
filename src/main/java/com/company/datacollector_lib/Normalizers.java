package com.company.datacollector_lib;

import com.company.datasets.other.loot.*;
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

    public static Loot toDivCardLoot(String string, String cardName) throws InvalidInputFormatException {
        switch (cardName.toLowerCase()) {
            case "buried treasure", "cameria's cut": return new StackableLoot(string, LootType.SCARAB, 1);
            case "disdain": return new StackableLoot(string, LootType.CURRENCY, 1);
            case "emperor's luck": return new StackableLoot(string, LootType.CURRENCY, 5);
            case "prejudice", "the forward gaze", "the undaunted", "jack in the box": return new Loot(string, LootType.UNIQUE_ITEM);
            case "justified ambition": return new MapLoot(string, LootType.SYNTH_MAP);
            case "more is never enough": return new StackableLoot(string, LootType.SCARAB, 4);
            case "runic luck": return new StackableLoot(string, LootType.CURRENCY, 10);
            case "sambodhi's vow": return new StackableLoot(string, LootType.FRAGMENT, 1);
            case "the gambler": return new StackableLoot(string, LootType.DIVINATIONCARDS, 1);
            case "the master artisan": return new StackableLoot(string, LootType.CURRENCY, 20);
            case "the tinkerer's table": return new StackableLoot(string, LootType.FOSSILS, 5);
            case "the tireless extractor": return new StackableLoot(string, LootType.OIL, 10);
            case "the wilted rose": return new GemLoot(string, LootType.GEM_CORRUPTED, 21, 0);
            case "three voices": return new StackableLoot(string, LootType.ESSENCES, 3);
            default:
                try {
                    return Loot.parseToLoot(string);
                } catch (InvalidLootFormatException e) {
                    throw new InvalidInputFormatException(e.getMessage());
                }
        }
    }

    public static List<Loot> toDivCardLootList(String string, String cardName) throws InvalidInputFormatException {
        if (string.trim().isEmpty()) {
            return List.of();
        }
        String[] reps = string.strip().split("\n");
        List<Loot> loot = new ArrayList<>();

        for (String rep : reps) {
            Matcher matcher = Pattern.compile("^([1-9]\\d*);(.+)$").matcher(rep);
            if (matcher.find()) {
                int amount = Integer.parseInt(matcher.group(1));
                String itemName = matcher.group(2);
                Loot l = toDivCardLoot(itemName, cardName);
                for (int i = 0; i < amount; i++) loot.add(l);
            } else loot.add(toDivCardLoot(rep, cardName));
        }

        return loot;
    }

    private Normalizers() {
    }

}
