package com.company.datasets.other.jun;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

public class BoardDeserializer extends StdDeserializer<Board> {

    public BoardDeserializer() {super(Board.class);}

    public Board deserialize(JsonParser jp, DeserializationContext deserializationContext) throws IOException {
        Board board = jp.getCodec().treeToValue(jp.readValueAsTree(), Board.class);

        board.setAllMembers(board.getAllMembers());
        board.updateRelations();

        return board;
    }

}
