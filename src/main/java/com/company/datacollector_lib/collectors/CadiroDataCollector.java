package com.company.datacollector_lib.collectors;

import collector.Question;
import com.company.api.DbWriter;
import com.company.datacollector_lib.DataCollector;
import com.company.dataset_lib.datasets.CadiroDataSet;
import com.company.dataset_lib.other.UniqueAndGoldCostPair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.company.utils.IOUtils.print;

public class CadiroDataCollector extends DataCollector<CadiroDataSet> {

    @Override
    protected List<Question> getQuestions() {
        return List.of(
                Question.ask("tier", "What is the tier?")
                        .regex("^[1-3]$")
                        .normalize(str -> Integer.parseInt(str))
                        .build(),
                Question.ask("uniquesWithCost", "Enter the prices.")
                        .multiline()
                        .regex("^.* \\d+$")
                        .normalize(CadiroDataCollector::toUniqueCostPairs)
                        .build()
        );
    }

    @Override
    protected CadiroDataSet mapToDataset(Map<String, Object> map) {
        return new CadiroDataSet(
                this.getMetadata(),
                (Integer) map.get("tier"),
                ((List<?>) map.get("uniquesWithCost")).stream().map(o -> (UniqueAndGoldCostPair) o).collect(Collectors.toList())
        );
    }

    @Override
    protected void saveData() {
        DbWriter.writeCadiroDataSets(this.data);
    }

    private static List<UniqueAndGoldCostPair> toUniqueCostPairs(String string) {
        if (string.isEmpty()) return List.of();
        List<UniqueAndGoldCostPair> pairs = new ArrayList<>();
        for (String s : string.split("\n")) {
            Matcher matcher = Pattern.compile("^(.*) (\\d+)$").matcher(s);
            if (matcher.find())
                pairs.add(new UniqueAndGoldCostPair(matcher.group(1), Integer.parseInt(matcher.group(2))));
            else print("Couldn't parse \"" + s + "\" to Cost. (skipped)");
        }
        return pairs;
    }

}
