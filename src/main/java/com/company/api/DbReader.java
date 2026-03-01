package com.company.api;

import com.company.Main;
import com.company.datasets.datasets.BossDropDataSet;
import com.company.datasets.datasets.MapDropDataSet;
import com.company.datasets.other.loot.*;
import com.company.datasets.other.metadata.Strategy;
import com.company.exceptions.SomethingIsWrongWithMyCodeException;
import com.company.exceptions.SqlConnectionException;
import com.company.exceptions.SqlInvalidDataException;

import java.sql.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DbReader {

    // TODO: https://github.com/xerial/sqlite-jdbc/issues/1289

    private static String getConnectionString() {
        final String dbPath = Main.properties.getProperty("dbPath");
        final String dbName = Main.properties.getProperty("dbName");
        return "jdbc:sqlite:" + dbPath + "/" + dbName;
    }

    public static Collection<Strategy> readStrategies() {
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

    static Map<Loot, Integer> readLoot(Connection conn) throws SQLException {
        final Statement stmt = conn.createStatement();
        final String query = "SELECT l.rowid, l.*, sl.stacksize, ml.tier, ml.layout, il.implicitAmount, gl.gemLevel, gl.gemQuality, ll.level, cl.description FROM loot AS l "
                + "LEFT JOIN stackableLoot AS sl ON l.rowid = sl.lootId "
                + "LEFT JOIN mapLoot AS ml ON l.rowid = ml.lootId "
                + "LEFT JOIN implicitCorruptedItemLoot AS il ON l.rowid = il.lootId "
                + "LEFT JOIN gemLoot AS gl ON l.rowid = gl.lootId "
                + "LEFT JOIN levelLoot AS ll ON l.rowid = ll.lootId "
                + "LEFT JOIN craftingBenchLoot AS cl ON l.rowid = cl.lootId";
        final ResultSet rs = stmt.executeQuery(query);
        Map<Loot, Integer> lootToId = new HashMap<>();
        while (rs.next()) {
            final String name = rs.getString("name");
            final LootType type = LootType.valueOf(rs.getString("type"));
            final Class<?> lootClass = Loot.typeToClass(type);
            int id = rs.getInt("rowid");
            Loot loot;
            if (lootClass == Loot.class) loot = new Loot(name, type);
            else if (lootClass == StackableLoot.class) {
                int stackize = rs.getInt("stacksize");
                if (rs.wasNull())
                    throw new SqlInvalidDataException("stacksize cannot be NULL for type " + type.name());
                loot = new StackableLoot(name, type, stackize);
            } else if (lootClass == MapLoot.class) {
                int tier = rs.getInt("tier");
                if (rs.wasNull()) tier = 16;
                String layout = rs.getString("layout");
                loot = new MapLoot(name, type, tier, layout);
            } else if (lootClass == LootWithLevel.class) {
                int level = rs.getInt("level");
                if (rs.wasNull()) throw new SqlInvalidDataException("level cannot be NULL for type " + type.name());
                loot = new LootWithLevel(name, type, level);
            } else if (lootClass == ImplicitCorruptedItem.class) {
                int implicits = rs.getInt("implicitAmount");
                if (rs.wasNull())
                    throw new SqlInvalidDataException("implicitAmount cannot be NULL for type " + type.name());
                loot = new ImplicitCorruptedItem(name, type, implicits);
            } else if (lootClass == GemLoot.class) {
                int level = rs.getInt("gemLevel");
                if (rs.wasNull()) level = 1;
                int quality = rs.getInt("gemQuality");
                if (rs.wasNull()) quality = 0;
                loot = new GemLoot(name, type, level, quality);
            } else if (lootClass == CraftingBenchLoot.class) {
                String description = rs.getString("description");
                loot = new CraftingBenchLoot(name, type, description);
            } else {
                throw new SomethingIsWrongWithMyCodeException("Invalid lootClass");
            }
            lootToId.put(loot, id);
        }
        return lootToId;
    }

    public static Collection<BossDropDataSet> readBossDropDataSets() {
        Map<Integer, Strategy> strategies = readStrategies().stream().collect(Collectors.toMap(Strategy::getId, Function.identity()));
        try (Connection conn = DriverManager.getConnection(getConnectionString())) {
            Map<Integer, Loot> loot = readLoot(conn).entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
            final Statement stmt = conn.createStatement();
            final String query = "SELECT d.rowid, d.strategyId, d.bossName, d.uber, d.witnessed, d.guaranteedDropLootId, l.lootId, d.areaQuantity FROM bossDropDataSet AS d "
                    + "LEFT JOIN bossDropExtraLoot AS l ON d.rowid = l.bossDropDataSetId;";
            final ResultSet rs = stmt.executeQuery(query);
            final Map<Integer, BossDropDataSet.BossDropDataSetBuilder> idsToBuilder = new HashMap<>();
            while (rs.next()) {
                int currId = rs.getInt("rowid");
                BossDropDataSet.BossDropDataSetBuilder builder;
                if (idsToBuilder.containsKey(currId)) {
                    builder = idsToBuilder.get(currId);
                    builder.extraDrop(loot.get(rs.getInt("lootId")));
                } else {
                    builder = BossDropDataSet.builder()
                            .strategy(strategies.get(rs.getInt("strategyId")))
                            .bossName(rs.getString("bossName"))
                            .uber(rs.getBoolean("uber"))
                            .witnessed(rs.getBoolean("witnessed"));
                    int lootId = rs.getInt("guaranteedDropLootId");
                    if (!rs.wasNull()) builder.guaranteedDrop(loot.get(lootId));
                    int quant = rs.getInt("areaQuantity");
                    if (!rs.wasNull()) builder.quantity(quant);
                    int extraLootId = rs.getInt("lootId");
                    if (!rs.wasNull()) builder.extraDrop(loot.get(extraLootId));
                    idsToBuilder.put(currId, builder);
                }
            }
            return idsToBuilder.values().stream().map(BossDropDataSet.BossDropDataSetBuilder::build).toList();
        } catch (SQLException e) {
            throw new SqlConnectionException(e);
        }
    }

    public static Collection<MapDropDataSet> readMapDropDataSets() {
        Map<Integer, Strategy> strategies = readStrategies().stream().collect(Collectors.toMap(Strategy::getId, Function.identity()));
        try (Connection conn = DriverManager.getConnection(getConnectionString())) {
            final Statement stmt = conn.createStatement();
            String query = "SELECT d.rowid, d.strategyId, d.conversionChance, d.conversionType, d.bossDropListId, mlt.typeName FROM mapDropDataSet AS d "
                    + "LEFT JOIN mapDropsList AS ml ON d.rowid = ml.mapDropDataSetId "
                    + "LEFT JOIN mapType AS mlt ON ml.mapTypeId = mlt.rowid "
                    + "ORDER BY d.rowid, ml.ordering ASC;";
            ResultSet rs = stmt.executeQuery(query);
            final Map<Integer, MapDropDataSet.MapDropDataSetBuilder> idsToBuilder = new HashMap<>();
            while (rs.next()) {
                int currId = rs.getInt("rowid");
                MapDropDataSet.MapDropDataSetBuilder builder;
                if (idsToBuilder.containsKey(currId)) {
                    builder = idsToBuilder.get(currId);
                    String mapDrop = rs.getString("typeName");
                    if (!rs.wasNull()) builder.mapDrop(LootType.valueOf(mapDrop));
                } else {
                    builder = MapDropDataSet.builder()
                            .strategy(strategies.get(rs.getInt("strategyId")))
                            .conversionChance(rs.getInt("conversionChance"));
                    String conversionType = rs.getString("conversionType");
                    if (!rs.wasNull()) builder.conversionType(LootType.valueOf(conversionType));
                    String mapDrop = rs.getString("typeName");
                    if (!rs.wasNull()) builder.mapDrop(LootType.valueOf(mapDrop));
                    idsToBuilder.put(currId, builder);
                }
            }
            query = "SELECT d.rowid, d.bossDropListId, blt.typeName FROM mapDropDataSet AS d "
                    + "LEFT JOIN bossMapsDropList AS bl ON d.bossDropListId = bl.bossDropListId "
                    + "LEFT JOIN mapType AS blt ON bl.mapTypeId = blt.rowid "
                    + "WHERE NOT d.bossDropListId IS NULL;";
            rs = stmt.executeQuery(query);
            while (rs.next()) {
                int currId = rs.getInt("rowid");
                MapDropDataSet.MapDropDataSetBuilder builder = idsToBuilder.get(currId);
                if (rs.getInt("bossDropListId") == 0) builder.zeroBossDrops();
                else builder.bossDrop(LootType.valueOf(rs.getString("typeName")));
            }
            return idsToBuilder.values().stream().map(MapDropDataSet.MapDropDataSetBuilder::build).toList();
        } catch (SQLException e) {
            throw new SqlConnectionException(e);
        }
    }

}
