package com.company.datasets.loot;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

public class LootDeserializer extends StdDeserializer<Loot> {
    public LootDeserializer() {
        super(Loot.class);
    }

    @Override
    public Loot deserialize(JsonParser jp, DeserializationContext deserializationContext) throws IOException {
        TreeNode node = jp.readValueAsTree();

        String typeString = node.get("type").toString();

        LootType lootType = LootType.valueOf(typeString.substring(1, typeString.length() - 1));

        Class<? extends Loot> clazz = Loot.typeToClass(lootType);

        if (clazz == Loot.class) {
            String name = node.get("name").toString();
            name = name.substring(1, name.length() - 1);
            return new Loot(name, lootType);
        }
        return jp.getCodec().treeToValue(node, clazz);
    }
}
