package com.br.luminous.exceptions;

public class DuplicateDeviceException extends RuntimeException {

    public DuplicateDeviceException() {
        super("Device already exists with name provided.");
    }
}
