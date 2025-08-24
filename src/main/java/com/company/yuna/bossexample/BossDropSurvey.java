package com.company.yuna.bossexample;

import berlin.yuna.typemap.model.LinkedTypeMap;
import com.company.datasets.datasets.BossDropDataSet;
import com.company.datasets.other.loot.Loot;
import com.company.datasets.other.metadata.Strategy;
import com.company.utils.ParseUtils;
import com.company.utils.Utils;
import com.company.yuna.Question;
import com.company.yuna.Survey;
import com.fasterxml.jackson.core.JsonProcessingException;

import static com.company.yuna.bossexample.Normalizers.*;
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
 *   <li><b>Code:</b> Fewer complexity when handling Annotations.</li>
 *   <li><b>Security & portability:</b> No deep reflective access, fewer permissions, works in
 *       restricted/serverless runtimes. No ObjectMapper or JSON handler needed which comes with a couple of CVE's</li>
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

    public static void main(final String[] args) throws JsonProcessingException {
        final Strategy strategy = new Strategy(null, "3.26", null, null, null, null, null, null);
        final LinkedTypeMap answers = Survey.run(strategy,
                Question.ask("boss", "Enter the name of the boss.")
                        .validate(isNotEmpty("Boss name required"))
                        .normalize(TO_STRING)
                        .build(),

                // order = 1
                Question.ask("uber", "Is the boss uber?")
                        .options(new String[]{"y", "n"})
                        .normalize(TO_BOOLEAN)
                        .build(),

                // order = 3
                Question.ask("witnessed", "Was the boss witnessed by the Maven?")
                        .options(new String[]{"y", "n"})
                        .normalize(TO_BOOLEAN)
                        .build(),

                // order = 4, emptyToNull = true, parsingFunc = parseToBossLoot
                Question.ask("guaranteedDrop", "Which unique was the guaranteed drop?")
                        .normalize(ParseUtils::parseToBossLoot)
                        .build(),

                // order = 5, multiline = true, parsingFunc = toLootList
                Question.ask("extraDrops", "Input extra drops to track.")
                        .normalize(ParseUtils::toLootList)
                        .multiline()
                        .build(),

                // order = 6, regex ^$|^\d+$, emptyToNull = true, parsingFunc = toInt
                Question.ask("quantity", "Enter the area quantity.")
                        .regex("^$|^\\d+$")
                        .normalize(TO_INTEGER)
                        .emptyToNull()
                        .build()
        );

        final BossDropDataSet dataSet = toDataSet(answers);
        System.out.println("\nAnswers:\n" + answers.toJson());
        System.out.println("\nBuilt dataset:\n" + dataSet);
    }

    public static BossDropDataSet toDataSet(final LinkedTypeMap answers) throws JsonProcessingException {
        return Utils.parseJson(Utils.toJson(answers), BossDropDataSet.class);
    }
}