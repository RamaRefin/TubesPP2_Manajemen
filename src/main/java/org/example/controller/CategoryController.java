package org.example.controller;

import org.example.model.DatabaseConnection;
import org.example.model.WasteItem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CategoryController {

    public List<WasteItem> getAllWasteItems() {
        List<WasteItem> items = new ArrayList<>();
        String query = "SELECT item_id, kategori, jenis_sampah, parent_id, jenis_sampah FROM waste";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                int itemId = rs.getInt("item_id");
                String itemName = rs.getString("kategori");
                String itemType = rs.getString("jenis_sampah");
                Integer parentId = rs.getObject("parent_id", Integer.class);
                String typeName = rs.getString("jenis_sampah");
                WasteItem item = new WasteItem(itemId, itemName, itemType, parentId, typeName);
                items.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }


    public List<WasteItem> getWasteTypesByCategory(int categoryId) {
        List<WasteItem> types = new ArrayList<>();
        String query = "SELECT item_id, kategori, jenis_sampah, parent_id, jenis_sampah FROM waste WHERE jenis_sampah = 'type' AND parent_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, categoryId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int itemId = rs.getInt("item_id");
                String itemName = rs.getString("kategori");
                String itemType = rs.getString("jenis_sampah");
                Integer parentId = rs.getObject("parent_id", Integer.class);
                String typeName = rs.getString("jenis_sampah");
                WasteItem type = new WasteItem(itemId, itemName, itemType, parentId, typeName);
                types.add(type);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return types;
    }


    public boolean addWasteItem(WasteItem item) {
        String query = "INSERT INTO waste (kategori, jenis_sampah, parent_id) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, item.getItemName());
            stmt.setString(2, item.getItemType());
            if (item.getParentId() != null) {
                stmt.setInt(3, item.getParentId());
            } else {
                stmt.setNull(3, java.sql.Types.INTEGER);
            }
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int id = generatedKeys.getInt(1);
                        item.setItemId(id);
                        return true;
                    }
                }
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }



    public boolean updateWasteItem(WasteItem item) {
        String query = "UPDATE waste SET kategori = ?, parent_id = ? WHERE item_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, item.getItemName());
            if(item.getParentId() != null){
                stmt.setInt(2, item.getParentId());
            } else {
                stmt.setNull(2, java.sql.Types.INTEGER);
            }
            stmt.setInt(3, item.getItemId());
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public boolean deleteWasteItem(int itemId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);  // Start transaction

            // 1. Delete all types with parent_id equal to the categoryId
            String deleteTypesQuery = "DELETE FROM waste WHERE parent_id = ?";
            stmt = conn.prepareStatement(deleteTypesQuery);
            stmt.setInt(1, itemId);
            stmt.executeUpdate();

            // 2. Delete the category itself
            String deleteCategoryQuery = "DELETE FROM waste WHERE item_id = ?";
            stmt = conn.prepareStatement(deleteCategoryQuery);
            stmt.setInt(1, itemId);
            stmt.executeUpdate();


            conn.commit();  // Commit transaction
            return true;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();  // Rollback transaction in case of error
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            return false;
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);  // Reset auto-commit to true
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}