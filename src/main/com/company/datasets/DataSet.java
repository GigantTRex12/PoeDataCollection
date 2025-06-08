package main.com.company.datasets;

import main.com.company.datasets.metadata.Strategy;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor(force = true)
@AllArgsConstructor
@Getter
@ToString
public abstract class DataSet {
    @JsonProperty("strategy")
    private final Strategy strategy;
}