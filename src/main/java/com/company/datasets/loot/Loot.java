package com.company.datasets.loot;

import com.company.exceptions.InvalidLootFormatException;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.*;

import static com.company.datasets.loot.LootType.*;
import static com.company.utils.Utils.contains;

@NoArgsConstructor(force = true)
@AllArgsConstructor
@Getter
@ToString
@EqualsAndHashCode
@JsonDeserialize(using = LootDeserializer.class)
public class Loot {
    private static final LootType[] stackable = {CATALYSTS, ESSENCES, DIVINATIONCARDS, CURRENCY, EXOTIC_CURRENCY, FRAGMENT, SCARAB, FOSSILS, SPLINTERS, SPLINTERS_BREACH, SPLINTERS_LEGION, OIL, INCUBATOR, SCOUTING_REPORT};
    private static final LootType[] corrImplicits = {UNIQUE_ITEM_IMPLICIT_CORRUPTED, RARE_ARMOUR_IMPLICIT_CORRUPTED, RARE_WEAPON_IMPLICIT_CORRUPTED, RARE_JEWELLRY_IMPLICIT_CORRUPTED, RARE_ITEM_IMPLICIT_CORRUPTED};
    private static final LootType[] maps = {MAP, UNIQUE_MAP, SYNTH_MAP, ELDER_MAP, SHAPER_MAP, CONQUEROR_MAP, T17_MAP, RARE_MAP_CORRUPTED, RARE_MAP_CORRUPTED_8MOD, RARE_MAP_CORRUPTED_IMPLICITS};
    private static final LootType[] corruptedMaps = {RARE_MAP_CORRUPTED, RARE_MAP_CORRUPTED_8MOD, RARE_MAP_CORRUPTED_IMPLICITS};
    private static final LootType[] gems = {GEM, GEM_CORRUPTED, GEM_AWAKENED};
    private static final LootType[] crafts = {GUFF_CRAFTING_BENCH, VORICI_CRAFTING_BENCH, TORA_CRAFTING_BENCH, IT_THAT_FLED_BREACHSTONE_CRAFT};

    @JsonProperty("name")
    protected final String name;
    @JsonProperty("type")
    @JsonDeserialize
    protected final LootType type;

    public static Loot parseToLoot(String rep) throws InvalidLootFormatException {
        String[] split = rep.split("(?<!\\\\);");
        if (split.length < 2) {
            throw new InvalidLootFormatException("Invalid Format to parse Loot: not enough Arguments");
        }
        String name = split[0].replaceAll("\\\\+", "");
        LootType type = parseLootType(split[1]);
        if (contains(stackable, type)) {
            if (split.length < 3) {
                throw new InvalidLootFormatException("Invalid Format to parse Loot: missing argument for stacksize");
            }
            int stacksize;
            try {
                stacksize = Integer.parseInt(split[2]);
            }
            catch (NumberFormatException e) {
                throw new InvalidLootFormatException("Invalid Format to parse Loot: stacksize is not an integer");
            }
            return new StackableLoot(name, type, stacksize);
        }
        else if (contains(corrImplicits, type)) {
            int implicitAmount;
            try {
                implicitAmount = Integer.parseInt(split[2]);
            }
            catch (NumberFormatException|IndexOutOfBoundsException e) {
                return new ImplicitCorruptedItem(name, type, 1);
            }
            return new ImplicitCorruptedItem(name, type, implicitAmount);
        }
        else if (contains(maps, type)) {
            if (split.length < 3) {
                return new MapLoot(name, type, 16, null);
            }
            int tier;
            try {
                tier = Integer.parseInt(split[2]);
            }
            catch (NumberFormatException e) {
                throw new InvalidLootFormatException("Invalid Format to parse Loot: tier is not an integer");
            }
            if (split.length >= 4) {
                if (contains(corruptedMaps, type)) {
                    int implicits = 0;
                    if (split.length >= 5) {
                        try {
                            implicits = Integer.parseInt(split[4]);
                        }
                        catch (NumberFormatException e) {
                            throw new InvalidLootFormatException("Invalid Format to parse Loot: implicit amount is not an integer");
                        }
                    }
                    return new CorruptedMapLoot(name, type, tier, split[3], implicits);
                }
                return new MapLoot(name, type, tier, split[3]);
            }
            return new MapLoot(name, type, tier, null);
        }
        else if (contains(gems, type)) {
            if (split.length < 3) {
                return new GemLoot(name, type);
            }
            String[] gemRep = split[2].split("/");
            if (gemRep.length < 1) {
                return new GemLoot(name, type);
            }
            int level;
            int quality = 0;
            try {
                level = Integer.parseInt(gemRep[0]);
            }
            catch (NumberFormatException e) {
                throw new InvalidLootFormatException("Invalid Format to parse Loot: level is not an integer");
            }
            if (gemRep.length > 1) {
                try {
                    quality = Integer.parseInt(gemRep[1]);
                }
                catch (NumberFormatException e) {
                    throw new InvalidLootFormatException("Invalid Format to parse Loot: quality is not an integer");
                }
            }
            return new GemLoot(name, type, level, quality);
        }
        else if (contains(crafts, type)) {
            if (split.length < 3) {
                return new CraftingBenchLoot(name, type, null);
            }
            return new CraftingBenchLoot(name, type, split[2]);
        }
        else {
            return new Loot(name, type);
        }
    }

