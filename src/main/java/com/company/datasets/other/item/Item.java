package com.company.datasets.other.item;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@Setter
@AllArgsConstructor
public class Item {
    private final ItemType type;
    @NonNull
    private List<ItemEffect> effects;
    private List<Mod> implicits;
    @NonNull
    private List<Mod> prefixes;
    @NonNull
    private List<Mod> suffixes;
    private List<Mod> enchantments;

    private List<Mod> getAffixes() {
        return Stream.concat(prefixes.stream(), suffixes.stream()).collect(Collectors.toList());
    }
}
