package com.br.luminous.service;

import com.br.luminous.entity.Address;
import com.br.luminous.entity.EnergyProvider;
import com.br.luminous.entity.User;
import com.br.luminous.models.AddressRequest;
import com.br.luminous.repository.AddressRepository;
import com.github.dockerjava.core.dockerfile.DockerfileStatement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class AddressServiceUnitTest {

    @Mock
    private AddressService addressService;

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private UserService userService;

    @Mock
    private EnergyProviderService energyProviderService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        EnergyProvider mockedEnergyProvider = new EnergyProvider();
        mockedEnergyProvider.setId(1L);
        when(energyProviderService.getEnergyProviderById(1L)).thenReturn(mockedEnergyProvider);

        User mockedUser = new User();
        mockedUser.setId(1L);
        when(userService.getUserById(1L)).thenReturn(mockedUser);
    }

    /*
    * CT006 - Apelido da residência em branco
    */
    @Test
    public void shouldNotCreateAddressGivenABlankAddressName() {
        var energyProvider = energyProviderService.getEnergyProviderById(1L);

        AddressRequest addressRequest = new AddressRequest();
        addressRequest.setCity("São Paulo");
        addressRequest.setCep("01200-000");
        addressRequest.setHouseNumber(203);
        addressRequest.setInputVoltage(110);
        addressRequest.setStreet("Rua Cafelândia");
        addressRequest.setNeighborhood("Sumaré");
        addressRequest.setEnergyProviderId(energyProvider.getId());
        addressRequest.setState("SP");
        addressRequest.setNickname(" ");
        addressRequest.setMainAddress(false);
        User user = userService.getUserById(1L);

        Exception exception = assertThrows(Exception.class, () -> {
            addressService.create(user.getId(), addressRequest);
        });

        assertEquals("Invalid address name.", exception.getMessage());
    }

    /*
    * CT007 - CEP Inválido
    */
    @Test
    public void shouldNotCreateAddressGivenAnInvalidCEP() {
        var energyProvider = energyProviderService.getEnergyProviderById(1L);

        AddressRequest addressRequest = new AddressRequest();
        addressRequest.setCity("São Paulo");
        addressRequest.setCep("ADCDE-FGH");
        addressRequest.setHouseNumber(203);
        addressRequest.setInputVoltage(110);
        addressRequest.setStreet("Rua Cafelândia");
        addressRequest.setNeighborhood("Sumaré");
        addressRequest.setEnergyProviderId(energyProvider.getId());
        addressRequest.setState("SP");
        addressRequest.setNickname("Casa da Mãe");
        addressRequest.setMainAddress(false);

        User user = userService.getUserById(1L);

        Exception exception = assertThrows(Exception.class, () -> {
            addressService.create(user.getId(), addressRequest);
        });

        assertEquals("Invalid address CEP.", exception.getMessage());
    }

    /*
     * CT008 - Voltagem de entrada inválida
     */
    @Test
    public void shouldNotCreateAddressGivenAnInvalidInputVoltage() {
        var energyProvider = energyProviderService.getEnergyProviderById(1L);

        AddressRequest addressRequest = new AddressRequest();
        addressRequest.setCity("São Paulo");
        addressRequest.setCep("01200-000");
        addressRequest.setHouseNumber(203);
        addressRequest.setInputVoltage(1000);
        addressRequest.setStreet("Rua Cafelândia");
        addressRequest.setNeighborhood("Sumaré");
        addressRequest.setEnergyProviderId(energyProvider.getId());
        addressRequest.setState("SP");
        addressRequest.setNickname("Casa da Mãe");
        addressRequest.setMainAddress(false);

        User user = userService.getUserById(1L);

        Exception exception = assertThrows(Exception.class, () -> {
            addressService.create(user.getId(), addressRequest);
        });

        assertEquals("Invalid address input voltage.", exception.getMessage());
    }

    /*
     * CT009 - Validar criação de residência (todas as entradas válidas)
     */
    @Test
    public void shouldCreateAddressGivenAValidAddressRequest() {
        var energyProvider = energyProviderService.getEnergyProviderById(1L);

        AddressRequest addressRequest = new AddressRequest();
        addressRequest.setCity("São Paulo");
        addressRequest.setCep("01200-000");
        addressRequest.setHouseNumber(203);
        addressRequest.setInputVoltage(110);
        addressRequest.setStreet("Rua Cafelândia");
        addressRequest.setNeighborhood("Sumaré");
        addressRequest.setEnergyProviderId(energyProvider.getId());
        addressRequest.setState("SP");
        addressRequest.setNickname("Casa da Mãe");
        addressRequest.setMainAddress(false);
        User user = userService.getUserById(1L);

        Address mockAddress = new Address();
        mockAddress.setCity("São Paulo");
        mockAddress.setCep("01200-000");
        mockAddress.setHouseNumber(203);
        mockAddress.setInputVoltage(110);
        mockAddress.setStreet("Rua Cafelândia");
        mockAddress.setNeighborhood("Sumaré");
        mockAddress.setEnergyProvider(energyProvider);
        mockAddress.setState("SP");
        mockAddress.setNickname("Casa da Mãe");
        mockAddress.setMainAddress(false);
        mockAddress.setUser(user);

        when(addressRepository.save(any(Address.class))).thenAnswer(invocation -> {
            Address addressToSave = invocation.getArgument(0);
            addressToSave.setId(1L);
            return addressToSave;
        });

        when(addressRepository.findById(1L)).thenReturn(Optional.of(mockAddress));

        Long addressId = addressService.create(user.getId(), addressRequest);
        Address addressCreated = addressRepository.findById(addressId).orElse(null);

        assertNotNull(addressCreated, "O endereço não foi cadastrado no sistema!");
        assertEquals(addressRequest.getCep(), addressCreated.getCep());
        assertEquals(addressRequest.getNeighborhood(), addressCreated.getNeighborhood());
        assertEquals(addressRequest.getCity(), addressCreated.getCity());
        assertEquals(addressRequest.getNickname(), addressCreated.getNickname());
        assertEquals(addressRequest.isMainAddress(), addressCreated.isMainAddress());
        assertEquals(addressRequest.getHouseNumber(), addressCreated.getHouseNumber());
        assertEquals(addressRequest.getInputVoltage(), addressCreated.getInputVoltage());
        assertEquals(addressRequest.getEnergyProviderId(), addressCreated.getEnergyProvider().getId());
        assertEquals(addressRequest.getState(), addressCreated.getState());
        assertEquals(addressRequest.getStreet(), addressCreated.getStreet());

        verify(addressRepository, times(1)).save(any(Address.class));
        verify(addressRepository, times(1)).findById(1L);
    }

}
