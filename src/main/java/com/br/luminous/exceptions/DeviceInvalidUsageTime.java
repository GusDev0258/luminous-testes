package com.br.luminous.exceptions;

import java.time.LocalTime;

public class DeviceInvalidUsageTime extends RuntimeException {

    public DeviceInvalidUsageTime(LocalTime usageTime) {
        super("Invalid usage time was given: " + usageTime.getHour() + "h.");
    }
}

