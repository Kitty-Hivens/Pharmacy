package haru.pharmacy.controller;

import haru.pharmacy.dto.SaleCreateDto;
import haru.pharmacy.dto.SaleResponseDto;
import haru.pharmacy.service.SaleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/sales")
@RequiredArgsConstructor
public class SaleController {

    private final SaleService service;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PHARMACIST')")
    public void create(@Valid @RequestBody SaleCreateDto dto, Principal principal) {
        service.createSale(dto, principal.getName());
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PHARMACIST')")
    public List<SaleResponseDto> getAll() {
        return service.getAllSales();
    }
}
