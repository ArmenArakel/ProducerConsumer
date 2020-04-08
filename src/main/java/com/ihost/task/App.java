package com.ihost.task;

import com.ihost.task.driver.Driver;

import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        int producersCount;
        int consumersCount;
        try(Scanner scan = new Scanner(System.in)) {

            System.out.print("Producers count: ");
            producersCount = scan.nextInt();
            System.out.print("Consumers count: ");
            consumersCount = scan.nextInt();
        }

        final Driver driver = new Driver(producersCount, consumersCount);
        driver.run();
    }
}
