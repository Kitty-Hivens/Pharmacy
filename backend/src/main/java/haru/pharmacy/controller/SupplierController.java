package haru.pharmacy.controller;

import haru.pharmacy.dto.SupplierDto;
import haru.pharmacy.service.SupplierService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/suppliers")
@RequiredArgsConstructor
@Tag(name = "Supplier", description = "Supplier Management")
public class SupplierController {

    private final SupplierService service;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new supplier", operationId = "createSupplier")
    public SupplierDto create(@Valid @RequestBody SupplierDto dto) {
        return service.create(dto);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PHARMACIST')")
    @Operation(summary = "Get all suppliers", operationId = "getAllSuppliers")
    public List<SupplierDto> getAll() {
        return service.getAll();
    }
}
