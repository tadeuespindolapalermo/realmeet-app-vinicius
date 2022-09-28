package br.com.sw2you.realmeet.controller;

import static java.util.concurrent.CompletableFuture.runAsync;
import static java.util.concurrent.CompletableFuture.supplyAsync;

import br.com.sw2you.realmeet.api.facade.AllocationsApi;
import br.com.sw2you.realmeet.api.model.*;
import br.com.sw2you.realmeet.service.AllocationService;
import br.com.sw2you.realmeet.util.ResponseEntityUtils;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AllocationController implements AllocationsApi {
    private final Executor controllersExecutor;
    private final AllocationService allocationService;

    public AllocationController(Executor controllersExecutor, AllocationService allocationService) {
        this.controllersExecutor = controllersExecutor;
        this.allocationService = allocationService;
    }

    @Override
    public CompletableFuture<ResponseEntity<AllocationDTO>> createAllocation(
        String apiKey,
        CreateAllocationDTO createAllocationDTO
    ) {
        return supplyAsync(() -> allocationService.createAllocation(createAllocationDTO), controllersExecutor)
            .thenApply(ResponseEntityUtils::created);
    }

    @Override
    public CompletableFuture<ResponseEntity<Void>> deleteAllocation(String apiKey, Long id) {
        return runAsync(() -> allocationService.deleteAllocation(id), controllersExecutor)
            .thenApply(ResponseEntityUtils::noContent);
    }

    @Override
    public CompletableFuture<ResponseEntity<Void>> updateAllocation(
        String apiKey,
        Long id,
        UpdateAllocationDTO updateAllocationDTO
    ) {
        return runAsync(() -> allocationService.updateAllocation(id, updateAllocationDTO), controllersExecutor)
            .thenApply(ResponseEntityUtils::noContent);
    }

    @Override
    public CompletableFuture<ResponseEntity<List<AllocationDTO>>> listAllocations(
        String apiKey,
        String employeeEmail,
        Long roomId,
        LocalDate starAt,
        LocalDate endAt,
        String orderBy,
        Integer limit,
        Integer page
    ) {
        return supplyAsync(
                () -> allocationService.listAllocations(employeeEmail, roomId, starAt, endAt, orderBy, limit, page),
                controllersExecutor
            )
            .thenApply(ResponseEntityUtils::ok);
    }
}