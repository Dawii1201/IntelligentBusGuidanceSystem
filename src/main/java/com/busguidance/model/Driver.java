package com.busguidance.model;

// Placeholder file for Member 2 to complete.
// TODO: Implement Driver attributes, constructor, getters, setters, and D1-D5 validation.
public class Driver {
    private final String driverID;
    private String name;
    private int experienceYears;
    private String licenseType; //Light,medium,heavy,public transport
    private String address;
    private String birthDate;

    public Driver(String driverID, String name, int experienceYears, String licenseType, String address, String birthDate)
     {
        if(!isValidDriverID(driverID)) {
            throw new IllegalArgumentException("Invalid Driver ID");
        }

        if(name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cant be empty");
        }
        if(experienceYears < 0) {
            throw new IllegalArgumentException("Invalid Experience Years");
        }
        if(licenseType == null || licenseType.trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid License Type");
        }
        if(address == null || address.trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid Address");
        }
        if(birthDate == null || birthDate.trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid Birth Date");
        }
     

     this.driverID = driverID;
     this.name = name;
     this.experienceYears = experienceYears;
     this.licenseType = licenseType;
     this.address = address;
     this.birthDate = birthDate;
    }

}