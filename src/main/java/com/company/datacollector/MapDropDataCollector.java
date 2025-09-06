package com.company.datacollector;

import com.company.datacollector.Survey.Question;
import com.company.datasets.datasets.MapDropDataSet;
import com.company.utils.ParseUtils;

import java.util.List;

public class MapDropDataCollector extends DataCollector<MapDropDataSet> {

    public MapDropDataCollector() {
        super(MapDropDataSet.class, MapDropDataSet::builder);
    }

    @Override
    protected List<Question> getQuestions() {
        return List.of(
                Question.ask("conversionType&conversionChance", "Enter the chance for converting maps from Influencing Scarab of conversion and conversion type.\ns:shaper, e:elder, c:conqueror, y:synthesis")
                        .regex("^\\s*$|^[1-9]\\d*[secy]$")
                        .normalizers(ParseUtils::toMapConversionType, ParseUtils::toConversionChance)
                        .build(),

                Question.ask("mapsInOrder", "Enter maps dropped.\nr:regular, s:shaper, e:elder, c:conqueror, y:synthesis, t:t17, u:unique, o: originator")
                        .regex("^$|^[rsecytuo\\-]*$")
                        .normalize((s, a) -> ParseUtils.toMapDropList(s.asString()))
                        .build(),

                Question.ask("bossDrops", "Enter maps dropped by boss.\nEmpty for not killing boss, - for no drops")
                        .regex("^-?$|^[rsecytuo,]*$")
                        .normalize(ParseUtils::toBossMapDropList)
                        .build()
        );
    }

}
