package com.company.yuna;

import berlin.yuna.typemap.model.LinkedTypeMap;
import berlin.yuna.typemap.model.Type;
import com.company.datasets.other.metadata.Strategy;

import java.util.List;
import java.util.function.BiFunction;

import static berlin.yuna.typemap.model.Type.typeOf;
import static com.company.yuna.bossexample.Normalizers.TO_INTEGER;
import static com.company.yuna.bossexample.Normalizers.TO_STRING;
import static com.company.yuna.bossexample.Validators.intRange;

public class Main {

    public static void main(String[] args) {
        final List<Question> survey = List.of(
                Question.ask("name", "Your name")
                        .validate(isNotEmpty("Name cannot be empty"))
                        .normalize(TO_STRING)
                        .build(),

                Question.ask("age", "Your age")
                        .validate(intRange(0, 150, "Enter a realistic age (0-150)"))
                        .normalize(TO_INTEGER)
                        .build(),

                Question.ask("coffee", "Do you like coffee? (y/n)")
                        .when(answer -> answer.asIntOpt("age").filter(age -> age >= 13).isPresent())
                        .validate((answer, answers) -> switch (answer.asString().toLowerCase()) {
                            case "y", "yes", "n", "no" -> Type.empty();
                            default -> typeOf("Please answer y/yes or n/no");
                        })
                        .normalize((answer, answers) -> switch (answer.asString().toLowerCase()) {
                            case "y", "yes" -> "yes";
                            default -> "no";
                        })
                        .build()
        );

        final LinkedTypeMap answers = Survey.run(survey, new Strategy());
        System.out.println(answers.toJson());
    }

    private static BiFunction<Type<String>, LinkedTypeMap, Type<String>> isNotEmpty(final String errorMessage) {
        return (answer, answers) -> isNotEmpty(answer) && !answer.value().isBlank()
                ? Type.empty()
                : typeOf(errorMessage);
    }

    private static boolean isNotEmpty(final Type<String> answer) {
        return answer != null && !answer.value().isBlank();
    }
}
