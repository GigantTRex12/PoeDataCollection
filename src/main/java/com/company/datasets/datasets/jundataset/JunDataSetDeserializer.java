package com.company.datasets.datasets.jundataset;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class JunDataSetDeserializer extends StdDeserializer<JunDataSet> {
    public JunDataSetDeserializer() {
        super(JunDataSet.class);
    }

    @Override
    public JunDataSet deserialize(JsonParser jp, DeserializationContext deserializationContext) throws IOException, JacksonException {
        TreeNode node = jp.readValueAsTree();

        AtomicBoolean isSafehouse = new AtomicBoolean(false);
        List<String> safehouseFields = List.of("safehouse", "leader", "leaderLoot");
        AtomicBoolean isEncounter = new AtomicBoolean(false);
        List<String> encounterFields = List.of("encounter1", "encounter2", "encounter3");

        node.fieldNames().forEachRemaining(fieldname -> {
            if (safehouseFields.contains(fieldname)) {
                isSafehouse.set(true);
            }
            if (encounterFields.contains(fieldname)) {
                isEncounter.set(true);
            }
        });

        if (isSafehouse.get() && isEncounter.get()) {
            return throwJacksonException("Both Datasets fit");
        }
        else if (isSafehouse.get()) {
            return jp.getCodec().treeToValue(node, SafehouseEncounterDataSet.class);
        }
        else if (isEncounter.get()) {
            return jp.getCodec().treeToValue(node, JunEncounterDataSet.class);
        }
        else {
            return throwJacksonException("No Dataset fits");
        }
    }

    private JunDataSet throwJacksonException(String message) throws JacksonException {
        throw new JacksonException(message) {
            @Override
            public JsonLocation getLocation() {
                return null;
            }
            @Override
            public String getOriginalMessage() {
                return message;
            }
            @Override
            public Object getProcessor() {
                return null;
            }
        };
    }
}
