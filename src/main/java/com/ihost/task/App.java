package com.ihost.task;

import com.ihost.task.driver.Driver;

import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);

        System.out.print("Producers count: ");
        int producersCount = scan.nextInt();
        System.out.print("Consumers count: ");
        int consumersCount = scan.nextInt();

        final Driver driver = new Driver(producersCount, consumersCount);
        driver.run();
    }
}
