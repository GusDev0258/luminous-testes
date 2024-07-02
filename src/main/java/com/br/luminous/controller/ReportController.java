package com.br.luminous.controller;

import com.br.luminous.exceptions.EmptyReportException;
import com.br.luminous.models.ApiResponse;
import com.br.luminous.models.ReportResponse;
import com.br.luminous.service.ReportService;
import javassist.NotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("api/report")
public class ReportController {

    private ReportService reportService;

    @GetMapping("/address/{addressId}")
    public ResponseEntity<ApiResponse<List<ReportResponse>>> getReport(@PathVariable Long addressId) {
        try {
            List<ReportResponse> reportResponses = reportService.getReport(addressId);
            var response = new ApiResponse<List<ReportResponse>>(true, "Reports", reportResponses);
            return ResponseEntity.ok().body(response);
        } catch (EmptyReportException ex) {
            var response = new ApiResponse<List<ReportResponse>>(false, ex.getMessage(), null);
            return ResponseEntity.ok().body(response);
        }
    }

}
