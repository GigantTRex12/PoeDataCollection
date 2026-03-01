package com.company.api;

import com.company.Main;
import com.company.datasets.datasets.BossDropDataSet;
import com.company.datasets.datasets.MapDropDataSet;
import com.company.datasets.other.loot.*;
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

    private static Map<Loot, Integer> writeLoot(Collection<Loot> loot, Connection conn) throws SQLException {
        if (loot.isEmpty()) return new HashMap<>();
        Set<Loot> restLoot = new HashSet<>();
        final Map<Loot, Integer> existingLoot = DbReader.readLoot(conn);
        final Map<Loot, Integer> lootToId = new HashMap<>();
        for (Loot l : loot) {
            Integer id = existingLoot.get(l);
            if (id == null) restLoot.add(l);
            else lootToId.put(l, id);
        }
        final String query = "INSERT INTO loot (name, type) VALUES (?,?);";
        final PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        for (Loot l : restLoot) {
            pstmt.setString(1, l.getName());
            pstmt.setString(2, l.getType().name());
            pstmt.executeUpdate();
            ResultSet keys = pstmt.getGeneratedKeys();
            int id;
            if (keys.next()) id = keys.getInt(1);
            else throw new SqlConnectionException("Did not generate an index.");
            PreparedStatement subPstmt;
            switch (l) {
                case StackableLoot sl -> {
                    final String subQuery = "INSERT INTO stackableLoot (lootId, stacksize) VALUES (?,?);";
                    subPstmt = conn.prepareStatement(subQuery);
                    subPstmt.setInt(1, id);
                    subPstmt.setInt(2, sl.getStackSize());
                    subPstmt.executeUpdate();
                }
                case MapLoot ml -> {
                    final String subQuery = "INSERT INTO mapLoot (lootId, tier, layout) VALUES (?,?,?);";
                    subPstmt = conn.prepareStatement(subQuery);
                    subPstmt.setInt(1, id);
                    subPstmt.setInt(2, ml.getTier());
                    subPstmt.setString(3, ml.getLayout());
                    subPstmt.executeUpdate();
                }
                case LootWithLevel ll -> {
                    final String subQuery = "INSERT INTO levelLoot (lootId, level) VALUES (?,?);";
                    subPstmt = conn.prepareStatement(subQuery);
                    subPstmt.setInt(1, id);
                    subPstmt.setInt(2, ll.getLevel());
                    subPstmt.executeUpdate();
                }
                case ImplicitCorruptedItem cl -> {
                    final String subQuery = "INSERT INTO implicitCorruptedItemLoot (lootId, implicitAmount) VALUES (?,?);";
                    subPstmt = conn.prepareStatement(subQuery);
                    subPstmt.setInt(1, id);
                    subPstmt.setInt(2, cl.getImplicitAmount());
                    subPstmt.executeUpdate();
                }
                case GemLoot gl -> {
                    final String subQuery = "INSERT INTO gemLoot (lootId, gemLevel, gemQuality) VALUES (?,?,?);";
                    subPstmt = conn.prepareStatement(subQuery);
                    subPstmt.setInt(1, id);
                    subPstmt.setInt(2, gl.getLevel());
                    subPstmt.setInt(3, gl.getQuality());
                    subPstmt.executeUpdate();
                }
                case CraftingBenchLoot bl -> {
                    final String subQuery = "INSERT INTO craftingBenchLoot (lootId, description) VALUES (?,?);";
                    subPstmt = conn.prepareStatement(subQuery);
                    subPstmt.setInt(1, id);
                    subPstmt.setString(2, bl.getDescription());
                    subPstmt.executeUpdate();
                }
                default -> {
                }
            }
            lootToId.put(l, id);
        }
        return lootToId;
    }

    public static void writeBossDropDatasets(Collection<BossDropDataSet> data) {
        try (Connection conn = DriverManager.getConnection(getConnectionString())) {
            Collection<Loot> loot = new ArrayList<>();
            for (BossDropDataSet dataset : data) {
                if (dataset.getGuaranteedDrop() != null) loot.add(dataset.getGuaranteedDrop());
                if (dataset.getExtraDrops() != null) loot.addAll(dataset.getExtraDrops());
            }
            Map<Loot, Integer> lootToId = writeLoot(loot, conn);

            final String query = "INSERT INTO bossDropDataset (strategyId, bossName, uber, witnessed, guaranteedDropLootId, areaQuantity) VALUES (?,?,?,?,?,?)";
            final PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            for (BossDropDataSet dataset : data) {
                pstmt.setInt(1, dataset.getStrategy().getId());
                pstmt.setString(2, dataset.getBossName());
                pstmt.setBoolean(3, dataset.isUber());
                pstmt.setBoolean(4, dataset.isWitnessed());
                Loot gd = dataset.getGuaranteedDrop();
                if (gd == null) pstmt.setNull(5, Types.INTEGER);
                else pstmt.setInt(5, lootToId.get(gd));
                if (dataset.getQuantity() == null) pstmt.setNull(6, Types.INTEGER);
                else pstmt.setInt(6, dataset.getQuantity());
                pstmt.executeUpdate();

                ResultSet keys = pstmt.getGeneratedKeys();
                int id;
                if (keys.next()) id = keys.getInt(1);
                else throw new SqlConnectionException("Did not generate an index.");
                if (dataset.getExtraDrops() != null && !dataset.getExtraDrops().isEmpty()) {
                    final String lootQuery = "INSERT INTO bossDropExtraLoot (bossDropDataSetId, lootId) VALUES (?,?);";
                    final PreparedStatement lootPstmt = conn.prepareStatement(lootQuery);
                    for (Loot l : dataset.getExtraDrops()) {
                        lootPstmt.setInt(1, id);
                        lootPstmt.setInt(2, lootToId.get(l));
                        lootPstmt.addBatch();
                    }
                    lootPstmt.executeBatch();
                }
            }
        } catch (SQLException e) {
            throw new SqlConnectionException(e);
        }
    }

    public static void writeMapDropDatasets(Collection<MapDropDataSet> data) {
        try (Connection conn = DriverManager.getConnection(getConnectionString())) {
            Set<LootType> allTypes = new HashSet<>();
            for (MapDropDataSet d : data) {
                allTypes.addAll(d.getMapsInOrder());
                if (d.getBossDrops() != null) allTypes.addAll(d.getBossDrops());
            }
            Map<LootType, Integer> types = writeMapTypes(allTypes, conn);
            final Statement stmt = conn.createStatement();
            final String maxIdQuery = "SELECT MAX(bossDropListId) AS max FROM bossMapsDropList;";
            final ResultSet rs = stmt.executeQuery(maxIdQuery);
            int bossDropId = 1;
            if (rs.next()) bossDropId = rs.getInt("max") + 1;
            final String query = "INSERT INTO mapDropDataSet (strategyId, conversionChance, conversionType, bossDropListId) VALUES (?,?,?,?);";
            final PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            for (MapDropDataSet d : data) {
                pstmt.setInt(1, d.getStrategy().getId());
                pstmt.setInt(2, d.getConversionChance());
                if (d.getConversionType() == null) pstmt.setNull(3, Types.VARCHAR);
                else pstmt.setString(3, d.getConversionType().name());
                if (d.getBossDrops() == null) pstmt.setNull(4, Types.INTEGER);
                else if (d.getBossDrops().isEmpty()) pstmt.setInt(4, 0);
                else {
                    final String bossQuery = "INSERT INTO bossMapsDropList (mapTypeId, bossDropListId) VALUES (?,?);";
                    final PreparedStatement bossPstmt = conn.prepareStatement(bossQuery);
                    for (LootType t : d.getBossDrops()) {
                        bossPstmt.setInt(1, types.get(t));
                        bossPstmt.setInt(2, bossDropId);
                        bossPstmt.addBatch();
                    }
                    bossPstmt.executeBatch();
                    pstmt.setInt(4, bossDropId++);
                }
                pstmt.executeUpdate();
                ResultSet keys = pstmt.getGeneratedKeys();
                int id;
                if (keys.next()) id = keys.getInt(1);
                else throw new SqlConnectionException("Did not generate an index.");
                final String mapQuery = "INSERT INTO mapDropsList (mapDropDataSetId, mapTypeId, ordering) VALUES (?,?,?);";
                final PreparedStatement mapPstmt = conn.prepareStatement(mapQuery);
                for (int i = 0; i < d.getMapsInOrder().size(); i++) {
                    mapPstmt.setInt(1, id);
                    mapPstmt.setInt(2, types.get(d.getMapsInOrder().get(i)));
                    mapPstmt.setInt(3, i);
                    mapPstmt.addBatch();
                }
                mapPstmt.executeBatch();
            }
        } catch (SQLException e) {
            throw new SqlConnectionException(e);
        }
    }

    private static Map<LootType, Integer> writeMapTypes(Set<LootType> types, Connection conn) throws SQLException {
        if (types.isEmpty()) return new HashMap<>();
        Set<LootType> restTypes = new HashSet<>(types);
        Map<LootType, Integer> typeToId = new HashMap<>();
        final Statement stmt = conn.createStatement();
        final String selectQuery = "SELECT rowid, typeName FROM mapType;";
        final ResultSet rs = stmt.executeQuery(selectQuery);
        while (rs.next()) {
            LootType t = LootType.valueOf(rs.getString("typeName"));
            if (restTypes.remove(t)) typeToId.put(t, rs.getInt("rowid"));
        }
        if (!restTypes.isEmpty()) {
            final String query = "INSERT INTO mapType (typeName) VALUES (?);";
            final PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            for (LootType t : restTypes) {
                pstmt.setString(1, t.name());
                pstmt.executeUpdate();
                ResultSet keys = pstmt.getGeneratedKeys();
                if (keys.next()) typeToId.put(t, keys.getInt(1));
                else throw new SqlConnectionException("Did not generate an index.");
            }
        }
        return typeToId;
    }

}
