package com.company.datacollector;

import com.company.datacollector.Survey.Question;
import com.company.datasets.datasets.UltimatumDataSet;
import com.company.datasets.other.loot.Loot;
import com.company.utils.ParseUtils;

import java.util.List;

import static com.company.utils.IOUtils.print;

public class UltimatumDataCollector extends DataCollector<UltimatumDataSet> {

    public UltimatumDataCollector() {
        super(UltimatumDataSet.class, UltimatumDataSet::builder);
    }

    @Override
    protected boolean validateDataSet(UltimatumDataSet dataSet) {
        if (dataSet.getRewards().isEmpty()) {
            print("Reward list should not be empty");
            return false;
        }
        return true;
    }

    @Override
    protected List<Question> getQuestions() {
        return List.of(
                Question.ask("rewardList", "Enter rewards from Ultimatum in order. (one line per reward)\nEnter \"-\" to skip reward.")
                        .normalize(ParseUtils::toLootList)
                        .multiline()
                        .build(),

                Question.ask("boss", "Was a boss encountered?")
                        .when(m -> ((List<Loot>) m.get("rewardList")).size() == 10)
                        .options(new String[]{"y", "n"}, new String[]{"yes", "no"})
                        .normalize(ParseUtils::toBool)
                        .build(),

                Question.ask("bossLoot", "Enter drops from boss.")
                        .when(m -> m.get("boss") != null && (boolean) m.get("boss"))
                        .normalize(ParseUtils::toLootList)
                        .multiline()
                        .build()
        );
    }

}
