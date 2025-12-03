package haru.pharmacy.controller;

import haru.pharmacy.dto.SupplierDto;
import haru.pharmacy.service.SupplierService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/suppliers")
@RequiredArgsConstructor
public class SupplierController {

    private final SupplierService service;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public SupplierDto create(@RequestBody SupplierDto dto) {
        return service.create(dto);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PHARMACIST')")
    public List<SupplierDto> getAll() {
        return service.getAll();
    }
}