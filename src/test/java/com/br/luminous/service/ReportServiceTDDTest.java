package com.br.luminous.service;

import com.br.luminous.exceptions.AddressNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class ReportServiceTDDTest {

    @Mock
    private ReportService reportService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

    }

    @Test
    public void shoulNotGetReportGivenAnInvalidAddress() {
        Long addressId = 189L;

        AddressNotFoundException exception = assertThrows(AddressNotFoundException.class, () -> {
           reportService.getReport(addressId);
        });

        assertEquals("Address not found.", exception.getMessage());
    }


}
