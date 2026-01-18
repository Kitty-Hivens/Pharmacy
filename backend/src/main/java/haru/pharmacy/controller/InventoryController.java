package haru.pharmacy.controller;

import haru.pharmacy.dto.InventoryAddDto;
import haru.pharmacy.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
@Tag(name = "Inventory", description = "Inventory and Stock Management")
public class InventoryController {

    private final InventoryService service;

    @PostMapping("/restock")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Restock inventory", operationId = "restockInventory")
    public void restock(@Valid @RequestBody InventoryAddDto dto) {
        service.addStock(dto);
    }
}
