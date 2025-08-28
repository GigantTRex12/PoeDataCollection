package com.company.datasets.other;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor(force = true)
@Getter
public class UniqueAndGoldCostPair {

    @JsonProperty("uniqueName")
    private final String uniqueName;
    @JsonProperty("goldCost")
    private final Integer goldCost;

}