    public static LootType parseLootType(String rep) throws InvalidLootFormatException {
        LootType type = null;
        try {
            type = valueOf(rep.toUpperCase());
        }
        catch (IllegalArgumentException e) {
            String lower = rep.toLowerCase();
            boolean unique = lower.contains("unique");
            boolean rare = lower.contains("rare");
            boolean weapon = lower.contains("weapon");
            boolean armour = lower.contains("armour") || lower.contains("armor");
            boolean jewellry = lower.contains("jewellry");
            boolean gem = lower.contains("gem");
            boolean corrupted = lower.contains("corrupted");
            if (lower.contains("essence")) {
                type = ESSENCES;
            }
            else if (lower.contains("catalyst")) {
                type = CATALYSTS;
            }
            else if (lower.contains("fossil")) {
                type = FOSSILS;
            }
            else if (lower.contains("offering")) {
                type = OFFERINGS;
            }
            else if (lower.contains("incubator")) {
                type = INCUBATOR;
            }
            else if (lower.contains("map")) {
                if (lower.contains("synth") || lower.contains("synthesis")) type = SYNTH_MAP;
                else if (lower.contains("conq") || lower.contains("conqueror")) type = CONQUEROR_MAP;
                else if (lower.contains("elder")) type = ELDER_MAP;
                else if (lower.contains("shaper")) type = SHAPER_MAP;
                else if (lower.contains("17")) type = T17_MAP;
                else if (unique) type = UNIQUE_MAP;
                else if (corrupted) {
                    if (lower.contains("8mod")) type = RARE_MAP_CORRUPTED_8MOD;
                    else if (lower.contains("implicits")) type = RARE_MAP_CORRUPTED_IMPLICITS;
                    else type = RARE_MAP_CORRUPTED;
                }
                else type = MAP;
            }
            else if (lower.contains("card") || lower.contains("divination")) {
                type = DIVINATIONCARDS;
            }
            else if (corrupted) {
                if (unique) type = UNIQUE_ITEM_IMPLICIT_CORRUPTED;
                else if (rare) {
                    if (weapon) type = RARE_WEAPON_IMPLICIT_CORRUPTED;
                    else if (armour) type = RARE_ARMOUR_IMPLICIT_CORRUPTED;
                    else if (jewellry) type = RARE_JEWELLRY_IMPLICIT_CORRUPTED;
                    else type = RARE_ITEM_IMPLICIT_CORRUPTED;
                }
                else if (gem) type = GEM_CORRUPTED;
            }
            else if (gem) {
                if (lower.contains("awakened")) type = GEM_AWAKENED;
                else type = GEM;

            }
            else if (unique) {
                if (lower.contains("boss")) type = BOSS_UNIQUE_ITEM;
                else type = UNIQUE_ITEM;
            }
            else if (rare) {
                if (weapon) type = RARE_WEAPON;
                else if (armour) type = RARE_ARMOUR;
                else if (jewellry) type = RARE_JEWELLRY;
                else type = RARE_ITEM;
            }
            else if (lower.contains("splinter")) {
                if (lower.contains("breach")) type = SPLINTERS_BREACH;
                else if (lower.contains("legion")) type = SPLINTERS_LEGION;
                else type = SPLINTERS;
            }
            else if (lower.contains("currency")) {
                if (lower.contains("special") || lower.contains("exotic")) type = EXOTIC_CURRENCY;
                else type = CURRENCY;
            }
            else if (lower.contains("ultimatum") || lower.contains("inscribed")) {
                type = INSCRIBED_ULTIMATUM;
            }
            else if (lower.contains("scouting") || lower.contains("report")) {
                type = SCOUTING_REPORT;
            }
            if (type == null) {
                throw new InvalidLootFormatException("Invalid Format to parse Loot: Cannot parse Loottype");
            }
        }
        return type;
    }

    public static Class<? extends Loot> typeToClass(LootType type) {
        if (contains(stackable, type)) {
            return StackableLoot.class;
        }
        if (contains(corrImplicits, type)) {
            return ImplicitCorruptedItem.class;
        }
        if (contains(corruptedMaps, type)) {
            return CorruptedMapLoot.class;
        }
        if (contains(maps, type)) {
            return MapLoot.class;
        }
        if (contains(gems, type)) {
            return GemLoot.class;
        }
        if (contains(crafts, type)) {
            return CraftingBenchLoot.class;
        }
        return Loot.class;
    }

}
