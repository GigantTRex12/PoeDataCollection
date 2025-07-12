package com.company.datasets.other.jun;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import static com.company.datasets.other.jun.Relation.RelationType.TRUSTED;

@NoArgsConstructor(force = true)
@AllArgsConstructor
@Getter
public class Relation {

    @NonNull
    @JsonProperty("type")
    private final RelationType type;

    @NonNull
    @JsonProperty("member1")
    private final Member.MemberName member1;
    @NonNull
    @JsonProperty("member2")
    private final Member.MemberName member2;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Relation relation = (Relation) o;

        if (member1 != relation.member1 && member1 != relation.member2) {return false;}
        if (member1 != relation.member1 && member2 != relation.member1) {return false;}
        if (member2 != relation.member2 && member2 != relation.member1) {return false;}
        if (member2 != relation.member2 && member1 != relation.member2) {return false;}

        return type == relation.type;
    }

    @Override
    public int hashCode() {
        return (member1.hashCode() + member2.hashCode()) * (42 + type.hashCode());
    }

    @Override
    public String toString() {
        String rep = type == TRUSTED ? "Trusted: " : "Rivals:";
        rep += member1.name();
        rep += "-";
        rep += member2.name();
        return rep;
    }

    public enum RelationType {
        TRUSTED, RIVALS
    }
}
