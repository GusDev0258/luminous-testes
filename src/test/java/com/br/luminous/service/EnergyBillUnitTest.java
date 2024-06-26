package com.br.luminous.service;

import com.br.luminous.entity.Address;
import com.br.luminous.entity.BillFile;
import com.br.luminous.entity.EnergyBill;
import com.br.luminous.mapper.EnergyBillRequestToEntity;
import com.br.luminous.mapper.EnergyBillToResponse;
import com.br.luminous.models.EnergyBillRequest;
import com.br.luminous.repository.EnergyBillRepository;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.ArrayList;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;


class EnergyBillUnitTest {

    @InjectMocks
    EnergyBillService energyBillService;
    @Mock
    private EnergyBillRepository energyBillRepository;
    @Mock
    private AddressService addressService;
    @Mock
    private BillFileService billFileService;
    @Mock
    private EnergyBillRequestToEntity energyBillRequestToEntity;
    @Mock
    private EnergyBillToResponse energyBillToResponse;

    @BeforeEach()
    void setUp() {
        MockitoAnnotations.openMocks(this);
        Address mockedAddress = new Address();
        mockedAddress.setId(1L);
        mockedAddress.setEnergyBills(new ArrayList<>());
        BillFile file = new BillFile();
        file.setId(1L);
        when(addressService.getAddressById(1L)).thenReturn(mockedAddress);
        when(billFileService.getById(1L)).thenReturn(file);
        when(energyBillRequestToEntity.mapper(any(EnergyBillRequest.class))).thenReturn(new EnergyBill());
        var energyBill = new EnergyBill();
        energyBill.setId(1L);
        when(energyBillRepository.save(any(EnergyBill.class))).thenReturn(energyBill);
        //Refatorar para ter as dependências mockadas.
    }

    @Test
    void shouldNotCreateAEnergyBillWithEqualsMonthOnDueDateAndReferenceDate() {
        var address = addressService.getAddressById(1L);
        var billFile = billFileService.getById(1L);
        EnergyBillRequest request = new EnergyBillRequest();
        request.setReferenceDate(LocalDate.of(2024, 05, 25));
        request.setDueDate(LocalDate.of(2024, 05, 10));
        request.setEnergyConsumptionReais(100.00);
        request.setEnergyConsumption_kWh(200.00);
        var id = energyBillService.create(address.getId(), billFile.getId(), request);
        assertNull(id, "Should not create a bill with the same month on due date and reference date");
    }

    @Test
    void shouldNotCreateAEnergyBillWithReferenceDateMonthBeforeTwoMonthsFromDueDate() {
        var address = addressService.getAddressById(1L);
        var billFile = billFileService.getById(1L);
        EnergyBillRequest request = new EnergyBillRequest();
        request.setReferenceDate(LocalDate.of(2024, 03, 25));
        request.setDueDate(LocalDate.of(2024, 05, 10));
        request.setEnergyConsumptionReais(100.00);
        request.setEnergyConsumption_kWh(200.00);
        var id = energyBillService.create(address.getId(), billFile.getId(), request);
        assertNull(id, "Should not create a bill with reference date before two months from due date");
    }

    @Test
    void shouldNotCreateAEnergyBillWithReferenceDateMonthGreaterThanDueDateMonth(){
        var address = addressService.getAddressById(1L);
        var billFile = billFileService.getById(1L);
        EnergyBillRequest request = new EnergyBillRequest();
        request.setReferenceDate(LocalDate.of(2024, 06, 25));
        request.setDueDate(LocalDate.of(2024, 05, 10));
        request.setEnergyConsumptionReais(100.00);
        request.setEnergyConsumption_kWh(200.00);
        var id = energyBillService.create(address.getId(), billFile.getId(), request);
        assertNull(id, "Should not create a bill with reference date greater than due date");
    }

    @Test
    void shouldNotCreateAEnergyBillWithInvalidDateFormat() {
        var address = addressService.getAddressById(1L);
        var billFile = billFileService.getById(1L);
        EnergyBillRequest request = new EnergyBillRequest();
        var referenceDate = "2024/10/04";
        var dueDate = "05/13/2024";
        request.setEnergyConsumptionReais(100.00);
        request.setEnergyConsumption_kWh(200.00);
        DateTimeException exception = assertThrows(DateTimeException.class, () -> {
            var referenceDateObject = LocalDate.parse(referenceDate);
            var dueDateObject = LocalDate.parse(dueDate);
            request.setReferenceDate(referenceDateObject);
            request.setDueDate(dueDateObject);
        });
        energyBillService.create(address.getId(), billFile.getId(), request);
        assertNotNull(exception);
    }

    @Test
    void shouldNotCreateAEnergyBillWithEmptyReferenceDate() {
        var address = addressService.getAddressById(1L);
        var billFile = billFileService.getById(1L);
        EnergyBillRequest request = new EnergyBillRequest();
        request.setReferenceDate(null);
        request.setDueDate(LocalDate.of(2024, 05, 10));
        request.setEnergyConsumptionReais(100.00);
        request.setEnergyConsumption_kWh(200.00);
        var id = energyBillService.create(address.getId(), billFile.getId(), request);
        assertNull(id, "Should not create a bill with empty reference date");
    }

