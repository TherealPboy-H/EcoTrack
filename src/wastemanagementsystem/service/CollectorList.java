/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package wastemanagementsystem.service;



import wastemanagementsystem.model.Collector;
import wastemanagementsystem.WasteManagementSystem;
import java.sql.*;

public class CollectorList {

    public void addCollector(Collector c) {
        String sql = "INSERT INTO Collector (CollectorID, CollectorName, ServiceArea) VALUES (?, ?, ?)";
        try (Connection conn = WasteManagementSystem.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, c.getID());
            pstmt.setString(2, c.getName());
            pstmt.setString(3, c.getServiceArea());
            pstmt.executeUpdate();
        } catch (SQLException e) { }
    }

    public Collector findCollectorByArea(String area) {
        String sql = "SELECT * FROM Collector WHERE ServiceArea = ?";
        try (Connection conn = WasteManagementSystem.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, area);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Collector(rs.getInt("CollectorID"), rs.getString("CollectorName"), rs.getString("ServiceArea"));
            }
        } 
        catch (SQLException e) 
        {
        
        }
        return null;
    }

    public int getCollectors() {
        try (Connection conn = WasteManagementSystem.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM Collector")) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { }
        return 0;
    }
}