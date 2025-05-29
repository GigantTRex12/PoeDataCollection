package com.company.DataCollectors;

import com.company.DataTypes.KalandraMistDataSet;
import com.company.DataTypes.LootType;
import com.company.Exceptions.InvalidInputFormatException;

import static com.company.IOSystem.*;
import static com.company.Utilities.isNumber;
import static com.company.Utilities.join;

public class KalandraMistDataCollector extends DataCollector {
    public KalandraMistDataCollector(String filename) {
        super(filename);
    }

    @Override
    public void collectData() {
        pickCurrStrat();
        while (true) {
            String action = input("What would you like to do? Enter Add/A to add Data, f/AddFull to add from single String or anything else to exit.\nInput exit at any time to throw away item.")
                    .toLowerCase();
            if (action.equals("a") || action.equals("add")) {
                String[] options = {"in map", "itemized", "lake", "exit"};
                String type = input("What type of mist does this item come from?", options)
                        .toLowerCase();
                if (type.equals("exit")) {
                    continue;
                }
                KalandraMistDataSet.MistType mistType = null;
                Integer tier = null;
                switch (type) {
                    case "in map":
                        mistType = KalandraMistDataSet.MistType.IN_MAP;
                        break;
                    case "itemized":
                        mistType = KalandraMistDataSet.MistType.ITEMIZED;
                        break;
                    case "lake":
                        mistType = KalandraMistDataSet.MistType.LAKE;
                        tier = inputInteger("What tier was the lake?");
                        break;
                }

                byte pos;
                byte neg;
                byte neut = 0;
                boolean continuing = false;
                while (true) {
                    String mods = input("Enter how many mods are positive, negative or neutral in format pos/neg/neut (e.g. 2/3)");
                    if (mods.equals("exit")) {
                        continuing = true;
                        continue;
                    }
                    try {
                        byte[] amounts = parseToAmounts(mods);
                        pos = amounts[0];
                        neg = amounts[1];
                        if (amounts.length >= 3) {
                            neut = amounts[2];
                        }
                        break;
                    } catch (InvalidInputFormatException e) {
                        print("Invalid Format");
                    }
                }
                if (continuing) {
                    continue;
                }

                String multiplier = input("Enter the multiplier. Leave Empty to skip",
                        "^$|^[eE][xX][iI][tT]$|^\\d+\\.\\d*$").toLowerCase();
                if (multiplier.equals("exit")) {
                    continue;
                }
                if (multiplier.trim().isEmpty()) {
                    multiplier = null;
                }

                LootType itemType = null;
                String itemT = input("What type is the item? (Ring/R/Amulet/A)")
                        .toLowerCase();
                if (itemT.equals("exit")) {
                    continue;
                }
                if (itemT.equals("r") || itemT.equals("ring")) {
                    itemType = LootType.RARE_JEWELLRY_RING;
                }
                else if (itemT.equals("a") || itemT.equals("amulet")) {
                    itemType = LootType.RARE_JEWELLRY_AMULET;
                }

                String itemText = multiLineInput("Paste the item text");

                this.dataSets.add(new KalandraMistDataSet(currStrat, mistType, tier, pos, neg, neut, itemText, itemType, multiplier));
            }
            else if (action.equals("f") || action.equals("addfull")) {
                String text = multiLineInput("Paste your text");
                String[] lines = text.split("\n");
                if (lines.length < 2) {
                    print("Invalid format: lines");
                    continue;
                }

                KalandraMistDataSet.MistType type;
                Integer tier = null;
                if (lines[0].equalsIgnoreCase("in map") || lines[0].equalsIgnoreCase("in-map")) {
                    type = KalandraMistDataSet.MistType.IN_MAP;
                }
                else if (lines[0].equalsIgnoreCase("itemized")) {
                    type = KalandraMistDataSet.MistType.ITEMIZED;
                }
                else if (lines[0].toLowerCase().startsWith("lake")) {
                    type = KalandraMistDataSet.MistType.LAKE;
                    String[] split = lines[0].split(" ");
                    if (split.length < 2) {
                        print("Invalid format: mist type");
                        continue;
                    }
                    tier = Integer.parseInt(split[1]);
                }
                else {
                    print("Invalid format: mist type");
                    continue;
                }

                byte[] amounts;
                try {
                    amounts = parseToAmounts(lines[1]);
                } catch (InvalidInputFormatException e) {
                    print("Invalid format: amounts");
                    continue;
                }
                byte pos = amounts[0];
                byte neg = amounts[1];
                byte neut = 0;
                if (amounts.length >= 3) {
                    neut = amounts[2];
                }

                LootType itemType = null;
                String multiplier = null;
                int descStart = 2;
                if (lines.length >= 3) {
                    if (lines[2].equals("Item Class: Rings")) {
                        itemType = LootType.RARE_JEWELLRY_RING;
                    } else if (lines[2].equals("Item Class: Amulets")) {
                        itemType = LootType.RARE_JEWELLRY_AMULET;
                    } else if (isNumber(lines[2])) {
                        multiplier = lines[2];
                        descStart = 3;
                        if (lines.length >= 4) {
                            if (lines[3].equals("Item Class: Rings")) {
                                itemType = LootType.RARE_JEWELLRY_RING;
                            } else if (lines[3].equals("Item Class: Amulets")) {
                                itemType = LootType.RARE_JEWELLRY_AMULET;
                            }
                        }
                    }
                }

                String itemText = join(lines, "\n", descStart);

                this.dataSets.add(new KalandraMistDataSet(currStrat, type, tier, pos, neg, neut, itemText, itemType, multiplier));
            }
            else {
                break;
            }
        }
        this.addAllDataToFile();
    }

    private byte[] parseToAmounts(String string) throws InvalidInputFormatException {
        try {
            byte[] result = new byte[2];
            String[] amounts = string.split("/");
            if (amounts.length >= 3) {
                result = new byte[3];
                result[2] = Byte.parseByte(amounts[2]);
            }
            if (amounts.length >= 2) {
                result[0] = Byte.parseByte(amounts[0]);
                result[1] = Byte.parseByte(amounts[1]);
            } else {
                throw new InvalidInputFormatException("Invalid format");
            }
            return result;
        }
        catch (NumberFormatException e) {
            throw new InvalidInputFormatException("Invalid format");
        }
    }
}
