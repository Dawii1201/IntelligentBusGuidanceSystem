package com.busguidance.repository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.busguidance.model.Bus;

public class BusRepository {

    private final Path filePath;

    // first line of bus.txt
    private static final String HEADER = "busID,capacity,fuelLevel,fuelType";

    // uses the real bus.txt under resources
    public BusRepository() {
        this.filePath = Path.of("src", "main", "resources", "bus.txt");
        createFileIfNotExists();
    }

    // lets the integration tests point at a temp file instead
    public BusRepository(Path filePath) {
        this.filePath = filePath;
        createFileIfNotExists();
    }

    // B1: reject duplicate bus IDs
    public boolean add(Bus bus) {
        if (bus == null) {
            throw new IllegalArgumentException("Bus cannot be null.");
        }

        List<Bus> buses = loadBuses();

        for (Bus existing : buses) {
            if (existing.getBusID().equals(bus.getBusID())) {
                throw new IllegalArgumentException("Duplicate bus ID is not allowed.");
            }
        }

        buses.add(bus);
        saveBuses(buses);
        return true;
    }

    public Bus retrieve(String busID) {
        if (busID == null || busID.trim().isEmpty()) {
            return null;
        }

        for (Bus bus : loadBuses()) {
            if (bus.getBusID().equals(busID)) {
                return bus;
            }
        }
        return null;
    }

    // B2: capacity can drop but not go up, and the ID can't change
    public boolean update(String busID, Bus updatedBus) {
        if (busID == null || busID.trim().isEmpty()) {
            throw new IllegalArgumentException("Bus ID cannot be empty.");
        }
        if (updatedBus == null) {
            throw new IllegalArgumentException("Updated bus cannot be null.");
        }

        List<Bus> buses = loadBuses();

        for (int i = 0; i < buses.size(); i++) {
            Bus existing = buses.get(i);

            if (existing.getBusID().equals(busID)) {

                if (!existing.getBusID().equals(updatedBus.getBusID())) {
                    throw new IllegalArgumentException("Bus ID cannot be changed during update.");
                }

                if (updatedBus.getCapacity() > existing.getCapacity()) {
                    throw new IllegalArgumentException("Bus capacity cannot increase during update.");
                }

                buses.set(i, updatedBus);
                saveBuses(buses);
                return true;
            }
        }
        return false; // no bus with that ID
    }

    public int count() {
        return loadBuses().size();
    }

    public List<Bus> retrieveAll() {
        return loadBuses();
    }

    // make the file (and resources folder) with just the header if it isn't there yet
    private void createFileIfNotExists() {
        try {
            Path parent = filePath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }

            if (!Files.exists(filePath)) {
                Files.createFile(filePath);
                Files.write(filePath, List.of(HEADER));
            } else if (Files.size(filePath) == 0) {
                Files.write(filePath, List.of(HEADER));
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not create bus storage file.", e);
        }
    }

    // read every row back into Bus objects. format: busID,capacity,fuelLevel,fuelType
    private List<Bus> loadBuses() {
        List<Bus> buses = new ArrayList<>();

        try {
            List<String> lines = Files.readAllLines(filePath);

            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i).trim();

                if (line.isEmpty() || line.equals(HEADER)) {
                    continue;
                }

                String[] parts = line.split(",", -1);
                if (parts.length != 4) {
                    throw new IllegalArgumentException("Invalid bus data format at line " + (i + 1));
                }

                String busID = parts[0].trim();
                int capacity = Integer.parseInt(parts[1].trim());
                double fuelLevel = Double.parseDouble(parts[2].trim());
                String fuelType = parts[3].trim();

                buses.add(new Bus(busID, capacity, fuelLevel, fuelType));
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not read bus storage file.", e);
        }
        return buses;
    }

    // rewrite the whole file with the current list
    private void saveBuses(List<Bus> buses) {
        List<String> lines = new ArrayList<>();
        lines.add(HEADER);

        for (Bus bus : buses) {
            lines.add(bus.getBusID() + ","
                    + bus.getCapacity() + ","
                    + bus.getFuelLevel() + ","
                    + bus.getFuelType());
        }

        try {
            Files.write(filePath, lines);
        } catch (IOException e) {
            throw new RuntimeException("Could not save bus storage file.", e);
        }
    }
}