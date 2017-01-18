package com.github.sutaakar.cargo.service;

/**
 * Locator of Car service. 
 */
public class CarServiceLocator {

    private static final CarService INSTANCE = new CarService();

    private CarServiceLocator() {
    }

    public static CarService getCarService() {
        return INSTANCE;
    }
}
