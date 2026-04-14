package wastemanagementsystem.model;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author lusani
 */


public class Collector {
    private int id;
    private String name;
    private String serviceArea;

    public Collector(int id, String name, String serviceArea) {
        this.id = id;
        this.name = name;
        this.serviceArea = serviceArea;
    }

    public int getID() 
    { 
        return id; 
    
    }
    public String getName() 
    { 
        
        return name; 
    }
    public String getServiceArea() 
    {
        
        return serviceArea;
    
    
    }
}
