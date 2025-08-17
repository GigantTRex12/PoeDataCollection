package com.company.yuna.bossexample;

import berlin.yuna.typemap.model.LinkedTypeMap;
import com.company.datasets.datasets.BossDropDataSet;
import com.company.datasets.other.loot.Loot;
import com.company.datasets.other.metadata.Strategy;
import com.company.yuna.Question;
import com.company.yuna.Survey;

import static com.company.yuna.bossexample.Normalizers.TO_BOOLEAN;
import static com.company.yuna.bossexample.Normalizers.TO_INTEGER;
import static com.company.yuna.bossexample.Normalizers.TO_LOOT;
import static com.company.yuna.bossexample.Normalizers.TO_STRING;
import static com.company.yuna.bossexample.Validators.IS_NUMBER;
import static com.company.yuna.bossexample.Validators.VALIDATE_YES_NO;
import static com.company.yuna.bossexample.Validators.isNotEmpty;

/**
 * Builds a {@code BossDropDataSet} using an explicit, reflection-free survey flow.
 *
 * <p>This class demonstrates how to collect user input with {@link Survey} and {@link Question}
 * and then map the resulting values into a domain object without relying on annotations or
 * reflection. All behavior (order, validation, normalization, and conditional logic) is defined
 * in code, not metadata.</p>
 *
 * <h2>Why no annotations / reflection?</h2>
 * <ul>
 *   <li><b>Deterministic control flow:</b> Questions run in the exact order they are declared;
 *       no classpath scanning or annotation ordering quirks.</li>
 *   <li><b>Performance & startup:</b> No reflective lookups, faster cold starts, fewer surprises
 *       under JIT and AOT.</li>
 *   <li><b>GraalVM/AOT friendly:</b> Avoids reflection configs and keeps native images small and
 *       predictable.</li>
 *   <li><b>Type-safety at boundaries:</b> Validation/normalization are plain functions; conversions
 *       are explicit and testable.</li>
 *   <li><b>Testability:</b> Pure functions and simple wiring; no annotation processors or runtime
 *       scanners to mock.</li>
 *   <li><b>Security & portability:</b> No deep reflective access, fewer permissions, works in
 *       restricted/serverless runtimes.</li>
 *   <li><b>Maintainability:</b> Behavior lives in code, making refactors obvious and reviewable.</li>
 * </ul>
 *
 * <p>Flow: define questions → run {@link Survey#run} → map {@link LinkedTypeMap} to
 * {@link BossDropDataSet} in {@code toDataSet}.</p>
 *
 * @see Survey
 * @see Question
 * @see BossDropDataSet
 */
public class BossDropSurvey {

    public static void main(final String[] args) {
        final LinkedTypeMap answers = Survey.run(
                Question.ask("bossName", "Enter the name of the boss.")
                        .validate(isNotEmpty("Boss name required"))
                        .normalize(TO_STRING)
                        .build(),

                // order = 1
                Question.ask("uber", "Is the boss uber? (y/n)")
                        .validate(VALIDATE_YES_NO)
                        .normalize(TO_BOOLEAN)
                        .build(),

                // order = 3, grouped, but grouping is dataset-level concern; we just collect the value
                Question.ask("witnessed", "Was the boss witnessed by the Maven? (y/n)")
                        .validate(VALIDATE_YES_NO)
                        .normalize(TO_BOOLEAN)
                        .build(),

                // order = 4, emptyToNull = true, parsingFunc = parseToBossLoot
                Question.ask("guaranteedDrop", "Which unique was the guaranteed drop?")
                        .normalize(TO_LOOT)
                        .build(),

                // order = 5, multiline = true, parsingFunc = toLootList
                Question.ask("extraDrops", "Input extra drops to track. (comma or newline separated)")
                        .normalize(TO_LOOT)
                        .build(),

                // order = 6, regex ^$|^\d+$, emptyToNull = true, parsingFunc = toInt
                Question.ask("quantity", "Enter the area quantity.")
                        .validate(IS_NUMBER)
                        .normalize(TO_INTEGER)
                        .build()
        );

        // No idea what this is :D
        final Strategy strategy = new Strategy(null, null, null, null, null, null, null, null);

        final BossDropDataSet dataSet = toDataSet(answers, strategy);
        System.out.println("\nAnswers:\n" + answers.toJson());
        System.out.println("\nBuilt dataset:\n" + dataSet);
    }

    // FIXME: Could be moved to the Constructor of BossDropDataSet
    // FIXME: DataSet objects aren't needed at all if the further processing is done on the simple TypeMap - no objectmapper or other heavy libs needed
    public static BossDropDataSet toDataSet(final LinkedTypeMap answers, final Strategy strategy) {
        return BossDropDataSet.builder()
                .strategy(strategy)
                .bossName(answers.asString("bossName"))
                .uber(answers.asBoolean("uber"))
                .witnessed(answers.asBoolean("witnessed"))
                .guaranteedDrop(answers.as(Loot.class, "guaranteedDrop"))
                .extraDrops(answers.asList(Loot.class, "extraDrops"))
                .quantity(answers.asInt())
                .build();
    }
}