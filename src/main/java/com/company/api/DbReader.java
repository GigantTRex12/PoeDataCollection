package com.company.api;

import com.company.Main;
import com.company.datasets.other.metadata.Strategy;
import com.company.exceptions.SqlConnectionException;

import java.sql.*;
import java.util.*;

public class DbReader {

    // TODO: https://github.com/xerial/sqlite-jdbc/issues/1289

    private static String getConnectionString() {
        final String dbPath = Main.properties.getProperty("dbPath");
        final String dbName = Main.properties.getProperty("dbName");
        return "jdbc:sqlite:" + dbPath + "/" + dbName;
    }

    public static List<Strategy> readStrategies() {
        try (Connection conn = DriverManager.getConnection(getConnectionString())) {
            final Statement stmt = conn.createStatement();
            final String query = "SELECT st.rowid, st.*, sc.scarabName, sl.scarabAmount FROM strategy AS st "
                    + "LEFT JOIN scarabList AS sl ON st.scarabListId = sl.scarabListId "
                    + "LEFT JOIN scarab AS sc ON sl.scarabId = sc.rowid;";
            final ResultSet rs = stmt.executeQuery(query);
            final Map<Integer, Strategy.StrategyBuilder> idsToBuilder = new LinkedHashMap<>();
            while (rs.next()) {
                int currId = rs.getInt("rowid");
                Strategy.StrategyBuilder builder;
                if (idsToBuilder.containsKey(currId)) {
                    builder = idsToBuilder.get(currId);
                    builder.scarab(rs.getString("scarabName"), rs.getInt("scarabAmount"));
                } else {
                    builder = new Strategy.StrategyBuilder(currId)
                            .league(rs.getString("league"))
                            .tree(rs.getString("atlasTreeDescription"))
                            .treeUrl(rs.getString("atlasTreeUrl"))
                            .mapLayout(rs.getString("mapLayout"))
                            .mapRolling(rs.getString("mapRollingDescription"))
                            .mapCraft(rs.getString("mapCraftingOption"));
                    rs.getInt("scarabListId");
                    if (!rs.wasNull()) {
                        builder.zeroScarabs();
                        String scarab = rs.getString("scarabName");
                        if (scarab != null) builder.scarab(scarab, rs.getInt("scarabAmount"));
                    }
                    idsToBuilder.put(currId, builder);
                }
            }
            return idsToBuilder.values().stream().map(Strategy.StrategyBuilder::build).toList();
        } catch (SQLException e) {
            throw new SqlConnectionException(e);
        }
    }

}
