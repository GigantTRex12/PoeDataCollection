package com.company.datasets.other.jun;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

public class SafehouseDeserializer extends StdDeserializer<Safehouse> {

    public SafehouseDeserializer() {super(Safehouse.class);}

    public Safehouse deserialize(JsonParser jp, DeserializationContext deserializationContext) throws IOException {
        Safehouse safehouse = jp.getCodec().treeToValue(jp.readValueAsTree(), Safehouse.class);

        safehouse.getMembers().forEach(m -> {
            m.setSafehouse(safehouse.getType());
            if (m.isLeader()) {safehouse.setLeader(m);}
        });

        return safehouse;
    }

}
