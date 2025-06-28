package com.company.datasets.other.item;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import javax.naming.directory.InvalidAttributesException;

@Getter
@Setter
public class Mod {
    private String name;
    @NonNull
    private String effect;
    @NonNull
    private int[] rangesMin;
    @NonNull
    private int[] rangesMax;
    @NonNull
    private int[] rolls;
    private Integer tier;
    private boolean metamod;

    public Mod(
            String name, @NonNull String effect, @NonNull int[] rangesMin, @NonNull int[] rangesMax, @NonNull int[] rolls, Integer tier
    ) throws InvalidAttributesException {
        this(name, effect, rangesMin, rangesMax, rolls, tier, false);
    }

    public Mod(
            String name, @NonNull String effect, @NonNull int[] rangesMin, @NonNull int[] rangesMax, @NonNull int[] rolls, Integer tier, boolean metamod
    ) throws InvalidAttributesException {
        this.name = name;
        this.effect = effect;
        if (rangesMin.length != rangesMax.length || rangesMin.length != rolls.length) {
            throw new InvalidAttributesException();
        }
        this.rangesMin = rangesMin;
        this.rangesMax = rangesMax;
        this.rolls = rolls;
        this.tier = tier;
        this.metamod = metamod;
    }
}
