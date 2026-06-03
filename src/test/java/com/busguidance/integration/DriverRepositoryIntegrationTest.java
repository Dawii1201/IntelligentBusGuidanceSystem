package com.busguidance.integration;

import com.busguidance.model.Driver;
import com.busguidance.repository.DriverRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for DriverRepository.
 * These tests use:
 * - real Driver objects
 * - real DriverRepository implementation
 * - real TXT files created inside a temporary test folder
 */
public class DriverRepositoryIntegrationTest {

    @TempDir
    Path tempDir;

    /*
     * Helper method to create a real TXT file path for each test.
     * JUnit creates the temp folder automatically.
     */
    private Path createTestFilePath() {
        return tempDir.resolve("driver.txt");
    }

    // helper method to create a valid Driver instance for testing
    private Driver createValidDriver() {
        return new Driver(
                "23@@abcdAB",
                "John Smith",
                5,
                "Heavy",
                "12|King Street|Melbourne|VIC|Australia",
                "15-06-1995"
        );
    }

    // -----------------------------------------------------
    // Test 1: valid drivers are stored correctly
    // -----------------------------------------------------

    @Test
    public void validDriverShouldBeStoredCorrectly() {
        Path testFile = createTestFilePath();

        DriverRepository repository = new DriverRepository(testFile);

        Driver driver = createValidDriver();

        repository.add(driver);

        // Create a new repository using the same file.
        // This proves the driver was saved into the TXT file, not just memory.
        DriverRepository newRepository = new DriverRepository(testFile);

        Driver savedDriver = newRepository.retrieve("23@@abcdAB");

        assertNotNull(savedDriver);
        assertEquals("23@@abcdAB", savedDriver.getDriverID());
        assertEquals("John Smith", savedDriver.getName());
        assertEquals(5, savedDriver.getExperienceYears());
        assertEquals("Heavy", savedDriver.getLicenseType());
        assertEquals("12|King Street|Melbourne|VIC|Australia", savedDriver.getAddress());
        assertEquals("15-06-1995", savedDriver.getBirthDate());
    }

    // -----------------------------------------------------
    // Test 2: invalid drivers are rejected
    // duplicate driver ID is invalid under D1.
    // -----------------------------------------------------

    @Test
    public void duplicateDriverShouldBeRejected() {
        Path testFile = createTestFilePath();

        DriverRepository repository = new DriverRepository(testFile);

        Driver driver1 = createValidDriver();

        Driver driver2 = new Driver(
                "23@@abcdAB", // duplicate driver ID
                "Sarah Lee",
                6,
                "Medium",
                "88|Queen Street|Melbourne|VIC|Australia",
                "22-03-1998"
        );

        repository.add(driver1);

        assertThrows(IllegalArgumentException.class, () -> repository.add(driver2));

        // Count should still be 1 because duplicate driver was rejected
        assertEquals(1, repository.count());
    }

    // -----------------------------------------------------
    // Test 3: updates are persisted correctly
    // -----------------------------------------------------

    @Test
    public void driverUpdateShouldBePersistedCorrectly() {
        Path testFile = createTestFilePath();

        DriverRepository repository = new DriverRepository(testFile);

        Driver originalDriver = createValidDriver();

        repository.add(originalDriver);

        Driver updatedDriver = new Driver(
                "23@@abcdAB", // same driver ID, because D5 says ID cannot change
                "John Smith", // same name, because D5 says name cannot change
                6,
                "PublicTransport",
                "99|Flinders Street|Melbourne|VIC|Australia",
                "15-06-1995"
        );

        boolean updateResult = repository.update("23@@abcdAB", updatedDriver);

        assertTrue(updateResult);

        // Reload using a new repository object to check file persistence
        DriverRepository newRepository = new DriverRepository(testFile);

        Driver savedDriver = newRepository.retrieve("23@@abcdAB");

        assertNotNull(savedDriver);
        assertEquals("23@@abcdAB", savedDriver.getDriverID());
        assertEquals("John Smith", savedDriver.getName());
        assertEquals(6, savedDriver.getExperienceYears());
        assertEquals("PublicTransport", savedDriver.getLicenseType());
        assertEquals("99|Flinders Street|Melbourne|VIC|Australia", savedDriver.getAddress());
    }

    // -----------------------------------------------------
    // Test 4: record counts are updated correctly
    // -----------------------------------------------------

    @Test
    public void driverCountShouldBeUpdatedCorrectly() {
        Path testFile = createTestFilePath();

        DriverRepository repository = new DriverRepository(testFile);

        assertEquals(0, repository.count());

        Driver driver1 = createValidDriver();

        Driver driver2 = new Driver(
                "45##abcdCD",
                "Sarah Lee",
                8,
                "Medium",
                "88|Queen Street|Melbourne|VIC|Australia",
                "22-03-1998"
        );

        repository.add(driver1);
        assertEquals(1, repository.count());

        repository.add(driver2);
        assertEquals(2, repository.count());

        // Reload from the same TXT file to confirm count is persisted
        DriverRepository newRepository = new DriverRepository(testFile);

        assertEquals(2, newRepository.count());
    }
}