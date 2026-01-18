package haru.pharmacy.controller;

import haru.pharmacy.dto.SaleCreateDto;
import haru.pharmacy.dto.SaleResponseDto;
import haru.pharmacy.service.SaleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

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
    @Operation(summary = "Get all sales history", operationId = "getAllSales")
    public List<SaleResponseDto> getAll() {
        return service.getAllSales();
    }
}
