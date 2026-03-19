package haru.pharmacy.controller;

import haru.pharmacy.dto.InventoryAddDto;
import haru.pharmacy.model.Inventory;
import haru.pharmacy.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
@Tag(name = "Inventory", description = "Inventory and Stock Management")
public class InventoryController {

    private final InventoryService service;

    @PostMapping("/restock")
    @PreAuthorize("hasAnyRole('ADMIN', 'PHARMACIST')")
    @Operation(summary = "Restock inventory", operationId = "restockInventory")
    public void restock(@Valid @RequestBody InventoryAddDto dto) {
        service.addStock(dto);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PHARMACIST')")
    @Operation(summary = "Get inventory items with search", operationId = "getInventory")
    public Page<Inventory> getAll(
            @Parameter(description = "Search by medicine name or batch number")
            @RequestParam(required = false) String search,

            @PageableDefault(sort = "expirationDate", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return service.getAll(search, pageable);
    }
}
