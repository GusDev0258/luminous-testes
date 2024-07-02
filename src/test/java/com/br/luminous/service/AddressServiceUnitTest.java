package com.br.luminous.service;

import com.br.luminous.entity.Address;
import com.br.luminous.entity.EnergyProvider;
import com.br.luminous.entity.User;
import com.br.luminous.mapper.AddressRequestToEntity;
import com.br.luminous.models.AddressRequest;
import com.br.luminous.repository.AddressRepository;
import com.br.luminous.repository.EnergyProviderRepository;
import com.br.luminous.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class AddressServiceUnitTest {

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private AddressRequestToEntity addressRequestToEntity;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserService userService;

    @Mock
    private EnergyProviderRepository energyProviderRepository;

    @InjectMocks
    private AddressService addressService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    private AddressRequest createValidAddressRequest() {
        AddressRequest addressRequest = new AddressRequest();
        addressRequest.setCity("São Paulo");
        addressRequest.setCep("01200-000");
        addressRequest.setHouseNumber(203);
        addressRequest.setInputVoltage(110);
        addressRequest.setStreet("Rua Cafelândia");
        addressRequest.setNeighborhood("Sumaré");
        addressRequest.setState("SP");
        addressRequest.setNickname("Casa da Mãe");
        addressRequest.setMainAddress(false);
        return addressRequest;
    }

    /*
    * CT006 - Apelido da residência em branco
    */
    @Test
    public void shouldNotCreateAddressGivenABlankAddressName() {
        // Arrange
        AddressRequest addressRequest = createValidAddressRequest();
        addressRequest.setNickname(" ");
        User user = new User();
        user.setId(1L);

        when(userService.getUserById(1L)).thenReturn(user);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> addressService.create(user.getId(), addressRequest)
        );

        // Assert
        assertEquals("Invalid address name.", exception.getMessage());
        verify(addressRepository, never()).save(any(Address.class));
    }

    /*
    * CT007 - CEP Inválido
    */
    @Test
    public void shouldNotCreateAddressGivenAnInvalidCEP() {
        // Arrange
        AddressRequest addressRequest = createValidAddressRequest();
        addressRequest.setCep("ABCDEF-GH");
        User user = new User();
        user.setId(1L);

        when(userService.getUserById(1L)).thenReturn(user);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> addressService.create(user.getId(), addressRequest)
        );

        // Assert
        assertEquals("Invalid address CEP.", exception.getMessage());
        verify(addressRepository, never()).save(any(Address.class));
    }

    /*
     * CT008 - Voltagem de entrada inválida
     */
    @Test
    public void shouldNotCreateAddressGivenAnInvalidInputVoltage() {
        // Arrange
        AddressRequest addressRequest = createValidAddressRequest();
        addressRequest.setInputVoltage(1000);
        User user = new User();
        user.setId(1L);

        when(userService.getUserById(1L)).thenReturn(user);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> addressService.create(user.getId(), addressRequest)
        );

        // Assert
        assertEquals("Invalid address input voltage.", exception.getMessage());
        verify(addressRepository, never()).save(any(Address.class));
    }

    /*
     * CT009 - Validar criação de residência (todas as entradas válidas)
     */
    @Test
    public void shouldCreateAddressGivenAValidAddressRequest() {
        // Arrange
        AddressRequest addressRequest = createValidAddressRequest();
        User user = new User();
        user.setId(1L);
        user.setAddresses(new ArrayList<>());

        EnergyProvider energyProvider = new EnergyProvider();
        energyProvider.setId(1L);


        Address mockAddress = new Address();
        mockAddress.setId(1L);
        mockAddress.setCity(addressRequest.getCity());
        mockAddress.setCep(addressRequest.getCep());
        mockAddress.setHouseNumber(addressRequest.getHouseNumber());
        mockAddress.setInputVoltage(addressRequest.getInputVoltage());
        mockAddress.setStreet(addressRequest.getStreet());
        mockAddress.setNeighborhood(addressRequest.getNeighborhood());
        mockAddress.setState(addressRequest.getState());
        mockAddress.setNickname(addressRequest.getNickname());
        mockAddress.setMainAddress(addressRequest.isMainAddress());
        mockAddress.setUser(user);

        when(userService.getUserById(1L)).thenReturn(user);
        when(addressRepository.save(any(Address.class))).thenAnswer(invocation -> {
            Address addressToSave = invocation.getArgument(0);
            addressToSave.setId(1L);
            return addressToSave;
        });
        when(addressRepository.findById(1L)).thenReturn(Optional.of(mockAddress));
        when(addressRequestToEntity.mapper(addressRequest)).thenReturn(mockAddress);
        when(energyProviderRepository.findById(addressRequest.getEnergyProviderId())).thenReturn(Optional.of(energyProvider));

        // Act
        Long addressId = addressService.create(user.getId(), addressRequest);
        Address addressCreated = addressRepository.findById(addressId).orElse(null);

        // Assert
        assertNotNull(addressCreated, "O endereço não foi cadastrado no sistema!");
        assertEquals(addressRequest.getCep(), addressCreated.getCep());
        assertEquals(addressRequest.getNeighborhood(), addressCreated.getNeighborhood());
        assertEquals(addressRequest.getCity(), addressCreated.getCity());
        assertEquals(addressRequest.getNickname(), addressCreated.getNickname());
        assertEquals(addressRequest.isMainAddress(), addressCreated.isMainAddress());
        assertEquals(addressRequest.getHouseNumber(), addressCreated.getHouseNumber());
        assertEquals(addressRequest.getInputVoltage(), addressCreated.getInputVoltage());
        assertEquals(addressRequest.getState(), addressCreated.getState());
        assertEquals(addressRequest.getStreet(), addressCreated.getStreet());

        verify(addressRepository, times(1)).save(any(Address.class));
        verify(addressRepository, times(1)).findById(1L);
    }
}
