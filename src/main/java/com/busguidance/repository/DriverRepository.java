package com.busguidance.repository;

import com.busguidance.model.Driver;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class DriverRepository {

    // point to driver.txt
    private final Path filePath;

    // CSV header used in the driver.txt file
    private static final String HEADER = "driverID,name,experienceYears,licenseType,address,birthdate";

    
     /// Default constructor.

    public DriverRepository() {
        this.filePath = Path.of("src", "main", "resources", "driver.txt");
        createFileIfNotExists();
    }

    
     //Constructor for testing.
     // This allows test cases to use a temporary file instead of the real driver.txt.
     
    public DriverRepository(Path filePath) {
        this.filePath = filePath;
        createFileIfNotExists();
    }

    
     // Add a new driver to the file.
     
     //D1 rule: driverID must be unique, so duplicates are rejected here.
    
    public boolean add(Driver driver) {
        if (driver == null) {
            throw new IllegalArgumentException("Driver cannot be null.");
        }

        List<Driver> drivers = loadDrivers();

        // Check duplicate driver ID
        for (Driver existingDriver : drivers) {
            if (existingDriver.getDriverID().equals(driver.getDriverID())) {
                throw new IllegalArgumentException("Duplicate driver ID is not allowed.");
            }
        }

        drivers.add(driver);
        saveDrivers(drivers);

        return true;
    }

    
     //Retrieve one driver using driverID.
     
      //@param driverID driver ID to search for
      //@return Driver object if found, otherwise null
     
    public Driver retrieve(String driverID) {
        if (driverID == null || driverID.trim().isEmpty()) {
            return null;
        }

        List<Driver> drivers = loadDrivers();

        for (Driver driver : drivers) {
            if (driver.getDriverID().equals(driverID)) {
                return driver;
            }
        }

        return null;
    }

    
      //Update an existing driver.
     // D4: If experienceYears is more than 10, licenseType cannot be changed.
     //D5 driverID and name cannot be changed during update.

    public boolean update(String driverID, Driver updatedDriver) {
        if (driverID == null || driverID.trim().isEmpty()) {
            throw new IllegalArgumentException("Driver ID cannot be empty.");
        }

        if (updatedDriver == null) {
            throw new IllegalArgumentException("Updated driver cannot be null.");
        }

        List<Driver> drivers = loadDrivers();

        for (int i = 0; i < drivers.size(); i++) {
            Driver existingDriver = drivers.get(i);

            if (existingDriver.getDriverID().equals(driverID)) {

                // D5: driverID cannot be changed
                if (!existingDriver.getDriverID().equals(updatedDriver.getDriverID())) {
                    throw new IllegalArgumentException("Driver ID cannot be changed during update.");
                }

                // D5: name cannot be changed
                if (!existingDriver.getName().equals(updatedDriver.getName())) {
                    throw new IllegalArgumentException("Driver name cannot be changed during update.");
                }

                // D4: licenseType cannot be changed if experience is more than 10 years
                if (existingDriver.getExperienceYears() > 10 &&
                        !existingDriver.getLicenseType().equals(updatedDriver.getLicenseType())) {
                    throw new IllegalArgumentException(
                            "Drivers with more than 10 years of experience cannot change license type."
                    );
                }

                // Replace old driver with updated driver
                drivers.set(i, updatedDriver);

                // Save the full updated list back into driver.txt
                saveDrivers(drivers);

                return true;
            }
        }

        // Driver was not found
        return false;
    }

    
     //Count how many drivers are stored in driver.txt.
     
    public int count() {
        return loadDrivers().size();
    }

    
      //Retrieve all drivers. Useful for testing and checking file contents.
     
    public List<Driver> retrieveAll() {
        return loadDrivers();
    }

    
     //Create the file and folder if they do not exist.Also writes the CSV header if the file is empty.
     
    private void createFileIfNotExists() {
        try {
            Path parentFolder = filePath.getParent();

            if (parentFolder != null) {
                Files.createDirectories(parentFolder);
            }

            if (!Files.exists(filePath)) {
                Files.createFile(filePath);
                Files.write(filePath, List.of(HEADER));
            } else if (Files.size(filePath) == 0) {
                Files.write(filePath, List.of(HEADER));
            }

        } catch (IOException e) {
            throw new RuntimeException("Could not create driver storage file.", e);
        }
    }

    // Load all drivers from driver.txt.
     
      //Expected file format:
     //driverID,name,experienceYears,licenseType,address,birthdate
     //23@@abcdAB,John Smith,5,Heavy,12|King Street|Melbourne|VIC|Australia,15-06-1995
     
    private List<Driver> loadDrivers() {
        List<Driver> drivers = new ArrayList<>();

        try {
            List<String> lines = Files.readAllLines(filePath);

            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i).trim();

                // Skip empty lines
                if (line.isEmpty()) {
                    continue;
                }

                // Skip header line
                if (line.equals(HEADER)) {
                    continue;
                }

                String[] parts = line.split(",", -1);

                if (parts.length != 6) {
                    throw new IllegalArgumentException(
                            "Invalid driver data format at line " + (i + 1)
                    );
                }

                String driverID = parts[0].trim();
                String name = parts[1].trim();
                int experienceYears = Integer.parseInt(parts[2].trim());
                String licenseType = parts[3].trim();
                String address = parts[4].trim();
                String birthdate = parts[5].trim();

                Driver driver = new Driver(
                        driverID,
                        name,
                        experienceYears,
                        licenseType,
                        address,
                        birthdate
                );

                drivers.add(driver);
            }

        } catch (IOException e) {
            throw new RuntimeException("Could not read driver storage file.", e);
        }

        return drivers;
    }

    
     //Save all drivers back into driver.txt.
     ///This rewrites the file with the newest driver list.
     
    private void saveDrivers(List<Driver> drivers) {
        List<String> lines = new ArrayList<>();

        // Add CSV header first
        lines.add(HEADER);

        // Add each driver as one CSV row
        for (Driver driver : drivers) {
            String line = driver.getDriverID() + ","
                    + driver.getName() + ","
                    + driver.getExperienceYears() + ","
                    + driver.getLicenseType() + ","
                    + driver.getAddress() + ","
                    + driver.getBirthDate();

            lines.add(line);
        }

        try {
            Files.write(filePath, lines);
        } catch (IOException e) {
            throw new RuntimeException("Could not save driver storage file.", e);
        }
    }
}