package com.company.utils;

import com.company.datasets.datasets.KalandraMistDataSet;
import com.company.datasets.other.loot.LootType;
import com.company.datasets.other.metadata.Strategy;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class FileUtilsTest {

    @Test
    void appendObjects() throws IOException {
        // given
        Path tempfile = Files.createTempFile("test", ".txt");
        tempfile.toFile().deleteOnExit();

        List<KalandraMistDataSet> list = List.of(
                new KalandraMistDataSet(
                        new Strategy(null, "3.25", null, null, null, null, null, null),
                        KalandraMistDataSet.MistType.IN_MAP, null,
                        2, 3, 0,
                        "This is a dummy text.\nThis is only for tests.",
                        LootType.RARE_JEWELLRY_RING,
                        "1.9"
                ),
                new KalandraMistDataSet(
                        new Strategy(null, "3.25", null, null, null, null, null, null),
                        KalandraMistDataSet.MistType.ITEMIZED, null,
                        2, 1, 1,
                        "This is another dummy text.\nThis is only for tests.",
                        LootType.RARE_JEWELLRY_AMULET,
                        "2.1"
                )
        );

        String expectedJson1 = "{\"strategy\":{\"league\":\"3.25\"},\"type\":\"IN_MAP\",\"lakeTier\":null,\"amountPositive\":2,\"amountNegative\":3,\"amountNeutral\":0,\"itemText\":\"This is a dummy text.\\nThis is only for tests.\",\"itemType\":\"RARE_JEWELLRY_RING\",\"multiplier\":\"1.9\"}";
        String expectedJson2 = "{\"strategy\":{\"league\":\"3.25\"},\"type\":\"ITEMIZED\",\"lakeTier\":null,\"amountPositive\":2,\"amountNegative\":1,\"amountNeutral\":1,\"itemText\":\"This is another dummy text.\\nThis is only for tests.\",\"itemType\":\"RARE_JEWELLRY_AMULET\",\"multiplier\":\"2.1\"}";

        // when
        FileUtils.append(tempfile.toString(), list);

        // then
        List<String> content = Files.readAllLines(tempfile).stream()
                .filter(s -> s.trim().length() > 0).collect(Collectors.toList());
        Assertions.assertEquals(2, content.size());
        Assertions.assertEquals(expectedJson1, content.get(0).trim());
        Assertions.assertEquals(expectedJson2, content.get(1).trim());
    }
}
