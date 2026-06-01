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
        if(driverID == null || driverID.trim().isEmpty()) {
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

    // D1: Driver ID rule...   driverID must unique and no driverID duplicates //Driver Repo must check this **********
    // driverID exactly 10 characters long, first 2 characters must be 2-9. atleast 2 special character 3-8. last 2 characters must uppercase A-Z

    public boolean validateDriverID() {
    if (driverID == null || driverID.length() != 10) {
        return false;
    }

    // First 2 characters must be 2-9
    if (!driverID.substring(0, 2).matches("[2-9]{2}")) {
        return false;
    }

    // Last 2 characters must be A-Z
    if (!driverID.substring(8, 10).matches("[A-Z]{2}")) {
        return false;
    }

    // Characters 3 to 8 means index 2 to 7
    String middlePart = driverID.substring(2, 8);

    int specialCharacterCount = 0;

    for (char c : middlePart.toCharArray()) {
        if (!Character.isLetterOrDigit(c)) {
            specialCharacterCount++;
        }
    }
    if (specialCharacterCount < 2) {
        return false;
    }

    return true;
}
       

}