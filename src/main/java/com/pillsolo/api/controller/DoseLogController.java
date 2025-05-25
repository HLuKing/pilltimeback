package com.pillsolo.api.controller;

import com.pillsolo.api.dto.DoseLogRequest;
import com.pillsolo.api.dto.DoseLogResponse;
import com.pillsolo.api.service.DoseLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/doses")
@RequiredArgsConstructor
public class DoseLogController {

    private final DoseLogService doseLogService;

    @PostMapping
    public ResponseEntity<DoseLogResponse> logDose(@RequestBody DoseLogRequest request) {
        return ResponseEntity.ok(doseLogService.logDose(request));
    }

    @GetMapping("/date")
    public ResponseEntity<List<DoseLogResponse>> getLogsByDate(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(doseLogService.getLogsByDate(date));
    }

    @GetMapping("/{pillId}/today")
    public ResponseEntity<Map<String, Boolean>> hasTakenToday(@PathVariable Long pillId) {
        boolean taken = doseLogService.hasTakenToday(pillId);
        return ResponseEntity.ok(Collections.singletonMap("taken", taken));
    }
}
