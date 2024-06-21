package com.br.luminous.controller;

import com.br.luminous.exceptions.AddressNotFoundException;
import com.br.luminous.exceptions.BillFileNotFoundException;
import com.br.luminous.exceptions.EnergyBillNotFoundException;
import com.br.luminous.models.ApiResponse;
import com.br.luminous.models.EnergyBillRequest;
import com.br.luminous.models.EnergyBillResponse;
import com.br.luminous.service.EnergyBillService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@AllArgsConstructor
@RequestMapping("/api/energyBill")
public class EnergyBillController {
    private EnergyBillService energyBillService;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EnergyBillResponse>> getEnergyBillById(@PathVariable Long id) {
        try {
            EnergyBillResponse energyBillResponse = energyBillService.getById(id);
            var apiResponse = new ApiResponse<EnergyBillResponse>(true, "Energy Bill found", energyBillResponse);
            return ResponseEntity.ok(apiResponse);
        } catch (EnergyBillNotFoundException e) {
            var apiResponse = new ApiResponse<EnergyBillResponse>(false, e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponse);
        }
    }

    @PostMapping("/address/{address_id}/billFile/{billFile_id}")
    public ResponseEntity<ApiResponse<Long>> createEnergyBill(@PathVariable Long address_id,
                                                              @PathVariable Long billFile_id, @RequestBody EnergyBillRequest energyBillRequest) {
        try {
            Long id = energyBillService.create(address_id, billFile_id, energyBillRequest);
            var response = new ApiResponse<Long>(true, "Energy bill created", id);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (AddressNotFoundException | BillFileNotFoundException e) {
            var response = new ApiResponse<Long>(false, e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @GetMapping("/getAll/{address_id}")
    public ResponseEntity<List<EnergyBillResponse>> getAll(@PathVariable Long address_id) {
        try {
            List<EnergyBillResponse> response = energyBillService.getAll(address_id);
            return ResponseEntity.ok(response);
        } catch (AddressNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<EnergyBillResponse>> update(@PathVariable Long id,
                                                                  @RequestBody EnergyBillRequest energyBillRequest) {
        try {
            EnergyBillResponse response = energyBillService.update(id, energyBillRequest);
            ApiResponse<EnergyBillResponse> apiResponse = new ApiResponse<>(true, "Energy Bill updated", response);
            return ResponseEntity.ok(apiResponse);
        } catch (EnergyBillNotFoundException e) {
            ApiResponse<EnergyBillResponse> apiResponse = new ApiResponse<>(false, e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponse);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        try {
            energyBillService.delete(id);
            var apiResponse = new ApiResponse<Void>(true, "EnergyBill Deleted", null);
            return ResponseEntity.ok().body(apiResponse);
        } catch (EnergyBillNotFoundException e) {
            var apiResponse = new ApiResponse<Void>(false, e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponse);
        }
    }
}
