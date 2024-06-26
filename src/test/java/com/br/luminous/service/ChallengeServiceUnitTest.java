package com.br.luminous.service;

import com.br.luminous.entity.Address;
import com.br.luminous.entity.Challenge;
import com.br.luminous.entity.Device;
import com.br.luminous.repository.AddressRepository;
import com.br.luminous.repository.ChallengeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class ChallengeServiceUnitTest {
    @Mock
    ChallengeRepository challengeRepository;
    @Mock
    AddressRepository addressRepository;
    ChallengeService challengeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        this.challengeService = new ChallengeService(challengeRepository, addressRepository);
        Address address = new Address();
        address.setId(1L);
        address.setRemainingChallenges(new ArrayList<>());
        address.setCompleteChallenges(new ArrayList<>());
        when(challengeRepository.save(any())).thenReturn(new Challenge(2, 1500, "airfryer"));
        when(challengeRepository.findById(1L)).thenReturn(Optional.of(new Challenge(2, 1500, "airfryer")));
        when(addressRepository.save(any())).thenReturn(address);
        when(addressRepository.findById(1L)).thenReturn(Optional.of(address));
    }
    @Test
    void shouldCalculateKWHToCreateChallenge() {
        Address address = addressRepository.findById(1L).orElseThrow();
        //Arrange
        int estimatedTime = 2;
        int power = 1500;
        String deviceName = "airfryer";
        Double expectedResult = 90.0;
        //Act
        Challenge challenge = this.challengeService.createChallenge(address,estimatedTime, power, deviceName);
        //Assert
        assertNotNull(challenge);
        assertEquals(expectedResult, challenge.getKWHMonthly());
    }
    @Test
    void shouldCreateAChallengeBasedOnDeviceConsumption() {
        Address address = addressRepository.findById(1L).orElseThrow();
        int estimatedTime = 2;
        int power = 1500;
        String deviceName = "airfryer";
        Double expectedKwh = 90.0;
        Challenge challenge = this.challengeService.createChallenge(address,estimatedTime, power, deviceName);
        assertNotNull(challenge);
        assertEquals(estimatedTime, challenge.getEstimatedTime());
        assertEquals(power, challenge.getPower());
        assertEquals(deviceName, challenge.getDeviceName());
        assertEquals(expectedKwh, challenge.getKWHMonthly());
        assertEquals(1, address.getRemainingChallenges().size());
    }

    @Test
    void shouldNotCreateChallengeWhenDeviceConsumptionIsLessThanLimit() {
        Address address = addressRepository.findById(1L).orElseThrow();
        when(challengeRepository.save(any())).thenReturn(new Challenge(24, 60, "geladeira"));
        int estimatedTime = 24;
        int power = 60;
        String deviceName = "geladeira";
        Challenge challenge = this.challengeService.createChallenge(address, estimatedTime, power, deviceName);
        assertNull(challenge);
        assertEquals(0, address.getRemainingChallenges().size());
    }

    @Test
    void shouldMarkChallengeAsPriority() {
        Address address = addressRepository.findById(1L).orElseThrow();
        when(challengeRepository.save(any())).thenReturn(new Challenge(2, 3200, "forno-eletrico"));
        int estimatedTime = 2;
        int power = 3200;
        String deviceName = "forno-eletrico";
        Challenge challenge = this.challengeService.createChallenge(address, estimatedTime, power, deviceName);
        assertNotNull(challenge);
        assertTrue(challenge.isPriority());
        assertEquals(deviceName, challenge.getDeviceName());
        assertEquals(1, address.getRemainingChallenges().size());
    }

    @Test
    void shouldCompleteAChallenge() {
        var address = addressRepository.findById(1L).orElseThrow();
        Device device = new Device();
        device.setPower(3200);
        device.setUsageTime(LocalTime.of(1,0));
        device.setName("forno-eletrico");
        Challenge challenge = new Challenge(2, 3200, "forno-eletrico");
        var isDone = this.challengeService.markChallengeAsDone(device, challenge, address);
        assertTrue(isDone);
        assertTrue(challenge.isCompleted());
    }

    @Test
    void shouldRemoveCompletedChallengesFromRemainingChallenges() {
        Address address = addressRepository.findById(1L).orElseThrow();
        when(challengeRepository.save(any())).thenReturn(new Challenge(2, 3200, "forno-eletrico"));
        int estimatedTime = 2;
        int power = 3200;
        String deviceName = "forno-eletrico";
        Challenge challenge = this.challengeService.createChallenge(address, estimatedTime, power, deviceName);
        assertNotNull(challenge);
        assertTrue(challenge.isPriority());
        assertEquals(deviceName, challenge.getDeviceName());
        assertEquals(1, address.getRemainingChallenges().size());
        Device device = new Device();
        device.setPower(3200);
        device.setUsageTime(LocalTime.of(1,0));
        device.setName("forno-eletrico");
        var isDone = this.challengeService.markChallengeAsDone(device, challenge, address);
        assertTrue(challenge.isCompleted());
        assertTrue(isDone);
        assertEquals(0, address.getRemainingChallenges().size());
        assertEquals(1, address.getCompleteChallenges().size());
    }
}
