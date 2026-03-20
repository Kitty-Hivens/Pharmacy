package haru.pharmacy.controller;

import haru.pharmacy.dto.InventoryAddDto;
import haru.pharmacy.dto.InventoryResponseDto;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<Void> restock(@Valid @RequestBody InventoryAddDto dto) {
        service.addStock(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PHARMACIST')")
    @Operation(summary = "Get inventory items with search", operationId = "getInventory")
    public Page<InventoryResponseDto> getAll(
            @Parameter(description = "Search by medicine name or batch number")
            @RequestParam(required = false) String search,

            @PageableDefault(sort = "expirationDate", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return service.getAll(search, pageable);
    }
}
