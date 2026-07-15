package com.irctc.irctc_backend.modules.pnr.controller;

import com.irctc.irctc_backend.modules.common.dto.ApiResponse;
import com.irctc.irctc_backend.modules.pnr.dto.PnrStatusResponse;
import com.irctc.irctc_backend.modules.pnr.service.PnrService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public/v1/pnr")
@RequiredArgsConstructor
public class PnrController {

    private final PnrService pnrService;

    @GetMapping("/{pnr}")
    public ResponseEntity<ApiResponse<PnrStatusResponse>> getPnrStatus(@PathVariable String pnr) {
        PnrStatusResponse data = pnrService.getPnrStatus(pnr);
        return ResponseEntity.ok(ApiResponse.success(data));
    }
}
