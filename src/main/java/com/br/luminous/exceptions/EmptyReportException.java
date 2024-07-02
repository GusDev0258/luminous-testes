package com.br.luminous.exceptions;

public class EmptyReportException extends Exception {
    public EmptyReportException() {
        super("There is no data to show in the report.");
    }
}
