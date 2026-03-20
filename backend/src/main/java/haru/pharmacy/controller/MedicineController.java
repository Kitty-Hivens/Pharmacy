package haru.pharmacy.controller;

import haru.pharmacy.dto.medicine.MedicineCreateDto;
import haru.pharmacy.dto.medicine.MedicineResponseDto;
import haru.pharmacy.dto.medicine.MedicineUpdateDto;
import haru.pharmacy.service.interfaces.MedicineService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/medicines")
@RequiredArgsConstructor
@Tag(name = "Medicine", description = "Medicine Inventory Management")
public class MedicineController {

    private final MedicineService service;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new medicine", operationId = "createMedicine")
    public ResponseEntity<MedicineResponseDto> create(@Valid @RequestBody MedicineCreateDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update existing medicine", operationId = "updateMedicine")
    public MedicineResponseDto update(@PathVariable Long id,
                                      @Valid @RequestBody MedicineUpdateDto dto) {
        return service.update(id, dto);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PHARMACIST')")
    @Operation(summary = "Get medicine by ID", operationId = "getMedicine")
    public MedicineResponseDto get(@PathVariable Long id) {
        return service.get(id);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PHARMACIST')")
    @Operation(summary = "Get all medicines", operationId = "getAllMedicines")
    public List<MedicineResponseDto> getAll() {
        return service.getAll();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete medicine", operationId = "deleteMedicine")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
