package wastemanagementsystem.model;



/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author lusani
 */


public class Household {
    private String houseID;
    private String housename;
    private String contactNo;
    private String address;
    private Collector assignedCollector;

    public Household(String houseID, String housename, String contactNo, String address) {
        this.houseID = houseID;
        this.housename = housename;
        this.contactNo = contactNo;
        this.address = address;
    }

    // Getters and Setters
    public String getHouseID()
    { 
        return houseID; 
    }
    public String getHousename() 
    {
        return housename; 
    }
    
    public String getContact() 
    { 
        return contactNo; 
    
    }
    
    public String getAddress() 
    
    {
        return address; 
    
    }
    public void setAssignedCollector(Collector c)
    { 
        this.assignedCollector = c; 
    
    }
    
    public Collector getAssignedCollector() 
    { 
        
        return assignedCollector; 
    
    }
}