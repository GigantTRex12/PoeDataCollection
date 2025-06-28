package com.company.datacollector;

import com.company.datasets.datasets.MapDropDataSet;

import static com.company.utils.IOUtils.input;
import static com.company.utils.Utils.splitToChars;

public class MapDropDataCollectorNew extends DataCollectorNew<MapDropDataSet> {

    public MapDropDataCollectorNew() {
        super();
    }

    @Override
    protected void addData() {
        // conversionChance and conversionType
        String conversion = input("Enter the chance for converting maps from Influencing Scarab of conversion and conversion type.\n" +
                "s:shaper, e:elder, c:conqueror, y:synthesis", "^\\s*$|^[1-9]\\d*[secy]$");
        char conversionType = '_';
        int conversionChance = 0;
        if (!conversion.isBlank()) {
            conversionType = conversion.charAt(conversion.length() - 1);
            conversionChance = Integer.parseInt(conversion.substring(0, conversion.length() - 1));
        }

        // mapsInOrder
        String maps = input("Enter maps dropped.\nr:regular, s:shaper, e:elder, c:conqueror, y:synthesis, t:t17, u:unique",
                "^$|^[rsecytu](-[rsecytu])*$");
        char[] mapDrops = splitToChars(maps, '-');

        // bossDrops
        String bossMaps = input("Enter maps dropped by boss.\nEmpty for not killing boss, - for no drops",
                "^-?$|^[rsecytu](,[rsecytu])*$");
        char[] bossDrops;
        if (bossMaps.isEmpty()) {
            bossDrops = null;
        }
        else if (bossMaps.equals("-")) {
            bossDrops = new char[0];
        }
        else {
            bossDrops = splitToChars(bossMaps, ',');
        }

        // creating DataSet
        this.data.add(new MapDropDataSet(currStrat, conversionChance, conversionType, mapDrops, bossDrops));
    }
}
