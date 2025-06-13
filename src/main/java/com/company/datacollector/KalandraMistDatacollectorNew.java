package main.com.company.datacollector;

import main.com.company.datasets.KalandraMistDataSet;
import main.com.company.datasets.loot.LootType;

import java.io.IOException;
import java.util.Map;

import static java.util.Map.entry;
import static main.com.company.utils.IOUtils.input;
import static main.com.company.utils.IOUtils.multiLineInput;

public class KalandraMistDatacollectorNew extends DataCollectorNew<KalandraMistDataSet> {
    private static final String mistFileName = "Data/mist.txt";

    public KalandraMistDatacollectorNew() throws IOException {
        super(mistFileName);
    }

    @Override
    protected void addData() {
        // type and lakeTier
        String type = input("What type of mist does this item come from?", "^in map$|^itemized$|^lake \\d+$")
                .toLowerCase();
        KalandraMistDataSet.MistType mistType;
        Integer tier = null;
        switch (type) {
            case "in map" -> mistType = KalandraMistDataSet.MistType.IN_MAP;
            case "itemized" -> mistType = KalandraMistDataSet.MistType.ITEMIZED;
            default -> {
                mistType = KalandraMistDataSet.MistType.LAKE;
                tier = Integer.parseInt(type.substring(5));
            }
        }

        // amountPositive, amountNegative and amountNeutral
        String mods = input("Enter how many mods are positive, negative or neutral", "\\d+\\/\\d+(\\/\\d+)?");
        String[] split = mods.split("/");
        byte pos = Byte.parseByte(split[0]);
        byte neg = Byte.parseByte(split[1]);
        byte neut = 0;
        if (split.length >= 3) {
            neut = Byte.parseByte(split[2]);
        }

        // multiplier
        String multiplier = input("Enter the multiplier. Leave Empty to skip",
                "^$|^\\d+\\.\\d*$").toLowerCase();
        if (multiplier.trim().isEmpty()) {
            multiplier = null;
        }

        // itemType
        LootType itemType = null;
        String itemT = input("What type is the item?", Map.ofEntries(entry("Ring", "r"), entry("Amulet", "a")), false)
                .toLowerCase();
        if (itemT.equals("r") || itemT.equals("ring")) {
            itemType = LootType.RARE_JEWELLRY_RING;
        }
        else if (itemT.equals("a") || itemT.equals("amulet")) {
            itemType = LootType.RARE_JEWELLRY_AMULET;
        }

        // itemText
        String itemText = multiLineInput("Paste the item text");

        // creating DataSet
        this.data.add(new KalandraMistDataSet(currStrat, mistType, tier, pos, neg, neut, itemText, itemType, multiplier));
    }
}
