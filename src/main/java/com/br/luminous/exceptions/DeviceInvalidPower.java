package com.br.luminous.exceptions;

public class DeviceInvalidPower extends RuntimeException {

    public DeviceInvalidPower(String invalidPower) {
        super("Invalid power was given: " + invalidPower + "w.");
    }
}
