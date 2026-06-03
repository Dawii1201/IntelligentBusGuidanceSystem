package com.busguidance.integration;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.busguidance.model.Bus;
import com.busguidance.repository.BusRepository;

// Integration tests for the bus side: real BusRepository + real Bus reading and
// writing a real .txt file (a temp file so we don't touch the actual bus.txt).
// To prove data really hits disk, most tests reopen the file with a second
// repository and read it back.
public class BusIntegrationTest {

    @TempDir
    Path tempDir;

    // 1. valid buses are stored correctly
    @Test
    public void validBusIsStoredAndReadBack() {
        Path file = tempDir.resolve("bus.txt");
        BusRepository repo = new BusRepository(file);

        repo.add(new Bus("12345678", 45, 80.0, "Diesel"));

        // reopen the same file with a new repo so we know it was actually saved
        BusRepository reopened = new BusRepository(file);
        Bus stored = reopened.retrieve("12345678");

        assertNotNull(stored);
        assertEquals(45, stored.getCapacity());
        assertEquals("Diesel", stored.getFuelType());
    }

    // 2. invalid buses are rejected (duplicate ID) and nothing extra gets written
    @Test
    public void duplicateBusIsRejectedAndNotStored() {
        Path file = tempDir.resolve("bus.txt");
        BusRepository repo = new BusRepository(file);

        repo.add(new Bus("12345678", 45, 80.0, "Diesel"));
        assertThrows(IllegalArgumentException.class,
                () -> repo.add(new Bus("12345678", 30, 60.0, "Hybrid")));

        // still only one row on disk
        assertEquals(1, new BusRepository(file).count());
    }

    // 3. updates are persisted correctly
    @Test
    public void updateIsPersistedToFile() {
        Path file = tempDir.resolve("bus.txt");
        BusRepository repo = new BusRepository(file);

        repo.add(new Bus("87654321", 50, 70.0, "Hybrid"));
        repo.update("87654321", new Bus("87654321", 35, 65.0, "Hybrid")); // capacity down

        BusRepository reopened = new BusRepository(file);
        Bus updated = reopened.retrieve("87654321");

        assertNotNull(updated);
        assertEquals(35, updated.getCapacity());
        assertEquals(65.0, updated.getFuelLevel());
    }

    // 4. record counts are updated correctly
    @Test
    public void countReflectsStoredBuses() {
        Path file = tempDir.resolve("bus.txt");
        BusRepository repo = new BusRepository(file);

        assertEquals(0, repo.count()); // empty to start

        repo.add(new Bus("11111111", 40, 90.0, "Diesel"));
        repo.add(new Bus("22222222", 30, 50.0, "Electricity"));
        assertEquals(2, repo.count());

        repo.update("11111111", new Bus("11111111", 20, 90.0, "Diesel"));
        assertEquals(2, new BusRepository(file).count()); // update doesn't add a row
    }


    @Test
    public void fileIsHumanReadable() throws Exception {
        Path file = tempDir.resolve("bus.txt");
        BusRepository repo = new BusRepository(file);
        repo.add(new Bus("12345678", 45, 80.0, "Diesel"));

        List<String> lines = Files.readAllLines(file);
        assertEquals("busID,capacity,fuelLevel,fuelType", lines.get(0));
        assertTrue(lines.get(1).startsWith("12345678,45,"));
    }
}