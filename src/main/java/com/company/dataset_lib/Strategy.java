package com.company.dataset_lib;

import dataset.Metadata;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public class Strategy extends Metadata {

    private final Integer id;
    private final String league;
    private final String tree;
    private final String treeUrl;
    private final String[] scarabs;
    private final String mapLayout;
    private final String mapRolling;
    private final String mapCraft;

}
