package com.company.datasets.other.jun;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Collection;
import java.util.List;

@NoArgsConstructor(force = true)
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Encounter {

    @JsonProperty("safehouse")
    private final Safehouse.SafehouseType safehouse;

    @JsonProperty("members")
    private final Collection<Member> members;

    @JsonProperty("actions")
    private final List<Action> actions;

    @JsonIgnore
    private boolean junTree;

    @JsonIgnore
    private List<Member> revealed;

}
