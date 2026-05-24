package com.company.datasets.datasets;

import com.company.datasets.annotations.Evaluate;
import com.company.datasets.annotations.Groupable;
import com.company.datasets.annotations.InputProperty;
import com.company.datasets.builder.DataSetBuilderInterface;
import com.company.datasets.other.loot.Loot;
import com.company.datasets.other.metadata.Strategy;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.company.datasets.annotations.Evaluate.EvaluationMode.COUNTER_BASED;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@NoArgsConstructor(force = true)
@Getter
@ToString(callSuper = true)
public class BossDropDataSet extends DataSet {

    @JsonProperty("boss")
    @InputProperty(message = "Enter the name of the boss.", order = 0)
    private final String bossName;

    @JsonProperty("uber")
    @InputProperty(message = "Is the boss uber?", options = {"y", "n"}, order = 1, parsingFunc = "toBool")
    private final boolean uber;

    @JsonProperty("witnessed")
    @InputProperty(message = "Was the boss witnessed by the Maven?", options = {"y", "n"}, order = 3, parsingFunc = "toBool")
    @Groupable(order = 2, filterByValue = true)
    private final boolean witnessed;

    @JsonProperty("guaranteedDrop")
    @InputProperty(message = "Which unique was the guaranteed drop?", order = 4, parsingFunc = "parseToBossLoot", emptyToNull = true)
    @Evaluate(order = 0)
    private final Loot guaranteedDrop;

    @JsonProperty("extraDrops")
    @InputProperty(message = "Input extra drops to track.", order = 5, parsingFunc = "toLootList", multiline = true)
    @Evaluate(order = 1, evaluationMode = COUNTER_BASED)
    private final List<Loot> extraDrops;

    @JsonProperty("quantity")
    @JsonInclude(NON_NULL)
    @InputProperty(message = "Enter the area quantity.", regex = "^$|^\\d+$", order = 6, parsingFunc = "toInt", emptyToNull = true)
    private final Integer quantity;

    @Builder
    public BossDropDataSet(Strategy strategy, String bossName, boolean uber, boolean witnessed, Loot guaranteedDrop, List<Loot> extraDrops, Integer quantity) {
        super(strategy);
        this.bossName = bossName;
        this.uber = uber;
        this.witnessed = witnessed;
        this.guaranteedDrop = guaranteedDrop;
        this.extraDrops = extraDrops;
        this.quantity = quantity;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        BossDropDataSet that = (BossDropDataSet) o;
        if (extraDrops == null || that.extraDrops == null) {
            if (!(extraDrops == null && that.extraDrops == null)) return false;
        }
        else if (extraDrops.size() == that.extraDrops.size()) {
            List<Loot> copy = new ArrayList<>(that.extraDrops);
            for (Loot l : extraDrops) {
                if (!copy.remove(l)) return false;
            }
        }
        else return false;
        return witnessed == that.witnessed && Objects.equals(lowerCaseBossname(), that.lowerCaseBossname()) && Objects.equals(guaranteedDrop, that.guaranteedDrop) && Objects.equals(quantity, that.quantity);
    }

    @Override
    public int hashCode() {
        return 49 * super.hashCode()
                + 23 * lowerCaseBossname().hashCode()
                + (witnessed ? 37 : 53)
                + 17 * (guaranteedDrop == null ? 2 : guaranteedDrop.hashCode())
                + 29 * (extraDrops.stream().mapToInt(Loot::hashCode).sum() + 5)
                + 19 * (quantity == null ? 2 : quantity.hashCode());
    }

    @Groupable(order = 1, force = true, filterByValue = true)
    public String lowerCaseBossname() {
        if (uber) return "UBER " + bossName.toLowerCase();
        return bossName.toLowerCase();
    }

    @Groupable(order = 3, ignoreNulls = true, filterByValue = true)
    public Integer quantInStepsOfTen() {
        return quantity != null ? (quantity / 10) * 10 : null;
    }

    public static class BossDropDataSetBuilder implements DataSetBuilderInterface<BossDropDataSet> {

        public BossDropDataSetBuilder() {
            this.extraDrops = new ArrayList<>();
        }

        public BossDropDataSetBuilder extraDrop(Loot l) {
            this.extraDrops.add(l);
            return this;
        }

    }

}
