package com.busguidance.unit;

import com.busguidance.model.Driver;
import com.busguidance.repository.DriverRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;


public class DriverTest {

    @TempDir
    Path tempDir;

    // Helper method to create a valid Driver instance for testing
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

    
    // D1 - Driver ID Rules

    @Test
    public void validDriverIDShouldBeAccepted() {
        assertDoesNotThrow(() -> createValidDriver());
    }

    @Test
    public void driverIDWithInvalidLengthShouldBeRejected() {
        assertThrows(IllegalArgumentException.class, () -> new Driver(
                "23@@abAB",
                "John Smith",
                5,
                "Heavy",
                "12|King Street|Melbourne|VIC|Australia",
                "15-06-1995"
        ));
    }

    @Test
    public void driverIDWithInvalidSpecialCharactersShouldBeRejected() {
        assertThrows(IllegalArgumentException.class, () -> new Driver(
                "23@abcdeAB",
                "John Smith",
                5,
                "Heavy",
                "12|King Street|Melbourne|VIC|Australia",
                "15-06-1995"
        ));
    }

    @Test
    public void duplicateDriverIDShouldBeRejected() {
        Path testFile = tempDir.resolve("driver.txt");
        DriverRepository repository = new DriverRepository(testFile);

        Driver driver1 = createValidDriver();

        Driver driver2 = new Driver(
                "23@@abcdAB",
                "Sarah Lee",
                6,
                "Medium",
                "88|Queen Street|Melbourne|VIC|Australia",
                "22-03-1998"
        );

        repository.add(driver1);

        assertThrows(IllegalArgumentException.class, () -> repository.add(driver2));
    }

    

    // D2 - Address Format
    // Street Number|Street Name|City|State|Country

    @Test
    public void validAddressShouldBeAccepted() {
        assertDoesNotThrow(() -> new Driver(
                "24##abcdCD",
                "Sarah Lee",
                6,
                "Medium",
                "88|Queen Street|Melbourne|VIC|Australia",
                "22-03-1998"
        ));
    }

    @Test
    public void addressWithMissingPartShouldBeRejected() {
        assertThrows(IllegalArgumentException.class, () -> new Driver(
                "25!!abcdEF",
                "Alex Brown",
                4,
                "Light",
                "88|Queen Street|Melbourne|VIC",
                "22-03-1998"
        ));
    }

    @Test
    public void addressWithNonNumericStreetNumberShouldBeRejected() {
        assertThrows(IllegalArgumentException.class, () -> new Driver(
                "27%%abcdIJ",
                "David White",
                3,
                "Medium",
                "88A|Queen Street|Melbourne|VIC|Australia",
                "22-03-1998"
        ));
    }

    // =====================================================
    // D3 - Birthdate Format
    // DD-MM-YYYY

    @Test
    public void validBirthdateShouldBeAccepted() {
        assertDoesNotThrow(() -> new Driver(
                "28&&abcdKL",
                "Emma Wilson",
                8,
                "Heavy",
                "10|Collins Street|Melbourne|VIC|Australia",
                "09-11-1992"
        ));
    }

    @Test
    public void birthdateWithWrongFormatShouldBeRejected() {
        assertThrows(IllegalArgumentException.class, () -> new Driver(
                "29@@abcdMN",
                "Tom Black",
                5,
                "Medium",
                "10|Collins Street|Melbourne|VIC|Australia",
                "1992-11-09"
        ));
    }

    @Test
    public void impossibleBirthdateShouldBeRejected() {
        assertThrows(IllegalArgumentException.class, () -> new Driver(
                "33!!abcdQR",
                "Peter Smith",
                5,
                "Heavy",
                "10|Collins Street|Melbourne|VIC|Australia",
                "31-02-1992"
        ));
    }

    // =====================================================
    // D4 - License Update Restriction
    // If experienceYears > 10, licenseType cannot be changed


    @Test
    public void driverWithMoreThanTenYearsExperienceCannotChangeLicense() {
        Driver driver = new Driver(
                "34$$abcdST",
                "Robert King",
                12,
                "Heavy",
                "55|Bourke Street|Melbourne|VIC|Australia",
                "11-05-1985"
        );

        assertThrows(IllegalArgumentException.class, () -> driver.setLicenseType("Medium"));
    }

    @Test
    public void driverWithExactlyTenYearsExperienceCanChangeLicense() {
        Driver driver = new Driver(
                "35%%abcdUV",
                "Anna Scott",
                10,
                "Medium",
                "55|Bourke Street|Melbourne|VIC|Australia",
                "11-05-1985"
        );

        assertDoesNotThrow(() -> driver.setLicenseType("Heavy"));
        assertEquals("Heavy", driver.getLicenseType());
    }

    @Test
    public void driverWithMoreThanTenYearsCanKeepSameLicense() {
        Driver driver = new Driver(
                "37@@abcdYZ",
                "Chris Martin",
                15,
                "PublicTransport",
                "55|Bourke Street|Melbourne|VIC|Australia",
                "11-05-1980"
        );

        assertDoesNotThrow(() -> driver.setLicenseType("PublicTransport"));
        assertEquals("PublicTransport", driver.getLicenseType());
    }

    // =====================================================
    // D5 - Immutable Fields
    // driverID and name cannot be modified during update
    // =====================================================

    @Test
    public void updateWithChangedDriverIDShouldBeRejected() {
        Path testFile = tempDir.resolve("driver.txt");
        DriverRepository repository = new DriverRepository(testFile);

        Driver originalDriver = createValidDriver();

        Driver updatedDriver = new Driver(
                "38##abcdAA",
                "John Smith",
                6,
                "Heavy",
                "99|Flinders Street|Melbourne|VIC|Australia",
                "15-06-1995"
        );

        repository.add(originalDriver);

        assertThrows(IllegalArgumentException.class,
                () -> repository.update("23@@abcdAB", updatedDriver));
    }

    @Test
    public void updateWithChangedNameShouldBeRejected() {
        Path testFile = tempDir.resolve("driver.txt");
        DriverRepository repository = new DriverRepository(testFile);

        Driver originalDriver = createValidDriver();

        Driver updatedDriver = new Driver(
                "23@@abcdAB",
                "Different Name",
                6,
                "Heavy",
                "99|Flinders Street|Melbourne|VIC|Australia",
                "15-06-1995"
        );

        repository.add(originalDriver);

        assertThrows(IllegalArgumentException.class,
                () -> repository.update("23@@abcdAB", updatedDriver));
    }

    @Test
    public void updateWithSameDriverIDAndNameShouldBeAccepted() {
        Path testFile = tempDir.resolve("driver.txt");
        DriverRepository repository = new DriverRepository(testFile);

        Driver originalDriver = createValidDriver();

        Driver updatedDriver = new Driver(
                "23@@abcdAB",
                "John Smith",
                6,
                "Heavy",
                "99|Flinders Street|Melbourne|VIC|Australia",
                "15-06-1995"
        );

        repository.add(originalDriver);

        assertTrue(repository.update("23@@abcdAB", updatedDriver));

        Driver savedDriver = repository.retrieve("23@@abcdAB");

        assertNotNull(savedDriver);
        assertEquals("23@@abcdAB", savedDriver.getDriverID());
        assertEquals("John Smith", savedDriver.getName());
        assertEquals("99|Flinders Street|Melbourne|VIC|Australia", savedDriver.getAddress());
    }
}