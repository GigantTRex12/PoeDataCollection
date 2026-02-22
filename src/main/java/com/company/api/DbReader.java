package com.company.api;

import com.company.Main;
import com.company.datasets.other.metadata.Strategy;
import com.company.exceptions.SqlConnectionException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DbReader {

    // TODO: https://github.com/xerial/sqlite-jdbc/issues/1289

    private static String getConnectionString() {
        final String dbPath = Main.properties.getProperty("dbPath");
        final String dbName = Main.properties.getProperty("dbName");
        return "jdbc:sqlite:" + dbPath + "/" + dbName;
    }

    public static List<Strategy> readStrategies() {
        try (Connection conn = DriverManager.getConnection(getConnectionString())) {
            Statement stmt = conn.createStatement();
            String query = "SELECT rowid, * FROM strategy;";
            ResultSet rs = stmt.executeQuery(query);
            List<Strategy> strategies = new ArrayList<>();
            while (rs.next()) {
                strategies.add(new Strategy(
                        rs.getInt("rowid"),
                        rs.getString("league"),
                        rs.getString("atlasTreeDescription"),
                        rs.getString("atlasTreeUrl"),
                        new String[0], // TODO
                        rs.getString("mapLayout"),
                        rs.getString("mapRollingDescription"),
                        rs.getString("mapCraftingOption")
                ));
            }
            return strategies;
        } catch (SQLException e) {
            throw new SqlConnectionException(e);
        }
    }

}
