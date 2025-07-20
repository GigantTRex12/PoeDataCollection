package com.company.datasets.other.jun;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

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
    private final List<List<Action>> actions;

    @JsonIgnore
    private boolean junTree;

    @JsonProperty("revealed")
    private List<Member> revealed;

    public List<Action> getDoneActions() {
        return actions == null ? new ArrayList<>() : actions.stream()
                .map(actionList -> actionList.get(actionList.size() - 1))
                .collect(Collectors.toList());
    }

}
