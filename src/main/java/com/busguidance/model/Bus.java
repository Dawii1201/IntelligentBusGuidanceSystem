package com.busguidance.model;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;


public class Bus {
    private String busID;
    private int capacity;
    private double fuelLevel;
    private String fuelType; // Diesel, Hybrid, Electricity

    public Bus(String busID, int capacity, double fuelLevel, String fuelType) {
        validateBusID(busID);
        validateCapacity(capacity);
        validateFuelLevel(fuelLevel);
        validateFuelType(fuelType);

        this.busID = busID;
        this.capacity = capacity;
        this.fuelLevel = fuelLevel;
        this.fuelType = fuelType;
    }

    public String getBusID() {
        return busID;
    }

    public int getCapacity() {
        return capacity;
    }

    public double getFuelLevel() {
        return fuelLevel;
    }

    public String getFuelType() {
        return fuelType;
    }

    /**
     * B2: Bus capacity cannot increase during update operations

     */
    public void setCapacity(int newCapacity) {
        validateCapacity(newCapacity);

        if (newCapacity > this.capacity) {
            throw new IllegalArgumentException("Bus capacity cannot be increased during update.");
        }

        this.capacity = newCapacity;
    }

    public void setFuelLevel(double fuelLevel) {
        validateFuelLevel(fuelLevel);
        this.fuelLevel = fuelLevel;
    }

    public void setFuelType(String fuelType) {
        validateFuelType(fuelType);
        this.fuelType = fuelType;
    }

    /**
     * B1: Bus ID must be exactly 8 characters long and all characters must be digits
     */
    private void validateBusID(String busID) {
        if (busID == null || !busID.matches("\\d{8}")) {
            throw new IllegalArgumentException("Bus ID must be exactly 8 digits.");
        }
    }

    private void validateCapacity(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Bus capacity must be greater than 0.");
        }
    }

    private void validateFuelLevel(double fuelLevel) {
        if (fuelLevel < 0 || fuelLevel > 100) {
            throw new IllegalArgumentException("Fuel level must be between 0 and 100.");
        }
    }

    private void validateFuelType(String fuelType) {
        if (fuelType == null ||
                !(fuelType.equals("Diesel") || fuelType.equals("Hybrid") || fuelType.equals("Electricity"))) {
            throw new IllegalArgumentException("Fuel type must be Diesel, Hybrid, or Electricity.");
        }
    }

    /**
     * Checks whether a driver is allowed to operate this bus.
     *
     * B3
     * B4
     * B5
     */
    public boolean canBeDrivenBy(Driver driver) {
        if (driver == null) {
            throw new IllegalArgumentException("Driver cannot be null.");
        }

        int driverAge = calculateAge(driver.getBirthDate());
        String licenseType = driver.getLicenseType();
        int experienceYears = driver.getExperienceYears();

        // B3
        if (driverAge > 50 && this.capacity >= 50) {
            return false;
        }

        // B4
        if (this.fuelType.equals("Electricity") && experienceYears < 5) {
            return false;
        }

        // B5
        if ((this.fuelType.equals("Electricity") || this.fuelType.equals("Hybrid"))
                && !(licenseType.equals("Heavy") || licenseType.equals("PublicTransport"))) {
            return false;
        }

        return true;
    }

    /**
     * Calculates driver age
     */
    private int calculateAge(String birthdate) {
        DateTimeFormatter formatter = DateTimeFormatter
                .ofPattern("dd-MM-uuuu")
                .withResolverStyle(ResolverStyle.STRICT);

        LocalDate birthDate = LocalDate.parse(birthdate, formatter);
        return Period.between(birthDate, LocalDate.now()).getYears();
    }
}