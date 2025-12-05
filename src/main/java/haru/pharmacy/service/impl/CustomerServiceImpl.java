package haru.pharmacy.service.impl;

import haru.pharmacy.dto.customer.CustomerCreateDto;
import haru.pharmacy.dto.customer.CustomerResponseDto;
import haru.pharmacy.dto.customer.CustomerUpdateDto;
import haru.pharmacy.exception.ResourceNotFoundException;
import haru.pharmacy.mapper.CustomerMapper;
import haru.pharmacy.model.Customer;
import haru.pharmacy.repository.CustomerRepository;
import haru.pharmacy.service.interfaces.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository repository;
    private final CustomerMapper mapper;

    @Override
    public CustomerResponseDto create(CustomerCreateDto dto) {
        Customer entity = mapper.toEntity(dto);
        return mapper.toDto(repository.save(entity));
    }

    @Override
    public CustomerResponseDto update(Long id, CustomerUpdateDto dto) {
        Customer entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("error.customer.not_found"));

        mapper.updateEntity(dto, entity);
        return mapper.toDto(repository.save(entity));
    }

    @Override
    public CustomerResponseDto get(Long id) {
        return repository.findById(id)
                .map(mapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("error.customer.not_found"));
    }

    @Override
    public List<CustomerResponseDto> getAll() {
        return repository.findAll().stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }
}
