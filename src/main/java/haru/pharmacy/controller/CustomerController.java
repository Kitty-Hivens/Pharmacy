package haru.pharmacy.controller;

import haru.pharmacy.dto.customer.CustomerCreateDto;
import haru.pharmacy.dto.customer.CustomerResponseDto;
import haru.pharmacy.dto.customer.CustomerUpdateDto;
import haru.pharmacy.service.interfaces.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService service;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PHARMACIST')")
    public CustomerResponseDto create(@RequestBody CustomerCreateDto dto) {
        return service.create(dto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PHARMACIST')")
    public CustomerResponseDto update(@PathVariable Long id,
                                      @RequestBody CustomerUpdateDto dto) {
        return service.update(id, dto);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PHARMACIST')")
    public CustomerResponseDto get(@PathVariable Long id) {
        return service.get(id);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PHARMACIST')")
    public List<CustomerResponseDto> getAll() {
        return service.getAll();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
