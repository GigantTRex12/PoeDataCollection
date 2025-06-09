package main.com.company.datacollector;

import main.com.company.datasets.loot.Loot;
import main.com.company.datasets.metadata.Strategy;
import main.com.company.datasets.UltimatumDataSet;
import main.com.company.exceptions.InvalidLootFormatException;
import main.com.company.exceptions.StrategyCreationInterruptedException;

import java.util.ArrayList;
import java.util.List;

import static main.com.company.utils.IOUtils.*;

public class UltimatumDataCollector extends DataCollector {
    public UltimatumDataCollector(String filename) {
        super(filename);
    }

    @Override
    public void collectData() {
        pickCurrStrat();
        while (true) {
            String action = input("What would you like to do?" +
                    "\nA/Add to add Data, S/Strat/Strategy to pick a different strategy," +
                    "\nP/PrintStrats to show available strats, AS/AddStrat to add a new Strat" +
                    "\nStore to store Data. Enter anything else to exit.")
                    .toLowerCase();
            if (action.equals("a") || action.equals("add")) {
                List<Loot> rewards = new ArrayList<>();
                while (true) {
                    print("Currently " + rewards.size() + " rewards in loot list");
                    String loot = input("Enter the next reward" +
                            "\nEnter empty String to skip reward" +
                            "\nEnter end to finish");
                    if (loot.equalsIgnoreCase("end")) {
                        break;
                    }
                    if (loot.isEmpty()) {
                        rewards.add(null);
                        continue;
                    }
                    Loot reward;
                    try {
                        reward = Loot.parseToLoot(loot);
                    }
                    catch (InvalidLootFormatException e) {
                        print(e.getMessage());
                        continue;
                    }
                    print(reward);
                    String[] options = {"y", "yes", "n", "no"};
                    String validation = input("Is that correct?", options).toLowerCase();
                    if (validation.equals("y") || validation.equals("yes")) {
                        rewards.add(reward);
                    }
                }

                String[] options = {"y", "yes", "n", "no", "", "exit"};
                String bossRep = input("Was a boss encountered?", options).toLowerCase();
                if (bossRep.equals("exit")) {
                    continue;
                }
                UltimatumDataSet newData;
                if (bossRep.equals("y") || bossRep.equals("yes")) {
                    boolean stop = false;
                    List<Loot> bossLoot = new ArrayList<>();
                    while (true) {
                        print("Currently " + bossLoot.size() + " rewards in loot list");
                        String loot = input("Enter the next reward" +
                                "\nEnter end to finish" +
                                "\nEnter exit to throw away dataset");
                        if (loot.equalsIgnoreCase("end")) {
                            break;
                        }
                        if (loot.equalsIgnoreCase("exit")) {
                            stop = true;
                            continue;
                        }
                        try {
                            bossLoot.add(Loot.parseToLoot(loot));
                        }
                        catch (InvalidLootFormatException e) {
                            print(e.getMessage());
                        }
                    }
                    if (stop) {
                        continue;
                    }
                    newData = new UltimatumDataSet(currStrat, rewards, true, bossLoot);
                }
                else {
                    newData = new UltimatumDataSet(currStrat, rewards);
                }
                this.dataSets.add(newData);
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
            else if (action.equals("store")) {
                this.addAllDataToFile();
            }
            else {
                break;
            }
        }
        this.addAllDataToFile();
    }
}
