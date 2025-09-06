package com.debt.debt.domain.debtsSimulation.controller;

import com.debt.debt.domain.debtsSimulation.dto.SimulationRequest;
import com.debt.debt.domain.debtsSimulation.dto.SimulationResponse;
import com.debt.debt.domain.debtsSimulation.service.DebtsSimulationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/simulation")
@RequiredArgsConstructor
public class DebtsSimulationController {

    private final DebtsSimulationService simulationService;

    @PostMapping
    public SimulationResponse runSimulation(@RequestBody SimulationRequest request) {
        return simulationService.runSimulation(request);
    }

    //비교 시뮬레이션
    @PostMapping("/compare")
    public List<SimulationResponse> runSimulationBatch(@RequestBody List<SimulationRequest> requests) {
        return simulationService.runSimulationBatch(requests);
    }
}