    @Test
    void shouldNotCreateAEnergyBillWithEmptyDueDate() {
        var address = addressService.getAddressById(1L);
        var billFile = billFileService.getById(1L);
        EnergyBillRequest request = new EnergyBillRequest();
        request.setReferenceDate(LocalDate.of(2024, 04, 10));
        request.setDueDate(null);
        request.setEnergyConsumptionReais(100.00);
        request.setEnergyConsumption_kWh(200.00);
        var id = energyBillService.create(address.getId(), billFile.getId(), request);
        assertNull(id, "Should not create a bill with empty due date");
    }

    @Test
    void shouldNotCreateAEnergyBillWithEmptyConsumptionInReais() {
        var address = addressService.getAddressById(1L);
        var billFile = billFileService.getById(1L);
        EnergyBillRequest request = new EnergyBillRequest();
        request.setReferenceDate(LocalDate.of(2024, 04, 10));
        request.setDueDate(LocalDate.of(2024, 05, 10));
        request.setEnergyConsumptionReais(null);
        request.setEnergyConsumption_kWh(200.00);
        var id = energyBillService.create(address.getId(), billFile.getId(), request);
        assertNull(id, "Should not create a bill with empty consumption in reais");
    }

    @Test
    void shouldNotCreateAEnergyBillWithNegativeConsumptionInReais() {
        var address = addressService.getAddressById(1L);
        var billFile = billFileService.getById(1L);
        EnergyBillRequest request = new EnergyBillRequest();
        request.setReferenceDate(LocalDate.of(2024, 04, 10));
        request.setDueDate(LocalDate.of(2024, 05, 10));
        request.setEnergyConsumptionReais(-40.00);
        request.setEnergyConsumption_kWh(200.00);
        var id = energyBillService.create(address.getId(), billFile.getId(), request);
        assertNull(id, "Should not create a bill with negative consumption in reais");
    }

    @Test
    void shouldNotCreateAEnergyBillWithNotNumericConsumptionInReais() {
        EnergyBillRequest request = new EnergyBillRequest();
        request.setReferenceDate(LocalDate.of(2024, 04, 10));
        request.setDueDate(LocalDate.of(2024, 05, 10));
        request.setEnergyConsumption_kWh(200.00);
        NumberFormatException exception = assertThrows(NumberFormatException.class, () -> {
            request.setEnergyConsumptionReais(Double.parseDouble("Devo falhar"));
        });
        assertNotNull(exception);
    }

    @Test
    void shouldNotCreateAEnergyBillWithNegativeKWhConsumption() {
        var address = addressService.getAddressById(1L);
        var billFile = billFileService.getById(1L);
        EnergyBillRequest request = new EnergyBillRequest();
        request.setReferenceDate(LocalDate.of(2024, 04, 10));
        request.setDueDate(LocalDate.of(2024, 05, 10));
        request.setEnergyConsumptionReais(100.00);
        request.setEnergyConsumption_kWh(-300.00);
        var id = energyBillService.create(address.getId(), billFile.getId(), request);
        assertNull(id, "Should not create a bill with negative consumption in kWh");
    }

    @Test
    void shouldNotCreateAEnergyBillWithNotNumerickWhConsumption() {
        var address = addressService.getAddressById(1L);
        var billFile = billFileService.getById(1L);
        EnergyBillRequest request = new EnergyBillRequest();
        request.setReferenceDate(LocalDate.of(2024, 04, 10));
        request.setDueDate(LocalDate.of(2024, 05, 10));
        request.setEnergyConsumptionReais(100.00);
        NumberFormatException exception = assertThrows(NumberFormatException.class, () -> {
            request.setEnergyConsumption_kWh(Double.parseDouble("Devo falhar"));
        });
        energyBillService.create(address.getId(), billFile.getId(), request);
        assertNotNull(exception);
    }

    @Test
    void shouldNotCreateAEnergyBillWithEmptyKWhConsumption()  {
        var address = addressService.getAddressById(1L);
        var billFile = billFileService.getById(1L);
        EnergyBillRequest request = new EnergyBillRequest();
        request.setReferenceDate(LocalDate.of(2024, 04, 10));
        request.setDueDate(LocalDate.of(2024, 05, 10));
        request.setEnergyConsumptionReais(100.00);
        request.setEnergyConsumption_kWh(null);
        var id = energyBillService.create(address.getId(), billFile.getId(), request);
        assertNull(id, "Should not create a bill with empty consumption in kWh");
    }

    @Test
    void shouldCreateAEnergyBill() {
        var address = addressService.getAddressById(1L);
        var billFile = billFileService.getById(1L);
        EnergyBillRequest request = new EnergyBillRequest();
        request.setReferenceDate(LocalDate.of(2024, 04, 10));
        request.setDueDate(LocalDate.of(2024, 05, 10));
        request.setEnergyConsumptionReais(100.00);
        request.setEnergyConsumption_kWh(200.00);
        var energyBillId = energyBillService.create(address.getId(), billFile.getId(), request);
        assertNotNull(energyBillId);
        assertEquals(1L, energyBillId);
    }

}