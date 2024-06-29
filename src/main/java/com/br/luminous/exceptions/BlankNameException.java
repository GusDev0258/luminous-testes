package com.br.luminous.exceptions;

public class BlankNameException extends RuntimeException {

    public BlankNameException() {
        super("The given name is blank.");
    }
}
