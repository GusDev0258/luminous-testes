package com.br.luminous.service;

import com.br.luminous.entity.Address;
import com.br.luminous.entity.BillFile;
import com.br.luminous.entity.User;
import com.br.luminous.mapper.EnergyBillRequestToEntity;
import com.br.luminous.mapper.EnergyBillToResponse;
import com.br.luminous.models.EnergyBillRequest;
import com.br.luminous.repository.EnergyBillRepository;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
public class EnergyBillServiceIntegrationTest {
    @Mock
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
        RestAssured.basePath = "/api/EnergyBill";
        MockitoAnnotations.openMocks(this);
        User mockedUser = new User();
        mockedUser.setId(1L);
        Address mockedAddress = new Address();
        mockedAddress.setId(1L);
        mockedAddress.setCep("89150000");
        mockedAddress.setCity("Ibirama");
        mockedAddress.setHouseNumber(999);
        mockedAddress.setInputVoltage(220);
        mockedAddress.setMainAddress(true);
        mockedAddress.setUser(mockedUser);
        mockedAddress.setNickname("Casinha");
        mockedAddress.setState("Santa Catarina");
        mockedAddress.setStreet("rua dos anjos");
        BillFile file = new BillFile();
        file.setId(1L);
        when(addressService.getAddressById(1L)).thenReturn(mockedAddress);
        when(billFileService.getById(1L)).thenReturn(file);
    }


}
