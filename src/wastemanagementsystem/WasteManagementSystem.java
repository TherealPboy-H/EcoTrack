/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package wastemanagementsystem;

import wastemanagementsystem.service.*;
import wastemanagementsystem.model.*;
import java.sql.*;
import java.util.*;


public class WasteManagementSystem 
{

    private final HouseList houseList = new HouseList();
    private final CollectorList collectors = new CollectorList();
    private final Scanner scan = new Scanner(System.in);

    // DB Settings
    private static final String URL = "jdbc:mysql://localhost:3306/EcoTrack";
    private static final String USER = "root";
    private static final String PASS = "5YearPlan$26";

    public static Connection getConnection() throws SQLException 
    {
        return DriverManager.getConnection(URL, USER, PASS);
    }

    public static void main(String[] args) {
        new WasteManagementSystem().run();
    }

    public void run() {
        int choice = 0;
        do {
            System.out.println("\n--- GreenTrack Waste System ---");
            System.out.println("1. Register Household   | 2. Register Collector    | 3. Submit Waste");
            System.out.println("4. View All Waste       | 5. Calculate Eco-Impact  | 6. System Report");
            System.out.println("7. Mark Collected       | 8. Search by Area        | 9. Delete Household");
            System.out.println("10. Exit");
            System.out.print("Choice: ");
            try 
            {
                choice = scan.nextInt();
                scan.nextLine();
                process(choice);
            } 
            catch (InputMismatchException e)
            {
                System.out.println("Error: Numbers only.");
                scan.nextLine();
            }
        } 
        
        while (choice != 10);
    }

    private void process(int choice) {
        switch (choice) {
            case 1 : registerHousehold();
            break;
            case 2 : registerCollector();
            break;
            case 3 : submitWaste();
            break;
            case 4 : houseList.viewWaste();
            break;
            case 5 : houseList.calEcoScore();
            break;
            case 6 : systemReport();
            break;
            case 7 : markCollection();
            break;
            case 8 : searchByArea();
            break;
            case 9 : deleteHousehold();
            break;
            case 10 : System.out.println("Goodbye");
            break;
            
            default : System.out.println("Invalid.");
        }
    }
    
    private void deleteHousehold() {
    System.out.print("Enter Household ID to delete: ");
    String id = scan.nextLine();
    
    // Safety check: Ask for confirmation
    System.out.print("Are you sure? This will permanently delete all waste logs for this house (yes/no): ");
    String confirm = scan.nextLine();
    
    if (confirm.equalsIgnoreCase("yes")) {
        houseList.deleteHouse(id);
    } else {
        System.out.println("Deletion cancelled.");
    }
}
    

   private void registerHousehold() {
    String id = "";
    // 1. Validate Household ID
    while (true) {
        System.out.print("Enter Household ID (4 digits, must start with 2): ");
        id = scan.nextLine().trim();

        // Regex: ^2 checks start, \d{3} checks for 3 more digits, $ ensures no extra characters
        if (id.matches("^2\\d{3}$")) {
            if (houseList.findHouse(id) == null) {
                break; // Valid and unique!
            } else {
                System.out.println("Error: This ID is already registered in the database.");
            }
        } else {
            System.out.println("Invalid ID! It must be exactly 4 digits and start with '2'.");
        }
    }

    System.out.print("Enter Head Name: ");
    String name = scan.nextLine();

    // 2. Validate Contact Number
    String contact = "";
    while (true) {
        System.out.print("Enter Contact Number (exactly 10 digits): ");
        contact = scan.nextLine().trim();

        if (contact.matches("^\\d{10}$")) {
            break; // Valid length!
        } else {
            System.out.println("Invalid Contact! Please enter exactly 10 digits (no letters or spaces).");
        }
    }

    System.out.print("Enter Address/Area: ");
    String addr = scan.nextLine();

    // Create and save
    Household h = new Household(id, name, contact, addr);
    
    // Auto-link collector (from previous logic)
    Collector c = collectors.findCollectorByArea(addr);
    if (c != null) h.setAssignedCollector(c);

    houseList.AddHouse(h);
    System.out.println("Household successfully registered.");
}

    private void registerCollector() {
        System.out.print("ID: "); int id = scan.nextInt(); scan.nextLine();
        System.out.print("Name: "); String name = scan.nextLine();
        System.out.print("Area: "); String area = scan.nextLine();
        collectors.addCollector(new Collector(id, name, area));
        System.out.println("Collector saved.");
    }

    private void submitWaste() {
        System.out.print("House ID: "); String id = scan.nextLine();
        if (houseList.findHouse(id) == null) { System.out.println("Not found."); return; }

        System.out.print("Item (e.g. Bottle): "); String item = scan.nextLine();
        System.out.print("Type (Plastic/Organic/Hazardous): "); String type = scan.nextLine();
        System.out.print("Weight (kg): "); double w = scan.nextDouble(); scan.nextLine();

        Waste waste = new Waste(item, type, w);
        houseList.addWasteToDB(id, waste);
        System.out.println("Logged. Impact: " + waste.impactScore());
    }

    private void markCollection() {
        System.out.print("House ID: "); String id = scan.nextLine();
        houseList.markCollection(id);
    }

    private void searchByArea() {
        System.out.print("Area: "); String area = scan.nextLine();
        for (Household h : houseList.getRawList()) {
            if (h.getAddress().equalsIgnoreCase(area)) {
                System.out.println("Found: " + h.getHousename() + " (ID: " + h.getHouseID() + ")");
            }
        }
    }

    private void systemReport() {
        System.out.println("\n--- Report ---");
        System.out.println("Total Households: " + houseList.getSumHouses());
        System.out.println("Total Collectors: " + collectors.getCollectors());
        houseList.wasteByCategory();
    }
}