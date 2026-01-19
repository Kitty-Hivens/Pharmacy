package haru.pharmacy.controller;

import haru.pharmacy.dto.SaleCreateDto;
import haru.pharmacy.dto.SaleResponseDto;
import haru.pharmacy.service.SaleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/sales")
@RequiredArgsConstructor
@Tag(name = "Sale", description = "Sales and POS Operations")
public class SaleController {

    private final SaleService service;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PHARMACIST')")
    @Operation(summary = "Create a new sale", operationId = "createSale")
    public void create(@Valid @RequestBody SaleCreateDto dto, Principal principal) {
        service.createSale(dto, principal.getName());
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PHARMACIST')")
    @Operation(summary = "Get sales history with filters", operationId = "getAllSales")
    public Page<SaleResponseDto> getAll(
            @Parameter(description = "Filter by start date (ISO DateTime)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,

            @Parameter(description = "Filter by end date (ISO DateTime)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,

            @PageableDefault(sort = "saleDateTime", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return service.getAllSales(from, to, pageable);
    }
}
