package com.busguidance.unit;

import com.busguidance.model.Bus;
import com.busguidance.model.Driver;
import com.busguidance.repository.BusRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

// Unit tests for the bus conditions B1-B5 (3+ cases each: normal, invalid, edge).
public class BusTest {

    @TempDir
    Path tempDir;

    // baseline valid bus, capacity under 50 so B3 stays out of the way
    private Bus createValidBus() {
        return new Bus("12345678", 40, 75.0, "Diesel");
    }

    // birthdate string for someone who is exactly `age` now, so the age boundary
    // tests don't break next year
    private String birthdateForAge(int age) {
        LocalDate dob = LocalDate.now().minusYears(age).minusDays(1);
        return dob.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
    }

    private Driver driver(int experienceYears, String licenseType, int age) {
        return new Driver("23@@abcdAB", "Test Driver", experienceYears, licenseType,
                "12|King Street|Melbourne|VIC|Australia", birthdateForAge(age));
    }

    // ---------- B1: 8 digit unique ID ----------

    @Test
    public void validBusIDShouldBeAccepted() {
        assertDoesNotThrow(() -> createValidBus());
    }

    @Test
    public void busIDWithLettersShouldBeRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> new Bus("1234567A", 40, 75.0, "Diesel"));
    }

    @Test
    public void busIDWithWrongLengthShouldBeRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> new Bus("1234567", 40, 75.0, "Diesel"));   // too short
        assertThrows(IllegalArgumentException.class,
                () -> new Bus("123456789", 40, 75.0, "Diesel")); // too long
    }

    @Test
    public void duplicateBusIDShouldBeRejected() {
        BusRepository repository = new BusRepository(tempDir.resolve("bus.txt"));
        repository.add(new Bus("12345678", 40, 75.0, "Diesel"));

        assertThrows(IllegalArgumentException.class,
                () -> repository.add(new Bus("12345678", 30, 50.0, "Hybrid")));
    }

    // ---------- B2: capacity can't go up on update ----------

    @Test
    public void decreasingCapacityShouldBeAccepted() {
        Bus bus = createValidBus();
        assertDoesNotThrow(() -> bus.setCapacity(30));
        assertEquals(30, bus.getCapacity());
    }

    @Test
    public void increasingCapacityShouldBeRejected() {
        Bus bus = createValidBus();
        assertThrows(IllegalArgumentException.class, () -> bus.setCapacity(60));
    }

    @Test
    public void sameCapacityShouldBeAccepted() {
        Bus bus = createValidBus();
        assertDoesNotThrow(() -> bus.setCapacity(40)); // equal is fine
        assertEquals(40, bus.getCapacity());
    }

    // ---------- B3: over 50 can't drive capacity >= 50 ----------

    @Test
    public void driverOlderThan50OnLargeBusShouldNotBeAllowed() {
        Bus bus = new Bus("11111111", 50, 90.0, "Diesel");
        assertFalse(bus.canBeDrivenBy(driver(10, "Heavy", 55)));
    }

    @Test
    public void driverOlderThan50OnSmallBusShouldBeAllowed() {
        Bus bus = new Bus("11111112", 40, 90.0, "Diesel"); // capacity < 50
        assertTrue(bus.canBeDrivenBy(driver(10, "Heavy", 55)));
    }

    @Test
    public void driverExactly50OnLargeBusShouldBeAllowed() {
        Bus bus = new Bus("11111113", 50, 90.0, "Diesel");
        assertTrue(bus.canBeDrivenBy(driver(10, "Heavy", 50))); // 50 is not "older than 50"
    }

    @Test
    public void driverUnder50OnLargeBusShouldBeAllowed() {
        Bus bus = new Bus("11111114", 60, 90.0, "Diesel");
        assertTrue(bus.canBeDrivenBy(driver(10, "Heavy", 40)));
    }

    // ---------- B4: electric needs 5+ years experience ----------

    @Test
    public void lowExperienceOnElectricBusShouldNotBeAllowed() {
        Bus bus = new Bus("22222221", 40, 90.0, "Electricity"); // small + Heavy so only B4 can fail
        assertFalse(bus.canBeDrivenBy(driver(3, "Heavy", 40)));
    }

    @Test
    public void exactlyFiveYearsOnElectricBusShouldBeAllowed() {
        Bus bus = new Bus("22222222", 40, 90.0, "Electricity");
        assertTrue(bus.canBeDrivenBy(driver(5, "Heavy", 40))); // exactly 5 is allowed
    }

    @Test
    public void lowExperienceOnDieselBusShouldBeAllowed() {
        Bus bus = new Bus("22222223", 40, 90.0, "Diesel"); // rule only applies to electric
        assertTrue(bus.canBeDrivenBy(driver(1, "Light", 40)));
    }

    // ---------- B5: electric/hybrid need Heavy or PublicTransport ----------

    @Test
    public void lightLicenceOnElectricBusShouldNotBeAllowed() {
        Bus bus = new Bus("33333331", 40, 90.0, "Electricity"); // 8 yrs exp so only B5 can fail
        assertFalse(bus.canBeDrivenBy(driver(8, "Light", 40)));
    }

    @Test
    public void mediumLicenceOnHybridBusShouldNotBeAllowed() {
        Bus bus = new Bus("33333332", 40, 90.0, "Hybrid");
        assertFalse(bus.canBeDrivenBy(driver(8, "Medium", 40)));
    }

    @Test
    public void heavyLicenceOnElectricBusShouldBeAllowed() {
        Bus bus = new Bus("33333333", 40, 90.0, "Electricity");
        assertTrue(bus.canBeDrivenBy(driver(8, "Heavy", 40)));
    }

    @Test
    public void publicTransportLicenceOnHybridBusShouldBeAllowed() {
        Bus bus = new Bus("33333334", 40, 90.0, "Hybrid");
        assertTrue(bus.canBeDrivenBy(driver(8, "PublicTransport", 40)));
    }

    @Test
    public void lightLicenceOnDieselBusShouldBeAllowed() {
        Bus bus = new Bus("33333335", 40, 90.0, "Diesel"); // rule only applies to electric/hybrid
        assertTrue(bus.canBeDrivenBy(driver(2, "Light", 40)));
    }
}