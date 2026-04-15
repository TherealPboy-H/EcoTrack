package wastemanagementsystem.service;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author lusani
 */

import wastemanagementsystem.model.*;
import wastemanagementsystem.WasteManagementSystem;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HouseList {

    public void AddHouse(Household house) 
    {
        String sql = "INSERT INTO Household (HouseholdID, HeadName, ContactNumber, Address) VALUES (?, ?, ?, ?)";
        try (Connection conn = WasteManagementSystem.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) 
        {
            pstmt.setString(1, house.getHouseID());
            pstmt.setString(2, house.getHousename());
            pstmt.setString(3, house.getContact());
            pstmt.setString(4, house.getAddress());
            pstmt.executeUpdate();
        } 
        
        catch (SQLException e)
        
        {
            
            System.out.println("Error: " + e.getMessage());
        
        }
    }

    public void addWasteToDB(String houseID, Waste waste) {
        String sql = "INSERT INTO Waste (WasteType, WasteName, Weight, HouseholdID, Status) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = WasteManagementSystem.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, waste.getType());
            pstmt.setString(2, waste.getItemName());
            pstmt.setDouble(3, waste.getWeight());
            pstmt.setString(4, houseID);
            pstmt.setString(5, "Pending");
            pstmt.executeUpdate();
        } catch (SQLException e) { System.out.println("Error: " + e.getMessage()); }
    }

    public Household findHouse(String id) {
        String sql = "SELECT * FROM Household WHERE HouseholdID = ?";
        try (Connection conn = WasteManagementSystem.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Household(rs.getString("HouseholdID"), rs.getString("HeadName"), rs.getString("ContactNumber"), rs.getString("Address"));
            }
        } catch (SQLException e) { }
        return null;
    }

    public void calEcoScore() {
        String sql = "SELECT HouseholdID, SUM(CASE " +
                     "WHEN WasteType = 'Plastic' THEN Weight * 3 " +
                     "WHEN WasteType = 'Organic' THEN Weight * 0.5 " +
                     "WHEN WasteType = 'Hazardous' THEN Weight * 6 " +
                     "ELSE Weight END) AS Impact FROM Waste GROUP BY HouseholdID";
        try (Connection conn = WasteManagementSystem.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            System.out.println("\n--- Eco-Impact Rankings ---");
            while (rs.next()) {
                System.out.println("House: " + rs.getString("HouseholdID") + " | Score: " + rs.getDouble("Impact") + " GSP");
            }
        } catch (SQLException e) { }
    }

    public void viewWaste() {
        String sql = "SELECT h.HeadName, w.WasteType, w.Weight, w.Status FROM Household h JOIN Waste w ON h.HouseholdID = w.HouseholdID";
        try (Connection conn = WasteManagementSystem.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            System.out.println("\n--- All Logged Waste ---");
            while (rs.next()) {
                System.out.println(rs.getString("HeadName") + " | " + rs.getString("WasteType") + " | " + rs.getDouble("Weight") + "kg | " + rs.getString("Status"));
            }
        } catch (SQLException e) { }
    }

    public void markCollection(String houseID) {
        String sql = "UPDATE Waste SET Status = 'Collected' WHERE HouseholdID = ? AND Status = 'Pending'";
        try (Connection conn = WasteManagementSystem.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, houseID);
            int rows = pstmt.executeUpdate();
            System.out.println(rows + " items updated.");
        } catch (SQLException e) { }
    }

    public void wasteByCategory() {
        String sql = "SELECT WasteType, SUM(Weight) as Total FROM Waste GROUP BY WasteType";
        try (Connection conn = WasteManagementSystem.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                System.out.println("- " + rs.getString("WasteType") + ": " + rs.getDouble("Total") + " kg");
            }
        } catch (SQLException e) { }
    }

    public List<Household> getRawList() {
        List<Household> list = new ArrayList<>();
        try (Connection conn = WasteManagementSystem.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM Household")) {
            while (rs.next()) {
                list.add(new Household(rs.getString("HouseholdID"), rs.getString("HeadName"), rs.getString("ContactNumber"), rs.getString("Address")));
            }
        } catch (SQLException e) { }
        return list;
    }
    
    public void deleteHouse(String id) {
    String deleteWasteSql = "DELETE FROM Waste WHERE HouseholdID = ?";
    String deleteHouseSql = "DELETE FROM Household WHERE HouseholdID = ?";

    try (Connection conn = WasteManagementSystem.getConnection()) {
        // Start transaction
        conn.setAutoCommit(false); 

        try (PreparedStatement pstmtWaste = conn.prepareStatement(deleteWasteSql);
             PreparedStatement pstmtHouse = conn.prepareStatement(deleteHouseSql)) {

            // 1. Delete associated waste first
            pstmtWaste.setString(1, id);
            pstmtWaste.executeUpdate();

            // 2. Delete the household
            pstmtHouse.setString(1, id);
            int affectedRows = pstmtHouse.executeUpdate();

            if (affectedRows > 0) {
                conn.commit(); // Save changes
                System.out.println("Household " + id + " and all its waste records have been deleted.");
            } else {
                System.out.println("Error: Household ID '" + id + "' not found.");
                conn.rollback(); 
            }
        } catch (SQLException e) {
            conn.rollback(); // Undo everything if an error occurs
            System.out.println("Database error during deletion: " + e.getMessage());
        }
    } catch (SQLException e) {
        System.out.println("Connection error: " + e.getMessage());
    }
}

    public int getSumHouses() {
        try (Connection conn = WasteManagementSystem.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM Household")) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { }
        return 0;
    }
}
