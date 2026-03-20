package haru.pharmacy.service;

import haru.pharmacy.dto.SupplierDto;
import haru.pharmacy.mapper.SupplierMapper;
import haru.pharmacy.repository.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SupplierService {

    private final SupplierRepository repository;
    private final SupplierMapper mapper;

    @Transactional
    public SupplierDto create(SupplierDto dto) {
        return mapper.toDto(repository.save(mapper.toEntity(dto)));
    }

    @Transactional(readOnly = true)
    public List<SupplierDto> getAll() {
        return repository.findAll().stream().map(mapper::toDto).toList();
    }
}