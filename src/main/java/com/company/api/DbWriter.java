package com.company.api;

import com.company.Main;
import com.company.datasets.other.metadata.Strategy;
import com.company.exceptions.SqlConnectionException;
import com.company.utils.Counter;

import java.sql.*;
import java.util.*;

public class DbWriter {

    // TODO: https://github.com/xerial/sqlite-jdbc/issues/1289

    private static String getConnectionString() {
        final String dbPath = Main.properties.getProperty("dbPath");
        final String dbName = Main.properties.getProperty("dbName");
        return "jdbc:sqlite:" + dbPath + "/" + dbName;
    }

    public static int writeStrategy(Strategy strategy) {
        try (Connection conn = DriverManager.getConnection(getConnectionString())) {
            // Prepare query and add values to prepared statement
            final String queryStrat = "INSERT INTO strategy(league, atlasTreeDescription, atlasTreeUrl, mapLayout, mapRollingDescription, mapCraftingOption, scarabListId) VALUES(?,?,?,?,?,?,?)";
            final PreparedStatement pstmt = conn.prepareStatement(queryStrat, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, strategy.getLeague());
            pstmt.setString(2, strategy.getTree());
            pstmt.setString(3, strategy.getTreeUrl());
            pstmt.setString(4, strategy.getMapLayout());
            pstmt.setString(5, strategy.getMapRolling());
            pstmt.setString(6, strategy.getMapCraft());
            // Check if scarabs is null or empty
            if (strategy.getScarabs() == null) pstmt.setNull(7, Types.INTEGER);
            else if (strategy.getScarabs().length == 0) pstmt.setInt(7, 0);
            else {
                // Check if equivalent scarabList already exists
                final Counter<String> theseScarabs = new Counter<>(strategy.getScarabs());
                final Statement scarabStmt = conn.createStatement();
                final String queryScarabList = "SELECT sl.scarabListId, s.scarabName, sl.scarabAmount FROM scarabList AS sl "
                        + "INNER JOIN scarab AS s ON sl.scarabId = s.rowid "
                        + "ORDER BY sl.scarabListId;";
                ResultSet rs = scarabStmt.executeQuery(queryScarabList);
                Counter<String> counter = new Counter<>();
                Integer lastId = null;
                while (rs.next()) {
                    int currId = rs.getInt("scarabListId");
                    if (lastId != null && currId == lastId) {
                        counter.add(rs.getString("scarabName"), rs.getInt("scarabAmount"));
                    } else {
                        if (lastId != null && theseScarabs.equals(counter)) {
                            pstmt.setInt(7, lastId);
                            break;
                        }
                        lastId = currId;
                        counter = new Counter<>();
                        counter.add(rs.getString("scarabName"), rs.getInt("scarabAmount"));
                    }
                }
                if (lastId != null && theseScarabs.equals(counter)) pstmt.setInt(7, lastId);
                else {
                    // create new scarabList
                    List<String> scarabs = new ArrayList<>(theseScarabs.keySet());
                    Map<String, Integer> scarabToId = writeScarabs(scarabs, conn);
                    // get new scarabListId
                    rs = scarabStmt.executeQuery("SELECT MAX(scarabListId) AS max FROM scarabList");
                    int max;
                    if (rs.next()) max = rs.getInt("max") + 1;
                    else max = 1;
                    // add new scarabList
                    final String insertScarabListQuery = "INSERT INTO scarabList (scarabId, scarabListId, scarabAmount) VALUES (?,?,?);";
                    final PreparedStatement scarabPstmt = conn.prepareStatement(insertScarabListQuery);
                    for (String s : scarabToId.keySet()) {
                        scarabPstmt.setInt(1, scarabToId.get(s));
                        scarabPstmt.setInt(2, max);
                        scarabPstmt.setInt(3, theseScarabs.get(s));
                        scarabPstmt.addBatch();
                    }
                    scarabPstmt.executeBatch();
                    pstmt.setInt(7, max);
                }
            }
            // execute query and return rowid
            pstmt.executeUpdate();
            ResultSet keys = pstmt.getGeneratedKeys();
            if (keys.next()) return keys.getInt(1);
            else throw new SqlConnectionException("Did not generate an index.");
        } catch (SQLException e) {
            throw new SqlConnectionException(e);
        }
    }

    private static Map<String, Integer> writeScarabs(Collection<String> scarab, Connection conn) throws SQLException {
        Set<String> restScarabs = new HashSet<>(scarab);
        Map<String, Integer> scarabToId = new HashMap<>();
        int highestId = -1;
        while (!restScarabs.isEmpty()) {
            // Check existing scarabs
            final String queryScarab = "SELECT rowid, scarabName FROM scarab WHERE rowid > " + highestId + ";";
            final Statement stmt = conn.createStatement();
            final ResultSet rs = stmt.executeQuery(queryScarab);
            while (!restScarabs.isEmpty() && rs.next()) {
                String s = rs.getString("scarabName");
                if (restScarabs.contains(s)) {
                    scarabToId.put(s, rs.getInt("rowid"));
                    restScarabs.remove(s);
                }
            }
            if (restScarabs.isEmpty()) break;
            // add rest Scarabs
            final String insertScarabQuery = "INSERT INTO scarab (scarabName) VALUES (?);";
            final PreparedStatement pstmt = conn.prepareStatement(insertScarabQuery);
            for (String s : restScarabs) {
                pstmt.setString(1, s);
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        }
        return scarabToId;
    }

}
