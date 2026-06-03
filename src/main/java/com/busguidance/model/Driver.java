package com.busguidance.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;

// Placeholder file for Member 2 to complete.

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
        // validate driverID syntax
        if (!isValidDriverID(driverID)) {
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
        // validate address and birthdate formats
        if (!isValidAddress(address)) {
            throw new IllegalArgumentException("Invalid Address");
        }

        if (!isValidBirthdate(birthDate)) {
            throw new IllegalArgumentException("Invalid Birth Date");
        }

        if (!isValidLicenseType(licenseType)) {
            throw new IllegalArgumentException("Invalid License Type");
        }
     

     this.driverID = driverID;
     this.name = name;
     this.experienceYears = experienceYears;
     this.licenseType = licenseType;
     this.address = address;
     this.birthDate = birthDate;
    }


    //Getters
        public String getDriverID() {
        return driverID;
    }

    public String getName() {
        return name;
    }

    public int getExperienceYears() {
        return experienceYears;
    }

    public String getLicenseType() {
        return licenseType;
    }

    public String getAddress() {
        return address;
    }

    public String getBirthDate() {
        return birthDate;
    }

    //Setters
    public void setExperienceYears(int experienceYears) {
        if (experienceYears < 0) {
            throw new IllegalArgumentException("Experience years cannot be negative.");
            }
         this.experienceYears = experienceYears;
        }
    
    public void setAddress(String address) {
        if (!isValidAddress(address)) {
            throw new IllegalArgumentException("Invalid address format.");
        }
        this.address = address;
    }

    public void setBirthdate(String birthdate) {
        if (!isValidBirthdate(birthdate)) {
            throw new IllegalArgumentException("Invalid birthdate format.");
        }

        this.birthDate = birthdate;
    }

    public void setLicenseType(String newLicenseType) {
        if (!isValidLicenseType(newLicenseType)) {
            throw new IllegalArgumentException("Invalid license type.");
        }

        // D4: Drivers with more than 10 years of experience cannot change licence type.
        if (this.experienceYears > 10 && !this.licenseType.equals(newLicenseType)) {
            throw new IllegalArgumentException(
                    "Drivers with more than 10 years of experience cannot change license type."
            );
        }

        this.licenseType = newLicenseType;
    }


    //Update driver details method
     public void updateDetails(
            int newExperienceYears,
            String newLicenseType,
            String newAddress,
            String newBirthdate
    ) {
        if (newExperienceYears < 0) {
            throw new IllegalArgumentException("Experience years cannot be negative.");
        }

        if (!isValidLicenseType(newLicenseType)) {
            throw new IllegalArgumentException("Invalid license type.");
        }

        if (!isValidAddress(newAddress)) {
            throw new IllegalArgumentException("Invalid address format.");
        }

        if (!isValidBirthdate(newBirthdate)) {
            throw new IllegalArgumentException("Invalid birthdate format.");
        }

        // D4: If current experience is more than 10 years, licence cannot be changed.
        if (this.experienceYears > 10 && !this.licenseType.equals(newLicenseType)) {
            throw new IllegalArgumentException(
                    "Drivers with more than 10 years of experience cannot change license type."
            );
        }

        this.experienceYears = newExperienceYears;
        this.licenseType = newLicenseType;
        this.address = newAddress;
        this.birthDate = newBirthdate;
    }
    



    // D1: Driver ID rule...   driverID must unique and no driverID duplicates //Driver Repo must check this **********
    // driverID exactly 10 characters long, first 2 characters must be 2-9. atleast 2 special character 3-8. last 2 characters must uppercase A-Z

    public boolean isValidDriverID() {
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

    // static helper to validate an arbitrary driverID string (useful in constructor)
    public static boolean isValidDriverID(String id) {
        if (id == null || id.length() != 10) {
            return false;
        }

        if (!id.substring(0, 2).matches("[2-9]{2}")) {
            return false;
        }

        if (!id.substring(8, 10).matches("[A-Z]{2}")) {
            return false;
        }

        String middlePart = id.substring(2, 8);
        int specialCharacterCount = 0;
        for (char c : middlePart.toCharArray()) {
            if (!Character.isLetterOrDigit(c)) {
                specialCharacterCount++;
            }
        }
        return specialCharacterCount >= 2;
    }


    //D2 Address format
    public static boolean isValidAddress(String address) {
    if (address == null || address.trim().isEmpty()) {
            return false;
        }

        String[] parts = address.split("\\|", -1);

    if (parts.length != 5) {
            return false;
        }

        String streetNumber = parts[0].trim();
        String streetName = parts[1].trim();
        String city = parts[2].trim();
        String state = parts[3].trim();
        String country = parts[4].trim();

    return streetNumber.matches("\\d+")
        && !streetName.isEmpty()
        && !city.isEmpty()
        && !state.isEmpty()
        && !country.isEmpty();
    }

    //D3 birth date format
        public static boolean isValidBirthdate(String birthdate) {
        if (birthdate == null || !birthdate.matches("\\d{2}-\\d{2}-\\d{4}")) {
            return false;
        }

        try {
            DateTimeFormatter formatter = DateTimeFormatter
                    .ofPattern("dd-MM-uuuu")
                    .withResolverStyle(ResolverStyle.STRICT);

            LocalDate.parse(birthdate, formatter);
            return true;
        } catch (Exception e) {
            return false;
        }
    }


     // Valid licence types 

    public static boolean isValidLicenseType(String licenseType) {
        if (licenseType == null) {
            return false;
        }

        return licenseType.equals("Light")
                || licenseType.equals("Medium")
                || licenseType.equals("Heavy")
                || licenseType.equals("PublicTransport");
    }

    // license type must be correct
    public static boolean isValidLicenseType1(String licenseType) {
    
        if (licenseType == null) {
            return false;
        }

        return licenseType.equals("Light")
                || licenseType.equals("Medium")
                || licenseType.equals("Heavy")
                || licenseType.equals("PublicTransport");
    }


}