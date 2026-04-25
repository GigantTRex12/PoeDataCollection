package com.company.datacollector_lib.collectors;

import collector.Question;
import com.company.api.DbWriter;
import com.company.datacollector_lib.DataCollector;
import com.company.dataset_lib.datasets.KalandraMistDataSet;
import exceptions.InvalidInputFormatException;

import java.util.List;
import java.util.Map;

import static com.company.dataset_lib.datasets.KalandraMistDataSet.ItemType.AMULET;
import static com.company.dataset_lib.datasets.KalandraMistDataSet.ItemType.RING;
import static com.company.dataset_lib.datasets.KalandraMistDataSet.MistType.*;

public class KalandraMistDataCollector extends DataCollector<KalandraMistDataSet> {

    @Override
    protected List<Question> getQuestions() {
        return List.of(
                Question.ask("type", "What type of mist does this item come from?")
                        .regex("^in map$|^itemized( guff [1-3])?$|^lake \\d+$")
                        .normalize(KalandraMistDataCollector::normalizeMistType)
                        .build(),
                Question.ask("amountPositive", "Enter how many mods are positive, negative or neutral")
                        .regex("^\\d+\\/\\d+(\\/\\d+)?$")
                        .normalize((answer, map) -> {
                            String[] split = answer.split("/");
                            map.put("amountPositive", Integer.parseInt(split[0]));
                            map.put("amountNegative", Integer.parseInt(split[1]));
                            if (split.length > 2) map.put("amountNeutral", Integer.parseInt(split[2]));
                        })
                        .build(),
                Question.ask("multiplier", "Enter the multiplier. Leave Empty to skip")
                        .regex("^$|^\\d+\\.?\\d*$")
                        .emptyToNull()
                        .build(),
                Question.ask("itemType", "What type is the item?")
                        .options(new String[]{"a", "r"}, new String[]{"amulet", "ring"})
                        .dontValidate()
                        .normalize(KalandraMistDataCollector::parseItemType)
                        .emptyToNull()
                        .build(),
                Question.ask("itemText", "Paste the item text")
                        .multiline()
                        .build()
        );
    }

    @Override
    protected KalandraMistDataSet mapToDataset(Map<String, Object> map) {
        return new KalandraMistDataSet(
                this.getMetadata(),
                (KalandraMistDataSet.MistType) map.get("type"),
                (Integer) map.get("tier"),
                (int) map.get("amountPositive"),
                (int) map.get("amountNegative"),
                (int) map.get("amountNeutral"),
                (String) map.get("itemText"),
                (KalandraMistDataSet.ItemType) map.get("itemType"),
                (String) map.get("multiplier")
        );
    }

    @Override
    protected void saveData() {
        DbWriter.writeKalandraMistDataSets(this.data);
        this.data.clear();
    }

    private static void normalizeMistType(String answer, Map<String, Object> map) throws InvalidInputFormatException {
        if (answer == null) {
            throw new InvalidInputFormatException("Answer should not be null");
        }
        if ("in map".equalsIgnoreCase(answer)) {
            map.put("itemType", IN_MAP);
        } else if (answer.toLowerCase().startsWith("itemized guff")) {
            map.put("itemType", ITEMIZED_GUFF);
            map.put("tier", Integer.parseInt(answer.substring(14)));
        } else if ("itemized".equalsIgnoreCase(answer)) {
            map.put("itemType", ITEMIZED);
        } else if (answer.toLowerCase().startsWith("lake")) {
            map.put("itemType", LAKE);
            map.put("tier", Integer.parseInt(answer.substring(5)));
        } else {
            throw new InvalidInputFormatException("Could not parse answer " + answer);
        }
    }

    public static KalandraMistDataSet.ItemType parseItemType(String string) {
        return switch (string.toLowerCase()) {
            case ("a"), ("amulet") -> AMULET;
            case ("r"), ("ring") -> RING;
            default -> null;
        };
    }

}
