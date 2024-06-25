package com.br.luminous.service;

import com.br.luminous.entity.Address;
import com.br.luminous.exceptions.AddressNotFoundException;
import com.br.luminous.models.ReportResponse;
import com.br.luminous.repository.AddressRepository;
import com.br.luminous.repository.ConsumptionRepository;
import com.br.luminous.repository.ReportProjection;
import javassist.NotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ReportService {
    public ConsumptionRepository consumptionRepository;
    public AddressRepository addressRepository;

    public List<ReportResponse> getReport(Long addressId) {
        Optional<Address> address = addressRepository.findById(addressId);

        if(address.isEmpty()) {
            throw new AddressNotFoundException();
        }

        List<ReportProjection> reportProjections = consumptionRepository.getReport(address.get().getId());
        List<ReportResponse> reportResponses = new ArrayList<>();


        for (ReportProjection projection : reportProjections) {
            ReportResponse reportResponse = new ReportResponse();
            reportResponse.setPeriod(projection.getPeriod());
            reportResponse.setName(projection.getName());
            reportResponse.setEnergyConsumptionKWh(projection.getEnergyConsumptionKWh());
            reportResponse.setEnergyConsumptionReais(projection.getEnergyConsumptionReais());
            reportResponses.add(reportResponse);
        }

        return reportResponses;
    }

}