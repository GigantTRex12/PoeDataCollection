package main.com.company.datacollector;

import main.com.company.datasets.MapDropDataSet;
import main.com.company.datasets.metadata.Strategy;
import main.com.company.exceptions.StrategyCreationInterruptedException;

import static main.com.company.utils.IOUtils.*;
import static main.com.company.utils.Utils.splitToChars;

public class MapDropDataCollector extends DataCollector {
    public MapDropDataCollector(String filename) {
        super(filename);
    }

    @Override
    public void collectData() {
        pickCurrStrat();
        while (true) {
            String action = input("What would you like to do?" +
                    "\nA/Add to add Data, S/Strat/Strategy to pick a different strategy, P/PrintStrats to show available strats, AS/AddStrat to add a new Strat" +
                    "\nEnter anything else to exit.")
                    .toLowerCase();
            if (action.equals("a") || action.equals("add")) {
                int conversionChance = inputInteger("Enter the chance for converting maps from Influencing Scarab of conversion" +
                        "\nEnter 0 or a negative number if not applicable");

                char conversionType = '_';
                if (conversionChance > 0) {
                    conversionType = input("Enter the type of conversion." +
                            "\ns:shaper, e:elder, c:conqueror, y:synthesis").charAt(0);
                }

                String mapDrops = input("Enter maps dropped in format r-r-r" +
                        "\nr:regular, s:shaper, e:elder, c:conqueror, y:synthesis, t:t17, u:unique" +
                        "\nType exit to throw away dataset",
                        "^(([rsecytuRSECYTU](-[rsecytuRSECYTU])*)|([eE][xX][iI][tT]))$")
                        .toLowerCase();
                if (mapDrops.equals("exit")) {
                    continue;
                }
                char[] maps = splitToChars(mapDrops, '-');

                String bossDrops = input("Enter maps dropped by boss in format r,r" +
                        "\nLeave empty for not killing boss and enter \"-\" for no drops" +
                        "\nType exit to throw away dataset",
                        "^(([rsecytuRSECYTU](,[rsecytuRSECYTU])*)|([eE][xX][iI][tT])|-|)$")
                        .toLowerCase();
                if (bossDrops.equals("exit")) {
                    continue;
                }
                char[] bossArray;
                if (bossDrops.equals("")) {
                    bossArray = null;
                }
                else if (bossDrops.equals("-")) {
                    bossArray = new char[0];
                }
                else {
                    bossArray = splitToChars(bossDrops, ',');
                }

                this.dataSets.add(new MapDropDataSet(currStrat, conversionChance, conversionType, maps, bossArray));
            }
            else if (action.equals("s") || action.equals("strat") || action.equals("strategy")) {
                pickCurrStrat();
            }
            else if (action.equals("p") || action.equals("printstrats")) {
                printList(Strategy.getAllList());
            }
            else if (action.equals("as") || action.equals("addstrat")) {
                try {
                    Strategy newStrat = Strategy.create();
                    currStrat = newStrat;
                    print("Added new Strategy and switched to new Strategy");
                } catch (StrategyCreationInterruptedException e) {
                    print("Strategy creation stopped");
                }
            }
            else {
                break;
            }
        }
        this.addAllDataToFile();
    }
}
