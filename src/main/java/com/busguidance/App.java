package com.busguidance;

import com.busguidance.model.Driver;
import com.busguidance.repository.DriverRepository;

public class App {
    public static void main(String[] args) {
        DriverRepository repository = new DriverRepository();

        Driver driver = new Driver(
                "56$$abcdEF",
                "Michael Tan",
                7,
                "Heavy",
                "25|Swanston Street|Melbourne|VIC|Australia",
                "12-08-1994"
        );

        if (repository.retrieve(driver.getDriverID()) == null) {
            repository.add(driver);
            System.out.println("Driver added successfully.");
        } else {
            System.out.println("Driver already exists.");
        }

        System.out.println("Total drivers: " + repository.count());
    }
}