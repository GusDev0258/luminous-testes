package com.br.luminous.service;

import com.br.luminous.entity.Address;
import com.br.luminous.entity.Challenge;
import com.br.luminous.entity.Device;
import com.br.luminous.repository.AddressRepository;
import com.br.luminous.repository.ChallengeRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ChallengeService {
    ChallengeRepository challengeRepository;
    AddressRepository addressRepository;
    private Map<String, Double> deviceLimit;

    public ChallengeService(ChallengeRepository challengeRepository, AddressRepository addressRepository) {
        deviceLimit = new HashMap<>();
        deviceLimit.put("ar-condicionado", 135.00);
        deviceLimit.put("geladeira", 45.00);
        deviceLimit.put("chuveiro", 25.00);
        this.challengeRepository = challengeRepository;
        this.addressRepository = addressRepository;
    }

    public Challenge createChallenge(Address address, int estimatedTime, int power, String deviceName) {
        try {
            String deviceNameFormated = deviceName.toLowerCase().replace(" ", "-");
            Challenge challenge = new Challenge(estimatedTime, power, deviceNameFormated);
            if (deviceLimit.containsKey(deviceNameFormated) && challenge.getKWHMonthly() <= deviceLimit.get(deviceNameFormated)) {
                return null;
            }
            this.challengeRepository.save(challenge);
            List<Challenge> challenges = address.getRemainingChallenges();
            challenges.add(challenge);
            address.setRemainingChallenges(challenges);
            this.addressRepository.save(address);
            return challenge;
        } catch (Exception exception) {
            throw new RuntimeException("Error creating challenge " + exception.getMessage());
        }
    }

    public boolean markChallengeAsDone(Device device, Challenge challenge, Address address) {
        var kwh = Challenge.calculateKwhMonthly(device.getUsageTime().getHour(), (int) device.getPower());
        if (kwh < challenge.getKWHMonthly()) {
            challenge.completeChallenge();
            this.challengeRepository.save(challenge);
            var challenges = address.getRemainingChallenges();
            challenges.remove(challenge);
            address.setRemainingChallenges(challenges);
            var completeChallenges = address.getCompleteChallenges();
            completeChallenges.add(challenge);
            address.setCompleteChallenges(completeChallenges);
            addressRepository.save(address);
            return true;
        }
        return false;
    }
}
