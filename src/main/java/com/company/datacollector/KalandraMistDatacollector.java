package com.company.datacollector;

import com.company.datacollector.Survey.Question;
import com.company.datasets.datasets.KalandraMistDataSet;
import com.company.utils.ParseUtils;

import java.util.List;

public class KalandraMistDatacollector extends DataCollector<KalandraMistDataSet> {

    public KalandraMistDatacollector() {
        super(KalandraMistDataSet.class, KalandraMistDataSet::builder);
    }

    @Override
    protected List<Question> getQuestions() {
        return List.of(
                Question.ask("type&tier", "What type of mist does this item come from?")
                        .regex("^in map$|^itemized( guff [1-3])?$|^lake \\d+$")
                        .normalizers(ParseUtils::parseMistType, ParseUtils::parseTier)
                        .build(),

                Question.ask("amountPositive&amountNegative&amountNeutral", "Enter how many mods are positive, negative or neutral")
                        .regex("^\\d+\\/\\d+(\\/\\d+)?$")
                        .normalizers(ParseUtils::parseAmountPos, ParseUtils::parseAmountNeg, ParseUtils::parseAmountNeut)
                        .build(),

                Question.ask("multiplier", "Enter the multiplier. Leave Empty to skip")
                        .regex("^$|^\\d+\\.?\\d*$")
                        .emptyToNull()
                        .build(),

                Question.ask("itemType", "What type is the item?")
                        .options(new String[]{"a", "r"}, new String[]{"amulet", "ring"})
                        .dontValidate()
                        .normalize(ParseUtils::parseToMistLoottype)
                        .emptyToNull()
                        .build(),

                Question.ask("itemText", "Paste the item text")
                        .multiline()
                        .build()
        );
    }

}
